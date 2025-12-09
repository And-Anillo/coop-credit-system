package com.coopcredit.credit_application_service.infrastructure.persistence.adapter;

import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.domain.port.output.AffiliateRepository;
import com.coopcredit.credit_application_service.infrastructure.persistence.entity.AffiliateEntity;
import com.coopcredit.credit_application_service.infrastructure.persistence.repository.AffiliateJpaRepository;
import com.coopcredit.credit_application_service.application.dto.AffiliateMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persistence Adapter: implements the AffiliateRepository output port using Spring Data JPA.
 */
@Component
public class AffiliateRepositoryAdapter implements AffiliateRepository {

    private final AffiliateJpaRepository jpaRepository;
    private final AffiliateMapper mapper;

    public AffiliateRepositoryAdapter(AffiliateJpaRepository jpaRepository, AffiliateMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = mapper.toEntity(affiliate);
        AffiliateEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Affiliate> findById(Long id) {
        Optional<AffiliateEntity> entityOpt = jpaRepository.findById(id);
        return entityOpt.map(mapper::toDomain);
    }

    @Override
    public Optional<Affiliate> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Iterable<Affiliate> findAllActive() {
        return jpaRepository.findByStatus("ACTIVE")
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByDocument(String document) {
        if (document == null) return false;
        return jpaRepository.existsByDocument(document);
    }

    @Override
    public Optional<Affiliate> findByDocument(String document) {
        if (document == null) return Optional.empty();
        return jpaRepository.findByDocument(document).map(mapper::toDomain);
    }
}
