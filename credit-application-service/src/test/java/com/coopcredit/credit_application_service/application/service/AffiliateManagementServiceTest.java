package com.coopcredit.credit_application_service.application.service;

import com.coopcredit.credit_application_service.application.dto.AffiliateMapper;
import com.coopcredit.credit_application_service.application.dto.AffiliateResponse;
import com.coopcredit.credit_application_service.application.dto.CreateAffiliateRequest;
import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.exception.DomainException;
import com.coopcredit.credit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import com.coopcredit.credit_application_service.domain.port.output.RiskCentralPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AffiliateManagementServiceTest {

    @Mock
    private AffiliateRepository affiliateRepository;

    @Mock
    private AffiliateMapper affiliateMapper;

    @Mock
    private RiskCentralPort riskCentralPort;

    @InjectMocks
    private AffiliateManagementService service;

    @Test
    void createAffiliate_success() {
        CreateAffiliateRequest req = new CreateAffiliateRequest(
            "Juan",
            "12345678",
            new BigDecimal("5000"),
            LocalDate.now()
        );

        Affiliate domain = Affiliate.create(req.name(), req.salary(), req.registrationDate(), req.document());
        Affiliate saved = Affiliate.reconstruct(1L, domain.getName(), domain.getSalary(), domain.getRegistrationDate(), domain.getStatus(), domain.getCreatedAt(), domain.getUpdatedAt(), domain.getDocument());

        AffiliateResponse response = new AffiliateResponse(
            saved.getId(),
            saved.getName(),
            req.document(),
            saved.getSalary(),
            saved.getRegistrationDate(),
            saved.getStatus().getLabel(),
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );

        when(affiliateRepository.findByName(req.name())).thenReturn(Optional.empty());
        when(affiliateMapper.toDomain(req)).thenReturn(domain);
        when(affiliateRepository.save(domain)).thenReturn(saved);
        when(affiliateMapper.toResponse(saved)).thenReturn(response);
        when(riskCentralPort.evaluateRisk("12345678", 5000.0, 12))
            .thenReturn(new RiskEvaluation(500, "BAJO", "Risk assessment completed"));

        AffiliateResponse result = service.create(req);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Juan", result.name());
        assertEquals(req.document(), result.document());
        verify(riskCentralPort, times(1)).evaluateRisk("12345678", 5000.0, 12);
    }

    @Test
    void createAffiliate_duplicate_throwsDomainException() {
        CreateAffiliateRequest req = new CreateAffiliateRequest(
            "Juan",
            "12345678",
            new BigDecimal("5000"),
            LocalDate.now()
        );

        Affiliate existing = Affiliate.create(req.name(), new BigDecimal("4000"), LocalDate.now().minusDays(10), "1001");
        when(affiliateRepository.findByName(req.name())).thenReturn(Optional.of(existing));

        DomainException ex = assertThrows(DomainException.class, () -> service.create(req));
        assertEquals("El afiliado con este documento ya existe", ex.getMessage());
    }
}
