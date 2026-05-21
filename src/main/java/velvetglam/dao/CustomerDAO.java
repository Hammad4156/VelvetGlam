package velvetglam.dao;

import velvetglam.model.Customer;
import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the customers table.
 * Provides full CRUD operations plus keyword search and loyalty-points update.
 *
 * Used by: Module 2 (Customer Management & Sales/Billing)
 *          Module 4 (Integration & Dashboard — customer count summary)
 */
public class CustomerDAO {

    // ── READ ─────────────────────────────────────────────────

    /** Returns all customers ordered alphabetically by name. */
    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT customer_id, name, contact, email, "
                   + "       loyalty_points, date_registered "
                   + "FROM customers ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {
            return mapAll(rs);
        }
    }

    /**
     * Searches customers whose name OR contact contains the keyword.
     * @param keyword partial name or contact (null / blank = return all)
     */
    public List<Customer> searchCustomers(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) return getAllCustomers();

        String sql = "SELECT customer_id, name, contact, email, "
                   + "       loyalty_points, date_registered "
                   + "FROM customers "
                   + "WHERE name LIKE ? OR contact LIKE ? "
                   + "ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword.trim() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    /** Finds a customer by their primary key. Returns null if not found. */
    public Customer findById(int customerId) throws SQLException {
        String sql = "SELECT customer_id, name, contact, email, "
                   + "       loyalty_points, date_registered "
                   + "FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /**
     * Returns the total number of registered customers.
     * Used by the Module 4 dashboard summary card.
     */
    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── CREATE ───────────────────────────────────────────────

    /**
     * Inserts a new customer record.
     * @return true if the row was inserted; the customer's ID is updated in place
     */
    public boolean addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers "
                   + "(name, contact, email, loyalty_points, date_registered) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            bindFields(ps, c);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) c.setCustomerId(keys.getInt(1));
                }
            }
            return rows > 0;
        }
    }

    // ── UPDATE ───────────────────────────────────────────────

    /** Updates an existing customer's profile fields. */
    public boolean updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers "
                   + "SET name=?, contact=?, email=?, loyalty_points=?, date_registered=? "
                   + "WHERE customer_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindFields(ps, c);
            ps.setInt(6, c.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Adds the given delta to a customer's existing loyalty points total.
     * Called automatically by SaleDAO after a sale is completed.
     *
     * @param customerId the target customer
     * @param delta      positive = earn points; negative = redeem
     */
    public void updateLoyaltyPoints(Connection conn, int customerId, int delta)
            throws SQLException {
        String sql = "UPDATE customers "
                   + "SET loyalty_points = GREATEST(0, loyalty_points + ?) "
                   + "WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, customerId);
            ps.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────

    /** Deletes a customer by primary key. */
    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helpers ──────────────────────────────────────────────

    /** Maps all rows in the ResultSet to a List&lt;Customer&gt;. */
    private List<Customer> mapAll(ResultSet rs) throws SQLException {
        List<Customer> list = new ArrayList<>();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    /** Maps a single ResultSet row to a Customer object. */
    private Customer mapRow(ResultSet rs) throws SQLException {
        Date d = rs.getDate("date_registered");
        LocalDate reg = (d != null) ? d.toLocalDate() : LocalDate.now();
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("name"),
            rs.getString("contact"),
            rs.getString("email"),
            rs.getInt("loyalty_points"),
            reg
        );
    }

    /**
     * Binds a Customer's fields into a PreparedStatement at positions 1–5.
     * Order: name, contact, email, loyalty_points, date_registered
     */
    private void bindFields(PreparedStatement ps, Customer c) throws SQLException {
        ps.setString(1, c.getName());
        ps.setString(2, c.getContact());
        ps.setString(3, c.getEmail());
        ps.setInt   (4, c.getLoyaltyPoints());
        ps.setDate  (5, Date.valueOf(c.getDateRegistered()));
    }
}
