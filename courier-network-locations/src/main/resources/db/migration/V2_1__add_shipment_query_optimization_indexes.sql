-- Add performance optimization indexes for walk_in_shipment table
-- These indexes improve query performance for the most common access patterns

-- Primary tracking number search index (most common customer lookup)
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_tracking_number 
ON walk_in_shipment (tracking_number);

-- Compound index for status + creation_date (common for admin dashboards and reporting)
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_status_creation 
ON walk_in_shipment (status, creation_date DESC);

-- Customer ID index (for customer history views)
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_customer_id 
ON walk_in_shipment (customer_id);

-- Origin location index (for location-based reports)
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_origin_id 
ON walk_in_shipment (origin_id);

-- Service type index (for service analytics)
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_service_type 
ON walk_in_shipment (service_type);

-- Delivery date index (for delivery planning)
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_estimated_delivery 
ON walk_in_shipment (estimated_delivery_date);

-- International shipment flag index (for customs processing)
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_international 
ON walk_in_shipment (international);

-- Composite index for destination lookups
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_destination 
ON walk_in_shipment (destination_country, destination_state, destination_city);

-- Index for fast counting by status
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_status 
ON walk_in_shipment (status);

-- Index for high-value shipment reporting
CREATE INDEX IF NOT EXISTS idx_walk_in_shipment_declared_value 
ON walk_in_shipment (declared_value DESC);
