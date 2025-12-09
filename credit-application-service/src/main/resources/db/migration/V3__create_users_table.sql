-- V3__create_users_table.sql
-- Flyway migration: Create users table for Spring Security with JWT

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create index on username for fast lookups in authentication
CREATE INDEX idx_users_username ON users(username);

-- Add constraints
ALTER TABLE users ADD CONSTRAINT ck_role_valid CHECK (role IN ('ROLE_AFILIADO', 'ROLE_ANALISTA', 'ROLE_ADMIN'));
