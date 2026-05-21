package velvetglam.ui.module1;

import velvetglam.dao.BrandDAO;
import velvetglam.model.Brand;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Modal dialog for adding a new brand or editing an existing one.
 */
public class BrandFormDialog extends JDialog {

    private static final Color C_PRIMARY = new Color(108, 52, 168);
    private static final Color C_BTN_OK  = new Color(56, 142, 60);
    private static final Color C_BTN_CX  = new Color(120, 120, 120);

    private final BrandDAO brandDAO = new BrandDAO();
    private final Brand    existing;
    private boolean        saved = false;

    private JTextField tfName, tfContact, tfCountry;

    public BrandFormDialog(Window parent, Brand existing) {
        super(parent,
              (existing == null ? "➕  Add New Brand" : "✏️  Edit Brand"),
              ModalityType.APPLICATION_MODAL);
        this.existing = existing;

        setSize(400, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        buildUI();
        if (existing != null) prefill();
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_PRIMARY, 1, true),
            "  Brand Details  ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), C_PRIMARY));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.insets  = new Insets(8, 10, 8, 10);
        g.weightx = 1.0;

        tfName    = new JTextField(20);
        tfContact = new JTextField(20);
        tfCountry = new JTextField(20);

        addRow(form, g, 0, "Brand Name *",      tfName);
        addRow(form, g, 1, "Supplier Contact",   tfContact);
        addRow(form, g, 2, "Country",            tfCountry);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btns.setBackground(Color.WHITE);
        JButton btnCancel = styledBtn("Cancel",    C_BTN_CX);
        JButton btnSave   = styledBtn("💾  Save",  C_BTN_OK);
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());
        btns.add(btnCancel);
        btns.add(btnSave);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }

    private void prefill() {
        tfName.setText(existing.getName());
        tfContact.setText(existing.getSupplierContact());
        tfCountry.setText(existing.getCountry());
    }

    private void save() {
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Brand name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Brand b = new Brand(
                existing != null ? existing.getBrandId() : 0,
                name,
                tfContact.getText().trim(),
                tfCountry.getText().trim()
            );
            if (existing == null) brandDAO.addBrand(b);
            else                  brandDAO.updateBrand(b);

            saved = true;
            JOptionPane.showMessageDialog(this,
                "Brand saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            String msg = ex.getMessage().contains("Duplicate entry")
                ? "A brand with that name already exists."
                : "Database error:\n" + ex.getMessage();
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ──────────────────────────────────────────────

    private void addRow(JPanel p, GridBagConstraints g, int row,
                        String labelText, JTextField field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        JLabel l = new JLabel(labelText + ":");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l, g);
        g.gridx = 1; g.gridwidth = 2;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(field, g);
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

    public boolean isSaved() { return saved; }
}
