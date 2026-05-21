package velvetglam.ui.module4;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import velvetglam.model.UserAccount;
import velvetglam.ui.module3.LoginDialog;
import velvetglam.util.DatabaseConnection;
import velvetglam.util.VGTheme;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * VelvetGlamApp — Main Entry Point.
 *
 * Applies FlatLaf modern Look and Feel, then shows login
 * and launches the full integrated application.
 *
 * OOP Concepts:
 *  - Abstraction     : DatabaseConnection hides JDBC
 *  - Exception Handling : DB checked at startup
 *  - Object Interaction : wires LoginDialog → MainAppFrame
 */
public class VelvetGlamApp {

    public static void main(String[] args) {

        // ── Apply FlatLaf + custom overrides ──────────────────
        applyTheme();

        SwingUtilities.invokeLater(() -> {
            if (!testDatabase()) {
                System.exit(1);
            }

            // Show full-screen login
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);

            UserAccount user = login.getLoggedInUser();
            if (user == null) System.exit(0);

            // Launch main app
            new MainAppFrame(user).setVisible(true);
        });
    }

    private static void applyTheme() {
        try {
            // FlatMacDarkLaf gives a modern premium dark feel
            FlatMacDarkLaf.setup();
            FlatLaf.updateUI();
        } catch (Exception e) {
            // Fallback to Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    try { UIManager.setLookAndFeel(info.getClassName()); } catch (Exception ignored) {}
                    break;
                }
            }
        }

        // Global overrides for premium feel
        UIManager.put("Table.showHorizontalLines",  false);
        UIManager.put("Table.showVerticalLines",    false);
        UIManager.put("Table.intercellSpacing",     new Dimension(0, 2));
        UIManager.put("ScrollBar.width",            8);
        UIManager.put("TabbedPane.tabHeight",       38);
        UIManager.put("Button.arc",                 8);
        UIManager.put("Component.arc",              8);
        UIManager.put("TextComponent.arc",          8);
        UIManager.put("ScrollPane.border",          BorderFactory.createEmptyBorder());
        UIManager.put("TableHeader.background",
            new javax.swing.plaf.ColorUIResource(VGTheme.C_PRIMARY));
        UIManager.put("TableHeader.foreground",
            new javax.swing.plaf.ColorUIResource(Color.WHITE));
        UIManager.put("TableHeader.font",
            new javax.swing.plaf.FontUIResource("Segoe UI", Font.BOLD, 12));
    }

    private static boolean testDatabase() {
        try (Connection c = DatabaseConnection.getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "<html><b>Cannot connect to the database.</b><br><br>"
                + "Please ensure:<br>"
                + "1. MySQL service is running<br>"
                + "2. Database <b>velvetglam_db</b> exists<br>"
                + "3. Schema SQL has been executed<br><br>"
                + "<font color='gray'>Error: " + e.getMessage() + "</font></html>",
                "Database Connection Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
