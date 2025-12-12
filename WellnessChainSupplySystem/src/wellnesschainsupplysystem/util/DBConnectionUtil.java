package wellnesschainsupplysystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

    // Adjust URL / user / password if you changed them in Docker
    private static final String URL = "jdbc:mysql://localhost:3360/wellness_chain_db";
    private static final String USER = "root";
    private static final String PASSWORD = "rootpwd";

    static {
        try {
            // Load MySQL driver (with Connector/J 8+)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySQL JDBC driver");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Success Connected to DB: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to DB");
            e.printStackTrace();
        }
    }
}
