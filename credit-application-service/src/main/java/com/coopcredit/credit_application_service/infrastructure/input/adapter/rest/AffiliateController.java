package com.coopcredit.credit_application_service.infrastructure.input.adapter.rest;

import com.coopcredit.credit_application_service.application.dto.AffiliateMapper;
import com.coopcredit.credit_application_service.application.dto.AffiliateResponse;
import com.coopcredit.credit_application_service.application.dto.CreateAffiliateRequest;
import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.port.input.CreateAffiliateUseCase;
import com.coopcredit.credit_application_service.domain.port.input.GetAffiliateUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/affiliates")
@Validated
public class AffiliateController {

    private final CreateAffiliateUseCase createUseCase;
    private final GetAffiliateUseCase getUseCase;
    private final com.coopcredit.credit_application_service.application.dto.AffiliateMapper mapper;

    public AffiliateController(CreateAffiliateUseCase createUseCase, GetAffiliateUseCase getUseCase, AffiliateMapper mapper) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AffiliateResponse> create(@Valid @RequestBody CreateAffiliateRequest request) {
        // Use the input port command API
        com.coopcredit.credit_application_service.domain.port.input.CreateAffiliateUseCase.CreateAffiliateCommand cmd =
            new com.coopcredit.credit_application_service.domain.port.input.CreateAffiliateUseCase.CreateAffiliateCommand(
                request.name(), request.document(), request.salary().toString(), request.registrationDate().toString()
            );

        Affiliate created = createUseCase.execute(cmd);
        AffiliateResponse response = mapper.toResponse(created);
        URI location = URI.create(String.format("/affiliates/%d", created.getId()));
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AffiliateResponse> getById(@PathVariable Long id) {
        Optional<Affiliate> opt = getUseCase.getById(id);
        return opt.map(a -> ResponseEntity.ok(mapper.toResponse(a)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<AffiliateResponse> getByDocument(@RequestParam(required = false) String document) {
        if (document == null || document.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Affiliate> opt = getUseCase.getByDocument(document);
        return opt.map(a -> ResponseEntity.ok(mapper.toResponse(a)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
