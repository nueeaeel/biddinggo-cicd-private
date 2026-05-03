-- 1. point_history 테이블 수정
ALTER TABLE point_history
    MODIFY type VARCHAR(20),
    ADD CONSTRAINT chk_point_type CHECK (`type` IN ('CHARGE','BID','EXCHANGE','REFUND'));

-- 2. bid 테이블 수정
ALTER TABLE bid
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER amount,
    ADD CONSTRAINT chk_bid_status CHECK (`status` IN ('ACTIVE', 'INACTIVE'));