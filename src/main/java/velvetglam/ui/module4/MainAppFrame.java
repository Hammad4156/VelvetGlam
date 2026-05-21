package velvetglam.ui.module4;

import velvetglam.model.UserAccount;
import velvetglam.ui.module1.Module1Panel;
import velvetglam.ui.module2.Module2Panel;
import velvetglam.ui.module3.Module3Panel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Module 4 — Main Application Frame.
 * Modern web-app style layout with Rose Gold + Deep Purple theme.
 */
public class MainAppFrame extends JFrame {

    // ── Colour Palette ────────────────────────────────────────
    private static final Color C_SIDEBAR_BG  = new Color( 18,   6,  45);
    private static final Color C_SIDEBAR_TOP = new Color( 30,  10,  70);
    private static final Color C_NAV_HOVER   = new Color( 50,  20, 100);
    private static final Color C_NAV_ACTIVE  = new Color(108,  52, 168);
    private static final Color C_PRIMARY     = new Color(108,  52, 168);
    private static final Color C_ROSE_GOLD   = new Color(198, 144, 120);
    private static final Color C_BG          = new Color(245, 243, 252);
    private static final Color C_STATUS_BG   = new Color( 22,   8,  55);
    private static final Color C_TEXT_LIGHT  = new Color(200, 175, 240);
    private static final Color C_TEXT_DIM    = new Color(110,  85, 150);

    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_MODULE1   = "MODULE1";
    private static final String CARD_MODULE2   = "MODULE2";
    private static final String CARD_MODULE3   = "MODULE3";

    private final UserAccount loggedInUser;
    private final CardLayout  cardLayout  = new CardLayout();
    private final JPanel      contentArea = new JPanel(cardLayout);
    private JButton           activeBtn   = null;
    private JLabel            lblStatus;

    public MainAppFrame(UserAccount user) {
        this.loggedInUser = user;
        setTitle("VelvetGlam  —  Smart Cosmetics Store Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        setPreferredSize(new Dimension(1400, 820));
        setLayout(new BorderLayout(0, 0));

        add(buildSidebar(),     BorderLayout.WEST);
        add(buildContentArea(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // ══════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0,          C_SIDEBAR_BG,
                    0, getHeight(), new Color(25, 8, 60));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(240, 0));

        // ── Logo Section ──────────────────────────────────────
        JPanel logoPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(80, 30, 140),
                    getWidth(), getHeight(), new Color(40, 10, 90));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Rose gold bottom border
                g2.setColor(C_ROSE_GOLD);
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
            }
        };
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(240, 110));
        logoPanel.setBorder(new EmptyBorder(22, 20, 18, 20));

        JLabel brandName = new JLabel("VelvetGlam");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandName.setForeground(Color.WHITE);
        brandName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("Smart Cosmetics Store");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brandSub.setForeground(C_ROSE_GOLD);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandTag = new JLabel("Management System");
        brandTag.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        brandTag.setForeground(C_TEXT_DIM);
        brandTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(brandName);
        logoPanel.add(Box.createVerticalStrut(3));
        logoPanel.add(brandSub);
        logoPanel.add(Box.createVerticalStrut(1));
        logoPanel.add(brandTag);
        sidebar.add(logoPanel);

        // ── User Profile Section ──────────────────────────────
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);
        userPanel.setMaximumSize(new Dimension(240, 80));
        userPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Avatar circle with initials
        String initials = loggedInUser.getUsername().substring(0, 1).toUpperCase();
        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        avatarRow.setOpaque(false);
        avatarRow.setMaximumSize(new Dimension(240, 50));

        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ROSE_GOLD);
                g2.fillOval(0, 0, 38, 38);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, 19 - fm.stringWidth(initials)/2, 19 + fm.getAscent()/2 - 2);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(38, 38); }
        };

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel uName = new JLabel(loggedInUser.getUsername());
        uName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uName.setForeground(Color.WHITE);

        JLabel uRole = new JLabel(loggedInUser.getRole());
        uRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        uRole.setForeground(C_ROSE_GOLD);

        userInfo.add(uName);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(uRole);

        avatarRow.add(avatar);
        avatarRow.add(userInfo);
        userPanel.add(avatarRow);
        sidebar.add(userPanel);

        // ── Divider ───────────────────────────────────────────
        sidebar.add(buildDivider());
        sidebar.add(Box.createVerticalStrut(8));

        // ── Navigation Label ──────────────────────────────────
        JLabel navLabel = new JLabel("  MAIN MENU");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        navLabel.setForeground(C_TEXT_DIM);
        navLabel.setMaximumSize(new Dimension(240, 22));
        sidebar.add(navLabel);
        sidebar.add(Box.createVerticalStrut(4));

        // ── Nav Buttons ───────────────────────────────────────
        boolean isManager = "Manager".equals(loggedInUser.getRole());

        JButton btnDash = makeNavBtn("Dashboard", "Overview & Analytics", CARD_DASHBOARD);
        sidebar.add(btnDash);
        setActive(btnDash);

        if (isManager) {
            sidebar.add(makeNavBtn("Products & Inventory", "Manage Products & Brands", CARD_MODULE1));
        }
        sidebar.add(makeNavBtn("Customers & Sales", "Billing & Customer Management", CARD_MODULE2));
        if (isManager) {
            sidebar.add(makeNavBtn("Staff Management", "Staff & Access Control", CARD_MODULE3));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildDivider());
        sidebar.add(Box.createVerticalStrut(8));

        // ── Logout ────────────────────────────────────────────
        JButton btnLogout = makeNavBtn("Logout", "Sign out of system", null);
        btnLogout.setForeground(new Color(255, 120, 120));
        btnLogout.addActionListener(e -> onLogout());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(16));

        return sidebar;
    }

    private JPanel buildDivider() {
        JPanel d = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    20, 0, new Color(0,0,0,0),
                    120, 0, new Color(90,50,130,180),
                    true);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 1);
            }
        };
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(240, 1));
        d.setPreferredSize(new Dimension(240, 1));
        return d;
    }

    private JButton makeNavBtn(String title, String subtitle, String cardName) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeBtn) {
                    // Active: gradient fill with left accent bar
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(108, 52, 168, 220),
                        getWidth(), 0, new Color(80, 30, 140, 100));
                    g2.setPaint(gp);
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 10, 10);
                    g2.setColor(C_ROSE_GOLD);
                    g2.fillRoundRect(8, 2, 3, getHeight()-4, 3, 3);
                }
                super.paintComponent(g);
            }
        };

        // Multi-line button content
        btn.setLayout(new BorderLayout(10, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 16));
        btn.setMaximumSize(new Dimension(240, 56));
        btn.setPreferredSize(new Dimension(240, 56));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Text panel inside button
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTitle.setForeground(this.activeBtn == null
            ? new Color(200, 175, 240) : Color.WHITE);

        JLabel lSub = new JLabel(subtitle);
        lSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lSub.setForeground(C_TEXT_DIM);

        textPanel.add(lTitle);
        textPanel.add(lSub);
        btn.add(textPanel, BorderLayout.CENTER);

        // Hover
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != activeBtn) {
                    btn.setOpaque(true);
                    btn.setBackground(C_NAV_HOVER);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != activeBtn) {
                    btn.setOpaque(false);
                }
            }
        });

        if (cardName != null) {
            btn.addActionListener(e -> {
                setActive(btn);
                cardLayout.show(contentArea, cardName);
                if (CARD_DASHBOARD.equals(cardName)) {
                    for (Component c : contentArea.getComponents()) {
                        if (c instanceof DashboardPanel dp && c.isVisible()) {
                            dp.refreshData();
                        }
                    }
                }
                setStatus("Viewing: " + title);
            });
        }

        // Store title label ref so setActive can update color
        btn.putClientProperty("titleLabel", lTitle);
        return btn;
    }

    private void setActive(JButton btn) {
        if (activeBtn != null) {
            activeBtn.setOpaque(false);
            JLabel old = (JLabel) activeBtn.getClientProperty("titleLabel");
            if (old != null) old.setForeground(C_TEXT_LIGHT);
        }
        activeBtn = btn;
        btn.setOpaque(false);
        JLabel lbl = (JLabel) btn.getClientProperty("titleLabel");
        if (lbl != null) lbl.setForeground(Color.WHITE);
        btn.repaint();
    }

    // ══════════════════════════════════════════════════════════
    //  CONTENT AREA
    // ══════════════════════════════════════════════════════════
    private JPanel buildContentArea() {
        contentArea.setBackground(C_BG);
        contentArea.add(new DashboardPanel(loggedInUser), CARD_DASHBOARD);
        contentArea.add(new Module1Panel(),               CARD_MODULE1);
        contentArea.add(new Module2Panel(),               CARD_MODULE2);
        contentArea.add(new Module3Panel(loggedInUser),   CARD_MODULE3);
        cardLayout.show(contentArea, CARD_DASHBOARD);
        return contentArea;
    }

    // ══════════════════════════════════════════════════════════
    //  STATUS BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0,0, C_STATUS_BG, getWidth(), 0, new Color(30,8,70));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(6, 18, 6, 18));

        lblStatus = new JLabel("System ready  —  all modules loaded successfully.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(120, 90, 170));

        JLabel right = new JLabel("VelvetGlam v1.0   |   OOP Lab   |   Group 10   |   Air University");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        right.setForeground(new Color(80, 55, 120));

        bar.add(lblStatus, BorderLayout.WEST);
        bar.add(right,     BorderLayout.EAST);
        return bar;
    }

    private void setStatus(String msg) {
        if (lblStatus != null) {
            lblStatus.setForeground(C_ROSE_GOLD);
            lblStatus.setText("->  " + msg);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  LOGOUT
    // ══════════════════════════════════════════════════════════
    private void onLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                velvetglam.ui.module3.LoginDialog dlg =
                    new velvetglam.ui.module3.LoginDialog(null);
                dlg.setVisible(true);
                UserAccount newUser = dlg.getLoggedInUser();
                if (newUser == null) System.exit(0);
                else new MainAppFrame(newUser).setVisible(true);
            });
        }
    }
}
