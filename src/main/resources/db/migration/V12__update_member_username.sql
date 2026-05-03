-- username의 길이를 20에서 100으로 변경하며 고유키 설정
ALTER TABLE `member` MODIFY COLUMN `username` VARCHAR(100) UNIQUE;