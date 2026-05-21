package velvetglam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class that abstracts all JDBC setup into one reusable place.
 * Every DAO opens its own short-lived connection so there are no stale-
 * connection problems across modules.
 *
 * Change PASSWORD if your XAMPP MySQL root has a password set.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/velvetglam_db"
                                           + "?useSSL=false&allowPublicKeyRetrieval=true"
                                           + "&serverTimezone=UTC";
    private static final String USER     = "root";
    private static final String PASSWORD = "Fani786";          // change if needed

    // Prevent instantiation
    private DatabaseConnection() {}

    /**
     * Returns a fresh Connection. Caller is responsible for closing it
     * (preferably via try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
