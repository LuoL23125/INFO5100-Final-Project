-- ============================================
-- Wellness Chain Supply System - Database Schema
-- ============================================
-- Run this script to create all required tables
-- ============================================

-- Use the database
USE wellness_chain_db;

-- ============================================
-- 1. USER ACCOUNT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS user_account (
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    full_name VARCHAR(100) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 2. CLINIC BRANCH TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS clinic_branch (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) DEFAULT NULL,
    city VARCHAR(100) DEFAULT NULL,
    opening_time VARCHAR(10) DEFAULT NULL,
    closing_time VARCHAR(10) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 3. THERAPIST PROFILE TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS therapist_profile (
    id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    branch_id INT NOT NULL,
    specialty VARCHAR(100) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_therapist_user (user_id),
    KEY fk_therapist_branch (branch_id),
    CONSTRAINT fk_therapist_user FOREIGN KEY (user_id) REFERENCES user_account (id),
    CONSTRAINT fk_therapist_branch FOREIGN KEY (branch_id) REFERENCES clinic_branch (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 4. CUSTOMER TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS customer (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 5. APPOINTMENT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS appointment (
    id INT NOT NULL AUTO_INCREMENT,
    branch_id INT NOT NULL,
    therapist_user_id INT NOT NULL,
    customer_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_appt_branch (branch_id),
    KEY fk_appt_therapist (therapist_user_id),
    KEY fk_appt_customer (customer_id),
    CONSTRAINT fk_appt_branch FOREIGN KEY (branch_id) REFERENCES clinic_branch (id),
    CONSTRAINT fk_appt_therapist FOREIGN KEY (therapist_user_id) REFERENCES user_account (id),
    CONSTRAINT fk_appt_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 6. PRODUCT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS product (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) DEFAULT NULL,
    unit VARCHAR(20) DEFAULT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 7. BRANCH INVENTORY TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS branch_inventory (
    id INT NOT NULL AUTO_INCREMENT,
    branch_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity_on_hand INT NOT NULL DEFAULT 0,
    reorder_threshold INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY branch_product_unique (branch_id, product_id),
    KEY fk_branch_inv_product (product_id),
    CONSTRAINT fk_branch_inv_branch FOREIGN KEY (branch_id) REFERENCES clinic_branch (id) ON DELETE CASCADE,
    CONSTRAINT fk_branch_inv_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 8. PURCHASE ORDER TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS purchase_order (
    id INT NOT NULL AUTO_INCREMENT,
    branch_id INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    submitted_at DATETIME DEFAULT NULL,
    reviewed_at DATETIME DEFAULT NULL,
    supplier_comment VARCHAR(255) DEFAULT NULL,
    created_by_user_id INT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_po_branch (branch_id),
    KEY fk_po_user (created_by_user_id),
    CONSTRAINT fk_po_branch FOREIGN KEY (branch_id) REFERENCES clinic_branch (id),
    CONSTRAINT fk_po_user FOREIGN KEY (created_by_user_id) REFERENCES user_account (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 9. PURCHASE ORDER ITEM TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id INT NOT NULL AUTO_INCREMENT,
    purchase_order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    KEY fk_poi_po (purchase_order_id),
    KEY fk_poi_product (product_id),
    CONSTRAINT fk_poi_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_order (id) ON DELETE CASCADE,
    CONSTRAINT fk_poi_product FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 10. WORK REQUEST TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS work_request (
    id INT NOT NULL AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME DEFAULT NULL,
    created_by_user_id INT DEFAULT NULL,
    related_po_id INT DEFAULT NULL,
    comments VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY fk_wr_user (created_by_user_id),
    KEY fk_wr_po (related_po_id),
    CONSTRAINT fk_wr_user FOREIGN KEY (created_by_user_id) REFERENCES user_account (id),
    CONSTRAINT fk_wr_po FOREIGN KEY (related_po_id) REFERENCES purchase_order (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- Schema creation complete!
-- ============================================