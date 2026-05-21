package velvetglam.dao;

import velvetglam.model.Product;
import velvetglam.model.ProductFactory;
import velvetglam.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the products table.
 * All queries JOIN brands and categories so returned Product objects already
 * carry brandName and categoryName for display — no extra lookups needed in the UI.
 */
public class ProductDAO {

    // Common SELECT used by all read methods
    private static final String BASE_SELECT =
        "SELECT p.product_id, p.name, p.brand_id, p.category_id, "
      + "       p.price, p.stock_qty, p.shade, p.description, "
      + "       b.name AS brand_name, c.name AS category_name "
      + "FROM   products  p "
      + "JOIN   brands    b ON p.brand_id    = b.brand_id "
      + "JOIN   categories c ON p.category_id = c.category_id ";

    // ── READ ─────────────────────────────────────────────────

    /** Returns all products ordered by name. */
    public List<Product> getAllProducts() throws SQLException {
        return runQuery(BASE_SELECT + "ORDER BY p.name", null);
    }

    /**
     * Searches products by keyword (matches product name OR brand name)
     * and/or by category.
     *
     * @param keyword    partial name / brand (null or blank = skip)
     * @param categoryId filter by category (0 = all categories)
     */
    public List<Product> searchProducts(String keyword, int categoryId) throws SQLException {
        StringBuilder sql   = new StringBuilder(BASE_SELECT + "WHERE 1=1 ");
        List<Object>  params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (p.name LIKE ? OR b.name LIKE ?) ");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        if (categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        sql.append("ORDER BY p.name");

        return runQuery(sql.toString(), params);
    }

    /**
     * Returns products whose stock_qty is below LOW_STOCK_THRESHOLD.
     * Used by the dashboard in Module 4 and the alert label in Module 1.
     */
    public List<Product> getLowStockProducts() throws SQLException {
        return runQuery(
            BASE_SELECT + "WHERE p.stock_qty < " + Product.LOW_STOCK_THRESHOLD
                        + " ORDER BY p.stock_qty ASC",
            null
        );
    }

    // ── CREATE ───────────────────────────────────────────────

    /**
     * Inserts a new product.
     * @return true if the row was inserted
     */
    public boolean addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products "
                   + "(name, brand_id, category_id, price, stock_qty, shade, description) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            bindProductFields(ps, p);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) p.setProductId(keys.getInt(1));
                }
            }
            return rows > 0;
        }
    }

    // ── UPDATE ───────────────────────────────────────────────

    /** Updates an existing product record. */
    public boolean updateProduct(Product p) throws SQLException {
        String sql = "UPDATE products "
                   + "SET name=?, brand_id=?, category_id=?, price=?, "
                   + "    stock_qty=?, shade=?, description=? "
                   + "WHERE product_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindProductFields(ps, p);
            ps.setInt(8, p.getProductId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ───────────────────────────────────────────────

    /** Deletes a product by its primary key. */
    public boolean deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Helpers ──────────────────────────────────────────────

    /**
     * Executes a SELECT query and maps every row to the correct Product subclass
     * via ProductFactory.
     *
     * @param sql    the complete SQL string (may contain ?)
     * @param params ordered list of bind parameters (null if none)
     */
    private List<Product> runQuery(String sql, List<Object> params) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);

            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    Object v = params.get(i);
                    if (v instanceof String  s) ps.setString(i + 1, s);
                    else if (v instanceof Integer n) ps.setInt(i + 1, n);
                    else ps.setObject(i + 1, v);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    /** Maps every row in the ResultSet to a Product instance. */
    private List<Product> mapAll(ResultSet rs) throws SQLException {
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            Product p = ProductFactory.create(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getInt("brand_id"),
                rs.getInt("category_id"),
                rs.getDouble("price"),
                rs.getInt("stock_qty"),
                rs.getString("shade"),
                rs.getString("description"),
                rs.getString("category_name")
            );
            p.setBrandName(rs.getString("brand_name"));
            p.setCategoryName(rs.getString("category_name"));
            list.add(p);
        }
        return list;
    }

    /** Binds product fields to a PreparedStatement for INSERT (positions 1-7). */
    private void bindProductFields(PreparedStatement ps, Product p) throws SQLException {
        ps.setString(1, p.getName());
        ps.setInt   (2, p.getBrandId());
        ps.setInt   (3, p.getCategoryId());
        ps.setDouble(4, p.getPrice());
        ps.setInt   (5, p.getStockQty());
        ps.setString(6, p.getShade());
        ps.setString(7, p.getDescription());
    }
}
