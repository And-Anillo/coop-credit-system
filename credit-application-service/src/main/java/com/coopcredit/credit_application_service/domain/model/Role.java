package com.coopcredit.credit_application_service.domain.model;

/**
 * Enum: Role
 * Defines the available roles for users in the system
 */
public enum Role {
    ROLE_AFILIADO("Afiliado"),
    ROLE_ANALISTA("Analista"),
    ROLE_ADMIN("Administrador");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
