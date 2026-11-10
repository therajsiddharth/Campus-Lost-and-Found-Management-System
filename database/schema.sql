CREATE DATABASE IF NOT EXISTS campus_lost_found
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_lost_found;

CREATE TABLE IF NOT EXISTS users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  student_code VARCHAR(30) UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  role ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
  account_status ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
  category_id INT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(60) NOT NULL UNIQUE,
  active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS items (
  item_id INT AUTO_INCREMENT PRIMARY KEY,
  reporter_id INT NOT NULL,
  category_id INT NOT NULL,
  item_name VARCHAR(120) NOT NULL,
  description TEXT,
  location VARCHAR(150) NOT NULL,
  item_type ENUM('LOST','FOUND') NOT NULL,
  item_status ENUM('PENDING','APPROVED','CLAIM_PENDING','RETURNED','REJECTED','CLOSED')
    NOT NULL DEFAULT 'PENDING',
  date_reported TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  verified_by INT NULL,
  verified_at TIMESTAMP NULL,
  CONSTRAINT fk_items_reporter FOREIGN KEY (reporter_id) REFERENCES users(user_id),
  CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
  CONSTRAINT fk_items_verifier FOREIGN KEY (verified_by) REFERENCES users(user_id),
  INDEX idx_items_type_status (item_type, item_status),
  INDEX idx_items_category (category_id),
  INDEX idx_items_reported (date_reported)
);

CREATE TABLE IF NOT EXISTS claims (
  claim_id INT AUTO_INCREMENT PRIMARY KEY,
  item_id INT NOT NULL,
  claimant_id INT NOT NULL,
  proof_description TEXT NOT NULL,
  claim_status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  claim_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reviewed_by INT NULL,
  reviewed_at TIMESTAMP NULL,
  CONSTRAINT fk_claim_item FOREIGN KEY (item_id) REFERENCES items(item_id),
  CONSTRAINT fk_claim_user FOREIGN KEY (claimant_id) REFERENCES users(user_id),
  CONSTRAINT fk_claim_admin FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
  INDEX idx_claim_status (claim_status)
);

CREATE TABLE IF NOT EXISTS notifications (
  notification_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  message VARCHAR(500) NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS admin_logs (
  log_id INT AUTO_INCREMENT PRIMARY KEY,
  admin_id INT NOT NULL,
  action VARCHAR(100) NOT NULL,
  target_type VARCHAR(40) NOT NULL,
  target_id INT NOT NULL,
  details VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_log_admin FOREIGN KEY (admin_id) REFERENCES users(user_id)
);

INSERT IGNORE INTO categories(category_name) VALUES
 ('Electronics'),('Documents'),('Accessories'),('Clothing'),
 ('Stationery'),('Bags'),('Keys'),('Other');

-- Optional useful DBMS queries for practical/viva:
-- SELECT item_type, item_status, COUNT(*) FROM items GROUP BY item_type, item_status;
-- SELECT i.item_id, i.item_name, u.full_name, c.category_name
-- FROM items i JOIN users u ON i.reporter_id=u.user_id
-- JOIN categories c ON i.category_id=c.category_id;
-- SELECT * FROM claims WHERE claim_status='PENDING' ORDER BY claim_date;
