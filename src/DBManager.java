import java.sql.*;

public class DBManager {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE"; // Change to your Oracle DB URL
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "@Sanika";

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertWeatherData(String location, String temperature, String condition, String humidity, String windspeed) {
        String sql = "INSERT INTO weather_data (location, temperature, weather_condition, humidity, windspeed) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, location);
            stmt.setString(2, temperature);
            stmt.setString(3, condition);
            stmt.setString(4, humidity);
            stmt.setString(5, windspeed);
            stmt.executeUpdate();
            System.out.println("Weather data inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
