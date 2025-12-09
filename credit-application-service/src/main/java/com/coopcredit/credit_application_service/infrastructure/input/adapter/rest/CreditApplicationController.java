package com.coopcredit.credit_application_service.infrastructure.input.adapter.rest;

import com.coopcredit.credit_application_service.application.dto.CreateCreditApplicationRequest;
import com.coopcredit.credit_application_service.application.dto.CreditApplicationResponse;
import com.coopcredit.credit_application_service.domain.port.input.CreateCreditApplicationUseCase;
import com.coopcredit.credit_application_service.domain.port.input.GetCreditApplicationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller: CreditApplicationController
 * Exposes credit application management endpoints
 */
@Slf4j
@RestController
@RequestMapping("/credit-applications")
@RequiredArgsConstructor
public class CreditApplicationController {

    private final CreateCreditApplicationUseCase createCreditApplicationUseCase;
    private final GetCreditApplicationUseCase getCreditApplicationUseCase;

    /**
     * Create a new credit application
     *
     * @param request the credit application request
     * @return 201 Created with the response
     */
    @PostMapping
    public ResponseEntity<CreditApplicationResponse> createCreditApplication(
            @Valid @RequestBody CreateCreditApplicationRequest request) {
        
        log.info("Received request to create credit application for affiliate ID: {}", request.affiliateId());
        
        // Delegate to service
        CreditApplicationResponse response = ((com.coopcredit.credit_application_service.application.service.CreditApplicationService) 
                createCreditApplicationUseCase).create(request);
        
        log.info("Credit application created successfully with ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a credit application by ID
     *
     * @param id the credit application ID
     * @return 200 OK with the credit application
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreditApplicationResponse> getCreditApplication(@PathVariable Long id) {
        log.debug("Received request to get credit application by ID: {}", id);
        
        CreditApplicationResponse response = getCreditApplicationUseCase.getById(id);
        
        log.debug("Credit application found with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all credit applications for an affiliate by document
     *
     * @param document the affiliate's document identifier
     * @return 200 OK with list of credit applications
     */
    @GetMapping("/by-affiliate/{document}")
    public ResponseEntity<List<CreditApplicationResponse>> getCreditApplicationsByAffiliate(
            @PathVariable String document) {
        
        log.debug("Received request to get credit applications for affiliate document: {}", document);
        
        List<CreditApplicationResponse> applications = getCreditApplicationUseCase.getAllByAffiliate(document);
        
        log.info("Retrieved {} credit applications for affiliate document: {}", applications.size(), document);
        return ResponseEntity.ok(applications);
    }
}
