-- ============================================================
--  VelvetGlam — Full Seed Data
--  Module 4: Integration & Dashboard
-- ============================================================
--
--  Run this AFTER velvetglam_schema.sql
--  This seeds all tables so the dashboard has real data to show.
-- ============================================================

USE velvetglam_db;

-- ── Brands ────────────────────────────────────────────────────
INSERT INTO brands (name, supplier_contact, country) VALUES
('MAC',          'mac@supplier.com',        'USA'),
('Maybelline',   'maybelline@supplier.com',  'USA'),
('L\'Oreal',     'loreal@supplier.com',      'France'),
('Neutrogena',   'neutro@supplier.com',      'USA'),
('Dove',         'dove@supplier.com',        'UK'),
('Lakme',        'lakme@supplier.com',       'India');

-- ── Categories ────────────────────────────────────────────────
INSERT INTO categories (name) VALUES
('Lip'),
('Eye'),
('Face'),
('Skin'),
('Hair'),
('Fragrance');

-- ── Products ──────────────────────────────────────────────────
INSERT INTO products (name, brand_id, category_id, price, stock_qty, shade, description) VALUES
-- Lip
('Ruby Lipstick',        1, 1,  850.00, 15, 'Ruby Red',     'Long-lasting matte lipstick'),
('Pink Gloss',           2, 1,  450.00,  3, 'Baby Pink',    'Shiny lip gloss — low stock!'),
('Nude Liner',           3, 1,  350.00,  8, 'Nude',         'Precise lip liner pencil'),
-- Eye
('Volumizing Mascara',   2, 2,  750.00, 12, 'Black',        'Lengthening and volumizing'),
('Smoky Eyeshadow',      1, 2, 1200.00,  4, 'Smoky Brown',  'Palette of 12 neutral shades'),
('Liquid Eyeliner',      3, 2,  550.00, 20, 'Jet Black',    'Precision tip liquid liner'),
-- Face
('HD Foundation',        1, 3, 2200.00,  6, 'Shade 03',     'Full coverage liquid foundation'),
('Compact Powder',       2, 3,  900.00,  2, 'Beige',        'Oil-control setting powder — low stock!'),
('Blush On',             6, 3,  700.00, 10, 'Peach Glow',   'Buildable blush for cheeks'),
-- Skin
('Moisturizing Cream',   4, 4,  600.00, 18, '',             '24hr hydrating cream'),
('Vitamin C Serum',      4, 4, 1500.00,  9, '',             'Brightening serum with 10% Vit C'),
('Sunscreen SPF50',      4, 4,  850.00,  0, '',             'Broad spectrum — OUT OF STOCK'),
-- Hair
('Argan Shampoo',        5, 5,  750.00, 11, '',             'Nourishing argan oil shampoo'),
('Deep Conditioner',     5, 5,  650.00,  7, '',             'Intense moisture mask'),
-- Fragrance
('Rose Perfume 50ml',    1, 6, 3500.00,  5, '',             'Floral eau de parfum');

-- ── Staff ─────────────────────────────────────────────────────
INSERT INTO staff (name, role, contact, salary) VALUES
('Amina Tanzil',      'Manager', '0300-1111111', 65000.00),
('Sufyan Kamran',     'Cashier', '0300-2222222', 35000.00),
('Hammad Ali',        'Cashier', '0300-3333333', 35000.00);

-- ── Users (login accounts) ────────────────────────────────────
INSERT INTO users (username, password, role, staff_id) VALUES
('admin',    'admin123',  'Manager', 1),
('sufyan',   'pass1234',  'Cashier', 2),
('hammad',   'pass5678',  'Cashier', 3);

-- ── Customers ─────────────────────────────────────────────────
INSERT INTO customers (name, contact, email, loyalty_points, date_registered) VALUES
('Sara Ahmed',     '0321-9999001', 'sara@email.com',   120, '2025-01-15'),
('Fatima Khan',    '0322-9999002', 'fatima@email.com',  85, '2025-03-10'),
('Zara Malik',     '0333-9999003', 'zara@email.com',    45, '2025-06-20'),
('Ayesha Siddiqui','0311-9999004', 'ayesha@email.com', 200, '2025-08-05'),
('Nida Rafiq',     '0312-9999005', '',                  30, '2026-01-12');

-- ── Sales (spread across last 7 days for the weekly chart) ────
INSERT INTO sales (customer_id, staff_id, total_amount, discount, sale_date) VALUES
(1, 2, 2700.00, 200.00, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
(2, 3, 1500.00,   0.00, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(3, 2, 3200.00, 300.00, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
(1, 3,  900.00,   0.00, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(4, 2, 5500.00, 500.00, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(5, 3, 2100.00, 100.00, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(2, 2, 1800.00,   0.00, CURDATE()),
(4, 3, 4400.00, 400.00, CURDATE());

-- ── Sale Items (linking sales to products) ────────────────────
INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) VALUES
-- Sale 1
(1, 1, 2,  850.00), (1, 4, 1,  750.00), (1, 10, 1,  600.00),
-- Sale 2
(2, 6, 1,  550.00), (2, 9, 1,  700.00), (2, 3, 1,  350.00),
-- Sale 3
(3, 7, 1, 2200.00), (3, 5, 1, 1200.00),
-- Sale 4
(4, 8, 1,  900.00),
-- Sale 5
(5, 15,1, 3500.00), (5, 11,1, 1500.00), (5, 7, 1, 2200.00),
-- Sale 6
(6, 13,1,  750.00), (6, 14,1,  650.00), (6, 1, 1,  850.00),
-- Sale 7
(7, 2, 2,  450.00), (7, 6, 1,  550.00), (7, 3, 2,  350.00),
-- Sale 8
(8, 7, 1, 2200.00), (8, 11,1, 1500.00), (8, 1, 1,  850.00);

-- ── Verify counts ─────────────────────────────────────────────
SELECT 'brands'     AS tbl, COUNT(*) AS rows FROM brands    UNION ALL
SELECT 'categories',        COUNT(*)         FROM categories UNION ALL
SELECT 'products',          COUNT(*)         FROM products   UNION ALL
SELECT 'staff',             COUNT(*)         FROM staff      UNION ALL
SELECT 'users',             COUNT(*)         FROM users      UNION ALL
SELECT 'customers',         COUNT(*)         FROM customers  UNION ALL
SELECT 'sales',             COUNT(*)         FROM sales      UNION ALL
SELECT 'sale_items',        COUNT(*)         FROM sale_items;
