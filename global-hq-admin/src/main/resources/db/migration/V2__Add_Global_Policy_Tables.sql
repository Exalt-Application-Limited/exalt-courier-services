-- Create global_policies table
CREATE TABLE IF NOT EXISTS global_policies (
    id BIGSERIAL PRIMARY KEY,
    policy_key VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    policy_content TEXT NOT NULL,
    policy_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL,
    is_mandatory BOOLEAN NOT NULL,
    effective_date TIMESTAMP,
    expiration_date TIMESTAMP,
    global_region_id BIGINT,
    version_number VARCHAR(20),
    last_updated_by VARCHAR(255),
    approval_status VARCHAR(50),
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    
    CONSTRAINT fk_global_policies_region FOREIGN KEY (global_region_id)
        REFERENCES global_regions (id)
);

-- Create index for quick lookups by policy_key
CREATE INDEX IF NOT EXISTS idx_global_policies_policy_key ON global_policies (policy_key);

-- Create index for policy type searches
CREATE INDEX IF NOT EXISTS idx_global_policies_policy_type ON global_policies (policy_type);

-- Create index for approval status searches
CREATE INDEX IF NOT EXISTS idx_global_policies_approval_status ON global_policies (approval_status);

-- Create index for searches by region
CREATE INDEX IF NOT EXISTS idx_global_policies_global_region_id ON global_policies (global_region_id);

-- Create index for effective date range queries
CREATE INDEX IF NOT EXISTS idx_global_policies_effective_date ON global_policies (effective_date);

-- Create index for expiration date range queries
CREATE INDEX IF NOT EXISTS idx_global_policies_expiration_date ON global_policies (expiration_date);

-- Create composite index for finding active policies by region
CREATE INDEX IF NOT EXISTS idx_global_policies_active_region ON global_policies (is_active, global_region_id);

-- Create composite index for finding mandatory policies by type
CREATE INDEX IF NOT EXISTS idx_global_policies_mandatory_type ON global_policies (is_mandatory, policy_type);

-- Create composite index for finding effective policies by type and region
CREATE INDEX IF NOT EXISTS idx_global_policies_effective_type_region ON global_policies (effective_date, policy_type, global_region_id);

-- Populate some initial global policies
INSERT INTO global_policies (
    policy_key,
    name,
    description,
    policy_content,
    policy_type,
    is_active,
    is_mandatory,
    effective_date,
    expiration_date,
    version_number,
    last_updated_by,
    approval_status
) VALUES
(
    'GLOBAL_SHIPPING_RATE',
    'Global Shipping Rate Policy',
    'Default shipping rate policy that applies to all regions unless overridden',
    'All courier services follow a standard pricing model based on weight, distance, and premium service options. Base rates are calculated using a formula of $0.50 per kilometer plus $2.00 per kilogram.',
    'SHIPPING_RATE',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL,
    '1.0',
    'system',
    'APPROVED'
),
(
    'GLOBAL_DELIVERY_SLA',
    'Global Delivery SLA Policy',
    'Default delivery service level agreement that applies to all regions unless overridden',
    'Standard delivery: 3-5 business days. Express delivery: 1-2 business days. Same-day delivery: Available for orders placed before 10AM local time within urban areas.',
    'DELIVERY_SLA',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL,
    '1.0',
    'system',
    'APPROVED'
),
(
    'GLOBAL_PROHIBITED_ITEMS',
    'Global Prohibited Items Policy',
    'List of items that are prohibited from shipping across all regions',
    'The following items are prohibited from shipping: Explosives, flammable materials, illegal substances, firearms, ammunition, perishable goods without appropriate packaging, live animals, currency, and valuable jewelry over $1000 in value.',
    'PROHIBITED_ITEMS',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL,
    '1.0',
    'system',
    'APPROVED'
),
(
    'GLOBAL_DRIVER_CONDUCT',
    'Global Driver Conduct Policy',
    'Rules of conduct for all delivery drivers across all regions',
    'All drivers must: Wear company uniform during delivery hours, maintain professional appearance, verify recipient identity, obtain delivery confirmation signature, treat customers with respect, follow traffic laws, and report any delivery issues immediately.',
    'DRIVER_CONDUCT',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    NULL,
    '1.0',
    'system',
    'APPROVED'
),
(
    'GLOBAL_COMMISSION_STRUCTURE',
    'Global Commission Structure Policy',
    'Default commission structure for delivery personnel that applies to all regions unless overridden',
    'Drivers earn base commission of 70% of delivery fee plus performance bonuses: +5% for on-time delivery rate above 95%, +3% for customer rating above 4.8/5, +2% for handling over 20 deliveries per day.',
    'COMMISSION_STRUCTURE',
    TRUE,
    FALSE,
    CURRENT_TIMESTAMP,
    NULL,
    '1.0',
    'system',
    'APPROVED'
);
