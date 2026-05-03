-- auction 테이블에 연장 여부를 확인하는 컬럼 추가
ALTER TABLE auction
    ADD COLUMN extension_yn VARCHAR(20) NOT NULL DEFAULT 'NO' COMMENT '연장 경매 여부 (YES/NO)'
AFTER winner_price;