-- Corporate Customer Onboarding Service Database Schema
-- Version: 1.0.0
-- Description: Creates tables for corporate customer onboarding with UUID keys and comprehensive business logic

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Corporate Onboarding Applications table
CREATE TABLE corporate_onboarding_applications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Application identification
    application_reference_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Company information
    company_name VARCHAR(200) NOT NULL,
    company_registration_number VARCHAR(50) NOT NULL,
    tax_identification_number VARCHAR(50),
    business_license_number VARCHAR(50),
    company_email VARCHAR(100) NOT NULL,
    company_phone VARCHAR(20) NOT NULL,
    company_website VARCHAR(200),
    
    -- Business classification
    business_type VARCHAR(50),
    industry_sector VARCHAR(50),
    company_size VARCHAR(20),
    annual_shipping_volume VARCHAR(20),
    
    -- Business address
    business_address_line1 VARCHAR(200) NOT NULL,
    business_address_line2 VARCHAR(200),
    business_city VARCHAR(100) NOT NULL,
    business_state_province VARCHAR(100),
    business_postal_code VARCHAR(20) NOT NULL,
    business_country VARCHAR(100) NOT NULL,
    
    -- Primary contact
    primary_contact_first_name VARCHAR(100) NOT NULL,
    primary_contact_last_name VARCHAR(100) NOT NULL,
    primary_contact_email VARCHAR(100) NOT NULL,
    primary_contact_phone VARCHAR(20) NOT NULL,
    primary_contact_position VARCHAR(100),
    
    -- Billing contact (optional)
    billing_contact_first_name VARCHAR(100),
    billing_contact_last_name VARCHAR(100),
    billing_contact_email VARCHAR(100),
    billing_contact_phone VARCHAR(20),
    
    -- Application status and processing
    application_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    kyb_verification_id VARCHAR(50),
    auth_service_user_id VARCHAR(50),
    billing_customer_id VARCHAR(50),
    
    -- Financial information
    requested_credit_limit DECIMAL(19,2),
    approved_credit_limit DECIMAL(19,2),
    payment_terms VARCHAR(30),
    volume_discount_tier VARCHAR(20),
    
    -- Service requirements
    sla_requirements TEXT,
    preferred_communication_method VARCHAR(20),
    
    -- Consent and agreements
    marketing_consent BOOLEAN NOT NULL DEFAULT FALSE,
    terms_accepted BOOLEAN NOT NULL,
    privacy_policy_accepted BOOLEAN NOT NULL,
    data_processing_agreement_accepted BOOLEAN NOT NULL,
    
    -- Status timestamps
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    rejected_at TIMESTAMP,
    rejected_by VARCHAR(100),
    rejection_reason VARCHAR(1000),
    
    -- Audit fields (from BaseEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Corporate Application Status History table
CREATE TABLE corporate_application_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    application_id UUID NOT NULL REFERENCES corporate_onboarding_applications(id) ON DELETE CASCADE,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    status_change_reason VARCHAR(500),
    changed_by VARCHAR(100),
    additional_notes TEXT,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Corporate Verification Documents table
CREATE TABLE corporate_verification_documents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    application_id UUID NOT NULL REFERENCES corporate_onboarding_applications(id) ON DELETE CASCADE,
    document_reference_id VARCHAR(50) UNIQUE NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_category VARCHAR(30) NOT NULL DEFAULT 'BUSINESS',
    
    -- File information
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    stored_file_path VARCHAR(500) NOT NULL,
    document_hash VARCHAR(128),
    
    -- Verification information
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    verified_at TIMESTAMP,
    verified_by VARCHAR(100),
    verification_notes TEXT,
    confidence_score DECIMAL(3,2),
    
    -- Document metadata
    document_expiry_date TIMESTAMP,
    document_issuer VARCHAR(200),
    document_number VARCHAR(100),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Review information
    review_required BOOLEAN NOT NULL DEFAULT TRUE,
    reviewed_at TIMESTAMP,
    reviewed_by VARCHAR(100),
    review_notes TEXT,
    rejection_reason VARCHAR(500),
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Corporate Customer Profiles table (after approval)
CREATE TABLE corporate_customer_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    application_id UUID NOT NULL REFERENCES corporate_onboarding_applications(id),
    customer_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Account information
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    account_manager_id VARCHAR(50),
    sales_representative_id VARCHAR(50),
    
    -- Billing and credit
    credit_limit DECIMAL(19,2),
    available_credit DECIMAL(19,2),
    payment_terms VARCHAR(30),
    billing_cycle VARCHAR(20) DEFAULT 'MONTHLY',
    
    -- Service configuration
    volume_discount_tier VARCHAR(20),
    priority_handling BOOLEAN NOT NULL DEFAULT FALSE,
    dedicated_support BOOLEAN NOT NULL DEFAULT FALSE,
    api_access_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    webhook_url VARCHAR(500),
    
    -- Metrics and analytics
    total_shipments_count BIGINT DEFAULT 0,
    total_revenue DECIMAL(19,2) DEFAULT 0.00,
    average_shipment_value DECIMAL(19,2) DEFAULT 0.00,
    last_shipment_date TIMESTAMP,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Business Credit Assessments table
CREATE TABLE business_credit_assessments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    application_id UUID NOT NULL REFERENCES corporate_onboarding_applications(id),
    assessment_reference_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Credit assessment details
    credit_score INTEGER,
    credit_rating VARCHAR(10),
    assessment_provider VARCHAR(100),
    assessment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Financial information
    annual_revenue DECIMAL(19,2),
    years_in_business INTEGER,
    employee_count INTEGER,
    
    -- Risk assessment
    risk_category VARCHAR(20),
    recommended_credit_limit DECIMAL(19,2),
    recommended_payment_terms VARCHAR(30),
    
    -- Assessment results
    assessment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assessment_notes TEXT,
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Corporate Service Agreements table
CREATE TABLE corporate_service_agreements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    application_id UUID NOT NULL REFERENCES corporate_onboarding_applications(id),
    agreement_reference_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Agreement details
    agreement_type VARCHAR(30) NOT NULL DEFAULT 'STANDARD',
    agreement_template_version VARCHAR(10),
    custom_terms TEXT,
    
    -- Service level agreements
    guaranteed_delivery_time INTEGER, -- in hours
    guaranteed_uptime_percentage DECIMAL(5,2),
    priority_support_included BOOLEAN NOT NULL DEFAULT FALSE,
    dedicated_account_manager BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Pricing and discounts
    base_shipping_rate DECIMAL(10,4),
    volume_discount_percentage DECIMAL(5,2),
    fuel_surcharge_waived BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Agreement lifecycle
    agreement_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    generated_at TIMESTAMP,
    sent_at TIMESTAMP,
    viewed_at TIMESTAMP,
    signed_at TIMESTAMP,
    signed_by_customer VARCHAR(200),
    signed_by_company VARCHAR(200),
    effective_date DATE,
    expiry_date DATE,
    
    -- Document storage
    agreement_document_path VARCHAR(500),
    signed_document_path VARCHAR(500),
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Create indexes for optimal performance
CREATE INDEX idx_corporate_application_reference_id ON corporate_onboarding_applications(application_reference_id);
CREATE INDEX idx_corporate_company_email ON corporate_onboarding_applications(company_email);
CREATE INDEX idx_corporate_company_registration ON corporate_onboarding_applications(company_registration_number);
CREATE INDEX idx_corporate_application_status ON corporate_onboarding_applications(application_status);
CREATE INDEX idx_corporate_auth_service_user_id ON corporate_onboarding_applications(auth_service_user_id);
CREATE INDEX idx_corporate_kyb_verification_id ON corporate_onboarding_applications(kyb_verification_id);
CREATE INDEX idx_corporate_industry_sector ON corporate_onboarding_applications(industry_sector);
CREATE INDEX idx_corporate_business_type ON corporate_onboarding_applications(business_type);
CREATE INDEX idx_corporate_company_size ON corporate_onboarding_applications(company_size);
CREATE INDEX idx_corporate_created_at ON corporate_onboarding_applications(created_at);

CREATE INDEX idx_corporate_status_history_application_id ON corporate_application_status_history(application_id);
CREATE INDEX idx_corporate_status_history_new_status ON corporate_application_status_history(new_status);
CREATE INDEX idx_corporate_status_history_created_at ON corporate_application_status_history(created_at);

CREATE INDEX idx_corporate_documents_application_id ON corporate_verification_documents(application_id);
CREATE INDEX idx_corporate_documents_reference_id ON corporate_verification_documents(document_reference_id);
CREATE INDEX idx_corporate_documents_type ON corporate_verification_documents(document_type);
CREATE INDEX idx_corporate_documents_status ON corporate_verification_documents(verification_status);
CREATE INDEX idx_corporate_documents_primary ON corporate_verification_documents(is_primary);

CREATE INDEX idx_corporate_profiles_customer_id ON corporate_customer_profiles(customer_id);
CREATE INDEX idx_corporate_profiles_account_status ON corporate_customer_profiles(account_status);
CREATE INDEX idx_corporate_profiles_account_manager ON corporate_customer_profiles(account_manager_id);

CREATE INDEX idx_credit_assessments_application_id ON business_credit_assessments(application_id);
CREATE INDEX idx_credit_assessments_reference_id ON business_credit_assessments(assessment_reference_id);
CREATE INDEX idx_credit_assessments_status ON business_credit_assessments(assessment_status);

CREATE INDEX idx_service_agreements_application_id ON corporate_service_agreements(application_id);
CREATE INDEX idx_service_agreements_reference_id ON corporate_service_agreements(agreement_reference_id);
CREATE INDEX idx_service_agreements_status ON corporate_service_agreements(agreement_status);

-- Create triggers for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_corporate_applications_updated_at 
    BEFORE UPDATE ON corporate_onboarding_applications 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_corporate_status_history_updated_at 
    BEFORE UPDATE ON corporate_application_status_history 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_corporate_documents_updated_at 
    BEFORE UPDATE ON corporate_verification_documents 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_corporate_profiles_updated_at 
    BEFORE UPDATE ON corporate_customer_profiles 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_credit_assessments_updated_at 
    BEFORE UPDATE ON business_credit_assessments 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_service_agreements_updated_at 
    BEFORE UPDATE ON corporate_service_agreements 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default data for testing and development
INSERT INTO corporate_onboarding_applications (
    application_reference_id, company_name, company_registration_number, company_email, company_phone,
    business_address_line1, business_city, business_postal_code, business_country,
    primary_contact_first_name, primary_contact_last_name, primary_contact_email, primary_contact_phone,
    application_status, business_type, industry_sector, company_size, annual_shipping_volume,
    terms_accepted, privacy_policy_accepted, data_processing_agreement_accepted,
    created_by, updated_by
) VALUES (
    'CORP-DEMO-001',
    'Demo Corporation Ltd',
    'REG123456789',
    'contact@democorp.com',
    '+1-555-0123',
    '123 Business Street',
    'Business City',
    '12345',
    'United States',
    'John',
    'Manager',
    'john.manager@democorp.com',
    '+1-555-0124',
    'DRAFT',
    'CORPORATION',
    'TECHNOLOGY',
    'MEDIUM',
    'HIGH',
    true,
    true,
    true,
    'system',
    'system'
);

-- Insert sample status history
INSERT INTO corporate_application_status_history (
    application_id, new_status, status_change_reason, changed_by, created_by, updated_by
) SELECT 
    id, 'DRAFT', 'Initial application created', 'system', 'system', 'system'
FROM corporate_onboarding_applications 
WHERE application_reference_id = 'CORP-DEMO-001';

COMMIT;