package velvetglam.dao;

import velvetglam.model.Customer;
import velvetglam.model.Sale;
import velvetglam.model.SaleItem;
import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the sales and sale_items tables.
 *
 * Key design decisions:
 *  - createSale() wraps all inserts and stock updates in a single JDBC
 *    transaction — if anything fails the entire sale is rolled back.
 *  - getSaleHistory() returns Sale objects without their items loaded;
 *    call getSaleItems(saleId) separately when you need the full receipt.
 *
 * Used by: Module 2 (Sales/Billing), Module 4 (Dashboard revenue card)
 */
public class SaleDAO {

    private final CustomerDAO customerDAO = new CustomerDAO();

    // ── CREATE (transactional) ───────────────────────────────

    /**
     * Persists a complete sale in one atomic transaction:
     *  1. INSERT into sales
     *  2. INSERT each SaleItem into sale_items
     *  3. Reduce stock_qty for each product sold
     *  4. Add earned loyalty points to the customer
     *
     * @param sale fully populated Sale with at least one SaleItem
     * @return the generated sale_id, or -1 on failure
     * @throws SQLException           on any DB error (transaction is rolled back)
     * @throws IllegalArgumentException if the cart is empty or a product is
     *                                  out of stock
     */
    public int createSale(Sale sale) throws SQLException {

        if (!sale.hasItems())
            throw new IllegalArgumentException("Cannot create a sale with an empty cart.");

        String insertSale = "INSERT INTO sales "
                          + "(customer_id, staff_id, total_amount, discount, sale_date) "
                          + "VALUES (?, ?, ?, ?, ?)";

        String insertItem = "INSERT INTO sale_items "
                          + "(sale_id, product_id, quantity, unit_price) "
                          + "VALUES (?, ?, ?, ?)";

        String checkStock  = "SELECT stock_qty FROM products WHERE product_id = ? FOR UPDATE";
        String reduceStock = "UPDATE products SET stock_qty = stock_qty - ? "
                           + "WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);  // BEGIN TRANSACTION
            try {
                // 1 ── Insert sale header
                int saleId;
                try (PreparedStatement ps = conn.prepareStatement(
                        insertSale, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt   (1, sale.getCustomerId());
                    ps.setInt   (2, sale.getStaffId());
                    ps.setDouble(3, sale.calculateTotal());
                    ps.setDouble(4, sale.getDiscount());
                    ps.setDate  (5, Date.valueOf(sale.getSaleDate()));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("Failed to retrieve sale_id.");
                        saleId = keys.getInt(1);
                        sale.setSaleId(saleId);
                    }
                }

                // 2 & 3 ── Insert items + reduce stock
                for (SaleItem item : sale.getItems()) {

                    // Check stock availability before reducing
                    try (PreparedStatement psCheck = conn.prepareStatement(checkStock)) {
                        psCheck.setInt(1, item.getProductId());
                        try (ResultSet rs = psCheck.executeQuery()) {
                            if (!rs.next())
                                throw new IllegalArgumentException(
                                    "Product not found: " + item.getProductName());
                            int available = rs.getInt("stock_qty");
                            if (available < item.getQuantity())
                                throw new IllegalArgumentException(
                                    "Insufficient stock for \"" + item.getProductName()
                                    + "\". Available: " + available
                                    + ", Requested: " + item.getQuantity());
                        }
                    }

                    // Insert sale_item row
                    try (PreparedStatement psItem = conn.prepareStatement(
                            insertItem, Statement.RETURN_GENERATED_KEYS)) {
                        psItem.setInt   (1, saleId);
                        psItem.setInt   (2, item.getProductId());
                        psItem.setInt   (3, item.getQuantity());
                        psItem.setDouble(4, item.getUnitPrice());
                        psItem.executeUpdate();
                        try (ResultSet keys = psItem.getGeneratedKeys()) {
                            if (keys.next()) item.setItemId(keys.getInt(1));
                        }
                    }
                    item.setSaleId(saleId);

                    // Reduce stock
                    try (PreparedStatement psStock = conn.prepareStatement(reduceStock)) {
                        psStock.setInt(1, item.getQuantity());
                        psStock.setInt(2, item.getProductId());
                        psStock.executeUpdate();
                    }
                }

                // 4 ── Award loyalty points
                int earnedPts = Customer.calculateEarnedPoints(sale.calculateTotal());
                if (earnedPts > 0) {
                    customerDAO.updateLoyaltyPoints(conn, sale.getCustomerId(), earnedPts);
                }

                conn.commit();  // COMMIT TRANSACTION
                return saleId;

            } catch (Exception ex) {
                conn.rollback();  // ROLLBACK on any failure
                if (ex instanceof SQLException sqle) throw sqle;
                throw new SQLException(ex.getMessage(), ex);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // ── READ ─────────────────────────────────────────────────

    /**
     * Returns all sales, most recent first.
     * Each Sale contains the customer name from a JOIN but items are NOT
     * loaded here — call getSaleItems(saleId) separately for receipt display.
     */
    public List<Sale> getAllSales() throws SQLException {
        return runSaleQuery(
            "SELECT s.sale_id, s.customer_id, c.name AS customer_name, "
          + "       s.staff_id, s.total_amount, s.discount, s.sale_date "
          + "FROM sales s "
          + "JOIN customers c ON s.customer_id = c.customer_id "
          + "ORDER BY s.sale_date DESC, s.sale_id DESC",
            null
        );
    }

    /**
     * Returns all sales for a specific customer, most recent first.
     * @param customerId the customer's PK
     */
    public List<Sale> getSalesByCustomer(int customerId) throws SQLException {
        return runSaleQuery(
            "SELECT s.sale_id, s.customer_id, c.name AS customer_name, "
          + "       s.staff_id, s.total_amount, s.discount, s.sale_date "
          + "FROM sales s "
          + "JOIN customers c ON s.customer_id = c.customer_id "
          + "WHERE s.customer_id = ? "
          + "ORDER BY s.sale_date DESC",
            customerId
        );
    }

    /**
     * Returns all sales on a specific date.
     * @param date the date to filter by
     */
    public List<Sale> getSalesByDate(LocalDate date) throws SQLException {
        String sql =
            "SELECT s.sale_id, s.customer_id, c.name AS customer_name, "
          + "       s.staff_id, s.total_amount, s.discount, s.sale_date "
          + "FROM sales s "
          + "JOIN customers c ON s.customer_id = c.customer_id "
          + "WHERE s.sale_date = ? "
          + "ORDER BY s.sale_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return mapSales(rs);
            }
        }
    }

    /**
     * Loads the individual SaleItem rows for a given sale_id.
     * Call this when you need to display or print a full receipt.
     */
    public List<SaleItem> getSaleItems(int saleId) throws SQLException {
        String sql =
            "SELECT si.item_id, si.sale_id, si.product_id, p.name AS product_name, "
          + "       si.quantity, si.unit_price "
          + "FROM sale_items si "
          + "JOIN products p ON si.product_id = p.product_id "
          + "WHERE si.sale_id = ?";

        List<SaleItem> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new SaleItem(
                        rs.getInt("item_id"),
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                    ));
                }
            }
        }
        return items;
    }

    /**
     * Returns the total revenue (sum of total_amount) for today's date.
     * Used by the Module 4 dashboard.
     */
    public double getTodayRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) "
                   + "FROM sales WHERE sale_date = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    /**
     * Returns the total number of sales transactions ever recorded.
     * Used by the Module 4 dashboard.
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── Helpers ──────────────────────────────────────────────

    /**
     * Runs a parameterised sale SELECT and maps the results.
     * @param sql   query string — may have one ? for an integer parameter
     * @param param the integer value to bind, or null if no parameter
     */
    private List<Sale> runSaleQuery(String sql, Integer param) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            if (param != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return mapSales(rs);
            }
        }
    }

    /** Maps all rows in the ResultSet to Sale objects (no items loaded). */
    private List<Sale> mapSales(ResultSet rs) throws SQLException {
        List<Sale> list = new ArrayList<>();
        while (rs.next()) {
            Date d       = rs.getDate("sale_date");
            Sale sale    = new Sale(
                rs.getInt   ("sale_id"),
                rs.getInt   ("customer_id"),
                rs.getInt   ("staff_id"),
                rs.getDouble("discount"),
                (d != null) ? d.toLocalDate() : LocalDate.now()
            );
            sale.setCustomerName(rs.getString("customer_name"));
            // Store the DB's saved total_amount so the history table can display it
            // without needing to re-fetch and sum all sale_items for every row.
            sale.setStoredTotal(rs.getDouble("total_amount"));
            list.add(sale);
        }
        return list;
    }
}
