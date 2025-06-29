-- Add optimized indexes for WalkInShipment table
CREATE INDEX IF NOT EXISTS idx_tracking_number ON walk_in_shipment (tracking_number);
CREATE INDEX IF NOT EXISTS idx_customer_id ON walk_in_shipment (customer_id);
CREATE INDEX IF NOT EXISTS idx_origin_id ON walk_in_shipment (origin_id);
CREATE INDEX IF NOT EXISTS idx_status ON walk_in_shipment (status);
CREATE INDEX IF NOT EXISTS idx_creation_date ON walk_in_shipment (creation_date);
CREATE INDEX IF NOT EXISTS idx_estimated_delivery_date ON walk_in_shipment (estimated_delivery_date);
CREATE INDEX IF NOT EXISTS idx_status_creation_date ON walk_in_shipment (status, creation_date);
CREATE INDEX IF NOT EXISTS idx_international ON walk_in_shipment (international);
CREATE INDEX IF NOT EXISTS idx_service_type ON walk_in_shipment (service_type);
CREATE INDEX IF NOT EXISTS idx_handled_by_staff ON walk_in_shipment (handled_by_staff_id);

-- Add optimized indexes for WalkInCustomer table
CREATE INDEX IF NOT EXISTS idx_customer_email ON walk_in_customer (email);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON walk_in_customer (phone);
CREATE INDEX IF NOT EXISTS idx_customer_location ON walk_in_customer (location_id);
CREATE INDEX IF NOT EXISTS idx_customer_name ON walk_in_customer (name);

-- Add optimized indexes for WalkInPayment table
CREATE INDEX IF NOT EXISTS idx_payment_shipment ON walk_in_payment (shipment_id);
CREATE INDEX IF NOT EXISTS idx_payment_transaction ON walk_in_payment (transaction_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON walk_in_payment (status);
CREATE INDEX IF NOT EXISTS idx_payment_method ON walk_in_payment (payment_method);
CREATE INDEX IF NOT EXISTS idx_payment_date ON walk_in_payment (payment_date);
CREATE INDEX IF NOT EXISTS idx_payment_amount ON walk_in_payment (amount);

-- Add optimized indexes for LocationStaff table
CREATE INDEX IF NOT EXISTS idx_staff_location ON location_staff (location_id);
CREATE INDEX IF NOT EXISTS idx_staff_role ON location_staff (role);
CREATE INDEX IF NOT EXISTS idx_staff_active ON location_staff (active);
CREATE INDEX IF NOT EXISTS idx_staff_email ON location_staff (email);

-- Add optimized indexes for PhysicalLocation table
CREATE INDEX IF NOT EXISTS idx_location_type ON physical_location (location_type);
CREATE INDEX IF NOT EXISTS idx_location_active ON physical_location (active);
CREATE INDEX IF NOT EXISTS idx_location_country ON physical_location (country);
CREATE INDEX IF NOT EXISTS idx_location_state ON physical_location (state);
CREATE INDEX IF NOT EXISTS idx_location_city ON physical_location (city);
CREATE INDEX IF NOT EXISTS idx_location_zip ON physical_location (zip_code);
CREATE INDEX IF NOT EXISTS idx_location_coords ON physical_location (latitude, longitude);

-- Add optimized indexes for LocationOperatingHours table
CREATE INDEX IF NOT EXISTS idx_hours_location ON location_operating_hours (physical_location_id);
CREATE INDEX IF NOT EXISTS idx_hours_day ON location_operating_hours (day_of_week);
