package com.coopcredit.credit_application_service.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Entity: Affiliate
 * Pure Java entity representing a member of the credit cooperative.
 */
public class Affiliate {

    private Long id;
    private final String document;
    private String name;
    private BigDecimal salary;
    private LocalDate registrationDate;
    private AffiliateStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Affiliate(String document) {
        if (document == null || document.isBlank()) {
            throw new IllegalArgumentException("Document cannot be null or blank");
        }
        this.document = document;
    }

    /**
     * Factory method to create a new Affiliate with basic validation
     */
    public static Affiliate create(String name, BigDecimal salary, LocalDate registrationDate, String document) {
        Affiliate affiliate = new Affiliate(document);
        affiliate.setName(name);
        affiliate.setSalary(salary);
        affiliate.setRegistrationDate(registrationDate);
        affiliate.status = AffiliateStatus.ACTIVE;
        affiliate.createdAt = LocalDateTime.now();
        affiliate.updatedAt = LocalDateTime.now();
        return affiliate;
    }

    /**
     * Reconstructs an Affiliate with all properties (used by adapters)
     */
    public static Affiliate reconstruct(Long id, String name, BigDecimal salary,
                                        LocalDate registrationDate, AffiliateStatus status,
                                        LocalDateTime createdAt, LocalDateTime updatedAt, String document) {
        Affiliate affiliate = new Affiliate(document);
        affiliate.id = id;
        affiliate.name = name;
        affiliate.salary = salary;
        affiliate.registrationDate = registrationDate;
        affiliate.status = status;
        affiliate.createdAt = createdAt;
        affiliate.updatedAt = updatedAt;
        return affiliate;
    }

    public void deactivate() {
        if (this.status == AffiliateStatus.INACTIVE) {
            throw new IllegalStateException("Affiliate is already inactive");
        }
        this.status = AffiliateStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactivate() {
        if (this.status == AffiliateStatus.ACTIVE) {
            throw new IllegalStateException("Affiliate is already active");
        }
        this.status = AffiliateStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSalary(BigDecimal newSalary) {
        if (newSalary == null || newSalary.signum() <= 0) {
            throw new IllegalArgumentException("Salary must be greater than zero");
        }
        this.salary = newSalary;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEligibleForCredit() {
        return this.status == AffiliateStatus.ACTIVE &&
               this.salary != null &&
               this.salary.signum() > 0;
    }

    // Getters
    public Long getId() { return id; }
    public String getDocument() { return document; }
    public String getName() { return name; }
    public BigDecimal getSalary() { return salary; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public AffiliateStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters used by factories/adapters
    protected void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Affiliate name cannot be blank");
        }
        this.name = name;
    }

    protected void setSalary(BigDecimal salary) {
        if (salary == null || salary.signum() <= 0) {
            throw new IllegalArgumentException("Salary must be greater than zero");
        }
        this.salary = salary;
    }

    protected void setRegistrationDate(LocalDate registrationDate) {
        if (registrationDate == null) {
            throw new IllegalArgumentException("Registration date cannot be null");
        }
        if (registrationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Registration date cannot be in the future");
        }
        this.registrationDate = registrationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Affiliate affiliate = (Affiliate) o;
        return Objects.equals(id, affiliate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Affiliate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", status=" + status +
                ", registrationDate=" + registrationDate +
                '}';
    }

    public enum AffiliateStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUSPENDED("Suspended");

        private final String label;

        AffiliateStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
