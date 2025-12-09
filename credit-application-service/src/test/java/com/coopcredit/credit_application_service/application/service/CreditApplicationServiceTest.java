package com.coopcredit.credit_application_service.application.service;

import com.coopcredit.credit_application_service.application.dto.CreateCreditApplicationRequest;
import com.coopcredit.credit_application_service.application.dto.CreditApplicationMapper;
import com.coopcredit.credit_application_service.application.dto.CreditApplicationResponse;
import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.entity.CreditApplication;
import com.coopcredit.credit_application_service.domain.exception.AffiliateNotFoundException;
import com.coopcredit.credit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import com.coopcredit.credit_application_service.domain.port.output.CreditApplicationRepository;
import com.coopcredit.credit_application_service.domain.port.output.RiskCentralPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditApplicationServiceTest {

    @Mock
    private AffiliateRepository affiliateRepository;

    @Mock
    private CreditApplicationRepository creditApplicationRepository;

    @Mock
    private RiskCentralPort riskCentralPort;

    @Mock
    private CreditApplicationMapper creditApplicationMapper;

    @InjectMocks
    private CreditApplicationService service;

    @Test
    void createCreditApplication_lowRisk_shouldApprove() {
        // Setup
        CreateCreditApplicationRequest request = new CreateCreditApplicationRequest(
                1L,
                new BigDecimal("5000000"),
                12
        );

        Affiliate affiliate = createTestAffiliate(1L);
        CreditApplication creditApp = CreditApplication.create(1L, new BigDecimal("5000000"), 12);
        creditApp.updateRiskEvaluation(400, "BAJO");
        creditApp.approve();
        CreditApplication saved = CreditApplication.reconstruct(1L, 1L, new BigDecimal("5000000"), 12,
                CreditApplication.CreditApplicationStatus.APPROVED, LocalDate.now(), 400, "BAJO",
                LocalDateTime.now(), LocalDateTime.now());

        CreditApplicationResponse response = new CreditApplicationResponse(
                1L, 1L, new BigDecimal("5000000"), 12, "Aprobado", LocalDate.now(),
                400, "BAJO", LocalDateTime.now(), LocalDateTime.now()
        );

        // Mock
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(affiliate));
        when(riskCentralPort.evaluateRisk("12345678", 5000000.0, 12))
                .thenReturn(new RiskEvaluation(400, "BAJO", "Low risk profile"));
        when(creditApplicationRepository.save(any(CreditApplication.class))).thenReturn(saved);
        when(creditApplicationMapper.toResponse(saved)).thenReturn(response);

        // Execute
        CreditApplicationResponse result = service.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Aprobado", result.status());
    }

    @Test
    void createCreditApplication_highRisk_shouldReject() {
        // Setup
        CreateCreditApplicationRequest request = new CreateCreditApplicationRequest(
                1L,
                new BigDecimal("5000000"),
                12
        );

        Affiliate affiliate = createTestAffiliate(1L);
        CreditApplication saved = CreditApplication.reconstruct(1L, 1L, new BigDecimal("5000000"), 12,
                CreditApplication.CreditApplicationStatus.REJECTED, LocalDate.now(), 900, "ALTO",
                LocalDateTime.now(), LocalDateTime.now());

        CreditApplicationResponse response = new CreditApplicationResponse(
                1L, 1L, new BigDecimal("5000000"), 12, "Rechazado", LocalDate.now(),
                900, "ALTO", LocalDateTime.now(), LocalDateTime.now()
        );

        // Mock
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(affiliate));
        when(riskCentralPort.evaluateRisk("12345678", 5000000.0, 12))
                .thenReturn(new RiskEvaluation(900, "ALTO", "High risk profile"));
        when(creditApplicationRepository.save(any(CreditApplication.class))).thenReturn(saved);
        when(creditApplicationMapper.toResponse(saved)).thenReturn(response);

        // Execute
        CreditApplicationResponse result = service.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Rechazado", result.status());
    }

    @Test
    void createCreditApplication_mediumRiskHighAmount_shouldReject() {
        // Setup
        CreateCreditApplicationRequest request = new CreateCreditApplicationRequest(
                1L,
                new BigDecimal("15000000"),  // Amount > 10,000,000
                12
        );

        Affiliate affiliate = createTestAffiliate(1L);
        CreditApplication saved = CreditApplication.reconstruct(1L, 1L, new BigDecimal("15000000"), 12,
                CreditApplication.CreditApplicationStatus.REJECTED, LocalDate.now(), 600, "MEDIO",
                LocalDateTime.now(), LocalDateTime.now());

        CreditApplicationResponse response = new CreditApplicationResponse(
                1L, 1L, new BigDecimal("15000000"), 12, "Rechazado", LocalDate.now(),
                600, "MEDIO", LocalDateTime.now(), LocalDateTime.now()
        );

        // Mock
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(affiliate));
        when(riskCentralPort.evaluateRisk("12345678", 15000000.0, 12))
                .thenReturn(new RiskEvaluation(600, "MEDIO", "Medium risk profile"));
        when(creditApplicationRepository.save(any(CreditApplication.class))).thenReturn(saved);
        when(creditApplicationMapper.toResponse(saved)).thenReturn(response);

        // Execute
        CreditApplicationResponse result = service.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("Rechazado", result.status());
    }

    @Test
    void createCreditApplication_affiliateNotFound_shouldThrow() {
        // Setup
        CreateCreditApplicationRequest request = new CreateCreditApplicationRequest(
                999L,
                new BigDecimal("5000000"),
                12
        );

        // Mock
        when(affiliateRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute & Assert
        assertThrows(AffiliateNotFoundException.class, () -> service.create(request));
    }

    @Test
    void createCreditApplication_affiliateInactive_shouldThrow() {
        // Setup
        CreateCreditApplicationRequest request = new CreateCreditApplicationRequest(
                1L,
                new BigDecimal("5000000"),
                12
        );

        Affiliate inactiveAffiliate = Affiliate.create("John Doe", new BigDecimal("50000"),
                LocalDate.now().minusYears(2), "12345678");
        inactiveAffiliate.deactivate();

        // Mock
        when(affiliateRepository.findById(1L)).thenReturn(Optional.of(inactiveAffiliate));

        // Execute & Assert
        assertThrows(AffiliateNotFoundException.class, () -> service.create(request));
    }

    private Affiliate createTestAffiliate(Long id) {
        Affiliate affiliate = Affiliate.create("John Doe", new BigDecimal("50000"),
                LocalDate.now().minusYears(2), "12345678");
        // Reconstruct with ID to simulate persisted affiliate
        return Affiliate.reconstruct(id, affiliate.getName(), affiliate.getSalary(),
                affiliate.getRegistrationDate(), affiliate.getStatus(),
                LocalDateTime.now(), LocalDateTime.now(), affiliate.getDocument());
    }
}
