package com.coopcredit.credit_application_service.application.service;

import com.coopcredit.credit_application_service.application.dto.AffiliateMapper;
import com.coopcredit.credit_application_service.application.dto.AffiliateResponse;
import com.coopcredit.credit_application_service.application.dto.CreateAffiliateRequest;
import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.exception.DomainException;
import com.coopcredit.credit_application_service.domain.port.input.CreateAffiliateUseCase;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import com.coopcredit.credit_application_service.domain.port.output.RiskCentralPort;
import com.coopcredit.credit_application_service.infrastructure.exception.InfrastructureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Application service for managing affiliates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AffiliateManagementService implements CreateAffiliateUseCase {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateMapper affiliateMapper;
    private final RiskCentralPort riskCentralPort;

    /**
     * Create an affiliate from a request and return a response DTO.
     * Business rule: there must not be an existing affiliate with the same document (checked by name for now).
     * Also evaluates the affiliate's risk with the external Risk Central service.
     */
    public AffiliateResponse create(CreateAffiliateRequest request) {
        // Duplicate check (using repository findByName as the current port supports it)
        Optional<Affiliate> existing = affiliateRepository.findByName(request.name());
        if (existing.isPresent()) {
            throw new DomainException("El afiliado con este documento ya existe", "AFFILIATE_DUPLICATE");
        }

        Affiliate domain = affiliateMapper.toDomain(request);
        Affiliate saved = affiliateRepository.save(domain);

        // Evaluate risk with external service
        try {
            var riskEvaluation = riskCentralPort.evaluateRisk(
                saved.getDocument(),
                saved.getSalary().doubleValue(),
                12  // default loan term in months
            );
            log.info("Risk evaluation completed for affiliate {}: score={}, riskLevel={}",
                    saved.getDocument(), riskEvaluation.getScore(), riskEvaluation.getRiskLevel());
        } catch (InfrastructureException e) {
            log.warn("Risk evaluation failed for affiliate {}: {}",
                    saved.getDocument(), e.getMessage());
            // Continue even if risk evaluation fails - it's informational
        }

        return affiliateMapper.toResponse(saved);
    }

    /**
     * Implementation of the domain input port. This method creates and returns the domain Affiliate.
     */
    @Override
    public Affiliate execute(CreateAffiliateCommand command) {
        // Convert command values to proper types and reuse create logic
        BigDecimal salary = new BigDecimal(command.salary());
        LocalDate date = LocalDate.parse(command.registrationDate());

        CreateAffiliateRequest req = new CreateAffiliateRequest(
            command.name(),
            command.document(),
            salary,
            date
        );

        AffiliateResponse response = create(req);
        // Retrieve the saved domain from repository (by name)
        return affiliateRepository.findByName(response.name()).orElseThrow(() ->
            new DomainException("El afiliado creado no se pudo recuperar", "AFFILIATE_RETRIEVE_FAILED")
        );
    }
}
