package velvetglam.ui.module3;

import velvetglam.dao.StaffDAO;
import velvetglam.dao.UserDAO;
import velvetglam.model.Staff;
import velvetglam.model.UserAccount;

import javax.swing.*;
import velvetglam.util.VGTheme;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Module 3 — Staff Management & Role-Based Login Panel.
 *
 * Designed to be embedded in the Module 4 dashboard or run standalone
 * via Module3Launcher for testing.
 *
 * Layout:
 *  ┌────────────────────────────────────────────────────────────┐
 *  │  Header bar (purple)  — shows logged-in user + Logout btn  │
 *  ├────────────────────────────────────────────────────────────┤
 *  │  JTabbedPane                                               │
 *  │    Tab 1 — 👩‍💼 Staff List                                  │
 *  │      [Search bar]                                          │
 *  │      [Staff JTable with role, contact, salary]             │
 *  │      [Add / Edit / Delete / Change Password buttons]       │
 *  │    Tab 2 — 🔐 Access Control (read-only permissions view)  │
 *  │      [Select staff member from list]                       │
 *  │      [Display permissions / username / role info]          │
 *  └────────────────────────────────────────────────────────────┘
 *
 * OOP Concepts Demonstrated:
 *  - Inheritance    : Staff extends Person; Manager & Cashier extend Staff
 *  - Abstraction    : abstract Person defines getPermissions() contract
 *  - Polymorphism   : getPermissions() behaves differently per role
 *  - Encapsulation  : all UI state fields are private
 *  - Exception Handling : every DAO call wrapped in try/catch
 */
public class Module3Panel extends JPanel {

    // ── Colour Palette (consistent with Module 1 & 2) ────────
    private static final Color C_PRIMARY  = new Color(108,  52, 168);
    private static final Color C_HEADER2  = new Color( 74,  20, 140);
    private static final Color C_SELECT   = new Color(206, 147, 216);
    private static final Color C_BG       = new Color(250, 248, 255);
    private static final Color C_ADD      = new Color( 56, 142,  60);
    private static final Color C_EDIT     = new Color( 25, 118, 210);
    private static final Color C_DEL      = new Color(211,  47,  47);
    private static final Color C_MISC     = new Color( 69,  90, 100);
    private static final Color C_ROW_ALT  = new Color(245, 240, 255);
    private static final Color C_MANAGER  = new Color(102,  0, 153);
    private static final Color C_CASHIER  = new Color( 21,101, 192);

    // ── DAOs ─────────────────────────────────────────────────
    private final StaffDAO staffDAO = new StaffDAO();
    private final UserDAO  userDAO  = new UserDAO();

    // ── Logged-In User ────────────────────────────────────────
    private final UserAccount loggedInUser;

    // ════════════════════════════════════════════════════════
    //  TAB 1 — STAFF LIST
    // ════════════════════════════════════════════════════════
    private DefaultTableModel staffModel;
    private JTable            staffTable;
    private JTextField        tfSearch;
    private JLabel            lblLoggedIn;

    // ════════════════════════════════════════════════════════
    //  TAB 2 — ACCESS CONTROL
    // ════════════════════════════════════════════════════════
    private JComboBox<Staff>  cbStaffSelect;
    private JTextArea         taPermissions;
    private JLabel            lblUsername;
    private JLabel            lblRole;

    // ── Constructor ──────────────────────────────────────────
    public Module3Panel(UserAccount loggedInUser) {
        this.loggedInUser = loggedInUser;
        setBackground(C_BG);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);

        loadStaffTable(null);
        loadAccessControlCombo();
    }

    // ── Header ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        // Left: title
        JLabel title = new JLabel("👩‍💼  Module 3 — Staff Management & Login System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        // Right: logged-in user + logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        String displayName = (loggedInUser != null)
            ? loggedInUser.getUsername() + "  (" + loggedInUser.getRole() + ")"
            : "Guest";

        lblLoggedIn = new JLabel("🔓  " + displayName);
        lblLoggedIn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoggedIn.setForeground(new Color(220, 200, 255));

        JButton btnLogout = btn("🚪  Logout", C_HEADER2);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLogout.addActionListener(e -> handleLogout());

        rightPanel.add(lblLoggedIn);
        rightPanel.add(btnLogout);

        bar.add(title,      BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    // ── Tabs ─────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(C_BG);
        tabs.addTab("👩‍💼  Staff List",     buildStaffListTab());
        tabs.addTab("🔐  Access Control",  buildAccessControlTab());
        return tabs;
    }

    // ════════════════════════════════════════════════════════
    //  TAB 1 — STAFF LIST
    // ════════════════════════════════════════════════════════
    private JPanel buildStaffListTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        p.add(buildSearchBar(),   BorderLayout.NORTH);
        p.add(buildStaffTable(),  BorderLayout.CENTER);
        p.add(buildStaffButtons(), BorderLayout.SOUTH);

        return p;
    }

    // ── Search Bar ───────────────────────────────────────────
    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setBackground(C_BG);

        tfSearch = new JTextField(22);
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfSearch.setToolTipText("Search by name, role, or contact");

        JButton btnSearch = btn("🔍  Search", C_MISC);
        JButton btnReset  = btn("↺  Reset",   C_MISC);

        btnSearch.addActionListener(e -> loadStaffTable(tfSearch.getText()));
        btnReset.addActionListener(e -> { tfSearch.setText(""); loadStaffTable(null); });
        tfSearch.addActionListener(e -> loadStaffTable(tfSearch.getText()));

        // Summary labels
        JLabel lblTitle = new JLabel("Staff Members");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(C_PRIMARY);

        bar.add(lblTitle);
        bar.add(Box.createHorizontalStrut(20));
        bar.add(new JLabel("Search: "));
        bar.add(tfSearch);
        bar.add(btnSearch);
        bar.add(btnReset);
        return bar;
    }

    // ── Staff Table ──────────────────────────────────────────
    private JScrollPane buildStaffTable() {
        String[] cols = {"ID", "Name", "Role", "Contact", "Salary (PKR)", "Username"};
        staffModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        staffTable = new JTable(staffModel);
        styleTable(staffTable);

        // Column widths
        int[] widths = {40, 180, 90, 130, 110, 120};
        for (int i = 0; i < widths.length; i++)
            staffTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Alternate row colours + role colour coding
        staffTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean selected,
                    boolean focused, int row, int col) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, col);
                if (selected) {
                    setBackground(C_SELECT);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : C_ROW_ALT);
                    // Colour-code the Role column
                    if (col == 2) {
                        String role = value != null ? value.toString() : "";
                        setForeground(Staff.ROLE_MANAGER.equals(role) ? C_MANAGER : C_CASHIER);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(Color.BLACK);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                return this;
            }
        });

        return new JScrollPane(staffTable);
    }

    // ── Staff CRUD Buttons ───────────────────────────────────
    private JPanel buildStaffButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBackground(C_BG);

        JButton btnAdd    = btn("➕  Add Staff",       C_ADD);
        JButton btnEdit   = btn("✏️  Edit",             C_EDIT);
        JButton btnDelete = btn("🗑  Delete",           C_DEL);
        JButton btnPwd    = btn("🔑  Change Password", C_MISC);
        JButton btnRefresh= btn("↺  Refresh",          C_MISC);

        // Role-based: only managers can add/delete/change passwords
        boolean isManager = loggedInUser != null &&
                            Staff.ROLE_MANAGER.equals(loggedInUser.getRole());
        btnAdd.setEnabled(isManager);
        btnDelete.setEnabled(isManager);
        btnPwd.setEnabled(isManager);

        btnAdd.addActionListener(e -> {
            StaffFormDialog dlg = new StaffFormDialog(
                SwingUtilities.getWindowAncestor(this), null);
            dlg.setVisible(true);
            if (dlg.isSaved()) loadStaffTable(tfSearch.getText());
        });

        btnEdit.addActionListener(e -> {
            Staff sel = getSelectedStaff();
            if (sel == null) { info("Please select a staff member to edit."); return; }
            StaffFormDialog dlg = new StaffFormDialog(
                SwingUtilities.getWindowAncestor(this), sel);
            dlg.setVisible(true);
            if (dlg.isSaved()) { loadStaffTable(tfSearch.getText()); loadAccessControlCombo(); }
        });

        btnDelete.addActionListener(e -> deleteStaff());

        btnPwd.addActionListener(e -> {
            Staff sel = getSelectedStaff();
            if (sel == null) { info("Please select a staff member to change password."); return; }
            try {
                UserAccount ua = userDAO.getUserByStaffId(sel.getStaffId());
                if (ua == null) {
                    info("No login account found for this staff member."); return;
                }
                ChangePasswordDialog dlg = new ChangePasswordDialog(
                    SwingUtilities.getWindowAncestor(this), ua);
                dlg.setVisible(true);
            } catch (SQLException ex) {
                error("Database error:\n" + ex.getMessage());
            }
        });

        btnRefresh.addActionListener(e -> { loadStaffTable(null); loadAccessControlCombo(); });

        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);
        p.add(btnPwd);
        p.add(Box.createHorizontalStrut(12));
        p.add(btnRefresh);
        return p;
    }

    // ════════════════════════════════════════════════════════
    //  TAB 2 — ACCESS CONTROL
    // ════════════════════════════════════════════════════════
    private JPanel buildAccessControlTab() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Top: combo to pick staff member
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setBackground(C_BG);
        topBar.add(boldLabel("Select Staff Member:"));
        cbStaffSelect = new JComboBox<>();
        cbStaffSelect.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStaffSelect.setPreferredSize(new Dimension(260, 30));
        cbStaffSelect.addActionListener(e -> refreshPermissionsPanel());
        topBar.add(cbStaffSelect);
        p.add(topBar, BorderLayout.NORTH);

        // Centre: info card
        p.add(buildPermissionsCard(), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPermissionsCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(240, 235, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 140, 210), 1),
            BorderFactory.createEmptyBorder(20, 28, 20, 28)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill   = GridBagConstraints.HORIZONTAL;

        // Username row
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        card.add(boldLabel("Username:"), gc);
        gc.gridx = 1; gc.weightx = 1;
        lblUsername = new JLabel("—");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(lblUsername, gc);

        // Role row
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        card.add(boldLabel("Role:"), gc);
        gc.gridx = 1; gc.weightx = 1;
        lblRole = new JLabel("—");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(lblRole, gc);

        // Separator
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2;
        card.add(new JSeparator(), gc);
        gc.gridwidth = 1;

        // Permissions header
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        card.add(boldLabel("Module Access Permissions  (getPermissions()):"), gc);
        gc.gridwidth = 1;

        // Permissions text area
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2;
        gc.weighty = 1; gc.fill = GridBagConstraints.BOTH;
        taPermissions = new JTextArea(6, 30);
        taPermissions.setFont(new Font("Consolas", Font.PLAIN, 13));
        taPermissions.setEditable(false);
        taPermissions.setBackground(Color.WHITE);
        taPermissions.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 150, 210), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        card.add(new JScrollPane(taPermissions), gc);

        // Hint
        gc.gridy = 5; gc.weighty = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        JLabel hint = new JLabel(
            "  ⓘ  Manager has full access. Cashier can only access Customer & Sales modules.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(120, 80, 160));
        card.add(hint, gc);

        return card;
    }

    // ── Load / Refresh ────────────────────────────────────────
    private void loadStaffTable(String keyword) {
        try {
            List<Staff> list = (keyword == null || keyword.isBlank())
                ? staffDAO.getAllStaff()
                : staffDAO.searchStaff(keyword);

            staffModel.setRowCount(0);
            for (Staff s : list) {
                String username = "—";
                try {
                    UserAccount ua = userDAO.getUserByStaffId(s.getStaffId());
                    if (ua != null) username = ua.getUsername();
                } catch (SQLException ignored) {}

                staffModel.addRow(new Object[]{
                    s.getStaffId(),
                    s.getName(),
                    s.getRole(),
                    s.getContact().isEmpty() ? "—" : s.getContact(),
                    String.format("%.0f", s.getSalary()),
                    username
                });
            }
        } catch (SQLException ex) {
            error("Failed to load staff:\n" + ex.getMessage());
        }
    }

    private void loadAccessControlCombo() {
        cbStaffSelect.removeAllItems();
        try {
            List<Staff> list = staffDAO.getAllStaff();
            for (Staff s : list) cbStaffSelect.addItem(s);
        } catch (SQLException ex) {
            error("Failed to load staff list:\n" + ex.getMessage());
        }
        refreshPermissionsPanel();
    }

    private void refreshPermissionsPanel() {
        Staff selected = (Staff) cbStaffSelect.getSelectedItem();
        if (selected == null) {
            lblUsername.setText("—");
            lblRole.setText("—");
            taPermissions.setText("(Select a staff member above to view their permissions)");
            return;
        }

        // Get linked username
        String username = "—";
        try {
            UserAccount ua = userDAO.getUserByStaffId(selected.getStaffId());
            if (ua != null) username = ua.getUsername();
        } catch (SQLException ignored) {}

        lblUsername.setText(username);
        lblRole.setText(selected.getRole());
        lblRole.setForeground(Staff.ROLE_MANAGER.equals(selected.getRole()) ? C_MANAGER : C_CASHIER);

        // Demonstrate Polymorphism — getPermissions() called on the Staff object
        String[] perms = selected.getPermissions();
        StringBuilder sb = new StringBuilder();
        sb.append("Accessible Modules for ").append(selected.getRole())
          .append(" (").append(selected.getName()).append("):\n\n");
        for (String perm : perms) {
            sb.append("  ✅  ").append(perm).append("\n");
        }

        // Show what's BLOCKED for cashiers
        if (Staff.ROLE_CASHIER.equals(selected.getRole())) {
            String[] allPerms = { "Products & Inventory", "Staff Management", "Dashboard" };
            sb.append("\nBlocked Modules:\n\n");
            for (String p : allPerms) {
                if (!Arrays.asList(perms).contains(p)) {
                    sb.append("  🚫  ").append(p).append("\n");
                }
            }
        }

        taPermissions.setText(sb.toString());
        taPermissions.setCaretPosition(0);
    }

    // ── Delete Staff ─────────────────────────────────────────
    private void deleteStaff() {
        Staff sel = getSelectedStaff();
        if (sel == null) { info("Please select a staff member to delete."); return; }

        // Prevent deleting the currently logged-in user
        if (loggedInUser != null && loggedInUser.getStaffId() == sel.getStaffId()) {
            error("You cannot delete your own account while logged in.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete staff member \"" + sel.getName() + "\" and their login account?\n"
            + "This action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            staffDAO.deleteStaff(sel.getStaffId());  // CASCADE deletes users row too
            loadStaffTable(tfSearch.getText());
            loadAccessControlCombo();
        } catch (SQLException ex) {
            error("Failed to delete staff member:\n" + ex.getMessage());
        }
    }

    // ── Logout ───────────────────────────────────────────────
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?",
            "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Close the window — in Module 4 integration this would return to the login screen
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();
    }

    // ── Helpers ──────────────────────────────────────────────
    private Staff getSelectedStaff() {
        int row = staffTable.getSelectedRow();
        if (row < 0) return null;
        int staffId = (int) staffModel.getValueAt(row, 0);
        try {
            return staffDAO.getStaffById(staffId);
        } catch (SQLException ex) {
            error("Could not retrieve staff record:\n" + ex.getMessage());
            return null;
        }
    }

    private void styleTable(JTable table) {
        VGTheme.styleTable(table);
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
