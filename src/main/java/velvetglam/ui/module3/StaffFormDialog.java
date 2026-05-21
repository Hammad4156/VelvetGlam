package velvetglam.ui.module3;

import velvetglam.dao.StaffDAO;
import velvetglam.dao.UserDAO;
import velvetglam.model.Staff;
import velvetglam.model.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Modal dialog for adding a new staff member or editing an existing one.
 *
 * When adding a new staff member, a linked UserAccount (username + password)
 * is also created here so the staff member can log in immediately.
 *
 * When editing, only staff fields (name, role, contact, salary) are updated.
 * Credential changes are handled separately via the "Change Password" button
 * in Module3Panel.
 *
 * OOP Concepts Demonstrated:
 *  - Encapsulation  : all form-state fields are private
 *  - Exception Handling : validates all inputs, handles duplicate usernames,
 *                          and shows user-friendly error messages
 */
public class StaffFormDialog extends JDialog {

    // ── Colour Palette ────────────────────────────────────────
    private static final Color C_PRIMARY = new Color(108,  52, 168);
    private static final Color C_SAVE    = new Color( 56, 142,  60);
    private static final Color C_CANCEL  = new Color(100, 100, 100);
    private static final Color C_BG      = new Color(250, 248, 255);
    private static final Color C_SECTION = new Color(240, 235, 255);

    // ── Staff Fields ─────────────────────────────────────────
    private final JTextField  tfName    = field(20);
    private final JComboBox<String> cbRole = new JComboBox<>(
            new String[]{Staff.ROLE_MANAGER, Staff.ROLE_CASHIER});
    private final JTextField  tfContact = field(15);
    private final JTextField  tfSalary  = field(12);

    // ── Credentials Fields (only for NEW staff) ───────────────
    private final JTextField     tfUsername = field(15);
    private final JPasswordField pfPassword = new JPasswordField(15);
    private final JPasswordField pfConfirm  = new JPasswordField(15);

    private final StaffDAO staffDAO = new StaffDAO();
    private final UserDAO  userDAO  = new UserDAO();
    private final Staff    existing; // null = new staff
    private       boolean  saved    = false;

    // ── Constructor ──────────────────────────────────────────
    public StaffFormDialog(Window owner, Staff existing) {
        super(owner,
              existing == null ? "Add New Staff Member" : "Edit Staff Member",
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
            ? "👩‍💼  Register New Staff Member"
            : "✏️  Edit Staff Member");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.WHITE);
        bar.add(lbl);
        return bar;
    }

    // ── Form ─────────────────────────────────────────────────
    private JPanel buildForm() {
        JPanel outer = new JPanel();
        outer.setBackground(C_BG);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(10, 20, 6, 20));

        outer.add(sectionLabel("Staff Details"));
        outer.add(buildStaffSection());

        // Credentials section is only shown when adding NEW staff
        if (existing == null) {
            outer.add(Box.createVerticalStrut(10));
            outer.add(sectionLabel("Login Credentials"));
            outer.add(buildCredentialsSection());
        }

        return outer;
    }

    private JPanel buildStaffSection() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_SECTION);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 150, 210), 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(5, 6, 5, 6);
        gc.anchor  = GridBagConstraints.WEST;

        addRow(p, gc, 0, "Full Name *",   tfName);
        addRow(p, gc, 1, "Role *",        cbRole);
        addRow(p, gc, 2, "Contact No.",   tfContact);
        addRow(p, gc, 3, "Salary (PKR) *", tfSalary);

        return p;
    }

    private JPanel buildCredentialsSection() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_SECTION);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 150, 210), 1),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(5, 6, 5, 6);
        gc.anchor  = GridBagConstraints.WEST;

        styleField(tfUsername);
        styleField(pfPassword);
        styleField(pfConfirm);

        addRow(p, gc, 0, "Username *",         tfUsername);
        addRow(p, gc, 1, "Password *",         pfPassword);
        addRow(p, gc, 2, "Confirm Password *", pfConfirm);

        JLabel hint = new JLabel("  ⓘ  Staff will use these credentials to log in.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(130, 100, 180));
        GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0; hc.gridy = 3; hc.gridwidth = 2;
        hc.anchor = GridBagConstraints.WEST;
        hc.insets = new Insets(4, 6, 2, 6);
        p.add(hint, hc);

        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel("  " + text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(C_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return l;
    }

    private void addRow(JPanel p, GridBagConstraints gc,
                        int row, String labelText, JComponent comp) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        JLabel lbl = new JLabel(labelText + "  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl, gc);

        gc.gridx = 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
        if (comp instanceof JTextField) {
            styleField((JTextField) comp);
        }
        p.add(comp, gc);
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
    private void populateForm(Staff s) {
        tfName.setText(s.getName());
        cbRole.setSelectedItem(s.getRole());
        tfContact.setText(s.getContact());
        tfSalary.setText(String.valueOf((int) s.getSalary()));
    }

    private void save() {
        // ── Validate staff fields ─────────────────────────
        String name    = tfName.getText().trim();
        String role    = (String) cbRole.getSelectedItem();
        String contact = tfContact.getText().trim();
        String salStr  = tfSalary.getText().trim();

        if (name.isEmpty()) {
            error("Staff name is required."); tfName.requestFocus(); return;
        }
        if (salStr.isEmpty()) {
            error("Salary is required."); tfSalary.requestFocus(); return;
        }
        double salary;
        try {
            salary = Double.parseDouble(salStr);
            if (salary < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            error("Salary must be a valid non-negative number.");
            tfSalary.requestFocus(); return;
        }

        // ── Validate credentials (new staff only) ─────────
        String username = null;
        String password = null;
        if (existing == null) {
            username = tfUsername.getText().trim();
            password = new String(pfPassword.getPassword());
            String confirm = new String(pfConfirm.getPassword());

            if (username.isEmpty()) {
                error("Username is required for login."); tfUsername.requestFocus(); return;
            }
            if (username.length() < 3) {
                error("Username must be at least 3 characters."); tfUsername.requestFocus(); return;
            }
            if (password.isEmpty()) {
                error("Password is required."); pfPassword.requestFocus(); return;
            }
            if (password.length() < 4) {
                error("Password must be at least 4 characters."); pfPassword.requestFocus(); return;
            }
            if (!password.equals(confirm)) {
                error("Passwords do not match."); pfConfirm.requestFocus(); return;
            }

            // Check duplicate username
            try {
                if (userDAO.usernameExists(username)) {
                    error("Username '" + username + "' is already taken.\nPlease choose a different username.");
                    tfUsername.requestFocus(); return;
                }
            } catch (SQLException ex) {
                error("Database error checking username:\n" + ex.getMessage()); return;
            }
        }

        // ── Persist ───────────────────────────────────────
        try {
            if (existing == null) {
                // 1. Insert staff
                Staff s = new Staff(0, name, role, contact, salary);
                staffDAO.addStaff(s);   // sets s.staffId

                // 2. Insert linked user account
                UserAccount ua = new UserAccount(0, username, password, role, s.getStaffId());
                userDAO.addUser(ua);

            } else {
                // Update staff only
                existing.setName(name);
                existing.setRole(role);
                existing.setContact(contact);
                existing.setSalary(salary);
                staffDAO.updateStaff(existing);

                // Also sync role in users table
                UserAccount ua = userDAO.getUserByStaffId(existing.getStaffId());
                if (ua != null) {
                    ua.setRole(role);
                    userDAO.updateUser(ua);
                }
            }
            saved = true;
            dispose();

        } catch (IllegalArgumentException ex) {
            error(ex.getMessage());
        } catch (IllegalStateException ex) {
            error(ex.getMessage());
        } catch (SQLException ex) {
            error("Database error:\n" + ex.getMessage());
        }
    }

    /** @return true if Save was clicked and the record was persisted. */
    public boolean isSaved() { return saved; }

    // ── Helpers ──────────────────────────────────────────────
    private JTextField field(int cols) { return new JTextField(cols); }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(200, 28));
    }

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
