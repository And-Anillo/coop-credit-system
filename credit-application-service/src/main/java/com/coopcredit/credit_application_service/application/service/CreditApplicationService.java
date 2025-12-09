package com.coopcredit.credit_application_service.application.service;

import com.coopcredit.credit_application_service.application.dto.CreateCreditApplicationRequest;
import com.coopcredit.credit_application_service.application.dto.CreditApplicationMapper;
import com.coopcredit.credit_application_service.application.dto.CreditApplicationResponse;
import com.coopcredit.credit_application_service.domain.entity.CreditApplication;
import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.exception.AffiliateNotFoundException;
import com.coopcredit.credit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.credit_application_service.domain.port.input.CreateCreditApplicationUseCase;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import com.coopcredit.credit_application_service.domain.port.output.CreditApplicationRepository;
import com.coopcredit.credit_application_service.domain.port.output.RiskCentralPort;
import com.coopcredit.credit_application_service.infrastructure.exception.InfrastructureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Application Service: CreditApplicationService
 * Orchestrates the credit application evaluation process
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditApplicationService implements CreateCreditApplicationUseCase {

    private final AffiliateRepository affiliateRepository;
    private final CreditApplicationRepository creditApplicationRepository;
    private final RiskCentralPort riskCentralPort;
    private final CreditApplicationMapper creditApplicationMapper;

    /**
     * Create a credit application with automatic risk evaluation and approval/rejection decision
     */
    @Transactional
    public CreditApplicationResponse create(CreateCreditApplicationRequest request) {
        // Step 1: Validate affiliate exists and is active
        Affiliate affiliate = affiliateRepository.findById(request.affiliateId())
                .orElseThrow(() -> new AffiliateNotFoundException(
                        "El afiliado con ID " + request.affiliateId() + " no fue encontrado"
                ));

        if (affiliate.getStatus() != Affiliate.AffiliateStatus.ACTIVE) {
            throw new AffiliateNotFoundException(
                    "El afiliado con ID " + request.affiliateId() + " no está activo"
            );
        }

        log.info("Creating credit application for affiliate ID: {}, amount: {}, term: {}",
                request.affiliateId(), request.amount(), request.term());

        // Step 2: Create domain entity (Status: PENDING)
        CreditApplication creditApp = CreditApplication.create(
                request.affiliateId(),
                request.amount(),
                request.term()
        );

        // Step 3: Evaluate risk with external service
        RiskEvaluation riskEvaluation;
        try {
            riskEvaluation = riskCentralPort.evaluateRisk(
                    affiliate.getDocument(),
                    request.amount().doubleValue(),
                    request.term()
            );
            log.info("Risk evaluation completed: score={}, riskLevel={}",
                    riskEvaluation.getScore(), riskEvaluation.getRiskLevel());
        } catch (InfrastructureException e) {
            log.error("Risk evaluation failed for affiliate ID: {}", request.affiliateId(), e);
            throw e;
        }

        // Step 4: Apply business rules for approval/rejection
        boolean isApproved = applyApprovalRules(request.amount(), riskEvaluation);

        // Step 5: Update entity with risk data and status
        creditApp.updateRiskEvaluation(riskEvaluation.getScore(), riskEvaluation.getRiskLevel());

        if (isApproved) {
            creditApp.approve();
            log.info("Credit application APPROVED for affiliate ID: {}", request.affiliateId());
        } else {
            creditApp.reject();
            log.info("Credit application REJECTED for affiliate ID: {}", request.affiliateId());
        }

        // Step 6: Save to repository
        CreditApplication saved = creditApplicationRepository.save(creditApp);

        // Step 7: Return response DTO
        return creditApplicationMapper.toResponse(saved);
    }

    /**
     * Apply business rules to determine if credit application should be approved
     *
     * @param amount the credit amount requested
     * @param riskEvaluation the risk evaluation result
     * @return true if approved, false if rejected
     */
    private boolean applyApprovalRules(BigDecimal amount, RiskEvaluation riskEvaluation) {
        String riskLevel = riskEvaluation.getRiskLevel();

        // Rule 1: Reject if high risk (Spanish: ALTO)
        if (riskLevel.contains("ALTO")) {
            log.debug("Rejecting application due to high risk level: {}", riskLevel);
            return false;
        }

        // Rule 2: Reject if medium risk (Spanish: MEDIO) and amount > 10,000,000
        if (riskLevel.contains("MEDIO") && amount.compareTo(new BigDecimal("10000000")) > 0) {
            log.debug("Rejecting application due to medium risk and high amount: {} > 10,000,000", amount);
            return false;
        }

        // Otherwise approve
        log.debug("Approving application with risk level: {} and amount: {}", riskLevel, amount);
        return true;
    }

    /**
     * Implementation of the domain input port
     */
    @Override
    public CreditApplication execute(CreateCreditApplicationCommand command) {
        CreateCreditApplicationRequest request = new CreateCreditApplicationRequest(
                command.affiliateId(),
                command.amount(),
                command.term()
        );
        CreditApplicationResponse response = create(request);
        // Retrieve the saved domain entity
        return creditApplicationRepository.findById(response.id())
                .orElseThrow(() -> new IllegalStateException(
                        "Credit application not found after creation: " + response.id()
                ));
    }
}
