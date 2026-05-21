package velvetglam.dao;

import velvetglam.model.Category;
import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the categories table.
 * Module 1 uses this to populate the category dropdown in the product form.
 * Module 2 also imports this class for filtering sales by category.
 */
public class CategoryDAO {

    /**
     * Returns all categories ordered alphabetically.
     */
    public List<Category> getAllCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, name FROM categories ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("name")
                ));
            }
        }
        return list;
    }

    /**
     * Looks up a single category by its primary key.
     * Returns null if not found.
     */
    public Category findById(int categoryId) throws SQLException {
        String sql = "SELECT category_id, name FROM categories WHERE category_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getInt("category_id"), rs.getString("name"));
                }
            }
        }
        return null;
    }
}
