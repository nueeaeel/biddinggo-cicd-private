-- 1. member 테이블 컬럼 추가 (id 다음 순서로 배치)
ALTER TABLE `member`
    ADD COLUMN `username` VARCHAR(20) AFTER `id`,
    ADD COLUMN `password` VARCHAR(255) AFTER `username`;

-- 2. 
RENAME TABLE `admin_inquiry` TO `direct_inquiry`;