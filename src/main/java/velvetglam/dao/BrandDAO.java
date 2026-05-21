package velvetglam.dao;

import velvetglam.model.Brand;
import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the brands table.
 * Supports full CRUD: list, add, update, delete.
 */
public class BrandDAO {

    // ── READ ─────────────────────────────────────────────────

    /** Returns all brands ordered by name. */
    public List<Brand> getAllBrands() throws SQLException {
        List<Brand> list = new ArrayList<>();
        String sql = "SELECT brand_id, name, supplier_contact, country "
                   + "FROM brands ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Finds a brand by its primary key. Returns null if not found. */
    public Brand findById(int brandId) throws SQLException {
        String sql = "SELECT brand_id, name, supplier_contact, country "
                   + "FROM brands WHERE brand_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, brandId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── CREATE ───────────────────────────────────────────────

    /**
     * Inserts a new brand.
     * @return true if the row was inserted successfully
     * @throws SQLException if the brand name is duplicate (UNIQUE constraint)
     */
    public boolean addBrand(Brand brand) throws SQLException {
        String sql = "INSERT INTO brands (name, supplier_contact, country) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, brand.getName());
            ps.setString(2, brand.getSupplierContact());
            ps.setString(3, brand.getCountry());
            int rows = ps.executeUpdate();

            // Write the generated PK back onto the Brand object
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) brand.setBrandId(keys.getInt(1));
                }
            }
            return rows > 0;
        }
    }

    // ── UPDATE ───────────────────────────────────────────────

    /**
     * Updates an existing brand record.
     * @return true if at least one row was updated
     */
    public boolean updateBrand(Brand brand) throws SQLException {
        String sql = "UPDATE brands SET name = ?, supplier_contact = ?, country = ? "
                   + "WHERE brand_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, brand.getName());
            ps.setString(2, brand.getSupplierContact());
            ps.setString(3, brand.getCountry());
            ps.setInt(4, brand.getBrandId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ───────────────────────────────────────────────

    /**
     * Deletes a brand by ID.
     * Note: linked products will be deleted automatically if ON DELETE CASCADE
     * is set on the FK, otherwise the DB will throw a constraint violation.
     *
     * @return true if the row was deleted
     */
    public boolean deleteBrand(int brandId) throws SQLException {
        String sql = "DELETE FROM brands WHERE brand_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, brandId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helper ───────────────────────────────────────────────

    private Brand mapRow(ResultSet rs) throws SQLException {
        return new Brand(
            rs.getInt("brand_id"),
            rs.getString("name"),
            rs.getString("supplier_contact"),
            rs.getString("country")
        );
    }
}
