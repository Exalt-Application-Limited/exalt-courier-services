-- Assignment and AssignmentStatusChange Tables Migration
-- Version: 1.0
-- Date: 2025-05-13

-- Create Assignment table
CREATE TABLE IF NOT EXISTS assignments (
    id BIGSERIAL PRIMARY KEY,
    assignment_id VARCHAR(50) NOT NULL UNIQUE,
    courier_id BIGINT REFERENCES couriers(id),
    route_id BIGINT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    pickup_latitude DOUBLE PRECISION NOT NULL,
    pickup_longitude DOUBLE PRECISION NOT NULL,
    pickup_address VARCHAR(500) NOT NULL,
    delivery_latitude DOUBLE PRECISION NOT NULL,
    delivery_longitude DOUBLE PRECISION NOT NULL,
    delivery_address VARCHAR(500) NOT NULL,
    pickup_contact_name VARCHAR(100),
    pickup_contact_phone VARCHAR(20),
    delivery_contact_name VARCHAR(100),
    delivery_contact_phone VARCHAR(20),
    pickup_time_window_start TIMESTAMP,
    pickup_time_window_end TIMESTAMP,
    delivery_time_window_start TIMESTAMP,
    delivery_time_window_end TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    assigned_at TIMESTAMP,
    accepted_at TIMESTAMP,
    pickup_at TIMESTAMP,
    delivered_at TIMESTAMP,
    completed_at TIMESTAMP,
    canceled_at TIMESTAMP,
    expected_duration_minutes INTEGER,
    actual_duration_minutes INTEGER,
    distance_km DOUBLE PRECISION,
    package_count INTEGER,
    notes VARCHAR(1000),
    rejection_reason VARCHAR(500),
    failure_reason VARCHAR(500),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

-- Create AssignmentStatusChange table
CREATE TABLE IF NOT EXISTS assignment_status_changes (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL REFERENCES assignments(id),
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    change_time TIMESTAMP NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    changed_by VARCHAR(100) NOT NULL,
    reason VARCHAR(500),
    notes VARCHAR(1000)
);

-- Create Assignment Package IDs table
CREATE TABLE IF NOT EXISTS assignment_package_ids (
    assignment_id BIGINT NOT NULL REFERENCES assignments(id),
    package_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (assignment_id, package_id)
);

-- Create spatial indices for faster geo queries
CREATE INDEX IF NOT EXISTS idx_assignment_pickup_location ON assignments USING gist (
    ST_SetSRID(ST_MakePoint(pickup_longitude, pickup_latitude), 4326)
);

CREATE INDEX IF NOT EXISTS idx_assignment_delivery_location ON assignments USING gist (
    ST_SetSRID(ST_MakePoint(delivery_longitude, delivery_latitude), 4326)
);

-- Create indices for common queries
CREATE INDEX IF NOT EXISTS idx_assignment_status ON assignments(status);
CREATE INDEX IF NOT EXISTS idx_assignment_courier_id ON assignments(courier_id);
CREATE INDEX IF NOT EXISTS idx_assignment_route_id ON assignments(route_id);
CREATE INDEX IF NOT EXISTS idx_assignment_created_at ON assignments(created_at);
CREATE INDEX IF NOT EXISTS idx_assignment_priority ON assignments(priority);
CREATE INDEX IF NOT EXISTS idx_assignment_delivery_window ON assignments(delivery_time_window_start, delivery_time_window_end);
CREATE INDEX IF NOT EXISTS idx_assignment_pickup_window ON assignments(pickup_time_window_start, pickup_time_window_end);

-- Create indices for status changes
CREATE INDEX IF NOT EXISTS idx_status_change_assignment_id ON assignment_status_changes(assignment_id);
CREATE INDEX IF NOT EXISTS idx_status_change_new_status ON assignment_status_changes(new_status);
CREATE INDEX IF NOT EXISTS idx_status_change_change_time ON assignment_status_changes(change_time);
CREATE INDEX IF NOT EXISTS idx_status_change_changed_by ON assignment_status_changes(changed_by);

-- Create index for package IDs
CREATE INDEX IF NOT EXISTS idx_assignment_package_id ON assignment_package_ids(package_id);
