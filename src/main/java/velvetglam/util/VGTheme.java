package velvetglam.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * VelvetGlam Global Theme Manager.
 *
 * Central design system — ALL colours, fonts, and component styles
 * are defined here. Every panel uses this class so the app stays
 * 100% visually consistent.
 *
 * OOP Concepts: Encapsulation, Abstraction, Utility class pattern.
 */
public final class VGTheme {

    // ════════════════════════════════════════════════════════
    //  COLOUR PALETTE — Luxury Cosmetics
    // ════════════════════════════════════════════════════════

    // Backgrounds
    public static final Color BG_DARK       = new Color( 14,   6,  35);   // App dark bg
    public static final Color BG_SIDEBAR    = new Color( 18,   6,  45);   // Sidebar
    public static final Color BG_CARD       = new Color( 26,  12,  60);   // Dark card
    public static final Color BG_CARD_LIGHT = Color.WHITE;                 // Light card
    public static final Color BG_PAGE       = new Color(246, 244, 254);    // Content area
    public static final Color BG_INPUT      = new Color( 40,  18,  80);   // Input fields (dark)
    public static final Color BG_INPUT_LT   = new Color(250, 247, 255);   // Input fields (light)
    public static final Color BG_HEADER     = new Color( 20,   8,  52);   // Panel headers
    public static final Color BG_TABLE_ALT  = new Color(248, 245, 255);   // Alt row light
    public static final Color BG_TABLE_SEL  = new Color(225, 190, 255);   // Selected row

    // Primary brand colors
    public static final Color C_PRIMARY     = new Color(108,  52, 168);   // Deep purple
    public static final Color C_PRIMARY_LT  = new Color(149,  96, 234);   // Light purple
    public static final Color C_PRIMARY_DK  = new Color( 72,  28, 120);   // Dark purple
    public static final Color C_ROSE_GOLD   = new Color(198, 144, 120);   // Rose gold
    public static final Color C_ROSE_LT     = new Color(240, 200, 180);   // Light rose
    public static final Color C_LAVENDER    = new Color(200, 175, 240);   // Lavender

    // Semantic colors
    public static final Color C_SUCCESS     = new Color( 39, 174,  96);
    public static final Color C_SUCCESS_LT  = new Color(212, 249, 230);
    public static final Color C_DANGER      = new Color(220,  53,  69);
    public static final Color C_DANGER_LT   = new Color(255, 220, 220);
    public static final Color C_WARNING     = new Color(255, 152,   0);
    public static final Color C_WARNING_LT  = new Color(255, 243, 220);
    public static final Color C_INFO        = new Color( 23, 162, 184);
    public static final Color C_INFO_LT     = new Color(210, 245, 250);

    // Text colors
    public static final Color TEXT_PRIMARY   = new Color( 30,  15,  65);   // Dark text
    public static final Color TEXT_SECONDARY = new Color( 90,  70, 130);   // Muted text
    public static final Color TEXT_WHITE     = Color.WHITE;
    public static final Color TEXT_LIGHT     = new Color(200, 175, 240);   // Light on dark
    public static final Color TEXT_DIM       = new Color(110,  85, 150);   // Dim on dark

    // Card accent colors for stat cards
    public static final Color ACCENT_BLUE    = new Color( 63, 114, 220);
    public static final Color ACCENT_RED     = new Color(210,  55,  55);
    public static final Color ACCENT_GREEN   = new Color( 39, 160,  80);
    public static final Color ACCENT_ORANGE  = new Color(218, 110,  40);
    public static final Color ACCENT_TEAL    = new Color( 20, 150, 140);
    public static final Color ACCENT_ROSE    = new Color(190,  80, 120);
    public static final Color ACCENT_INDIGO  = new Color( 75,  70, 200);
    public static final Color ACCENT_GOLD    = new Color(180, 140,  40);

    // ════════════════════════════════════════════════════════
    //  TYPOGRAPHY
    // ════════════════════════════════════════════════════════
    public static final Font FONT_H1     = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_H3     = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_H4     = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_TINY   = new Font("Segoe UI", Font.PLAIN,  9);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD,  11);
    public static final Font FONT_NUM    = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FONT_TABLE  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_THEAD  = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_BTN    = new Font("Segoe UI", Font.BOLD,  12);

    // Prevent instantiation
    private VGTheme() {}

    // ════════════════════════════════════════════════════════
    //  BUTTON FACTORY
    // ════════════════════════════════════════════════════════

    /** Primary filled button (purple gradient) */
    public static JButton primaryBtn(String text) {
        return styledBtn(text, C_PRIMARY, C_PRIMARY_LT, Color.WHITE);
    }

    /** Success green button */
    public static JButton successBtn(String text) {
        return styledBtn(text, C_SUCCESS, new Color(46, 200, 110), Color.WHITE);
    }

    /** Danger red button */
    public static JButton dangerBtn(String text) {
        return styledBtn(text, C_DANGER, new Color(240, 80, 80), Color.WHITE);
    }

    /** Warning orange button */
    public static JButton warningBtn(String text) {
        return styledBtn(text, C_WARNING, new Color(255, 180, 50), Color.WHITE);
    }

    /** Neutral grey button */
    public static JButton neutralBtn(String text) {
        return styledBtn(text, new Color(120, 100, 160),
                         new Color(140, 120, 180), Color.WHITE);
    }

    /** Rose gold accent button */
    public static JButton roseBtn(String text) {
        return styledBtn(text, C_ROSE_GOLD, C_ROSE_LT, Color.WHITE);
    }

    private static JButton styledBtn(String text, Color c1, Color c2, Color fg) {
        JButton b = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovered = true; repaint();
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovered = false; repaint();
                    }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color from = hovered ? c2 : c1;
                Color to   = hovered ? c1 : c2;
                GradientPaint gp = new GradientPaint(0,0,from,getWidth(),0,to);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Subtle shine on top half
                g2.setColor(new Color(255,255,255,30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 8, 8);
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BTN);
        b.setForeground(fg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ════════════════════════════════════════════════════════
    //  INPUT FIELD FACTORY
    // ════════════════════════════════════════════════════════

    public static JTextField inputField() {
        JTextField f = new JTextField();
        styleInput(f);
        return f;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        styleInput(f);
        f.setEchoChar('●');
        return f;
    }

    public static JComboBox<?> comboBox() {
        JComboBox<?> c = new JComboBox<>();
        c.setFont(FONT_BODY);
        c.setBackground(BG_INPUT_LT);
        c.setForeground(TEXT_PRIMARY);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 180, 230), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return c;
    }

    private static void styleInput(JTextField f) {
        f.setFont(FONT_BODY);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(BG_INPUT_LT);
        f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 180, 230), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setPreferredSize(new Dimension(0, 38));
        // Focus highlight
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C_PRIMARY, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 180, 230), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    // ════════════════════════════════════════════════════════
    //  TABLE STYLING
    // ════════════════════════════════════════════════════════

    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setSelectionBackground(BG_TABLE_SEL);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(BG_PAGE);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setFont(FONT_TABLE);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (sel) {
                    setBackground(BG_TABLE_SEL);
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : BG_TABLE_ALT);
                    setForeground(TEXT_PRIMARY);
                }
                return this;
            }
        });

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_THEAD);
        header.setBackground(C_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val == null ? "" : val.toString());
                lbl.setFont(FONT_THEAD);
                lbl.setForeground(Color.WHITE);
                lbl.setBackground(C_PRIMARY);
                lbl.setOpaque(true);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
    }

    // ════════════════════════════════════════════════════════
    //  SECTION LABEL
    // ════════════════════════════════════════════════════════

    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H3);
        l.setForeground(TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    // ════════════════════════════════════════════════════════
    //  CARD PANEL
    // ════════════════════════════════════════════════════════

    /** Creates a white rounded card panel */
    public static JPanel card() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 14, 14);
                // White card
                g2.setColor(BG_CARD_LIGHT);
                g2.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 14, 14);
            }
            @Override public boolean isOpaque() { return false; }
        };
    }

    /** Card with colored left accent bar */
    public static JPanel accentCard(Color accent) {
        JPanel card = card();
        card.setLayout(new BorderLayout());
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(4, 0));
        bar.setOpaque(true);
        card.add(bar, BorderLayout.WEST);
        return card;
    }

    // ════════════════════════════════════════════════════════
    //  SCROLL PANE STYLING
    // ════════════════════════════════════════════════════════

    public static JScrollPane scrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(null);
        sp.setBackground(BG_PAGE);
        sp.getViewport().setBackground(BG_PAGE);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        sp.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        return sp;
    }

    // ════════════════════════════════════════════════════════
    //  PANEL HEADER
    // ════════════════════════════════════════════════════════

    public static JPanel panelHeader(String title, String subtitle) {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, BG_HEADER,
                    getWidth(), 0, new Color(45, 15, 100));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Rose gold bottom line
                g2.setColor(C_ROSE_GOLD);
                g2.fillRect(0, getHeight()-3, getWidth(), 3);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 24, 16, 24));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel ttl = new JLabel(title);
        ttl.setFont(FONT_H2);
        ttl.setForeground(Color.WHITE);
        ttl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel(subtitle);
        sub.setFont(FONT_SMALL);
        sub.setForeground(new Color(160, 130, 200));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(ttl);
        left.add(Box.createVerticalStrut(3));
        left.add(sub);

        bar.add(left, BorderLayout.CENTER);
        return bar;
    }

    // ════════════════════════════════════════════════════════
    //  BADGE LABEL  (notification count)
    // ════════════════════════════════════════════════════════

    public static JLabel badge(int count, Color bg) {
        JLabel b = new JLabel(count > 99 ? "99+" : String.valueOf(count)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 9));
        b.setForeground(Color.WHITE);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setPreferredSize(new Dimension(20, 20));
        b.setOpaque(false);
        return b;
    }

    // ════════════════════════════════════════════════════════
    //  MODERN SCROLLBAR
    // ════════════════════════════════════════════════════════

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor      = new Color(180, 140, 220, 160);
            trackColor      = new Color(240, 235, 250);
        }
        @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
        @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
        private JButton zeroBtn() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 8, 8);
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(trackColor);
            g2.fillRect(r.x, r.y, r.width, r.height);
        }
    }
}
