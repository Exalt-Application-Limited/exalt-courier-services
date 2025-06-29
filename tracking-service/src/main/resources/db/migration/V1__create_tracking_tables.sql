-- Create packages table
CREATE TABLE packages (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(30) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    sender_name VARCHAR(255) NOT NULL,
    sender_address TEXT NOT NULL,
    recipient_name VARCHAR(255) NOT NULL,
    recipient_address TEXT NOT NULL,
    recipient_phone VARCHAR(20),
    recipient_email VARCHAR(255),
    estimated_delivery_date TIMESTAMP NOT NULL,
    actual_delivery_date TIMESTAMP,
    weight DOUBLE PRECISION,
    dimensions VARCHAR(50),
    order_id VARCHAR(50),
    courier_id BIGINT,
    route_id BIGINT,
    signature_required BOOLEAN DEFAULT FALSE,
    signature_image TEXT,
    delivery_instructions TEXT,
    delivery_attempts INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create tracking_events table
CREATE TABLE tracking_events (
    id BIGSERIAL PRIMARY KEY,
    package_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    description TEXT NOT NULL,
    event_time TIMESTAMP NOT NULL,
    location VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    courier_id BIGINT,
    facility_id BIGINT,
    scan_type VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tracking_events_package FOREIGN KEY (package_id) REFERENCES packages(id) ON DELETE CASCADE
);

-- Create indexes for improved query performance
CREATE INDEX idx_packages_tracking_number ON packages(tracking_number);
CREATE INDEX idx_packages_status ON packages(status);
CREATE INDEX idx_packages_courier_id ON packages(courier_id);
CREATE INDEX idx_packages_route_id ON packages(route_id);
CREATE INDEX idx_packages_order_id ON packages(order_id);
CREATE INDEX idx_packages_recipient_name ON packages(recipient_name);
CREATE INDEX idx_packages_estimated_delivery_date ON packages(estimated_delivery_date);
CREATE INDEX idx_packages_actual_delivery_date ON packages(actual_delivery_date);

CREATE INDEX idx_tracking_events_package_id ON tracking_events(package_id);
CREATE INDEX idx_tracking_events_status ON tracking_events(status);
CREATE INDEX idx_tracking_events_event_time ON tracking_events(event_time);
CREATE INDEX idx_tracking_events_courier_id ON tracking_events(courier_id);
CREATE INDEX idx_tracking_events_facility_id ON tracking_events(facility_id);
CREATE INDEX idx_tracking_events_location ON tracking_events(location);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers to automatically update updated_at
CREATE TRIGGER update_packages_updated_at
BEFORE UPDATE ON packages
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tracking_events_updated_at
BEFORE UPDATE ON tracking_events
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column(); 