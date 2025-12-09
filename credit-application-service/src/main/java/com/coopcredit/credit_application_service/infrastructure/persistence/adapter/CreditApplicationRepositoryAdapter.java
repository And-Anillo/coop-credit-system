package com.coopcredit.credit_application_service.infrastructure.persistence.adapter;

import com.coopcredit.credit_application_service.application.dto.CreditApplicationMapper;
import com.coopcredit.credit_application_service.domain.entity.CreditApplication;
import com.coopcredit.credit_application_service.domain.port.output.CreditApplicationRepository;
import com.coopcredit.credit_application_service.infrastructure.persistence.entity.CreditApplicationEntity;
import com.coopcredit.credit_application_service.infrastructure.persistence.repository.CreditApplicationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter: CreditApplicationRepositoryAdapter
 * Implements the CreditApplicationRepository output port
 * Converts between domain and persistence layers
 */
@Component
@RequiredArgsConstructor
public class CreditApplicationRepositoryAdapter implements CreditApplicationRepository {

    private final CreditApplicationJpaRepository jpaRepository;
    private final CreditApplicationMapper mapper;

    @Override
    public CreditApplication save(CreditApplication creditApplication) {
        CreditApplicationEntity entity = mapper.toEntity(creditApplication);
        CreditApplicationEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CreditApplication> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<CreditApplication> findAllByAffiliateId(Long affiliateId) {
        return jpaRepository.findAllByAffiliateId(affiliateId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
