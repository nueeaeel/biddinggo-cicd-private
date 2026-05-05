ALTER TABLE admin_inquiry
    ADD COLUMN category VARCHAR(20) DEFAULT NULL AFTER admin_id;