-- =====================================================
-- MEMBER
-- =====================================================
CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(20) NULL,
                        nickname VARCHAR(20) NULL UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        image_url VARCHAR(255) NULL,
                        point BIGINT NOT NULL DEFAULT 0 CHECK (point >= 0),
                        bank_code VARCHAR(20) NULL,
                        bank_account VARCHAR(20) NULL,
                        grade VARCHAR(20) NOT NULL DEFAULT 'BRONZE'
                            CHECK (grade IN ('BRONZE','SILVER','GOLD')),
                        role VARCHAR(20) NOT NULL DEFAULT 'USER'
                            CHECK (role IN ('USER','ADMIN')),
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING','ACTIVE','INACTIVE','DELETED')),
                        last_change_nick DATETIME NULL,
                        created_at DATETIME NOT NULL
);

-- =====================================================
-- SOCIAL ACCOUNT
-- =====================================================
CREATE TABLE social_account (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                member_id BIGINT NOT NULL,
                                provider VARCHAR(20) NOT NULL,
                                provider_id VARCHAR(255) NOT NULL,
                                created_at DATETIME NOT NULL,
                                CONSTRAINT uq_provider UNIQUE (provider, provider_id),
                                CONSTRAINT fk_social_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- =====================================================
-- ADDRESS
-- =====================================================
CREATE TABLE address (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         member_id BIGINT NOT NULL,
                         zipcode VARCHAR(20) NOT NULL,
                         address VARCHAR(255) NOT NULL,
                         detail_address VARCHAR(255) NOT NULL,
                         created_at DATETIME NOT NULL,
                         CONSTRAINT fk_address_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- =====================================================
-- PAYMENT
-- =====================================================
CREATE TABLE payment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         member_id BIGINT NOT NULL,
                         order_id VARCHAR(255) NOT NULL UNIQUE,
                         payment_key VARCHAR(255) UNIQUE,
                         payment_method VARCHAR(20) NOT NULL,
                         amount BIGINT NOT NULL CHECK (amount >= 0),
                         status VARCHAR(20) NOT NULL
                             CHECK (status IN ('WAITING_FOR_DEPOSIT','DONE','CANCELED','PARTIAL_CANCELED','ABORTED','EXPIRED')),
                         approved_at DATETIME NULL,
                         created_at DATETIME NOT NULL,
                         FOREIGN KEY (member_id) REFERENCES member(id)
);

-- =====================================================
-- VIRTUAL ACCOUNT
-- =====================================================
CREATE TABLE virtual_account (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 payment_id BIGINT NOT NULL,
                                 bank_code VARCHAR(20) NOT NULL,
                                 bank_account VARCHAR(20) NOT NULL,
                                 account_holder_name VARCHAR(20) NOT NULL,
                                 due_date DATETIME NOT NULL,
                                 created_at DATETIME NOT NULL,
                                 FOREIGN KEY (payment_id) REFERENCES payment(id)
);

-- =====================================================
-- CATEGORY
-- =====================================================
CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          large VARCHAR(20) NOT NULL,
                          mid VARCHAR(20) NOT NULL,
                          small VARCHAR(20) NOT NULL
);

-- =====================================================
-- AUCTION ITEM
-- =====================================================
CREATE TABLE auction_item (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              seller_id BIGINT NOT NULL,
                              category_id BIGINT NOT NULL,
                              brand VARCHAR(20) NULL,
                              name VARCHAR(20) NOT NULL,
                              quality VARCHAR(20) NOT NULL DEFAULT '최상',
                              description TEXT NULL,
                              status VARCHAR(20) NOT NULL DEFAULT 'ON_AUCTION'
                                  CHECK (status IN ('PENDING','ON_AUCTION','CANCELLED','RETURNED','SOLD','UNSOLD')),
                              inspection_status VARCHAR(20) NOT NULL DEFAULT 'NONE'
                                  CHECK (inspection_status IN ('NONE','PENDING','PASSED','FAILED')),
                              returned_at DATETIME NULL,
                              created_at DATETIME NOT NULL,
                              FOREIGN KEY (seller_id) REFERENCES member(id),
                              FOREIGN KEY (category_id) REFERENCES category(id)
);

-- =====================================================
-- ITEM IMAGE
-- =====================================================
CREATE TABLE item_image (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            item_id BIGINT NOT NULL,
                            url VARCHAR(255) NOT NULL,
                            display_order INT NOT NULL CHECK (display_order > 0),
                            type VARCHAR(20) NOT NULL,
                            size INT NOT NULL CHECK (size > 0),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (item_id) REFERENCES auction_item(id)
);

-- =====================================================
-- AUCTION
-- =====================================================
CREATE TABLE auction (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         item_id BIGINT NOT NULL UNIQUE,
                         winner_id BIGINT NULL,
                         seller_id BIGINT NOT NULL,
                         type VARCHAR(20) NOT NULL DEFAULT 'NORMAL'
                             CHECK (type IN ('INSPECTION','NORMAL','TIME_DEAL')),
                         inspection_yn VARCHAR(20) NULL
        CHECK (inspection_yn IN ('YES','NO')),
                         status VARCHAR(20) NOT NULL DEFAULT 'ON_GOING'
                             CHECK (status IN ('PENDING','ON_GOING','ENDED','CANCELLED')),
                         start_price BIGINT NOT NULL DEFAULT 0 CHECK (start_price >= 0),
                         bid_unit INT NOT NULL DEFAULT 1000 CHECK (bid_unit > 0),
                         vickrey_price BIGINT NULL,
                         bid_count INT NOT NULL DEFAULT 0 CHECK (bid_count >= 0),
                         buy_now_price BIGINT NULL,
                         wish_count INT NOT NULL DEFAULT 0 CHECK (wish_count >= 0),
                         start_date DATETIME NOT NULL,
                         end_date DATETIME NOT NULL,
                         cancel_date DATETIME NULL,
                         winner_price BIGINT NULL,
                         created_at DATETIME NOT NULL,
                         FOREIGN KEY (item_id) REFERENCES auction_item(id),
                         FOREIGN KEY (seller_id) REFERENCES member(id),
                         FOREIGN KEY (winner_id) REFERENCES member(id)
);

-- =====================================================
-- BID
-- =====================================================
CREATE TABLE bid (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     auction_id BIGINT NOT NULL,
                     bidder_id BIGINT NOT NULL,
                     amount BIGINT NOT NULL CHECK (amount > 0),
                     created_at DATETIME NOT NULL,
                     FOREIGN KEY (auction_id) REFERENCES auction(id),
                     FOREIGN KEY (bidder_id) REFERENCES member(id)
);

-- =====================================================
-- POINT HISTORY
-- =====================================================
CREATE TABLE point_history (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               member_id BIGINT NOT NULL,
                               payment_id BIGINT NULL,
                               bid_id BIGINT NULL,
                               type VARCHAR(20) NOT NULL
                                   CHECK (type IN ('CHARGE','BID','REFUND')),
                               amount BIGINT NOT NULL DEFAULT 0,
                               created_at DATETIME NOT NULL,
                               FOREIGN KEY (member_id) REFERENCES member(id),
                               FOREIGN KEY (payment_id) REFERENCES payment(id),
                               FOREIGN KEY (bid_id) REFERENCES bid(id)
);

-- =====================================================
-- WISHLIST
-- =====================================================
CREATE TABLE wishlist (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          member_id BIGINT NOT NULL,
                          auction_id BIGINT NOT NULL,
                          created_at DATETIME NOT NULL,
                          UNIQUE (member_id, auction_id),
                          FOREIGN KEY (member_id) REFERENCES member(id),
                          FOREIGN KEY (auction_id) REFERENCES auction(id)
);

-- =====================================================
-- INSPECTION
-- =====================================================
CREATE TABLE inspection (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            item_id BIGINT NOT NULL,
                            received_at DATETIME NULL,
                            status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                                CHECK (status IN ('PENDING','PASSED','FAILED')),
                            completed_at DATETIME NULL,
                            failure_reason TEXT NULL,
                            carrier VARCHAR(20) NULL,
                            tracking_number VARCHAR(255) NULL,
                            created_at DATETIME NOT NULL,
                            FOREIGN KEY (item_id) REFERENCES auction_item(id)
);

-- =====================================================
-- WINNER DEAL
-- =====================================================
CREATE TABLE winner_deal (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             auction_id BIGINT NOT NULL UNIQUE,
                             winner_id BIGINT NOT NULL,
                             seller_id BIGINT NOT NULL,
                             winner_price BIGINT NOT NULL DEFAULT 0 CHECK (winner_price >= 0),
                             status VARCHAR(20) NOT NULL DEFAULT 'PAID'
                                 CHECK (status IN ('PAID','CANCELLED','CONFIRMED')),
                             delivery_status VARCHAR(20) NOT NULL DEFAULT 'SHIPPED'
                                 CHECK (delivery_status IN ('SHIPPED','DELIVERED')),
                             confirmed_at DATETIME NULL,
                             zipcode VARCHAR(20) NULL,
                             address VARCHAR(255) NULL,
                             detail_address VARCHAR(255) NULL,
                             tel VARCHAR(20) NULL,
                             recipient VARCHAR(20) NULL,
                             carrier VARCHAR(20) NULL,
                             tracking_number VARCHAR(30) NULL,
                             created_at DATETIME NOT NULL,
                             FOREIGN KEY (auction_id) REFERENCES auction(id),
                             FOREIGN KEY (winner_id) REFERENCES member(id),
                             FOREIGN KEY (seller_id) REFERENCES member(id)
);

-- =====================================================
-- REVIEW
-- =====================================================
CREATE TABLE review (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        deal_id BIGINT NOT NULL UNIQUE,
                        writer_id BIGINT NOT NULL,
                        target_id BIGINT NOT NULL,
                        rating DECIMAL(2,1) NOT NULL DEFAULT 0.0
                            CHECK (rating >= 0.0 AND rating <= 5.0),
                        content TEXT NULL,
                        created_at DATETIME NOT NULL,
                        FOREIGN KEY (deal_id) REFERENCES winner_deal(id),
                        FOREIGN KEY (writer_id) REFERENCES member(id),
                        FOREIGN KEY (target_id) REFERENCES member(id)
);

-- =====================================================
-- NOTIFICATION
-- =====================================================
CREATE TABLE notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              receiver_id BIGINT NOT NULL,
                              type VARCHAR(20) NOT NULL,
                              content TEXT NOT NULL,
                              url VARCHAR(255) NULL,
                              read_at DATETIME NULL,
                              created_at DATETIME NOT NULL,
                              FOREIGN KEY (receiver_id) REFERENCES member(id)
);

-- =====================================================
-- REPORT
-- =====================================================
CREATE TABLE report (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        reporter_id BIGINT NOT NULL,
                        target_type VARCHAR(20) NOT NULL,
                        target_id BIGINT NOT NULL,
                        reason TEXT NOT NULL,
                        created_at DATETIME NOT NULL,
                        FOREIGN KEY (reporter_id) REFERENCES member(id)
);

-- =====================================================
-- AUCTION INQUIRY
-- =====================================================
CREATE TABLE auction_inquiry (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 auction_id BIGINT NOT NULL,
                                 writer_id BIGINT NOT NULL,
                                 answerer_id BIGINT NULL,
                                 content TEXT NOT NULL,
                                 answer TEXT NULL,
                                 answered_at DATETIME NULL,
                                 status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                                     CHECK (status IN ('ACTIVE','DELETED')),
                                 created_at DATETIME NOT NULL,
                                 FOREIGN KEY (auction_id) REFERENCES auction(id),
                                 FOREIGN KEY (writer_id) REFERENCES member(id),
                                 FOREIGN KEY (answerer_id) REFERENCES member(id)
);

-- =====================================================
-- ADMIN INQUIRY
-- =====================================================
CREATE TABLE admin_inquiry (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               writer_id BIGINT NOT NULL,
                               admin_id BIGINT NOT NULL,
                               content TEXT NOT NULL,
                               answer TEXT NULL,
                               answered_at DATETIME NULL,
                               created_at DATETIME NOT NULL,
                               FOREIGN KEY (writer_id) REFERENCES member(id),
                               FOREIGN KEY (admin_id) REFERENCES member(id)
);

-- =====================================================
-- NOTICE
-- =====================================================
CREATE TABLE notice (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(20) NOT NULL,
                        content TEXT NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                            CHECK (status IN ('ACTIVE','DELETED')),
                        created_at DATETIME NOT NULL
);