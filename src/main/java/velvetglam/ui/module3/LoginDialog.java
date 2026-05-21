package velvetglam.ui.module3;

import velvetglam.dao.UserDAO;
import velvetglam.model.UserAccount;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;

/**
 * VelvetGlam — Full-Screen Login Page.
 *
 * Replaces the old dialog-box login with a full-screen professional
 * login experience styled like a modern web application.
 *
 * OOP Concepts Demonstrated:
 *  - Encapsulation  : credentials are private, never exposed
 *  - Exception Handling : handles empty fields, wrong credentials, DB errors
 */
public class LoginDialog extends JDialog {

    // ── Colour Palette (Rose Gold + Deep Purple) ──────────────
    private static final Color C_DARK_BG    = new Color( 18,   8,  40);   // very dark purple bg
    private static final Color C_PRIMARY    = new Color(108,  52, 168);   // main purple
    private static final Color C_PRIMARY2   = new Color( 74,  20, 140);   // darker purple
    private static final Color C_ROSE_GOLD  = new Color(198, 144, 120);   // rose gold accent
    private static final Color C_ROSE2      = new Color(232, 180, 160);   // lighter rose gold
    private static final Color C_CARD_BG    = new Color( 30,  12,  65);   // dark card
    private static final Color C_FIELD_BG   = new Color( 45,  20,  85);   // input background
    private static final Color C_FIELD_BOR  = new Color( 90,  50, 140);   // input border
    private static final Color C_TEXT       = new Color(240, 230, 255);   // main text
    private static final Color C_SUBTEXT    = new Color(160, 130, 200);   // subtitle text
    private static final Color C_ERROR      = new Color(255, 100, 100);   // error red
    private static final Color C_SUCCESS    = new Color( 80, 200, 120);   // success green

    // ── Fields ───────────────────────────────────────────────
    private final JTextField     tfUsername = new JTextField();
    private final JPasswordField pfPassword = new JPasswordField();
    private       JLabel         lblError;

    private final UserDAO     userDAO  = new UserDAO();
    private       UserAccount loggedIn = null;

    // ── Constructor ──────────────────────────────────────────
    public LoginDialog(Window owner) {
        super(owner, "VelvetGlam — Login", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);   // remove title bar for full-screen feel

        // Full screen size
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with dark background
        JPanel main = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Dark gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0,             new Color( 12,  4, 30),
                    getWidth(), getHeight(), new Color( 35, 10, 75));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative circles
                g2.setColor(new Color(108, 52, 168, 40));
                g2.fillOval(-100, -100, 400, 400);
                g2.fillOval(getWidth() - 200, getHeight() - 200, 400, 400);
                g2.setColor(new Color(198, 144, 120, 25));
                g2.fillOval(getWidth() - 300, -50, 350, 350);
                g2.fillOval(-80, getHeight() - 250, 300, 300);
            }
        };
        main.setOpaque(false);
        setContentPane(main);

        // Center the login card
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(buildLoginCard());
        main.add(center, BorderLayout.CENTER);

        // Bottom branding bar
        main.add(buildBottomBar(), BorderLayout.SOUTH);

        // Enter key on password
        pfPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        });
        tfUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) pfPassword.requestFocus();
            }
        });
    }

    // ── Login Card ───────────────────────────────────────────
    private JPanel buildLoginCard() {
        // Rounded card panel
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Card background
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                // Top accent line (rose gold)
                GradientPaint gp = new GradientPaint(
                    0, 0, C_ROSE_GOLD, getWidth(), 0, C_PRIMARY);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                // Subtle inner glow
                g2.setColor(new Color(108, 52, 168, 30));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 24, 24);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 520));
        card.setBorder(new EmptyBorder(40, 44, 40, 44));

        // ── Logo section ──────────────────────────────────────
        JLabel logoIcon = new JLabel("VelvetGlam");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 30));
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Rose gold underline effect label
        JLabel logoAccent = new JLabel("SMART COSMETICS STORE");
        logoAccent.setFont(new Font("Segoe UI", Font.BOLD, 10));
        logoAccent.setForeground(C_ROSE_GOLD);
        logoAccent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(C_TEXT);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Sign in to your staff account");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(C_SUBTEXT);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logoIcon);
        card.add(Box.createVerticalStrut(4));
        card.add(logoAccent);
        card.add(Box.createVerticalStrut(24));
        card.add(welcomeLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(32));

        // ── Username field ────────────────────────────────────
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        styleLoginField(tfUsername, "Enter your username");
        card.add(tfUsername);
        card.add(Box.createVerticalStrut(18));

        // ── Password field ────────────────────────────────────
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        pfPassword.setEchoChar('●');
        styleLoginField(pfPassword, "Enter your password");
        card.add(pfPassword);
        card.add(Box.createVerticalStrut(8));

        // ── Show password ─────────────────────────────────────
        JCheckBox cbShow = new JCheckBox("Show password");
        cbShow.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbShow.setForeground(C_SUBTEXT);
        cbShow.setOpaque(false);
        cbShow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbShow.addActionListener(e ->
            pfPassword.setEchoChar(cbShow.isSelected() ? (char)0 : '●'));
        card.add(cbShow);
        card.add(Box.createVerticalStrut(12));

        // ── Error label ───────────────────────────────────────
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblError.setForeground(C_ERROR);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblError);
        card.add(Box.createVerticalStrut(12));

        // ── Login button ──────────────────────────────────────
        JButton btnLogin = buildLoginButton();
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(14));

        // ── Cancel link ───────────────────────────────────────
        JLabel lblCancel = new JLabel("Exit Application");
        lblCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCancel.setForeground(C_SUBTEXT);
        lblCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblCancel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
            @Override public void mouseEntered(MouseEvent e) {
                lblCancel.setForeground(C_ROSE2);
            }
            @Override public void mouseExited(MouseEvent e) {
                lblCancel.setForeground(C_SUBTEXT);
            }
        });
        card.add(lblCancel);

        return card;
    }

    // ── Login Button ─────────────────────────────────────────
    private JButton buildLoginButton() {
        JButton btn = new JButton("Sign In") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, C_PRIMARY,
                    getWidth(), 0, new Color(160, 80, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(332, 46));
        btn.setPreferredSize(new Dimension(332, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> attemptLogin());

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            }
        });
        return btn;
    }

    // ── Bottom bar ───────────────────────────────────────────
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel lbl = new JLabel("VelvetGlam v1.0  |  OOP Lab Project  |  Group 10  |  Air University");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(80, 60, 110));
        bar.add(lbl);
        return bar;
    }

    // ── Field helpers ─────────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(C_SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleLoginField(JTextField f, String placeholder) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(C_TEXT);
        f.setBackground(C_FIELD_BG);
        f.setCaretColor(C_ROSE2);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_FIELD_BOR, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.setMaximumSize(new Dimension(332, 46));
        f.setPreferredSize(new Dimension(332, 46));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Focus border highlight
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_ROSE_GOLD, 2),
                    BorderFactory.createEmptyBorder(9, 13, 9, 13)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_FIELD_BOR, 1),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    // ── Login Logic ──────────────────────────────────────────
    private void attemptLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());
        lblError.setText(" ");

        if (username.isEmpty()) {
            lblError.setText("  Please enter your username.");
            tfUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            lblError.setText("  Please enter your password.");
            pfPassword.requestFocus();
            return;
        }

        try {
            UserAccount account = userDAO.authenticate(username, password);
            if (account == null) {
                lblError.setText("  Incorrect username or password. Please try again.");
                pfPassword.setText("");
                pfPassword.requestFocus();
            } else {
                loggedIn = account;
                dispose();
            }
        } catch (SQLException ex) {
            lblError.setText("  Database error: " + ex.getMessage());
        }
    }

    public UserAccount getLoggedInUser() { return loggedIn; }
}
