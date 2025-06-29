-- Create tables for payout service

-- Earnings table to track courier earnings
CREATE TABLE IF NOT EXISTS earnings (
    id VARCHAR(36) PRIMARY KEY,
    courier_id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    earned_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Payout table to track payouts to couriers
CREATE TABLE IF NOT EXISTS payout (
    id VARCHAR(36) PRIMARY KEY,
    courier_id VARCHAR(36) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payout_period_start DATE NOT NULL,
    payout_period_end DATE NOT NULL,
    scheduled_at TIMESTAMP,
    processed_at TIMESTAMP,
    payment_reference VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Payout_items table to link earnings to payouts
CREATE TABLE IF NOT EXISTS payout_items (
    payout_id VARCHAR(36) NOT NULL,
    earning_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (payout_id, earning_id),
    FOREIGN KEY (payout_id) REFERENCES payout(id),
    FOREIGN KEY (earning_id) REFERENCES earnings(id)
);

-- Create combined index for common query pattern
CREATE INDEX idx_earnings_courier_date ON earnings(courier_id, earned_at);
-- Create index for payouts by courier
CREATE INDEX idx_payout_courier ON payout(courier_id);
-- Create index for status-based queries
CREATE INDEX idx_payout_status ON payout(status);
-- Create index for date range queries
CREATE INDEX idx_payout_period ON payout(payout_period_start, payout_period_end);
