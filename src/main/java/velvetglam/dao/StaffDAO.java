package velvetglam.dao;

import velvetglam.model.Staff;
import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the {@code staff} table.
 *
 * All methods open a fresh connection, execute their query,
 * and close everything via try-with-resources.
 *
 * OOP Concepts Demonstrated:
 *  - Encapsulation  : SQL is hidden behind clean method signatures
 *  - Exception Handling : every SQL error is propagated as SQLException
 *                          so the UI layer can show user-friendly messages
 *  - Database Connectivity : JDBC via DatabaseConnection utility class
 */
public class StaffDAO {

    // ── CREATE ───────────────────────────────────────────────
    /**
     * Inserts a new staff row and sets the generated staffId back on the object.
     *
     * @throws SQLException on any database error
     */
    public void addStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO staff (name, role, contact, salary) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, staff.getName());
            ps.setString(2, staff.getRole());
            ps.setString(3, staff.getContact());
            ps.setDouble(4, staff.getSalary());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) staff.setStaffId(keys.getInt(1));
            }
        }
    }

    // ── READ (ALL) ───────────────────────────────────────────
    /**
     * Returns every staff member, ordered by name.
     */
    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT staff_id, name, role, contact, salary FROM staff ORDER BY name";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    // ── READ (by ID) ─────────────────────────────────────────
    public Staff getStaffById(int staffId) throws SQLException {
        String sql = "SELECT staff_id, name, role, contact, salary FROM staff WHERE staff_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    // ── READ (search) ────────────────────────────────────────
    /**
     * Full-text search across name, role, and contact.
     * Passing an empty/null keyword returns all staff (same as getAllStaff).
     */
    public List<Staff> searchStaff(String keyword) throws SQLException {
        List<Staff> list = new ArrayList<>();
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        String sql = "SELECT staff_id, name, role, contact, salary " +
                     "FROM staff " +
                     "WHERE name LIKE ? OR role LIKE ? OR contact LIKE ? " +
                     "ORDER BY name";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────
    public void updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET name=?, role=?, contact=?, salary=? WHERE staff_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, staff.getName());
            ps.setString(2, staff.getRole());
            ps.setString(3, staff.getContact());
            ps.setDouble(4, staff.getSalary());
            ps.setInt(5, staff.getStaffId());
            ps.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────
    /**
     * Deletes the staff record with the given ID.
     * The matching users row is deleted automatically via CASCADE in MySQL.
     */
    public void deleteStaff(int staffId) throws SQLException {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.executeUpdate();
        }
    }

    // ── COUNTS ───────────────────────────────────────────────
    /** Returns the total number of staff rows (used by the dashboard). */
    public int getTotalStaffCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getManagerCount() throws SQLException {
        return countByRole(Staff.ROLE_MANAGER);
    }

    public int getCashierCount() throws SQLException {
        return countByRole(Staff.ROLE_CASHIER);
    }

    private int countByRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM staff WHERE role = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // ── Private Mapper ───────────────────────────────────────
    private Staff map(ResultSet rs) throws SQLException {
        return new Staff(
            rs.getInt("staff_id"),
            rs.getString("name"),
            rs.getString("role"),
            rs.getString("contact"),
            rs.getDouble("salary")
        );
    }
}
