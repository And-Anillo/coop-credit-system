package com.coopcredit.credit_application_service.application.service;

import com.coopcredit.credit_application_service.application.dto.CreditApplicationMapper;
import com.coopcredit.credit_application_service.application.dto.CreditApplicationResponse;
import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.exception.DomainException;
import com.coopcredit.credit_application_service.domain.port.input.GetCreditApplicationUseCase;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import com.coopcredit.credit_application_service.domain.port.output.CreditApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application Service: GetCreditApplicationService
 * Implements read operations for credit applications
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCreditApplicationService implements GetCreditApplicationUseCase {

    private final CreditApplicationRepository creditApplicationRepository;
    private final AffiliateRepository affiliateRepository;
    private final CreditApplicationMapper creditApplicationMapper;

    /**
     * Get a credit application by ID
     */
    @Transactional(readOnly = true)
    @Override
    public CreditApplicationResponse getById(Long id) {
        log.debug("Fetching credit application by ID: {}", id);
        
        return creditApplicationRepository.findById(id)
                .map(creditApplicationMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Credit application not found with ID: {}", id);
                    return new DomainException(
                            "Solicitud de crédito no encontrada",
                            "CREDIT_APPLICATION_NOT_FOUND"
                    );
                });
    }

    /**
     * Get all credit applications for an affiliate by document
     */
    @Transactional(readOnly = true)
    @Override
    public List<CreditApplicationResponse> getAllByAffiliate(String affiliateDocument) {
        log.debug("Fetching all credit applications for affiliate document: {}", affiliateDocument);
        
        // Step 1: Find affiliate by document
        Affiliate affiliate = affiliateRepository.findByDocument(affiliateDocument)
                .orElseThrow(() -> {
                    log.warn("Affiliate not found with document: {}", affiliateDocument);
                    return new DomainException(
                            "El afiliado no fue encontrado",
                            "AFFILIATE_NOT_FOUND"
                    );
                });

        // Step 2: Find all applications for the affiliate
        List<CreditApplicationResponse> applications = creditApplicationRepository
                .findAllByAffiliateId(affiliate.getId())
                .stream()
                .map(creditApplicationMapper::toResponse)
                .toList();

        log.info("Found {} credit applications for affiliate ID: {}", applications.size(), affiliate.getId());
        return applications;
    }
}
