ALTER TABLE point_history
    DROP CONSTRAINT chk_point_type;

ALTER TABLE point_history
    ADD CONSTRAINT chk_point_type CHECK (`type` IN ('CHARGE', 'BID', 'EXCHANGE', 'REFUND', 'SETTLEMENT'));
