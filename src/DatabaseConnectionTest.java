import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionTest {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:orcl"; // Use your correct SID/service name
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "@Sanika";

    public static void main(String[] args) {
        try {
            // Load Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Oracle JDBC Driver loaded successfully.");

            // Attempt connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (connection != null) {
                System.out.println("✅ Database connection successful!");
                connection.close();
            } else {
                System.out.println("❌ Failed to make a connection.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error during database connection:");
            e.printStackTrace();
        }
    }
}
