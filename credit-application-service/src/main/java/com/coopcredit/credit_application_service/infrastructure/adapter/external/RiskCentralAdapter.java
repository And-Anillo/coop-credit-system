package com.coopcredit.credit_application_service.infrastructure.adapter.external;

import com.coopcredit.credit_application_service.domain.model.RiskEvaluation;
import com.coopcredit.credit_application_service.domain.port.output.RiskCentralPort;
import com.coopcredit.credit_application_service.infrastructure.exception.InfrastructureException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Adapter for external Risk Central Service
 * Implements the RiskCentralPort to communicate with the external risk evaluation API
 */
@Component
public class RiskCentralAdapter implements RiskCentralPort {

    private final RestClient restClient;

    public RiskCentralAdapter(RestClient riskCentralRestClient) {
        this.restClient = riskCentralRestClient;
    }

    @Override
    public RiskEvaluation evaluateRisk(String document, Double amount, Integer term) {
        try {
            // Build request payload
            RiskEvaluationRequestDto request = new RiskEvaluationRequestDto(
                document,
                new BigDecimal(amount),
                term
            );

            // Call the external service
            RiskEvaluationResponseDto response = restClient
                    .post()
                    .uri("/risk-evaluation")
                    .body(request)
                    .retrieve()
                    .body(RiskEvaluationResponseDto.class);

            if (response == null) {
                throw new InfrastructureException("El servicio de riesgo devolvió una respuesta nula");
            }

            // Map to domain model
            return new RiskEvaluation(
                response.score(),
                response.riskLevel(),
                response.detail()
            );
        } catch (RestClientException e) {
            throw new InfrastructureException(
                "Error al comunicarse con el servicio de riesgo: " + e.getMessage(),
                e
            );
        } catch (Exception e) {
            throw new InfrastructureException(
                "Error inesperado durante la evaluación de riesgo: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Internal DTO for Risk Central request
     */
    private record RiskEvaluationRequestDto(
        String document,
        BigDecimal amount,
        Integer term
    ) {}

    /**
     * Internal DTO for Risk Central response
     */
    private record RiskEvaluationResponseDto(
        String document,
        Integer score,
        String riskLevel,
        String detail
    ) {}
}
