# VelvetGlam
A Smart Cosmetic and Makeup Management Store made using Java and MySQL. This was made for BSCS 2nd semester OOP project.

Project Description:
================================================================
  VelvetGlam — Module 4: System Integration & Dashboard
  Group 10 | OOP Lab | Semester 2 | Air University
================================================================

WHAT THIS MODULE CONTAINS
--------------------------
Module 4 is the final integration module. It wires together
all three previous modules into one complete working application.

New files written for Module 4:
  • VelvetGlamApp.java      — Main entry point (run this!)
  • MainAppFrame.java       — Main window with sidebar navigation
  • DashboardPanel.java     — Live stats dashboard with charts
  • DashboardDAO.java       — Queries all tables for dashboard metrics
  • velvetglam_schema.sql   — Full DB schema (all 8 tables)
  • velvetglam_seed.sql     — Sample data for all modules

All existing files (models, DAOs, panels from Modules 1-3)
are copied into this project so it compiles independently.


HOW TO RUN
----------
1. Start XAMPP and make sure MySQL is running.

2. Open phpMyAdmin (http://localhost/phpmyadmin).

3. Run velvetglam_schema.sql  (creates the database + all tables).

4. Run velvetglam_seed.sql    (inserts sample data).

5. Open VelvetGlam-Module4 folder in IntelliJ IDEA as a Maven project.

6. Wait for Maven to download the MySQL connector dependency.

7. Run: velvetglam.ui.module4.VelvetGlamApp

8. Login with:
     Username: admin      Password: admin123   (Manager - full access)
     Username: sufyan     Password: pass1234   (Cashier - limited access)
     Username: hammad     Password: pass5678   (Cashier - limited access)


FEATURES IN MODULE 4
---------------------
DASHBOARD (DashboardPanel):
  ✅ Total Products card
  ✅ Low Stock Items card (highlighted in red if > 0)
  ✅ Total Customers card
  ✅ Today's Revenue card
  ✅ Total Sales card
  ✅ Total Revenue card
  ✅ Staff Members card
  ✅ Brands card
  ✅ Weekly Revenue bar chart (last 7 days)
  ✅ Products by Category horizontal bar chart
  ✅ Top 5 Selling Products table
  ✅ Refresh button (reloads all stats live)

SIDEBAR NAVIGATION (MainAppFrame):
  ✅ Dashboard link
  ✅ Products & Inventory  → Module 1 panel (Manager only)
  ✅ Customers & Sales     → Module 2 panel (all roles)
  ✅ Staff Management      → Module 3 panel (Manager only)
  ✅ Logout (returns to login screen)

ROLE-BASED ACCESS:
  ✅ Manager sees all 4 panels
  ✅ Cashier only sees Dashboard + Customers & Sales
  ✅ Sidebar buttons hidden for unauthorized sections

INTEGRATION:
  ✅ Login from Module 3 reused as the app entry gate
  ✅ All Module 1, 2, 3 panels embedded without modification
  ✅ Database connection tested before any UI shown
  ✅ Logout re-shows login screen without restarting app


OOP CONCEPTS IN MODULE 4
-------------------------
- Object Interaction : DashboardPanel uses DashboardDAO which queries
                       products, sales, customers, staff (all modules)
- Abstraction        : DatabaseConnection hides all JDBC setup
- Polymorphism       : UserAccount.getRole() drives conditional display
- Encapsulation      : all fields private; access via methods only
- Exception Handling : DB connection checked at startup with friendly
                       error dialog; all DAO calls wrapped in try/catch


DATABASE TABLES USED
--------------------
Module 4 interacts with ALL 8 tables:
  brands, categories, products, customers, sales, sale_items, staff, users


PROJECT STRUCTURE
-----------------
VelvetGlam-Module4/
├── pom.xml
├── velvetglam_schema.sql     ← Run this first in phpMyAdmin
├── velvetglam_seed.sql       ← Run this second in phpMyAdmin
└── src/main/java/velvetglam/
    ├── util/
    │   └── DatabaseConnection.java
    ├── model/
    │   ├── Person.java, Staff.java, Manager.java, Cashier.java
    │   ├── UserAccount.java
    │   ├── Product.java, LipProduct.java, EyeProduct.java, ...
    │   ├── Brand.java, Category.java, ProductFactory.java
    │   ├── Customer.java
    │   ├── Sale.java, SaleItem.java, Billable.java
    ├── dao/
    │   ├── ProductDAO.java, BrandDAO.java, CategoryDAO.java
    │   ├── CustomerDAO.java, SaleDAO.java
    │   ├── StaffDAO.java, UserDAO.java
    │   └── DashboardDAO.java          ← NEW in Module 4
    └── ui/
        ├── module1/  (Module1Panel, ProductFormDialog, BrandFormDialog)
        ├── module2/  (Module2Panel, CustomerFormDialog, SaleReceiptDialog)
        ├── module3/  (Module3Panel, LoginDialog, StaffFormDialog, ChangePasswordDialog)
        └── module4/                   ← NEW in Module 4
            ├── VelvetGlamApp.java     ← MAIN CLASS
            ├── MainAppFrame.java
            └── DashboardPanel.java

================================================================
