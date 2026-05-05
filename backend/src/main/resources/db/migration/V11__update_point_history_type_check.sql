ALTER TABLE point_history
    MODIFY COLUMN type VARCHAR(20)
    CHECK (type IN ('CHARGE', 'BID', 'EXCHANGE'));