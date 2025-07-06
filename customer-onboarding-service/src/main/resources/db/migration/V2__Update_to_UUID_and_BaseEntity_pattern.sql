-- Customer Onboarding Service Database Schema Migration V2
-- Updates schema to use UUID primary keys and BaseEntity pattern
-- Adds CustomerProfile table that extends shared User entity

-- Enable UUID extension for PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Drop existing tables (since we're moving to UUID pattern)
DROP TABLE IF EXISTS customer_verification_documents CASCADE;
DROP TABLE IF EXISTS customer_application_status_history CASCADE;
DROP TABLE IF EXISTS customer_onboarding_applications CASCADE;
DROP SEQUENCE IF EXISTS customer_onboarding_ref_seq;

-- Create customer onboarding applications table with UUID and BaseEntity pattern
CREATE TABLE customer_onboarding_applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Customer onboarding specific fields
    application_reference_id VARCHAR(50) UNIQUE NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth VARCHAR(10),
    national_id VARCHAR(50),
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    application_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
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
    rejection_reason VARCHAR(500)
);

-- Create customer application status history table with UUID and BaseEntity pattern
CREATE TABLE customer_application_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Status history specific fields
    application_id UUID NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    change_reason VARCHAR(500),
    notes VARCHAR(1000),
    changed_by VARCHAR(100),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (application_id) REFERENCES customer_onboarding_applications(id) ON DELETE CASCADE
);

-- Create customer verification documents table with UUID and BaseEntity pattern
CREATE TABLE customer_verification_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Document specific fields
    application_id UUID NOT NULL,
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
    uploaded_by VARCHAR(100),
    
    FOREIGN KEY (application_id) REFERENCES customer_onboarding_applications(id) ON DELETE CASCADE
);

-- Create customer profiles table that extends shared User entity
CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Foreign key to shared User entity (assumes users table exists in shared schema)
    user_id UUID NOT NULL UNIQUE,
    
    -- Foreign key to onboarding application (optional, for linking)
    onboarding_application_id UUID UNIQUE,
    
    -- Customer-specific profile fields
    customer_reference_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Preferred delivery address
    preferred_delivery_address_line1 VARCHAR(200),
    preferred_delivery_address_line2 VARCHAR(200),
    preferred_delivery_city VARCHAR(100),
    preferred_delivery_state_province VARCHAR(100),
    preferred_delivery_postal_code VARCHAR(20),
    preferred_delivery_country VARCHAR(100),
    
    -- Billing address
    billing_address_line1 VARCHAR(200),
    billing_address_line2 VARCHAR(200),
    billing_city VARCHAR(100),
    billing_state_province VARCHAR(100),
    billing_postal_code VARCHAR(20),
    billing_country VARCHAR(100),
    
    -- Customer segmentation and preferences
    customer_segment VARCHAR(50), -- INDIVIDUAL, SMALL_BUSINESS, ENTERPRISE
    preferred_communication_method VARCHAR(20), -- EMAIL, SMS, PHONE, APP
    marketing_consent BOOLEAN DEFAULT FALSE,
    sms_notifications_enabled BOOLEAN DEFAULT TRUE,
    email_notifications_enabled BOOLEAN DEFAULT TRUE,
    push_notifications_enabled BOOLEAN DEFAULT TRUE,
    delivery_instructions VARCHAR(500),
    
    -- Business integration fields
    billing_customer_id VARCHAR(100),
    kyc_verified BOOLEAN DEFAULT FALSE,
    kyc_verification_date TIMESTAMP,
    customer_tier VARCHAR(20) DEFAULT 'BRONZE', -- BRONZE, SILVER, GOLD, PLATINUM
    account_activated BOOLEAN DEFAULT FALSE,
    account_activation_date TIMESTAMP,
    
    -- Customer analytics fields
    last_order_date TIMESTAMP,
    total_orders_count INTEGER DEFAULT 0,
    total_amount_spent DECIMAL(12,2) DEFAULT 0.00,
    credit_limit DECIMAL(12,2),
    payment_terms_days INTEGER DEFAULT 30,
    profile_notes VARCHAR(1000),
    
    FOREIGN KEY (onboarding_application_id) REFERENCES customer_onboarding_applications(id) ON DELETE SET NULL
);

-- Create indexes for optimal performance
CREATE INDEX idx_application_reference_id ON customer_onboarding_applications(application_reference_id);
CREATE INDEX idx_customer_email ON customer_onboarding_applications(customer_email);
CREATE INDEX idx_application_status ON customer_onboarding_applications(application_status);
CREATE INDEX idx_auth_service_user_id ON customer_onboarding_applications(auth_service_user_id);
CREATE INDEX idx_kyc_verification_id ON customer_onboarding_applications(kyc_verification_id);
CREATE INDEX idx_created_at ON customer_onboarding_applications(created_at);

CREATE INDEX idx_application_id ON customer_application_status_history(application_id);
CREATE INDEX idx_changed_at ON customer_application_status_history(changed_at);
CREATE INDEX idx_to_status ON customer_application_status_history(to_status);

CREATE INDEX idx_user_id ON customer_profiles(user_id);
CREATE INDEX idx_customer_reference_id ON customer_profiles(customer_reference_id);
CREATE INDEX idx_onboarding_application_id ON customer_profiles(onboarding_application_id);
CREATE INDEX idx_customer_segment ON customer_profiles(customer_segment);
CREATE INDEX idx_kyc_verified ON customer_profiles(kyc_verified);
CREATE INDEX idx_account_activated ON customer_profiles(account_activated);

-- Create sequence for customer reference ID generation
CREATE SEQUENCE customer_ref_seq START 100000 INCREMENT 1;

-- Add table and column comments for documentation
COMMENT ON TABLE customer_onboarding_applications IS 'Customer onboarding applications using UUID and BaseEntity pattern';
COMMENT ON TABLE customer_application_status_history IS 'Status change history for customer applications';
COMMENT ON TABLE customer_verification_documents IS 'Customer verification documents with UUID pattern';
COMMENT ON TABLE customer_profiles IS 'Customer profiles extending shared User entity';

COMMENT ON COLUMN customer_onboarding_applications.application_reference_id IS 'Unique reference ID (e.g., CUST-ONB-2025-100001)';
COMMENT ON COLUMN customer_onboarding_applications.application_status IS 'Application status: DRAFT, SUBMITTED, KYC_IN_PROGRESS, APPROVED, REJECTED';
COMMENT ON COLUMN customer_profiles.customer_reference_id IS 'Unique customer reference ID (e.g., CUST-2025-100001)';
COMMENT ON COLUMN customer_profiles.user_id IS 'Foreign key to shared users table';
COMMENT ON COLUMN customer_profiles.customer_tier IS 'Customer tier for pricing and service levels';

-- Update trigger for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_customer_onboarding_applications_updated_at 
    BEFORE UPDATE ON customer_onboarding_applications 
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_customer_application_status_history_updated_at 
    BEFORE UPDATE ON customer_application_status_history 
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_customer_verification_documents_updated_at 
    BEFORE UPDATE ON customer_verification_documents 
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();

CREATE TRIGGER update_customer_profiles_updated_at 
    BEFORE UPDATE ON customer_profiles 
    FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();