package velvetglam.dao;

import velvetglam.model.UserAccount;
import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the {@code users} table.
 *
 * Handles login authentication, user creation, and credential management.
 *
 * OOP Concepts Demonstrated:
 *  - Encapsulation  : SQL + credential logic hidden behind clean API
 *  - Exception Handling : SQL and duplicate-username errors surfaced clearly
 *  - Database Connectivity : JDBC via DatabaseConnection
 */
public class UserDAO {

    // ── LOGIN AUTHENTICATION ─────────────────────────────────
    /**
     * Attempts to authenticate a user.
     *
     * @param username plain-text username
     * @param password plain-text password
     * @return the matching UserAccount, or {@code null} if credentials are wrong
     * @throws SQLException on any database error
     */
    public UserAccount authenticate(String username, String password) throws SQLException {
        if (username == null || username.isBlank() ||
            password == null || password.isBlank()) {
            return null;
        }

        String sql = "SELECT user_id, username, password, role, staff_id " +
                     "FROM users WHERE username = ? AND password = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;   // credentials did not match
    }

    // ── CREATE ───────────────────────────────────────────────
    /**
     * Inserts a new user row linked to a staff member.
     *
     * @throws SQLException          on database error
     * @throws IllegalStateException if the username is already taken
     */
    public void addUser(UserAccount user) throws SQLException {
        if (usernameExists(user.getUsername())) {
            throw new IllegalStateException(
                "Username '" + user.getUsername() + "' is already taken. Please choose another.");
        }

        String sql = "INSERT INTO users (username, password, role, staff_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getStaffId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setUserId(keys.getInt(1));
            }
        }
    }

    // ── READ ─────────────────────────────────────────────────
    public UserAccount getUserByStaffId(int staffId) throws SQLException {
        String sql = "SELECT user_id, username, password, role, staff_id " +
                     "FROM users WHERE staff_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<UserAccount> getAllUsers() throws SQLException {
        List<UserAccount> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, staff_id FROM users ORDER BY username";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────
    /**
     * Updates username, password, and role for an existing user.
     * If username is being changed, checks the new name is not already taken
     * by a DIFFERENT user.
     */
    public void updateUser(UserAccount user) throws SQLException {
        // Check uniqueness only if name actually changed
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND user_id <> ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setInt(2, user.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalStateException(
                        "Username '" + user.getUsername() + "' is already taken.");
                }
            }
        }

        String upd = "UPDATE users SET username=?, password=?, role=? WHERE user_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(upd)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getUserId());
            ps.executeUpdate();
        }
    }

    // ── DELETE ───────────────────────────────────────────────
    public void deleteUserByStaffId(int staffId) throws SQLException {
        String sql = "DELETE FROM users WHERE staff_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.executeUpdate();
        }
    }

    // ── HELPERS ──────────────────────────────────────────────
    /**
     * Returns true if the username already exists in the users table.
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ── Mapper ───────────────────────────────────────────────
    private UserAccount map(ResultSet rs) throws SQLException {
        return new UserAccount(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getInt("staff_id")
        );
    }
}
