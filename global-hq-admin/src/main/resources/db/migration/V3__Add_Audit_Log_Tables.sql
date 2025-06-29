-- Create audit log table for configuration changes
CREATE TABLE IF NOT EXISTS global_configuration_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    component VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255),
    entity_type VARCHAR(255),
    entity_name VARCHAR(255),
    field_name VARCHAR(255),
    old_value TEXT,
    new_value TEXT,
    user_id VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255),
    user_agent VARCHAR(512),
    region_code VARCHAR(50),
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_global_audit_component ON global_configuration_audit_logs (component);
CREATE INDEX IF NOT EXISTS idx_global_audit_action ON global_configuration_audit_logs (action);
CREATE INDEX IF NOT EXISTS idx_global_audit_entity_type_id ON global_configuration_audit_logs (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_global_audit_user_id ON global_configuration_audit_logs (user_id);
CREATE INDEX IF NOT EXISTS idx_global_audit_region_code ON global_configuration_audit_logs (region_code);
CREATE INDEX IF NOT EXISTS idx_global_audit_created_at ON global_configuration_audit_logs (created_at DESC);
