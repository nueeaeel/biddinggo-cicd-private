ALTER TABLE address
    ADD COLUMN default_yn TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE auction_inquiry
    ADD COLUMN title VARCHAR(50) AFTER writer_id;

ALTER TABLE auction_inquiry
    ADD COLUMN secret_yn TINYINT(1) NOT NULL DEFAULT 0 AFTER content;

UPDATE auction_inquiry
SET title = '기존 문의글입니다'
WHERE title IS NULL;

ALTER TABLE auction_inquiry
    MODIFY COLUMN title VARCHAR(50) NOT NULL;