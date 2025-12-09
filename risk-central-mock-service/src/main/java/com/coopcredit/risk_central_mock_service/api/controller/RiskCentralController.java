package com.coopcredit.risk_central_mock_service.api.controller;

import com.coopcredit.risk_central_mock_service.api.dto.RiskEvaluationRequest;
import com.coopcredit.risk_central_mock_service.api.dto.RiskEvaluationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/risk-evaluation")
@Validated
public class RiskCentralController {

    /**
     * Evaluate credit risk based on document, amount, and term.
     * Uses a deterministic algorithm based on document hash.
     */
    @PostMapping
    public ResponseEntity<RiskEvaluationResponse> evaluateRisk(@Valid @RequestBody RiskEvaluationRequest request) {
        // Generate deterministic seed from document
        long seed = Math.abs(request.document().hashCode());

        // Generate score between 300 and 950
        // Use seed to ensure same document always gets same score
        int score = 300 + (int) ((seed % 651));

        // Determine risk level and detail based on score
        String riskLevel;
        String detail;

        if (score <= 500) {
            riskLevel = "ALTO RIESGO";
            detail = "El solicitante presenta un alto riesgo de incumplimiento basado en el análisis del documento";
        } else if (score <= 700) {
            riskLevel = "MEDIO RIESGO";
            detail = "El solicitante presenta un riesgo moderado. Se recomienda solicitar garantías adicionales";
        } else {
            riskLevel = "BAJO RIESGO";
            detail = "El solicitante presenta bajo riesgo de incumplimiento. Se aprueba el crédito";
        }

        RiskEvaluationResponse response = new RiskEvaluationResponse(
            request.document(),
            score,
            riskLevel,
            detail,
            LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
