ALTER TABLE winner_deal
    MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PAID'
    CHECK (`status` IN ('PAID', 'SHIPPED', 'CONFIRMED', 'CANCELLED'));

UPDATE winner_deal
SET status = 'SHIPPED'
WHERE status = 'PAID'
  AND (delivery_status = 'DELIVERED' OR tracking_number IS NOT NULL);

ALTER TABLE winner_deal
    DROP COLUMN delivery_status;
