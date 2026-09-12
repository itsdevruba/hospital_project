package projecthospital.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    // Database credentials
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "1234";

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Load Oracle JDBC driver
        Class.forName("oracle.jdbc.driver.OracleDriver");

        // Always create a new connection (fixes ORA-17008: Closed connection issue)
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        System.out.println("Database connected successfully!");
        return conn;
    }

    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
