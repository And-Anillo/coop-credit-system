package com.coopcredit.credit_application_service.application.service;

import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.port.input.GetAffiliateUseCase;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAffiliateService implements GetAffiliateUseCase {

    private final AffiliateRepository affiliateRepository;

    @Override
    public Optional<Affiliate> getById(Long id) {
        return affiliateRepository.findById(id);
    }

    @Override
    public Optional<Affiliate> getByName(String name) {
        return affiliateRepository.findByName(name);
    }

    
    @Override
    public Optional<Affiliate> getByDocument(String document) {
        return affiliateRepository.findByDocument(document);
    }
}
