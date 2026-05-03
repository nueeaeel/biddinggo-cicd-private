ALTER TABLE winner_deal
    ADD COLUMN deal_number VARCHAR(50) NULL AFTER seller_id;

UPDATE winner_deal
SET deal_number = CONCAT('WD-', DATE_FORMAT(created_at, '%Y%m%d'), '-', LPAD(id, 6, '0'))
WHERE deal_number IS NULL;

ALTER TABLE winner_deal
    MODIFY COLUMN deal_number VARCHAR(50) NOT NULL,
    ADD CONSTRAINT uk_winner_deal_deal_number UNIQUE (deal_number);
