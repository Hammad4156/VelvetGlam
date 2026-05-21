package velvetglam.ui.module2;

import velvetglam.dao.CustomerDAO;
import velvetglam.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Modal dialog for adding a new customer or editing an existing one.
 *
 * Used by Module2Panel's Customers tab.
 * On save, validates all inputs before calling CustomerDAO.
 */
public class CustomerFormDialog extends JDialog {

    // ── Colour Palette (matches Module2Panel) ────────────────
    private static final Color C_PRIMARY = new Color(108, 52, 168);
    private static final Color C_SAVE    = new Color( 56, 142,  60);
    private static final Color C_CANCEL  = new Color(100, 100, 100);
    private static final Color C_BG      = new Color(250, 248, 255);

    // ── Fields ───────────────────────────────────────────────
    private final JTextField tfName    = field(20);
    private final JTextField tfContact = field(15);
    private final JTextField tfEmail   = field(20);
    private final JTextField tfPoints  = field(6);

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final Customer    existing;    // null = new customer
    private boolean           saved = false;

    // ── Constructor ──────────────────────────────────────────
    public CustomerFormDialog(Window owner, Customer existing) {
        super(owner,
              existing == null ? "Add New Customer" : "Edit Customer",
              ModalityType.APPLICATION_MODAL);

        this.existing = existing;
        setBackground(C_BG);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildForm(),    BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (existing != null) populateForm(existing);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    // ── Header ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(C_PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        JLabel lbl = new JLabel(existing == null
            ? "👤  Register New Customer"
            : "✏️  Edit Customer");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.WHITE);
        bar.add(lbl);
        return bar;
    }

    // ── Form ─────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        addRow(p, gc, 0, "Full Name *",     tfName);
        addRow(p, gc, 1, "Contact No.",     tfContact);
        addRow(p, gc, 2, "Email Address",   tfEmail);
        addRow(p, gc, 3, "Loyalty Points",  tfPoints);

        tfPoints.setText("0");

        // Disable points editing for a new customer (always starts at 0)
        if (existing == null) tfPoints.setEnabled(false);

        return p;
    }

    private void addRow(JPanel p, GridBagConstraints gc,
                        int row, String labelText, JTextField field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        JLabel lbl = new JLabel(labelText + "  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl, gc);

        gc.gridx = 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(220, 28));
        p.add(field, gc);
        gc.fill = GridBagConstraints.NONE;
    }

    // ── Buttons ──────────────────────────────────────────────
    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        p.setBackground(C_BG);

        JButton btnSave   = btn("💾  Save",    C_SAVE);
        JButton btnCancel = btn("✕  Cancel",  C_CANCEL);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnCancel);
        p.add(btnSave);
        return p;
    }

    // ── Logic ────────────────────────────────────────────────
    private void populateForm(Customer c) {
        tfName.setText(c.getName());
        tfContact.setText(c.getContact());
        tfEmail.setText(c.getEmail());
        tfPoints.setText(String.valueOf(c.getLoyaltyPoints()));
    }

    private void save() {
        // Validate
        String name = tfName.getText().trim();
        if (name.isEmpty()) {
            error("Customer name is required.");
            tfName.requestFocus();
            return;
        }

        int points = 0;
        if (!tfPoints.getText().trim().isEmpty()) {
            try {
                points = Integer.parseInt(tfPoints.getText().trim());
                if (points < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                error("Loyalty points must be a non-negative whole number.");
                tfPoints.requestFocus();
                return;
            }
        }

        try {
            if (existing == null) {
                // New customer
                Customer c = new Customer(0, name,
                    tfContact.getText().trim(),
                    tfEmail.getText().trim(),
                    0, LocalDate.now());
                customerDAO.addCustomer(c);
            } else {
                // Update existing
                existing.setName(name);
                existing.setContact(tfContact.getText().trim());
                existing.setEmail(tfEmail.getText().trim());
                existing.setLoyaltyPoints(points);
                customerDAO.updateCustomer(existing);
            }
            saved = true;
            dispose();

        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (SQLException ex) {
            error("Database error:\n" + ex.getMessage());
        }
    }

    /** @return true if the user clicked Save and the record was persisted */
    public boolean isSaved() { return saved; }

    // ── Helpers ──────────────────────────────────────────────
    private JTextField field(int cols) { return new JTextField(cols); }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
