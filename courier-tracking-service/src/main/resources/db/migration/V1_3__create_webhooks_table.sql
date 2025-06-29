CREATE TABLE webhooks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    url VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    secret_key VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_webhooks_event_type ON webhooks(event_type);
CREATE INDEX idx_webhooks_active ON webhooks(active);

-- Insert some default webhooks for testing
INSERT INTO webhooks (name, url, event_type, active, created_at, updated_at)
VALUES 
    ('Package Status Change Webhook', 'http://localhost:8080/webhook/package-status', 'PACKAGE_STATUS_CHANGE', true, NOW(), NOW()),
    ('Tracking Event Webhook', 'http://localhost:8080/webhook/tracking-event', 'TRACKING_EVENT', true, NOW(), NOW()),
    ('Delivery Webhook', 'http://localhost:8080/webhook/delivery', 'DELIVERY', true, NOW(), NOW()); 