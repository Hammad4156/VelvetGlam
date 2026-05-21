package velvetglam.ui.module1;

import velvetglam.dao.ProductDAO;
import velvetglam.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Modal dialog for adding a new product or editing an existing one.
 *
 * Validation (IllegalArgumentException from Product constructor) is caught
 * here so the user sees friendly messages instead of stack traces.
 */
public class ProductFormDialog extends JDialog {

    // ── Palette (matches Module1Panel) ───────────────────────
    private static final Color C_PRIMARY = new Color(108, 52, 168);
    private static final Color C_BTN_OK  = new Color(56, 142, 60);
    private static final Color C_BTN_CX  = new Color(120, 120, 120);

    private final ProductDAO productDAO = new ProductDAO();
    private final Product    existing;           // null → add mode
    private boolean          saved = false;

    // ── Form Fields ──────────────────────────────────────────
    private JTextField  tfName, tfPrice, tfStock, tfShade;
    private JTextArea   taDesc;
    private JComboBox<Brand>    cbBrand;
    private JComboBox<Category> cbCategory;

    // ────────────────────────────────────────────────────────
    public ProductFormDialog(Window parent, Product existing,
                             List<Brand> brands, List<Category> categories) {
        super(parent,
              (existing == null ? "➕  Add New Product" : "✏️  Edit Product"),
              ModalityType.APPLICATION_MODAL);
        this.existing = existing;

        setSize(500, 490);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        buildUI(brands, categories);
        if (existing != null) prefill();
    }

    // ── UI Construction ──────────────────────────────────────

    private void buildUI(List<Brand> brands, List<Category> categories) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_PRIMARY, 1, true),
            "  Product Details  ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), C_PRIMARY));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.insets  = new Insets(6, 8, 6, 8);
        g.weightx = 1.0;

        // Row 0 – Name
        tfName = new JTextField(22);
        addFormRow(form, g, 0, "Product Name *", tfName);

        // Row 1 – Brand
        cbBrand = new JComboBox<>(brands.toArray(new Brand[0]));
        styleCombo(cbBrand);
        addFormRow(form, g, 1, "Brand *", cbBrand);

        // Row 2 – Category
        cbCategory = new JComboBox<>(categories.toArray(new Category[0]));
        styleCombo(cbCategory);
        addFormRow(form, g, 2, "Category *", cbCategory);

        // Row 3 – Price
        tfPrice = new JTextField(22);
        addFormRow(form, g, 3, "Price (PKR) *", tfPrice);

        // Row 4 – Stock
        tfStock = new JTextField(22);
        addFormRow(form, g, 4, "Stock Qty *", tfStock);

        // Row 5 – Shade / Size / Scent
        tfShade = new JTextField(22);
        addFormRow(form, g, 5, "Shade / Size / Scent", tfShade);

        // Row 6 – Description (multi-line)
        g.gridx = 0; g.gridy = 6; g.gridwidth = 1;
        form.add(label("Description:"), g);

        taDesc = new JTextArea(3, 22);
        taDesc.setFont(fieldFont());
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        taDesc.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        JScrollPane sp = new JScrollPane(taDesc);
        sp.setPreferredSize(new Dimension(260, 68));
        g.gridx = 1; g.gridwidth = 2;
        form.add(sp, g);

        // Button panel
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnRow.setBackground(Color.WHITE);
        JButton btnCancel = styledBtn("Cancel", C_BTN_CX);
        JButton btnSave   = styledBtn("💾  Save", C_BTN_OK);
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());
        btnRow.add(btnCancel);
        btnRow.add(btnSave);

        setLayout(new BorderLayout(0, 0));
        add(form,   BorderLayout.CENTER);
        add(btnRow, BorderLayout.SOUTH);
    }

    /** Pre-fills the form when editing an existing product. */
    private void prefill() {
        tfName.setText(existing.getName());
        tfPrice.setText(String.valueOf(existing.getPrice()));
        tfStock.setText(String.valueOf(existing.getStockQty()));
        tfShade.setText(existing.getShade());
        taDesc.setText(existing.getDescription());

        for (int i = 0; i < cbBrand.getItemCount(); i++) {
            if (cbBrand.getItemAt(i).getBrandId() == existing.getBrandId()) {
                cbBrand.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cbCategory.getItemCount(); i++) {
            if (cbCategory.getItemAt(i).getCategoryId() == existing.getCategoryId()) {
                cbCategory.setSelectedIndex(i);
                break;
            }
        }
    }

    // ── Save Logic ───────────────────────────────────────────

    private void save() {
        try {
            // ── Input parsing & validation ────────────────────
            String name = tfName.getText().trim();
            if (name.isEmpty())
                throw new IllegalArgumentException("Product name is required.");

            String priceStr = tfPrice.getText().trim();
            String stockStr = tfStock.getText().trim();
            if (priceStr.isEmpty() || stockStr.isEmpty())
                throw new IllegalArgumentException("Price and stock quantity are required.");

            double price;
            int    stock;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Price must be a valid number (e.g. 1500 or 1500.50).");
            }
            try {
                stock = Integer.parseInt(stockStr);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Stock quantity must be a whole number.");
            }

            Brand    brand = (Brand)    cbBrand.getSelectedItem();
            Category cat   = (Category) cbCategory.getSelectedItem();
            if (brand == null || cat == null)
                throw new IllegalArgumentException("Brand and category must be selected.");

            // ProductFactory validates price/stock via the Product constructor
            Product p = ProductFactory.create(
                existing != null ? existing.getProductId() : 0,
                name,
                brand.getBrandId(),
                cat.getCategoryId(),
                price,
                stock,
                tfShade.getText().trim(),
                taDesc.getText().trim(),
                cat.getName()
            );

            if (existing == null) productDAO.addProduct(p);
            else                  productDAO.updateProduct(p);

            saved = true;
            JOptionPane.showMessageDialog(this,
                "Product saved successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(), "Validation Error",
                JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error:\n" + ex.getMessage(), "DB Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Small Helpers ────────────────────────────────────────

    private void addFormRow(JPanel p, GridBagConstraints g,
                            int row, String labelText, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(label(labelText + ":"), g);
        g.gridx = 1; g.gridwidth = 2;
        if (field instanceof JTextField tf) tf.setFont(fieldFont());
        p.add(field, g);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private Font fieldFont() { return new Font("Segoe UI", Font.PLAIN, 13); }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(fieldFont());
        cb.setBackground(Color.WHITE);
    }

    private JButton styledBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Accessor ─────────────────────────────────────────────
    /** @return true if the user successfully saved the product */
    public boolean isSaved() { return saved; }
}
