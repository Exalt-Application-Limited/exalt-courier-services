-- Customer Onboarding Service Database Schema
-- Version 1.0.0

-- Create customer onboarding applications table
CREATE TABLE customer_onboarding_applications (
    id BIGSERIAL PRIMARY KEY,
    application_reference_id VARCHAR(50) UNIQUE NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth VARCHAR(10),
    national_id VARCHAR(50),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    application_status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    kyc_verification_id VARCHAR(100),
    auth_service_user_id VARCHAR(100),
    billing_customer_id VARCHAR(100),
    preferred_communication_method VARCHAR(20) DEFAULT 'EMAIL',
    marketing_consent BOOLEAN DEFAULT FALSE,
    terms_accepted BOOLEAN DEFAULT FALSE,
    privacy_policy_accepted BOOLEAN DEFAULT FALSE,
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Create customer application status history table
CREATE TABLE customer_application_status_history (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    change_reason VARCHAR(255),
    notes TEXT,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES customer_onboarding_applications(id) ON DELETE CASCADE
);

-- Create customer verification documents table
CREATE TABLE customer_verification_documents (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_reference_id VARCHAR(100),
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),
    verification_status VARCHAR(50) DEFAULT 'PENDING',
    verification_notes TEXT,
    verified_by VARCHAR(100),
    verified_at TIMESTAMP,
    document_verification_service_id VARCHAR(100),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    uploaded_by VARCHAR(100),
    FOREIGN KEY (application_id) REFERENCES customer_onboarding_applications(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_customer_onboarding_email ON customer_onboarding_applications(customer_email);
CREATE INDEX idx_customer_onboarding_phone ON customer_onboarding_applications(customer_phone);
CREATE INDEX idx_customer_onboarding_status ON customer_onboarding_applications(application_status);
CREATE INDEX idx_customer_onboarding_reference ON customer_onboarding_applications(application_reference_id);
CREATE INDEX idx_customer_onboarding_kyc ON customer_onboarding_applications(kyc_verification_id);
CREATE INDEX idx_customer_onboarding_auth_user ON customer_onboarding_applications(auth_service_user_id);
CREATE INDEX idx_customer_onboarding_created_at ON customer_onboarding_applications(created_at);

CREATE INDEX idx_status_history_application ON customer_application_status_history(application_id);
CREATE INDEX idx_status_history_status ON customer_application_status_history(to_status);
CREATE INDEX idx_status_history_changed_at ON customer_application_status_history(changed_at);

CREATE INDEX idx_verification_docs_application ON customer_verification_documents(application_id);
CREATE INDEX idx_verification_docs_type ON customer_verification_documents(document_type);
CREATE INDEX idx_verification_docs_status ON customer_verification_documents(verification_status);
CREATE INDEX idx_verification_docs_uploaded_at ON customer_verification_documents(uploaded_at);

-- Create sequences for reference ID generation
CREATE SEQUENCE customer_onboarding_ref_seq START 100000 INCREMENT 1;

-- Add comments for documentation
COMMENT ON TABLE customer_onboarding_applications IS 'Main table for customer onboarding applications';
COMMENT ON TABLE customer_application_status_history IS 'Tracks status changes for customer applications';
COMMENT ON TABLE customer_verification_documents IS 'Stores information about customer verification documents';

COMMENT ON COLUMN customer_onboarding_applications.application_reference_id IS 'Unique reference ID for customer application (e.g., CUST-ONB-2025-100001)';
COMMENT ON COLUMN customer_onboarding_applications.application_status IS 'Current status of the application (DRAFT, SUBMITTED, KYC_IN_PROGRESS, etc.)';
COMMENT ON COLUMN customer_onboarding_applications.kyc_verification_id IS 'Reference ID from KYC service';
COMMENT ON COLUMN customer_onboarding_applications.auth_service_user_id IS 'User ID from auth service';
COMMENT ON COLUMN customer_onboarding_applications.billing_customer_id IS 'Customer ID from billing service';

-- Insert initial data (if needed)
-- This can be used for testing or default configurations

-- Set table ownership and permissions (adjust as needed for your environment)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON customer_onboarding_applications TO customer_onboarding_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON customer_application_status_history TO customer_onboarding_user;
-- GRANT SELECT, INSERT, UPDATE, DELETE ON customer_verification_documents TO customer_onboarding_user;
-- GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO customer_onboarding_user;