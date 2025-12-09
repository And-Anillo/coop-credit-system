-- Flyway Migration: Create Credit Application Tables
-- Version: V2
-- Description: Create credit_applications table with foreign key to affiliates

CREATE TABLE credit_applications (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL REFERENCES affiliates(id),
    amount NUMERIC(15, 2) NOT NULL,
    term INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    submission_date DATE NOT NULL,
    risk_score INTEGER,
    risk_level VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_amount_positive CHECK (amount > 0),
    CONSTRAINT check_term_positive CHECK (term > 0),
    CONSTRAINT check_valid_status CHECK (status IN ('PENDIENTE', 'APROBADO', 'RECHAZADO'))
);

-- Create index on affiliate_id for fast lookups
CREATE INDEX idx_credit_applications_affiliate_id ON credit_applications(affiliate_id);

-- Create index on status for filtering
CREATE INDEX idx_credit_applications_status ON credit_applications(status);
