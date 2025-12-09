package com.coopcredit.credit_application_service.domain.exception;

/**
 * Exception thrown when an Affiliate is not found
 */
public class AffiliateNotFoundException extends DomainException {

    public AffiliateNotFoundException(Long affiliateId) {
        super("El afiliado con ID " + affiliateId + " no fue encontrado",
              "AFFILIATE_NOT_FOUND");
    }

    public AffiliateNotFoundException(String message) {
        super(message, "AFFILIATE_NOT_FOUND");
    }
}
