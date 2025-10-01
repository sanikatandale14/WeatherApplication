import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnectTest {

    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:XE"; // Replace if different
        String username = "system"; // Replace with actual username
        String password = "@Sanika"; // Replace with actual password

        try {
            // Load the Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Connect to the database
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to Oracle 10g successfully!");

            // Close connection
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed.");
            e.printStackTrace();
        }
    }
}
