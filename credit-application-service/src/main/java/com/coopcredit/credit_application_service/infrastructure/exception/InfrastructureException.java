package com.coopcredit.credit_application_service.infrastructure.exception;

/**
 * Runtime exception for infrastructure layer failures
 */
public class InfrastructureException extends RuntimeException {

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
