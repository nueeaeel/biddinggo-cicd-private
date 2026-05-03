UPDATE auction_inquiry
SET status = 'PENDING'
WHERE status = 'ACTIVE';

ALTER TABLE auction_inquiry
    MODIFY status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

ALTER TABLE auction_inquiry
    ADD CONSTRAINT chk_inquiry_status CHECK (status IN ('PENDING', 'ANSWERED'));