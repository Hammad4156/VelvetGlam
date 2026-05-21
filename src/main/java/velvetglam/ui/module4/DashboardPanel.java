package velvetglam.ui.module4;

import velvetglam.dao.DashboardDAO;
import velvetglam.model.UserAccount;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

/**
 * Module 4 — Dashboard Panel (Rose Gold + Deep Purple theme).
 */
public class DashboardPanel extends JPanel {

    // ── Colour Palette ────────────────────────────────────────
    private static final Color C_BG         = new Color(245, 243, 252);
    private static final Color C_PRIMARY     = new Color(108,  52, 168);
    private static final Color C_ROSE_GOLD   = new Color(198, 144, 120);
    private static final Color C_CARD_BG     = Color.WHITE;
    private static final Color C_HEADER_BG   = new Color( 22,   8,  55);

    // Card accent colors
    private static final Color C_BLUE    = new Color( 63, 114, 220);
    private static final Color C_RED     = new Color(210,  55,  55);
    private static final Color C_GREEN   = new Color( 39, 160,  80);
    private static final Color C_ORANGE  = new Color(218, 110,  40);
    private static final Color C_TEAL    = new Color( 20, 150, 140);
    private static final Color C_ROSE    = new Color(190,  80, 120);
    private static final Color C_INDIGO  = new Color( 75,  70, 200);
    private static final Color C_GOLD    = new Color(180, 140,  40);

    private final UserAccount  loggedInUser;
    private final DashboardDAO dao = new DashboardDAO();

    // Stat labels
    private JLabel lblProducts, lblLowStock, lblCustomers, lblTodayRev,
                   lblSales, lblTotalRev, lblStaff, lblBrands;

    // Charts
    private WeeklyRevenueChart  weeklyChart;
    private CategoryBarChart    categoryChart;
    private TopProductsPanel    topPanel;

    public DashboardPanel(UserAccount user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);

        add(buildHeader(), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(buildBody());
        scroll.setBorder(null);
        scroll.setBackground(C_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(C_BG);
        add(scroll, BorderLayout.CENTER);

        refreshData();
    }

    // ── Header ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, C_HEADER_BG,
                    getWidth(), 0, new Color(50, 15, 110));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Rose gold bottom accent
                g2.setColor(C_ROSE_GOLD);
                g2.fillRect(0, getHeight()-3, getWidth(), 3);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Store overview and analytics  —  " + LocalDate.now());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(160, 130, 200));

        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel userTag = new JLabel("Logged in as:  " + loggedInUser.getUsername()
                + "  (" + loggedInUser.getRole() + ")");
        userTag.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTag.setForeground(new Color(160, 130, 200));

        JButton btnRefresh = buildHeaderBtn("Refresh Data");
        btnRefresh.addActionListener(e -> refreshData());

        right.add(userTag);
        right.add(btnRefresh);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton buildHeaderBtn(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(108, 52, 168, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(C_ROSE_GOLD);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 18, 7, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Body ─────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(C_BG);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        body.add(sectionLabel("Store Overview"));
        body.add(Box.createVerticalStrut(12));
        body.add(buildStatCards());

        body.add(Box.createVerticalStrut(24));
        body.add(sectionLabel("Analytics"));
        body.add(Box.createVerticalStrut(12));
        body.add(buildChartsRow());

        body.add(Box.createVerticalStrut(24));
        body.add(sectionLabel("Top Selling Products"));
        body.add(Box.createVerticalStrut(12));
        topPanel = new TopProductsPanel();
        topPanel.setAlignmentX(LEFT_ALIGNMENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        body.add(topPanel);

        return body;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(new Color(60, 30, 100));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ── Stat Cards ───────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel grid = new JPanel(new GridLayout(2, 4, 14, 14));
        grid.setOpaque(false);
        grid.setAlignmentX(LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));

        lblProducts   = new JLabel("—");
        lblLowStock   = new JLabel("—");
        lblCustomers  = new JLabel("—");
        lblTodayRev   = new JLabel("—");
        lblSales      = new JLabel("—");
        lblTotalRev   = new JLabel("—");
        lblStaff      = new JLabel("—");
        lblBrands     = new JLabel("—");

        grid.add(makeCard("Total Products",    lblProducts,  C_BLUE,   "Products in store"));
        grid.add(makeCard("Low Stock Alert",   lblLowStock,  C_RED,    "Below threshold"));
        grid.add(makeCard("Total Customers",   lblCustomers, C_TEAL,   "Registered customers"));
        grid.add(makeCard("Today's Revenue",   lblTodayRev,  C_GREEN,  "Sales made today"));
        grid.add(makeCard("Total Sales",       lblSales,     C_INDIGO, "All transactions"));
        grid.add(makeCard("Total Revenue",     lblTotalRev,  C_ORANGE, "All time earnings"));
        grid.add(makeCard("Staff Members",     lblStaff,     C_ROSE,   "Active staff"));
        grid.add(makeCard("Brands",            lblBrands,    C_GOLD,   "Product brands"));

        return grid;
    }

    private JPanel makeCard(String title, JLabel valueLabel, Color accent, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // White card
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Colored top bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);
                // Subtle shadow-like bottom
                g2.setColor(new Color(accent.getRed(), accent.getGreen(),
                                      accent.getBlue(), 15));
                g2.fillRoundRect(0, getHeight()-20, getWidth(), 20, 14, 14);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lTitle.setForeground(new Color(50, 40, 80));

        JLabel lSub = new JLabel(subtitle);
        lSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lSub.setForeground(new Color(150, 130, 180));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(lTitle);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lSub);

        card.add(valueLabel, BorderLayout.NORTH);
        card.add(textPanel,  BorderLayout.SOUTH);
        return card;
    }

    // ── Charts Row ───────────────────────────────────────────
    private JPanel buildChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        weeklyChart   = new WeeklyRevenueChart();
        categoryChart = new CategoryBarChart();

        row.add(wrapChart(weeklyChart,   "Revenue — Last 7 Days"));
        row.add(wrapChart(categoryChart, "Products by Category"));
        return row;
    }

    private JPanel wrapChart(JPanel chart, String title) {
        JPanel w = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        w.setOpaque(false);
        w.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel ttl = new JLabel(title);
        ttl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ttl.setForeground(new Color(60, 30, 100));
        w.add(ttl,   BorderLayout.NORTH);
        w.add(chart, BorderLayout.CENTER);
        return w;
    }

    // ── Refresh ──────────────────────────────────────────────
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int    tp, ls, tc, ts, tst, br, tsc;
            double tr, trev;
            Map<String, Double>  weekly;
            Map<String, Integer> cats, tops;
            String err;

            @Override protected Void doInBackground() {
                try {
                    tp  = dao.getTotalProducts();
                    ls  = dao.getLowStockCount();
                    tc  = dao.getTotalCustomers();
                    tr  = dao.getTodayRevenue();
                    ts  = dao.getTotalSales();
                    trev= dao.getTotalRevenue();
                    tst = dao.getTotalStaff();
                    br  = dao.getTotalBrands();
                    tsc = dao.getTodaySalesCount();
                    weekly = dao.getWeeklyRevenue();
                    cats   = dao.getProductsByCategory();
                    tops   = dao.getTopSellingProducts();
                } catch (SQLException ex) { err = ex.getMessage(); }
                return null;
            }

            @Override protected void done() {
                if (err != null) {
                    JOptionPane.showMessageDialog(DashboardPanel.this,
                        "Dashboard error:\n" + err, "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                lblProducts .setText(String.valueOf(tp));
                lblLowStock .setText(String.valueOf(ls));
                lblCustomers.setText(String.valueOf(tc));
                lblTodayRev .setText("Rs " + String.format("%,.0f", tr));
                lblSales    .setText(String.valueOf(ts));
                lblTotalRev .setText("Rs " + String.format("%,.0f", trev));
                lblStaff    .setText(String.valueOf(tst));
                lblBrands   .setText(String.valueOf(br));

                weeklyChart  .setData(weekly);
                categoryChart.setData(cats);
                topPanel     .setData(tops);
                revalidate(); repaint();
            }
        };
        worker.execute();
    }

    // ══════════════════════════════════════════════════════════
    //  WEEKLY REVENUE CHART
    // ══════════════════════════════════════════════════════════
    private static class WeeklyRevenueChart extends JPanel {
        private Map<String, Double> data;

        WeeklyRevenueChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(100, 200));
        }

        void setData(Map<String, Double> data) { this.data = data; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int pL = 52, pB = 30, pT = 14, pR = 10;
            int cW = w - pL - pR, cH = h - pB - pT;

            double maxVal = data.values().stream()
                .mapToDouble(Double::doubleValue).max().orElse(1.0);
            if (maxVal == 0) maxVal = 1;

            String[] keys = data.keySet().toArray(new String[0]);
            double[] vals = data.values().stream()
                .mapToDouble(Double::doubleValue).toArray();
            int n = keys.length;

            // Grid lines
            g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            g2.setColor(new Color(200, 185, 230, 80));
            for (int i = 1; i <= 4; i++) {
                int y = pT + cH - (int)(cH * i / 4.0);
                g2.drawLine(pL, y, pL + cW, y);
            }
            g2.setStroke(new BasicStroke(1));

            // Axes
            g2.setColor(new Color(180, 160, 220));
            g2.drawLine(pL, pT, pL, pT + cH);
            g2.drawLine(pL, pT + cH, pL + cW, pT + cH);

            int barW = Math.max(6, (cW - (n+1)*6) / n);
            int gap  = (cW - n * barW) / (n+1);

            for (int i = 0; i < n; i++) {
                int bH = (int)(vals[i] / maxVal * cH);
                int x  = pL + gap + i * (barW + gap);
                int y  = pT + cH - bH;

                // Bar with gradient
                GradientPaint gp = new GradientPaint(
                    x, y, new Color(168, 100, 230),
                    x, y + bH, new Color(80, 30, 150));
                g2.setPaint(gp);
                g2.fillRoundRect(x, y, barW, bH, 5, 5);

                // Rose gold top cap
                g2.setColor(C_ROSE_GOLD);
                g2.fillRoundRect(x, y, barW, 4, 3, 3);

                // Value label
                if (vals[i] > 0) {
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
                    g2.setColor(new Color(80, 40, 130));
                    String val = String.format("%.0f", vals[i]);
                    int sw = g2.getFontMetrics().stringWidth(val);
                    g2.drawString(val, x + barW/2 - sw/2, y - 3);
                }

                // X-axis label
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(new Color(120, 90, 160));
                int sw = g2.getFontMetrics().stringWidth(keys[i]);
                g2.drawString(keys[i], x + barW/2 - sw/2, pT + cH + 14);
            }

            // Y-axis label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            g2.setColor(new Color(120, 90, 160));
            g2.drawString("PKR", 2, pT + cH/2);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  CATEGORY CHART
    // ══════════════════════════════════════════════════════════
    private static class CategoryBarChart extends JPanel {
        private Map<String, Integer> data;
        private static final Color[] COLORS = {
            new Color(108,52,168), new Color(63,114,220),
            new Color(39,160,80),  new Color(218,110,40),
            new Color(190,80,120), new Color(20,150,140)
        };

        CategoryBarChart() { setOpaque(false); setPreferredSize(new Dimension(100, 200)); }
        void setData(Map<String, Integer> data) { this.data = data; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int pL = 90, pR = 40, pT = 10, pB = 10;
            int cW = w - pL - pR, cH = h - pT - pB;

            String[] keys = data.keySet().toArray(new String[0]);
            int[] vals = data.values().stream().mapToInt(Integer::intValue).toArray();
            int maxV = 1;
            for (int v : vals) maxV = Math.max(maxV, v);

            int rowH = cH / Math.max(keys.length, 1);

            for (int i = 0; i < keys.length; i++) {
                int bH  = Math.max(rowH - 8, 10);
                int bW  = (int)((double)vals[i] / maxV * cW);
                int y   = pT + i * rowH + (rowH - bH) / 2;
                Color c = COLORS[i % COLORS.length];

                // Background track
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                g2.fillRoundRect(pL, y, cW, bH, 6, 6);

                // Filled bar
                GradientPaint gp = new GradientPaint(
                    pL, y, c.brighter(),
                    pL + Math.max(bW, 2), y, c);
                g2.setPaint(gp);
                g2.fillRoundRect(pL, y, Math.max(bW, 4), bH, 6, 6);

                // Category name
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(new Color(60, 40, 100));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(keys[i], pL - fm.stringWidth(keys[i]) - 6,
                              y + bH/2 + fm.getAscent()/2 - 1);

                // Value
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                String cnt = String.valueOf(vals[i]);
                if (bW > 20) {
                    g2.drawString(cnt,
                        pL + bW - g2.getFontMetrics().stringWidth(cnt) - 5,
                        y + bH/2 + 4);
                } else {
                    g2.setColor(c);
                    g2.drawString(cnt, pL + bW + 4, y + bH/2 + 4);
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  TOP PRODUCTS PANEL
    // ══════════════════════════════════════════════════════════
    private static class TopProductsPanel extends JPanel {
        private final JPanel rows = new JPanel();

        TopProductsPanel() {
            setLayout(new BorderLayout(0, 0)) ;
            setOpaque(false);

            // Header
            JPanel header = buildRow("#", "Product Name", "Units Sold", true, null);
            add(header, BorderLayout.NORTH);

            rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
            rows.setOpaque(false);
            add(rows, BorderLayout.CENTER);
        }

        void setData(Map<String, Integer> data) {
            rows.removeAll();
            if (data == null || data.isEmpty()) {
                JLabel lbl = new JLabel("  No sales data yet.");
                lbl.setForeground(new Color(150, 120, 180));
                lbl.setBorder(new EmptyBorder(8, 4, 4, 4));
                rows.add(lbl);
            } else {
                Color[] accents = {C_GOLD, new Color(150,150,150),
                    new Color(180,120,80), C_BLUE, C_GREEN};
                int rank = 1;
                for (Map.Entry<String, Integer> e : data.entrySet()) {
                    Color ac = accents[Math.min(rank-1, accents.length-1)];
                    rows.add(buildRow(rank + "", e.getKey(),
                             e.getValue() + " units", false, ac));
                    rank++;
                }
            }
            rows.revalidate();
            rows.repaint();
        }

        private static final Color C_GOLD = new Color(180, 140, 40);
        private static final Color C_BLUE = new Color(63, 114, 220);
        private static final Color C_GREEN = new Color(39, 160, 80);

        private JPanel buildRow(String rank, String name, String qty,
                                boolean isHeader, Color accent) {
            JPanel row = new JPanel(new BorderLayout(0, 0)) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    if (isHeader) {
                        GradientPaint gp = new GradientPaint(
                            0, 0, new Color(80,30,140),
                            getWidth(), 0, new Color(108,52,168));
                        g2.setPaint(gp);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                        if (accent != null) {
                            g2.setColor(new Color(accent.getRed(),
                                accent.getGreen(), accent.getBlue(), 60));
                            g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                        }
                    }
                }
            };
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(10, 14, 10, 14));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

            Font f = isHeader
                ? new Font("Segoe UI", Font.BOLD, 12)
                : new Font("Segoe UI", Font.PLAIN, 13);
            Color fg = isHeader ? Color.WHITE : new Color(50, 30, 80);

            JLabel lRank = new JLabel(rank);
            lRank.setFont(isHeader ? f : new Font("Segoe UI", Font.BOLD, 12));
            lRank.setForeground(isHeader ? Color.WHITE
                : (accent != null ? accent : new Color(130, 100, 180)));
            lRank.setPreferredSize(new Dimension(30, 20));

            JLabel lName = new JLabel(name);
            lName.setFont(f);
            lName.setForeground(fg);

            JLabel lQty = new JLabel(qty);
            lQty.setFont(isHeader ? f : new Font("Segoe UI", Font.BOLD, 12));
            lQty.setForeground(isHeader ? Color.WHITE : C_BLUE);
            lQty.setHorizontalAlignment(SwingConstants.RIGHT);
            lQty.setPreferredSize(new Dimension(100, 20));

            row.add(lRank, BorderLayout.WEST);
            row.add(lName, BorderLayout.CENTER);
            row.add(lQty,  BorderLayout.EAST);

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(new EmptyBorder(isHeader ? 0 : 2, 0, isHeader ? 0 : 2, 0));
            wrapper.add(row, BorderLayout.CENTER);
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            return wrapper;
        }
    }
}
