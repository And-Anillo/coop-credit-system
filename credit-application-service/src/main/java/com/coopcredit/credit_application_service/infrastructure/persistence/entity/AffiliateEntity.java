package com.coopcredit.credit_application_service.infrastructure.persistence.entity;

import jakarta.persistence.*;
import com.coopcredit.credit_application_service.domain.entity.Affiliate;
import com.coopcredit.credit_application_service.infrastructure.persistence.converter.AffiliateStatusConverter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA Entity: AffiliateEntity
 * Maps the domain Affiliate to the database row in the `affiliates` table.
 */
@Entity
@Table(name = "affiliates")
public class AffiliateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "document", nullable = true, unique = true)
    private String document;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "status", nullable = false)
    @Convert(converter = AffiliateStatusConverter.class)
    private Affiliate.AffiliateStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Affiliate.AffiliateStatus getStatus() {
        return status;
    }

    public void setStatus(Affiliate.AffiliateStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
