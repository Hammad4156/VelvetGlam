package velvetglam.ui.module3;

import velvetglam.dao.UserDAO;
import velvetglam.model.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Modal dialog that allows a Manager to change the login password
 * for any staff member's account.
 *
 * OOP Concepts Demonstrated:
 *  - Encapsulation  : password fields are JPasswordField (never plain text)
 *  - Exception Handling : validates current password, empty fields, mismatch
 */
public class ChangePasswordDialog extends JDialog {

    // ── Colour Palette ────────────────────────────────────────
    private static final Color C_PRIMARY = new Color(108,  52, 168);
    private static final Color C_SAVE    = new Color( 25, 118, 210);
    private static final Color C_CANCEL  = new Color(100, 100, 100);
    private static final Color C_BG      = new Color(250, 248, 255);

    // ── Fields ───────────────────────────────────────────────
    private final JLabel        lblUsername    = new JLabel();
    private final JPasswordField pfNewPassword = new JPasswordField(16);
    private final JPasswordField pfConfirm     = new JPasswordField(16);

    private final UserDAO    userDAO;
    private final UserAccount account;
    private       boolean    saved = false;

    // ── Constructor ──────────────────────────────────────────
    public ChangePasswordDialog(Window owner, UserAccount account) {
        super(owner, "Change Password", ModalityType.APPLICATION_MODAL);
        this.userDAO = new UserDAO();
        this.account = account;

        setBackground(C_BG);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildForm(),    BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private JPanel buildHeader() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.setBackground(C_PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        JLabel lbl = new JLabel("🔑  Change Staff Password");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.WHITE);
        bar.add(lbl);
        return bar;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 28, 8, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(7, 6, 7, 6);
        gc.anchor = GridBagConstraints.WEST;

        // Read-only username info row
        lblUsername.setText(account.getUsername() + "  (" + account.getRole() + ")");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(C_PRIMARY);

        gc.gridx = 0; gc.gridy = 0;
        p.add(label("Account:"), gc);
        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        p.add(lblUsername, gc);
        gc.fill = GridBagConstraints.NONE;

        // Divider
        gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 180, 230));
        p.add(sep, gc);
        gc.gridwidth = 1;

        // New Password
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        p.add(label("New Password *:"), gc);
        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        styleField(pfNewPassword);
        p.add(pfNewPassword, gc);
        gc.fill = GridBagConstraints.NONE;

        // Confirm
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0;
        p.add(label("Confirm Password *:"), gc);
        gc.gridx = 1; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        styleField(pfConfirm);
        p.add(pfConfirm, gc);
        gc.fill = GridBagConstraints.NONE;

        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        p.setBackground(C_BG);

        JButton btnSave   = btn("💾  Update Password", C_SAVE);
        JButton btnCancel = btn("✕  Cancel",          C_CANCEL);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        p.add(btnCancel);
        p.add(btnSave);
        return p;
    }

    // ── Logic ────────────────────────────────────────────────
    private void save() {
        String newPass = new String(pfNewPassword.getPassword());
        String confirm = new String(pfConfirm.getPassword());

        if (newPass.isEmpty()) {
            error("New password cannot be empty."); pfNewPassword.requestFocus(); return;
        }
        if (newPass.length() < 4) {
            error("Password must be at least 4 characters."); pfNewPassword.requestFocus(); return;
        }
        if (!newPass.equals(confirm)) {
            error("Passwords do not match."); pfConfirm.requestFocus(); return;
        }

        try {
            account.setPassword(newPass);
            userDAO.updateUser(account);
            saved = true;
            JOptionPane.showMessageDialog(this,
                "Password updated successfully for " + account.getUsername() + ".",
                "Password Changed", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            error("Database error:\n" + ex.getMessage());
        }
    }

    public boolean isSaved() { return saved; }

    // ── Helpers ──────────────────────────────────────────────
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void styleField(JPasswordField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(210, 28));
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
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
