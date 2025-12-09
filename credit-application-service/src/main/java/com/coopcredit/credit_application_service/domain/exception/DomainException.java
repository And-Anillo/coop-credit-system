package com.coopcredit.credit_application_service.domain.exception;

/**
 * Base domain exception for all domain-specific errors
 */
public class DomainException extends RuntimeException {

    private final String code;

    public DomainException(String message, String code) {
        super(message);
        this.code = code;
    }

    public DomainException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
