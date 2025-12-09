package com.coopcredit.credit_application_service.domain.exception;

/**
 * Exception thrown when a credit application violates domain rules
 */
public class InvalidApplicationException extends DomainException {

    public InvalidApplicationException(String message) {
        super(message, "INVALID_APPLICATION");
    }

    public InvalidApplicationException(String message, Throwable cause) {
        super(message, "INVALID_APPLICATION", cause);
    }
}
