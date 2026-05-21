-- ============================================================
--  VelvetGlam — Complete Database Schema
--  velvetglam_db
--  Module 4: Integration & Dashboard
-- ============================================================
--
--  Run this file in MySQL Workbench or phpMyAdmin (XAMPP)
--  BEFORE running the application.
--
--  This script creates the database and all 8 tables with
--  proper foreign key relationships.
-- ============================================================

-- Create & select the database
CREATE DATABASE IF NOT EXISTS velvetglam_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE velvetglam_db;

-- ── Drop tables in reverse FK dependency order ────────────────
DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS brands;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS staff;

-- ── 1. brands ─────────────────────────────────────────────────
--  Used by: Module 1, Module 4
CREATE TABLE brands (
    brand_id         INT          NOT NULL AUTO_INCREMENT,
    name             VARCHAR(100) NOT NULL,
    supplier_contact VARCHAR(50),
    country          VARCHAR(50),
    PRIMARY KEY (brand_id)
) ENGINE=InnoDB;

-- ── 2. categories ─────────────────────────────────────────────
--  Used by: Module 1, Module 4
CREATE TABLE categories (
    category_id INT         NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL,
    PRIMARY KEY (category_id)
) ENGINE=InnoDB;

-- ── 3. products ───────────────────────────────────────────────
--  Used by: Module 1, Module 2, Module 4
CREATE TABLE products (
    product_id  INT           NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL,
    brand_id    INT,
    category_id INT,
    price       DOUBLE        NOT NULL DEFAULT 0,
    stock_qty   INT           NOT NULL DEFAULT 0,
    shade       VARCHAR(50),
    description TEXT,
    PRIMARY KEY (product_id),
    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id)    REFERENCES brands(brand_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories(category_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ── 4. customers ──────────────────────────────────────────────
--  Used by: Module 2, Module 4
CREATE TABLE customers (
    customer_id     INT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    contact         VARCHAR(20),
    email           VARCHAR(100),
    loyalty_points  INT          NOT NULL DEFAULT 0,
    date_registered DATE         NOT NULL,
    PRIMARY KEY (customer_id)
) ENGINE=InnoDB;

-- ── 5. staff ─────────────────────────────────────────────────
--  Used by: Module 3, Module 4
CREATE TABLE staff (
    staff_id INT          NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL,   -- 'Manager' or 'Cashier'
    contact  VARCHAR(20),
    salary   DOUBLE       NOT NULL DEFAULT 0,
    PRIMARY KEY (staff_id)
) ENGINE=InnoDB;

-- ── 6. users ─────────────────────────────────────────────────
--  Used by: Module 3, Module 4
CREATE TABLE users (
    user_id  INT          NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL,   -- mirrors staff.role
    staff_id INT,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_user_staff
        FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ── 7. sales ─────────────────────────────────────────────────
--  Used by: Module 2, Module 4
CREATE TABLE sales (
    sale_id      INT    NOT NULL AUTO_INCREMENT,
    customer_id  INT,
    staff_id     INT,
    total_amount DOUBLE NOT NULL DEFAULT 0,
    discount     DOUBLE NOT NULL DEFAULT 0,
    sale_date    DATE   NOT NULL,
    PRIMARY KEY (sale_id),
    CONSTRAINT fk_sale_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_sale_staff
        FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ── 8. sale_items ─────────────────────────────────────────────
--  Used by: Module 2, Module 4
CREATE TABLE sale_items (
    item_id    INT    NOT NULL AUTO_INCREMENT,
    sale_id    INT    NOT NULL,
    product_id INT,
    quantity   INT    NOT NULL DEFAULT 1,
    unit_price DOUBLE NOT NULL DEFAULT 0,
    PRIMARY KEY (item_id),
    CONSTRAINT fk_item_sale
        FOREIGN KEY (sale_id)    REFERENCES sales(sale_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_item_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
--  Verify
-- ============================================================
SHOW TABLES;
