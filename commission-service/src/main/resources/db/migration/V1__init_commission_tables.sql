-- Create tables for commission service

-- Partner table to manage commission entities
CREATE TABLE IF NOT EXISTS partner (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    partner_type VARCHAR(50) NOT NULL,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Commission rule table to define commission structures
CREATE TABLE IF NOT EXISTS commission_rule (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    partner_type VARCHAR(50) NOT NULL,
    rate_type VARCHAR(20) NOT NULL, -- FIXED, PERCENTAGE
    rate_value DECIMAL(10, 2) NOT NULL,
    min_amount DECIMAL(10, 2),
    max_amount DECIMAL(10, 2),
    start_date DATE NOT NULL,
    end_date DATE,
    priority INT DEFAULT 0,
    conditions TEXT, -- JSON structure for complex conditions
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Commission entry table to track calculated commissions
CREATE TABLE IF NOT EXISTS commission_entry (
    id VARCHAR(36) PRIMARY KEY,
    partner_id VARCHAR(36) NOT NULL,
    rule_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    base_amount DECIMAL(10, 2) NOT NULL,
    commission_amount DECIMAL(10, 2) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_id VARCHAR(36),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (partner_id) REFERENCES partner(id),
    FOREIGN KEY (rule_id) REFERENCES commission_rule(id)
);

-- Partner payment table to track payments to partners
CREATE TABLE IF NOT EXISTS partner_payment (
    id VARCHAR(36) PRIMARY KEY,
    partner_id VARCHAR(36) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP,
    payment_method VARCHAR(50),
    reference_number VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (partner_id) REFERENCES partner(id)
);

-- Payment details table to link commission entries to payments
CREATE TABLE IF NOT EXISTS payment_details (
    payment_id VARCHAR(36) NOT NULL,
    commission_entry_id VARCHAR(36) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (payment_id, commission_entry_id),
    FOREIGN KEY (payment_id) REFERENCES partner_payment(id),
    FOREIGN KEY (commission_entry_id) REFERENCES commission_entry(id)
);

-- Create indexes for common query patterns
CREATE INDEX idx_commission_entry_partner ON commission_entry(partner_id);
CREATE INDEX idx_commission_entry_status ON commission_entry(status);
CREATE INDEX idx_commission_entry_date ON commission_entry(transaction_date);
CREATE INDEX idx_commission_rule_partner_type ON commission_rule(partner_type);
CREATE INDEX idx_commission_rule_status ON commission_rule(status);
CREATE INDEX idx_commission_rule_dates ON commission_rule(start_date, end_date);
CREATE INDEX idx_partner_payment_partner ON partner_payment(partner_id);
CREATE INDEX idx_partner_payment_status ON partner_payment(status);
CREATE INDEX idx_partner_payment_date ON partner_payment(payment_date);
CREATE INDEX idx_partner_payment_period ON partner_payment(period_start, period_end);
