package velvetglam.dao;

import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data Access Object that powers the Module 4 Dashboard.
 *
 * Provides aggregated stats by querying across ALL tables in velvetglam_db.
 * This class is the only DAO in the system that spans multiple modules' tables,
 * which is exactly what makes it belong to Module 4 (Integration).
 *
 * OOP Concepts Demonstrated:
 *  - Abstraction     : DatabaseConnection hides JDBC boilerplate
 *  - Encapsulation   : all SQL is private; callers see only typed return values
 *  - Exception Handling : every method propagates SQLException for the UI to handle
 *  - Object Interaction : queries products, sales, customers, staff — all modules
 */
public class DashboardDAO {

    // ── PRODUCT STATS (Module 1) ─────────────────────────────

    /**
     * Total number of product rows in the products table.
     */
    public int getTotalProducts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Number of products whose stock_qty is below the low-stock threshold (5).
     */
    public int getLowStockCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE stock_qty < 5";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Total number of distinct brands registered.
     */
    public int getTotalBrands() throws SQLException {
        String sql = "SELECT COUNT(*) FROM brands";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── CUSTOMER & SALES STATS (Module 2) ────────────────────

    /**
     * Total number of registered customers.
     */
    public int getTotalCustomers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Revenue (total_amount - discount) from sales made TODAY.
     */
    public double getTodayRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount - discount), 0) "
                   + "FROM sales WHERE sale_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /**
     * Total revenue across all sales ever recorded.
     */
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount - discount), 0) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /**
     * Total number of sales transactions ever made.
     */
    public int getTotalSales() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Number of sales made today.
     */
    public int getTodaySalesCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales WHERE sale_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── STAFF STATS (Module 3) ───────────────────────────────

    /**
     * Total number of staff members.
     */
    public int getTotalStaff() throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Number of staff with the Manager role.
     */
    public int getManagerCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff WHERE role = 'Manager'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── CHART DATA ───────────────────────────────────────────

    /**
     * Returns daily revenue for the last 7 days (inclusive of today),
     * ordered from oldest to newest.
     *
     * Key   = date string "dd MMM" (e.g. "14 May")
     * Value = net revenue on that day
     */
    public Map<String, Double> getWeeklyRevenue() throws SQLException {
        String sql = "SELECT DATE(sale_date) AS day, "
                   + "       COALESCE(SUM(total_amount - discount), 0) AS revenue "
                   + "FROM sales "
                   + "WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) "
                   + "GROUP BY DATE(sale_date) "
                   + "ORDER BY day ASC";

        Map<String, Double> result = new LinkedHashMap<>();

        // Pre-fill all 7 days with 0 so missing days show as gaps
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            result.put(String.format("%02d %s", d.getDayOfMonth(),
                    d.getMonth().getDisplayName(
                            java.time.format.TextStyle.SHORT,
                            java.util.Locale.ENGLISH)), 0.0);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate d = rs.getDate("day").toLocalDate();
                String key  = String.format("%02d %s", d.getDayOfMonth(),
                        d.getMonth().getDisplayName(
                                java.time.format.TextStyle.SHORT,
                                java.util.Locale.ENGLISH));
                result.put(key, rs.getDouble("revenue"));
            }
        }
        return result;
    }

    /**
     * Returns product count per category name.
     * Used for the category distribution bar.
     */
    public Map<String, Integer> getProductsByCategory() throws SQLException {
        String sql = "SELECT c.name, COUNT(p.product_id) AS cnt "
                   + "FROM categories c "
                   + "LEFT JOIN products p ON p.category_id = c.category_id "
                   + "GROUP BY c.name ORDER BY cnt DESC";

        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("name"), rs.getInt("cnt"));
            }
        }
        return result;
    }

    /**
     * Returns the top-5 best-selling products by total quantity sold.
     * Key = product name, Value = total qty sold.
     */
    public Map<String, Integer> getTopSellingProducts() throws SQLException {
        String sql = "SELECT p.name, COALESCE(SUM(si.quantity), 0) AS total_sold "
                   + "FROM products p "
                   + "LEFT JOIN sale_items si ON si.product_id = p.product_id "
                   + "GROUP BY p.product_id, p.name "
                   + "ORDER BY total_sold DESC "
                   + "LIMIT 5";

        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("name"), rs.getInt("total_sold"));
            }
        }
        return result;
    }
}
