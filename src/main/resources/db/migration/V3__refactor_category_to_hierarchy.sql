-- =====================================================
-- CATEGORY HIERARCHY REFACTOR
-- - 기존 로컬 auction 도메인 데이터는 유지하지 않는다.
-- - category를 parent-child 계층 구조로 재정의한다.
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE review;
TRUNCATE TABLE winner_deal;
TRUNCATE TABLE point_history;
TRUNCATE TABLE wishlist;
TRUNCATE TABLE auction_inquiry;
TRUNCATE TABLE bid;
TRUNCATE TABLE auction;
TRUNCATE TABLE inspection;
TRUNCATE TABLE item_image;
TRUNCATE TABLE auction_item;

SET FOREIGN_KEY_CHECKS = 1;

SET @category_fk_name = (
    SELECT CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'auction_item'
      AND COLUMN_NAME = 'category_id'
      AND REFERENCED_TABLE_NAME = 'category'
    LIMIT 1
);

SET @drop_category_fk_sql = IF(
    @category_fk_name IS NOT NULL,
    CONCAT('ALTER TABLE auction_item DROP FOREIGN KEY ', @category_fk_name),
    'SELECT 1'
);

PREPARE drop_category_fk_stmt FROM @drop_category_fk_sql;
EXECUTE drop_category_fk_stmt;
DEALLOCATE PREPARE drop_category_fk_stmt;

DROP TABLE category;

CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT NULL,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL CHECK (level IN (1, 2, 3)),
    INDEX idx_category_parent_id (parent_id),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id)
);

ALTER TABLE auction_item
    ADD CONSTRAINT fk_auction_item_category
        FOREIGN KEY (category_id) REFERENCES category(id);
