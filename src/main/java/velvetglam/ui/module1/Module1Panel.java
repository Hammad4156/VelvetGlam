package velvetglam.ui.module1;

import velvetglam.dao.BrandDAO;
import velvetglam.dao.CategoryDAO;
import velvetglam.dao.ProductDAO;
import velvetglam.model.Brand;
import velvetglam.model.Category;
import velvetglam.model.Product;

import javax.swing.*;
import velvetglam.util.VGTheme;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Module 1 — Product & Inventory Management Panel.
 *
 * This JPanel is designed to be embedded in the main application frame
 * (Module 4 dashboard) or run standalone via Module1Launcher for testing.
 *
 * Layout:
 *   ┌─────────────────────────────────────────────────────┐
 *   │  Header bar (purple)                                │
 *   ├─────────────────────────────────────────────────────┤
 *   │  JTabbedPane                                        │
 *   │    Tab 1 — 📦 Products                              │
 *   │      [Search bar + category filter]                 │
 *   │      [Products JTable — low-stock rows highlighted] │
 *   │      [Low-stock alert label]  [CRUD buttons]        │
 *   │    Tab 2 — 🏷️ Brands                               │
 *   │      [Brands JTable]                                │
 *   │      [CRUD buttons]                                 │
 *   └─────────────────────────────────────────────────────┘
 */
public class Module1Panel extends JPanel {

    // ── Colour Palette ───────────────────────────────────────
    private static final Color C_PRIMARY   = new Color(108,  52, 168);
    private static final Color C_LOW_STOCK = new Color(255, 205, 210);   // soft red
    private static final Color C_SELECT    = new Color(206, 147, 216);   // light purple
    private static final Color C_BG        = new Color(250, 248, 255);
    private static final Color C_ADD       = new Color( 56, 142,  60);
    private static final Color C_EDIT      = new Color( 25, 118, 210);
    private static final Color C_DEL       = new Color(211,  47,  47);
    private static final Color C_MISC      = new Color( 69,  90, 100);

    // ── DAOs ─────────────────────────────────────────────────
    private final ProductDAO  productDAO  = new ProductDAO();
    private final BrandDAO    brandDAO    = new BrandDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    // ── Product Tab ──────────────────────────────────────────
    private DefaultTableModel productModel;
    private JTable            productTable;
    private JTextField        tfSearch;
    private JComboBox<Category> cbCatFilter;
    private JLabel            lblLowStock;

    // ── Brand Tab ────────────────────────────────────────────
    private DefaultTableModel brandModel;
    private JTable            brandTable;

    // ─────────────────────────────────────────────────────────
    public Module1Panel() {
        setLayout(new BorderLayout());
        setBackground(C_BG);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTabs(),    BorderLayout.CENTER);

        loadProducts();
        loadBrands();
    }

    // ══════════════════════════════════════════════════════════
    //  HEADER
    // ══════════════════════════════════════════════════════════

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel title = new JLabel("📦   Module 1 — Product & Inventory Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("VelvetGlam  |  Sufyan Kamran  ");
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sub.setForeground(new Color(220, 200, 255));
        bar.add(sub, BorderLayout.EAST);

        return bar;
    }

    // ══════════════════════════════════════════════════════════
    //  TABS
    // ══════════════════════════════════════════════════════════

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(C_BG);
        tabs.addTab("  📦  Products  ",  buildProductTab());
        tabs.addTab("  🏷️   Brands    ", buildBrandTab());
        return tabs;
    }

    // ══════════════════════════════════════════════════════════
    //  PRODUCT TAB
    // ══════════════════════════════════════════════════════════

    private JPanel buildProductTab() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        p.add(buildSearchBar(),  BorderLayout.NORTH);
        p.add(buildProductTable(), BorderLayout.CENTER);
        p.add(buildProductBottom(), BorderLayout.SOUTH);

        return p;
    }

    // ── Search bar ───────────────────────────────────────────

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setBackground(C_BG);

        tfSearch = new JTextField(16);
        tfSearch.setFont(fieldFont());
        tfSearch.putClientProperty("JTextField.placeholderText", "Search by name or brand…");

        cbCatFilter = new JComboBox<>();
        cbCatFilter.setFont(fieldFont());
        cbCatFilter.addItem(new Category(0, "All Categories"));
        try { categoryDAO.getAllCategories().forEach(cbCatFilter::addItem); }
        catch (SQLException ignored) {}

        JButton btnSearch = actionBtn("🔍  Search", C_PRIMARY);
        JButton btnReset  = actionBtn("↺  Reset",   C_MISC);

        btnSearch.addActionListener(e -> searchProducts());
        btnReset.addActionListener(e -> {
            tfSearch.setText("");
            cbCatFilter.setSelectedIndex(0);
            loadProducts();
        });

        bar.add(label("Search:"));
        bar.add(tfSearch);
        bar.add(label("Category:"));
        bar.add(cbCatFilter);
        bar.add(btnSearch);
        bar.add(btnReset);
        return bar;
    }

    // ── Product table ────────────────────────────────────────

    private JScrollPane buildProductTable() {
        String[] cols = {"ID", "Name", "Brand", "Category",
                         "Price (PKR)", "Stock", "Shade / Size", "Description"};
        productModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 0 || c == 5) ? Integer.class : String.class;
            }
        };
        productTable = new JTable(productModel);
        styleTable(productTable);
        productTable.setDefaultRenderer(Object.class, new LowStockRenderer());

        // Column widths
        int[] widths = {45, 165, 110, 90, 105, 55, 90, 200};
        for (int i = 0; i < widths.length; i++)
            productTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        return new JScrollPane(productTable);
    }

    // ── Bottom bar: alert + buttons ──────────────────────────

    private JPanel buildProductBottom() {
        JPanel bottom = new JPanel(new BorderLayout(0, 0));
        bottom.setBackground(C_BG);
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        lblLowStock = new JLabel("⚠️   Low Stock: — ");
        lblLowStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLowStock.setForeground(new Color(183, 28, 28));
        bottom.add(lblLowStock, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        btns.setBackground(C_BG);

        JButton btnAdd     = actionBtn("➕  Add",      C_ADD);
        JButton btnEdit    = actionBtn("✏️  Edit",     C_EDIT);
        JButton btnDelete  = actionBtn("🗑️  Delete",   C_DEL);
        JButton btnRefresh = actionBtn("↺  Refresh",  C_MISC);

        btnAdd.addActionListener(e -> openProductForm(null));
        btnEdit.addActionListener(e -> editSelectedProduct());
        btnDelete.addActionListener(e -> deleteSelectedProduct());
        btnRefresh.addActionListener(e -> loadProducts());

        btns.add(btnAdd); btns.add(btnEdit); btns.add(btnDelete); btns.add(btnRefresh);
        bottom.add(btns, BorderLayout.EAST);
        return bottom;
    }

    // ── Product data logic ───────────────────────────────────

    void loadProducts() {
        productModel.setRowCount(0);
        try {
            List<Product> list = productDAO.getAllProducts();
            for (Product p : list) {
                productModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getBrandName(),
                    p.getCategoryName(),
                    String.format("%.2f", p.getPrice()),
                    p.getStockQty(),
                    p.getShade(),
                    p.getDescription()
                });
            }
            long low = list.stream().filter(Product::isLowStock).count();
            if (low > 0) {
                lblLowStock.setText("⚠️   Low Stock Alert: " + low
                    + " item(s) below threshold (" + Product.LOW_STOCK_THRESHOLD + ")");
                lblLowStock.setForeground(new Color(183, 28, 28));
            } else {
                lblLowStock.setText("✅  All products have sufficient stock.");
                lblLowStock.setForeground(new Color(27, 94, 32));
            }
        } catch (SQLException ex) {
            error("Failed to load products:\n" + ex.getMessage());
        }
    }

    private void searchProducts() {
        productModel.setRowCount(0);
        try {
            String   kw  = tfSearch.getText().trim();
            Category cat = (Category) cbCatFilter.getSelectedItem();
            int catId = (cat != null && cat.getCategoryId() > 0) ? cat.getCategoryId() : 0;

            List<Product> list = productDAO.searchProducts(kw, catId);
            for (Product p : list) {
                productModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getBrandName(),
                    p.getCategoryName(), String.format("%.2f", p.getPrice()),
                    p.getStockQty(), p.getShade(), p.getDescription()
                });
            }
        } catch (SQLException ex) {
            error("Search failed:\n" + ex.getMessage());
        }
    }

    private void openProductForm(Product existing) {
        try {
            List<Brand>    brands = brandDAO.getAllBrands();
            List<Category> cats   = categoryDAO.getAllCategories();
            if (brands.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please add at least one brand before adding products.",
                    "No Brands", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            ProductFormDialog dlg = new ProductFormDialog(
                SwingUtilities.getWindowAncestor(this), existing, brands, cats);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadProducts();
        } catch (SQLException ex) {
            error(ex.getMessage());
        }
    }

    private void editSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) { warn("Please select a product to edit."); return; }
        int id = (int) productModel.getValueAt(row, 0);
        try {
            productDAO.getAllProducts().stream()
                .filter(p -> p.getProductId() == id)
                .findFirst()
                .ifPresent(this::openProductForm);
        } catch (SQLException ex) { error(ex.getMessage()); }
    }

    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) { warn("Please select a product to delete."); return; }
        int    id   = (int)    productModel.getValueAt(row, 0);
        String name = (String) productModel.getValueAt(row, 1);
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete product  \"" + name + "\" ?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            try { productDAO.deleteProduct(id); loadProducts(); }
            catch (SQLException ex) { error(ex.getMessage()); }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  BRAND TAB
    // ══════════════════════════════════════════════════════════

    private JPanel buildBrandTab() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        p.add(buildBrandTable(),   BorderLayout.CENTER);
        p.add(buildBrandButtons(), BorderLayout.SOUTH);
        return p;
    }

    private JScrollPane buildBrandTable() {
        String[] cols = {"ID", "Brand Name", "Supplier Contact", "Country"};
        brandModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        brandTable = new JTable(brandModel);
        styleTable(brandTable);
        int[] widths = {50, 180, 220, 150};
        for (int i = 0; i < widths.length; i++)
            brandTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        return new JScrollPane(brandTable);
    }

    private JPanel buildBrandButtons() {
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        btns.setBackground(C_BG);

        JButton btnAdd     = actionBtn("➕  Add",      C_ADD);
        JButton btnEdit    = actionBtn("✏️  Edit",     C_EDIT);
        JButton btnDelete  = actionBtn("🗑️  Delete",   C_DEL);
        JButton btnRefresh = actionBtn("↺  Refresh",  C_MISC);

        btnAdd.addActionListener(e -> openBrandForm(null));
        btnEdit.addActionListener(e -> editSelectedBrand());
        btnDelete.addActionListener(e -> deleteSelectedBrand());
        btnRefresh.addActionListener(e -> loadBrands());

        btns.add(btnAdd); btns.add(btnEdit); btns.add(btnDelete); btns.add(btnRefresh);
        return btns;
    }

    void loadBrands() {
        brandModel.setRowCount(0);
        try {
            brandDAO.getAllBrands().forEach(b -> brandModel.addRow(new Object[]{
                b.getBrandId(), b.getName(), b.getSupplierContact(), b.getCountry()
            }));
        } catch (SQLException ex) { error(ex.getMessage()); }
    }

    private void openBrandForm(Brand existing) {
        BrandFormDialog dlg = new BrandFormDialog(
            SwingUtilities.getWindowAncestor(this), existing);
        dlg.setVisible(true);
        if (dlg.isSaved()) { loadBrands(); loadProducts(); }
    }

    private void editSelectedBrand() {
        int row = brandTable.getSelectedRow();
        if (row < 0) { warn("Please select a brand to edit."); return; }
        Brand b = new Brand(
            (int)    brandModel.getValueAt(row, 0),
            (String) brandModel.getValueAt(row, 1),
            (String) brandModel.getValueAt(row, 2),
            (String) brandModel.getValueAt(row, 3)
        );
        openBrandForm(b);
    }

    private void deleteSelectedBrand() {
        int row = brandTable.getSelectedRow();
        if (row < 0) { warn("Please select a brand to delete."); return; }
        int    id   = (int)    brandModel.getValueAt(row, 0);
        String name = (String) brandModel.getValueAt(row, 1);
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete brand  \"" + name + "\"?\nAll linked products will also be removed.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            try { brandDAO.deleteBrand(id); loadBrands(); loadProducts(); }
            catch (SQLException ex) { error(ex.getMessage()); }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  SHARED HELPERS
    // ══════════════════════════════════════════════════════════

    private void styleTable(JTable t) {
        VGTheme.styleTable(t);
    }

    private JButton actionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private Font fieldFont() { return new Font("Segoe UI", Font.PLAIN, 13); }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    // ── Custom cell renderer — highlights low-stock rows ─────
    private class LowStockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, col);

            if (!isSelected) {
                try {
                    Object sv = productModel.getValueAt(row, 5);  // Stock column
                    int stock = Integer.parseInt(sv.toString());
                    c.setBackground(stock < Product.LOW_STOCK_THRESHOLD
                        ? C_LOW_STOCK : Color.WHITE);
                } catch (NumberFormatException e) {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
}
