package velvetglam.ui.module2;

import velvetglam.model.Sale;
import velvetglam.model.SaleItem;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

/**
 * Modal dialog that displays a formatted sale receipt.
 *
 * Accepts a Sale that already has its items loaded (via SaleDAO.getSaleItems).
 * Provides a "Copy to Clipboard" button so staff can paste the receipt
 * elsewhere if needed.
 */
public class SaleReceiptDialog extends JDialog {

    // ── Colour Palette ───────────────────────────────────────
    private static final Color C_PRIMARY = new Color(108, 52, 168);
    private static final Color C_GREEN   = new Color( 56, 142,  60);
    private static final Color C_COPY    = new Color( 25, 118, 210);
    private static final Color C_BG      = new Color(250, 248, 255);

    // ── Constructor ──────────────────────────────────────────
    public SaleReceiptDialog(Window owner, Sale sale) {
        super(owner, "🧾  Sale Receipt  —  #" + sale.getSaleId(),
              ModalityType.APPLICATION_MODAL);

        setBackground(C_BG);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(sale), BorderLayout.NORTH);
        add(buildReceiptArea(sale), BorderLayout.CENTER);
        add(buildButtons(sale),     BorderLayout.SOUTH);

        setSize(420, 560);
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    // ── Header bar ───────────────────────────────────────────
    private JPanel buildHeader(Sale sale) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel left = new JLabel("🧾  Receipt  #" + sale.getSaleId());
        left.setFont(new Font("Segoe UI", Font.BOLD, 15));
        left.setForeground(Color.WHITE);
        bar.add(left, BorderLayout.WEST);

        JLabel right = new JLabel(sale.getSaleDate().toString() + "  ");
        right.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        right.setForeground(new Color(220, 200, 255));
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // ── Receipt text area ────────────────────────────────────
    private JScrollPane buildReceiptArea(Sale sale) {
        // Ensure items are attached before generating receipt
        JTextArea area = new JTextArea(sale.generateReceipt());
        area.setFont(new Font("Courier New", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(255, 255, 255));
        area.setForeground(new Color(30, 30, 30));
        area.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        area.setCaretPosition(0);

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        return sp;
    }

    // ── Buttons ──────────────────────────────────────────────
    private JPanel buildButtons(Sale sale) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        p.setBackground(C_BG);

        JButton btnCopy  = btn("📋  Copy to Clipboard", C_COPY);
        JButton btnClose = btn("✓  Close",              C_GREEN);

        btnCopy.addActionListener(e -> {
            Toolkit.getDefaultToolkit()
                   .getSystemClipboard()
                   .setContents(new StringSelection(sale.generateReceipt()), null);
            JOptionPane.showMessageDialog(this,
                "Receipt copied to clipboard!",
                "Copied", JOptionPane.INFORMATION_MESSAGE);
        });

        btnClose.addActionListener(e -> dispose());

        p.add(btnCopy);
        p.add(btnClose);
        return p;
    }

    // ── Helper ───────────────────────────────────────────────
    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 18, 7, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
