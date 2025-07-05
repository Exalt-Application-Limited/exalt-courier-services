-- Customer Support Communication Service Database Schema
-- Version: 1.0.0
-- Description: Creates tables for customer support tickets, messages, and communication workflows

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Support Tickets table
CREATE TABLE support_tickets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Ticket identification
    ticket_reference_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Customer information
    customer_id VARCHAR(50) NOT NULL,
    customer_email VARCHAR(100),
    customer_name VARCHAR(100),
    customer_phone VARCHAR(20),
    
    -- Ticket details
    subject VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(30) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    
    -- Assignment
    assigned_agent_id VARCHAR(50),
    assigned_agent_name VARCHAR(100),
    assigned_team VARCHAR(30),
    
    -- Related references
    shipment_reference_id VARCHAR(50),
    order_reference_id VARCHAR(100),
    
    -- Timestamps
    due_date TIMESTAMP,
    resolved_at TIMESTAMP,
    closed_at TIMESTAMP,
    first_response_at TIMESTAMP,
    escalated_at TIMESTAMP,
    escalated_to VARCHAR(50),
    
    -- Resolution
    resolution_notes VARCHAR(1000),
    internal_notes VARCHAR(1000),
    
    -- Customer feedback
    customer_satisfaction_rating INTEGER CHECK (customer_satisfaction_rating >= 1 AND customer_satisfaction_rating <= 5),
    customer_feedback VARCHAR(1000),
    
    -- Flags
    is_urgent BOOLEAN NOT NULL DEFAULT FALSE,
    requires_callback BOOLEAN NOT NULL DEFAULT FALSE,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    auto_close_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Communication tracking
    last_customer_response_at TIMESTAMP,
    last_agent_response_at TIMESTAMP,
    response_count INTEGER NOT NULL DEFAULT 0,
    
    -- Metadata
    tags VARCHAR(500),
    source VARCHAR(200),
    
    -- Audit fields (from BaseEntity)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Ticket Messages table
CREATE TABLE ticket_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    
    -- Sender information
    sender_id VARCHAR(50) NOT NULL,
    sender_name VARCHAR(100),
    sender_email VARCHAR(100),
    sender_type VARCHAR(20) NOT NULL,
    
    -- Message content
    content TEXT NOT NULL,
    message_type VARCHAR(20),
    
    -- Message properties
    is_internal BOOLEAN NOT NULL DEFAULT FALSE,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP,
    read_by VARCHAR(50),
    
    -- Auto-generation
    is_auto_generated BOOLEAN NOT NULL DEFAULT FALSE,
    auto_generation_source VARCHAR(100),
    
    -- Threading
    reply_to_message_id UUID,
    
    -- Channel and editing
    channel VARCHAR(200),
    edited_at TIMESTAMP,
    edited_by VARCHAR(50),
    edit_reason VARCHAR(500),
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Ticket Attachments table
CREATE TABLE ticket_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    
    -- File information
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_hash VARCHAR(128),
    
    -- Upload details
    uploaded_by VARCHAR(50) NOT NULL,
    uploaded_by_type VARCHAR(20) NOT NULL, -- CUSTOMER, AGENT, SYSTEM
    
    -- File properties
    is_image BOOLEAN NOT NULL DEFAULT FALSE,
    is_document BOOLEAN NOT NULL DEFAULT FALSE,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Virus scanning
    virus_scan_status VARCHAR(20) DEFAULT 'PENDING',
    virus_scan_result VARCHAR(100),
    scanned_at TIMESTAMP,
    
    -- Access tracking
    download_count INTEGER NOT NULL DEFAULT 0,
    last_downloaded_at TIMESTAMP,
    last_downloaded_by VARCHAR(50),
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Message Attachments table (for attachments on specific messages)
CREATE TABLE message_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    message_id UUID NOT NULL REFERENCES ticket_messages(id) ON DELETE CASCADE,
    
    -- File information
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_hash VARCHAR(128),
    
    -- Upload details
    uploaded_by VARCHAR(50) NOT NULL,
    
    -- File properties
    is_image BOOLEAN NOT NULL DEFAULT FALSE,
    is_inline BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Ticket Status History table
CREATE TABLE ticket_status_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    
    -- Status change details
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    change_reason VARCHAR(500),
    changed_by VARCHAR(50) NOT NULL,
    changed_by_type VARCHAR(20) NOT NULL, -- CUSTOMER, AGENT, SYSTEM
    
    -- Additional context
    notes TEXT,
    auto_generated BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Agent Assignments table
CREATE TABLE agent_assignments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    ticket_id UUID NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
    
    -- Agent information
    agent_id VARCHAR(50) NOT NULL,
    agent_name VARCHAR(100),
    agent_email VARCHAR(100),
    team VARCHAR(30),
    
    -- Assignment details
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(50),
    assignment_reason VARCHAR(500),
    
    -- Assignment status
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    unassigned_at TIMESTAMP,
    unassigned_by VARCHAR(50),
    unassignment_reason VARCHAR(500),
    
    -- Performance metrics
    first_response_time_hours INTEGER,
    resolution_time_hours INTEGER,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Support Agent Profiles table
CREATE TABLE support_agent_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Agent identification
    agent_id VARCHAR(50) UNIQUE NOT NULL,
    auth_service_user_id VARCHAR(50),
    
    -- Agent information
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    
    -- Assignment details
    team VARCHAR(30),
    role VARCHAR(50),
    skill_level VARCHAR(20), -- JUNIOR, SENIOR, SPECIALIST, SUPERVISOR
    
    -- Availability
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    max_concurrent_tickets INTEGER DEFAULT 10,
    current_ticket_count INTEGER NOT NULL DEFAULT 0,
    
    -- Specializations
    categories TEXT, -- JSON array of categories agent can handle
    languages VARCHAR(500), -- Comma-separated language codes
    
    -- Performance metrics
    average_response_time_hours DECIMAL(5,2),
    average_resolution_time_hours DECIMAL(5,2),
    customer_satisfaction_avg DECIMAL(3,2),
    tickets_resolved_total INTEGER NOT NULL DEFAULT 0,
    
    -- Work schedule
    timezone VARCHAR(50),
    work_hours_start TIME,
    work_hours_end TIME,
    work_days VARCHAR(20), -- MON,TUE,WED,THU,FRI
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Knowledge Base Articles table
CREATE TABLE knowledge_base_articles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    version BIGINT NOT NULL DEFAULT 0,
    
    -- Article identification
    article_reference_id VARCHAR(50) UNIQUE NOT NULL,
    
    -- Content
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    summary VARCHAR(500),
    
    -- Classification
    category VARCHAR(30),
    subcategory VARCHAR(50),
    tags VARCHAR(500),
    
    -- Publication
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    published_at TIMESTAMP,
    published_by VARCHAR(50),
    
    -- Usage tracking
    view_count INTEGER NOT NULL DEFAULT 0,
    helpful_votes INTEGER NOT NULL DEFAULT 0,
    unhelpful_votes INTEGER NOT NULL DEFAULT 0,
    last_viewed_at TIMESTAMP,
    
    -- Maintenance
    reviewed_at TIMESTAMP,
    reviewed_by VARCHAR(50),
    next_review_date DATE,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Create indexes for optimal performance
CREATE INDEX idx_ticket_reference_id ON support_tickets(ticket_reference_id);
CREATE INDEX idx_customer_id ON support_tickets(customer_id);
CREATE INDEX idx_ticket_status ON support_tickets(status);
CREATE INDEX idx_ticket_priority ON support_tickets(priority);
CREATE INDEX idx_ticket_category ON support_tickets(category);
CREATE INDEX idx_assigned_agent_id ON support_tickets(assigned_agent_id);
CREATE INDEX idx_shipment_reference ON support_tickets(shipment_reference_id);
CREATE INDEX idx_ticket_created_at ON support_tickets(created_at);
CREATE INDEX idx_ticket_due_date ON support_tickets(due_date);

CREATE INDEX idx_message_ticket_id ON ticket_messages(ticket_id);
CREATE INDEX idx_message_sender_id ON ticket_messages(sender_id);
CREATE INDEX idx_message_created_at ON ticket_messages(created_at);
CREATE INDEX idx_message_is_internal ON ticket_messages(is_internal);
CREATE INDEX idx_message_is_read ON ticket_messages(is_read);

CREATE INDEX idx_attachment_ticket_id ON ticket_attachments(ticket_id);
CREATE INDEX idx_attachment_uploaded_by ON ticket_attachments(uploaded_by);
CREATE INDEX idx_attachment_file_hash ON ticket_attachments(file_hash);

CREATE INDEX idx_msg_attachment_message_id ON message_attachments(message_id);

CREATE INDEX idx_status_history_ticket_id ON ticket_status_history(ticket_id);
CREATE INDEX idx_status_history_created_at ON ticket_status_history(created_at);

CREATE INDEX idx_agent_assignment_ticket_id ON agent_assignments(ticket_id);
CREATE INDEX idx_agent_assignment_agent_id ON agent_assignments(agent_id);
CREATE INDEX idx_agent_assignment_is_active ON agent_assignments(is_active);

CREATE INDEX idx_agent_profile_agent_id ON support_agent_profiles(agent_id);
CREATE INDEX idx_agent_profile_team ON support_agent_profiles(team);
CREATE INDEX idx_agent_profile_is_available ON support_agent_profiles(is_available);

CREATE INDEX idx_kb_article_reference_id ON knowledge_base_articles(article_reference_id);
CREATE INDEX idx_kb_article_category ON knowledge_base_articles(category);
CREATE INDEX idx_kb_article_status ON knowledge_base_articles(status);

-- Create triggers for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_support_tickets_updated_at 
    BEFORE UPDATE ON support_tickets 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ticket_messages_updated_at 
    BEFORE UPDATE ON ticket_messages 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ticket_attachments_updated_at 
    BEFORE UPDATE ON ticket_attachments 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_message_attachments_updated_at 
    BEFORE UPDATE ON message_attachments 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ticket_status_history_updated_at 
    BEFORE UPDATE ON ticket_status_history 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_agent_assignments_updated_at 
    BEFORE UPDATE ON agent_assignments 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_support_agent_profiles_updated_at 
    BEFORE UPDATE ON support_agent_profiles 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_knowledge_base_articles_updated_at 
    BEFORE UPDATE ON knowledge_base_articles 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data for development and testing
INSERT INTO support_agent_profiles (
    agent_id, first_name, last_name, email, team, role, skill_level,
    categories, languages, max_concurrent_tickets,
    created_by, updated_by
) VALUES 
(
    'AGENT-001',
    'Sarah',
    'Johnson',
    'sarah.johnson@exalt.com',
    'general-support',
    'Support Agent',
    'SENIOR',
    '["GENERAL_INQUIRY", "ACCOUNT_MANAGEMENT", "BILLING_INQUIRY"]',
    'en,es',
    15,
    'system',
    'system'
),
(
    'AGENT-002',
    'Mike',
    'Chen',
    'mike.chen@exalt.com',
    'technical-team',
    'Technical Specialist',
    'SPECIALIST',
    '["TECHNICAL_SUPPORT", "SERVICE_DISRUPTION"]',
    'en,zh',
    8,
    'system',
    'system'
);

-- Insert sample knowledge base articles
INSERT INTO knowledge_base_articles (
    article_reference_id, title, content, summary, category, status,
    is_public, published_at, published_by, created_by, updated_by
) VALUES 
(
    'KB-TRACK-001',
    'How to Track Your Shipment',
    'To track your shipment, follow these steps: 1. Visit our tracking page 2. Enter your tracking number 3. View real-time updates...',
    'Step-by-step guide for tracking shipments',
    'SHIPMENT_TRACKING',
    'PUBLISHED',
    true,
    CURRENT_TIMESTAMP,
    'AGENT-001',
    'system',
    'system'
),
(
    'KB-BILLING-001',
    'Understanding Your Invoice',
    'Your invoice contains several sections: 1. Service charges 2. Additional fees 3. Taxes and surcharges...',
    'Explanation of invoice components',
    'BILLING_INQUIRY',
    'PUBLISHED',
    true,
    CURRENT_TIMESTAMP,
    'AGENT-001',
    'system',
    'system'
);

-- Insert sample support ticket for testing
INSERT INTO support_tickets (
    ticket_reference_id, customer_id, customer_email, customer_name,
    subject, description, category, priority, status,
    assigned_agent_id, assigned_agent_name, assigned_team,
    source, created_by, updated_by
) VALUES (
    'TKT-DEMO-001',
    'CUST-001',
    'demo@customer.com',
    'Demo Customer',
    'Unable to track my package',
    'I have been trying to track my package with reference ID PKG-123456 but the tracking page shows no results.',
    'SHIPMENT_TRACKING',
    'NORMAL',
    'OPEN',
    'AGENT-001',
    'Sarah Johnson',
    'general-support',
    'web',
    'system',
    'system'
);

COMMIT;