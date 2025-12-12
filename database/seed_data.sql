-- ============================================
-- Wellness Chain Supply System - Sample Data
-- ============================================
-- Run this script after schema.sql to populate test data
-- ============================================

USE wellness_chain_db;

-- ============================================
-- 1. USER ACCOUNTS (4 Roles)
-- ============================================
-- Password for all users: password123

INSERT INTO user_account (username, password, role, full_name, email) VALUES
-- Wellness Enterprise - Admin
('branchadmin', 'password123', 'BRANCH_ADMIN', 'Sarah Chen', 'sarah.chen@wellnesschain.com'),
-- Wellness Enterprise - Normal
('therapist1', 'password123', 'THERAPIST', 'Michael Wong', 'michael.wong@wellnesschain.com'),
('therapist2', 'password123', 'THERAPIST', 'Emily Zhang', 'emily.zhang@wellnesschain.com'),
-- Supplier Enterprise - Admin
('supplieradmin', 'password123', 'SUPPLIER_ADMIN', 'David Lee', 'david.lee@supplier.com'),
-- Supplier Enterprise - Normal
('supplierstaff', 'password123', 'SUPPLIER_STAFF', 'Jessica Park', 'jessica.park@supplier.com');

-- ============================================
-- 2. CLINIC BRANCHES (Wellness Enterprise)
-- ============================================
INSERT INTO clinic_branch (name, address, city, opening_time, closing_time) VALUES
('Cloud Pavilion - Downtown', '100 King St W', 'Toronto', '09:00', '21:00'),
('Cloud Pavilion - North York', '500 Yonge St', 'Toronto', '10:00', '20:00'),
('Cloud Pavilion - Scarborough', '11 Ellesmere Rd', 'Toronto', '09:00', '20:00');

-- ============================================
-- 3. THERAPIST PROFILES
-- ============================================
-- Link therapist users to their branches
INSERT INTO therapist_profile (user_id, branch_id, specialty) VALUES
(2, 1, 'Swedish Massage, Deep Tissue'),      -- therapist1 at Downtown
(3, 2, 'Acupuncture, Reflexology');          -- therapist2 at North York

-- ============================================
-- 4. CUSTOMERS
-- ============================================
INSERT INTO customer (name, phone, email, notes) VALUES
('John Smith', '416-555-0101', 'john.smith@email.com', 'Prefers morning appointments'),
('Alice Johnson', '416-555-0102', 'alice.j@email.com', 'Allergic to lavender'),
('Robert Brown', '416-555-0103', 'r.brown@email.com', 'Regular customer since 2023'),
('Maria Garcia', '416-555-0104', 'maria.g@email.com', 'Prefers female therapist'),
('James Wilson', '416-555-0105', 'j.wilson@email.com', 'First time customer');

-- ============================================
-- 5. PRODUCTS (Supplier Catalog)
-- ============================================
INSERT INTO product (name, category, unit, unit_price) VALUES
('Lavender Massage Oil 1L', 'OIL', 'bottle', 45.00),
('Eucalyptus Massage Oil 1L', 'OIL', 'bottle', 42.00),
('Disposable Massage Sheets (100 pack)', 'LINEN', 'box', 30.00),
('Cotton Towels (50 pack)', 'LINEN', 'box', 75.00),
('Aromatherapy Diffuser', 'EQUIPMENT', 'piece', 80.00),
('Hot Stone Set', 'EQUIPMENT', 'set', 150.00),
('Massage Table Paper Roll', 'SUPPLIES', 'roll', 25.00),
('Hand Sanitizer Gallon', 'SUPPLIES', 'gallon', 35.00);

-- ============================================
-- 6. BRANCH INVENTORY
-- ============================================
INSERT INTO branch_inventory (branch_id, product_id, quantity_on_hand, reorder_threshold) VALUES
-- Downtown Branch
(1, 1, 5, 3),   -- Lavender Oil
(1, 2, 4, 3),   -- Eucalyptus Oil
(1, 3, 10, 5),  -- Disposable Sheets
(1, 7, 8, 4),   -- Table Paper
-- North York Branch
(2, 1, 3, 3),   -- Lavender Oil (LOW!)
(2, 3, 15, 5),  -- Disposable Sheets
(2, 4, 6, 4),   -- Cotton Towels
-- Scarborough Branch
(3, 1, 8, 5),   -- Lavender Oil
(3, 2, 6, 3),   -- Eucalyptus Oil
(3, 3, 20, 10); -- Disposable Sheets

-- ============================================
-- 7. SAMPLE APPOINTMENTS
-- ============================================
INSERT INTO appointment (branch_id, therapist_user_id, customer_id, start_time, end_time, status, notes) VALUES
-- Past appointments (completed)
(1, 2, 1, '2025-01-10 10:00:00', '2025-01-10 11:00:00', 'COMPLETED', 'Deep tissue massage, client satisfied'),
(1, 2, 2, '2025-01-10 14:00:00', '2025-01-10 15:00:00', 'COMPLETED', 'Swedish massage'),
(2, 3, 3, '2025-01-11 11:00:00', '2025-01-11 12:00:00', 'COMPLETED', 'Acupuncture session'),
-- Upcoming appointments (scheduled)
(1, 2, 4, '2025-01-20 09:00:00', '2025-01-20 10:00:00', 'SCHEDULED', 'New customer - intake required'),
(1, 2, 1, '2025-01-20 11:00:00', '2025-01-20 12:00:00', 'SCHEDULED', 'Follow-up session'),
(2, 3, 5, '2025-01-21 14:00:00', '2025-01-21 15:00:00', 'SCHEDULED', 'First visit - consultation'),
-- Cancelled appointment
(1, 2, 3, '2025-01-15 16:00:00', '2025-01-15 17:00:00', 'CANCELLED', 'Customer cancelled - rescheduling');

-- ============================================
-- 8. SAMPLE PURCHASE ORDER (Complete Workflow)
-- ============================================
-- This PO has gone through the full workflow: SUBMITTED -> APPROVED -> PACKED -> SHIPPED -> DELIVERED
INSERT INTO purchase_order (branch_id, status, created_at, submitted_at, reviewed_at, supplier_comment, created_by_user_id) VALUES
(3, 'DELIVERED', '2025-01-08 10:00:00', '2025-01-08 10:00:00', '2025-01-08 14:30:00', 'Delivered successfully', 1);

INSERT INTO purchase_order_item (purchase_order_id, product_id, quantity, unit_price) VALUES
(1, 1, 10, 45.00);  -- 10 bottles of Lavender Oil

-- ============================================
-- 9. WORK REQUESTS (Audit Trail)
-- ============================================
INSERT INTO work_request (type, status, created_at, updated_at, created_by_user_id, related_po_id, comments) VALUES
-- Purchase Order work request
('PURCHASE_ORDER', 'COMPLETED', '2025-01-08 10:00:00', '2025-01-08 14:30:00', 1, 1, 'Order approved by supplier admin'),
-- Shipment work request
('SHIPMENT', 'COMPLETED', '2025-01-08 15:00:00', '2025-01-09 11:00:00', 5, 1, 'Delivered to Scarborough branch');

-- ============================================
-- 10. PENDING PURCHASE ORDER (For Demo)
-- ============================================
-- This PO is waiting for approval - good for demo
INSERT INTO purchase_order (branch_id, status, created_at, submitted_at, created_by_user_id) VALUES
(1, 'SUBMITTED', NOW(), NOW(), 1);

INSERT INTO purchase_order_item (purchase_order_id, product_id, quantity, unit_price) VALUES
(2, 3, 5, 30.00);  -- 5 boxes of Disposable Sheets

INSERT INTO work_request (type, status, created_at, updated_at, created_by_user_id, related_po_id, comments) VALUES
('PURCHASE_ORDER', 'OPEN', NOW(), NOW(), 1, 2, 'New order - awaiting supplier approval');

-- ============================================
-- Data seeding complete!
-- 
-- TEST ACCOUNTS:
-- +--------------+-------------+----------------+
-- | Username     | Password    | Role           |
-- +--------------+-------------+----------------+
-- | branchadmin  | password123 | BRANCH_ADMIN   |
-- | therapist1   | password123 | THERAPIST      |
-- | therapist2   | password123 | THERAPIST      |
-- | supplieradmin| password123 | SUPPLIER_ADMIN |
-- | supplierstaff| password123 | SUPPLIER_STAFF |
-- +--------------+-------------+----------------+
-- ============================================