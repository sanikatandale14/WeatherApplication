import java.sql.*;
import org.json.simple.JSONObject;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
public class WeatherDatabaseManager {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE"; // Change XE to your SID if different
    private static final String USER = "system";
    private static final String PASSWORD = "@Sanika";

    public static void saveWeatherData(JSONObject weatherData) {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = "INSERT INTO weather_data (id, location, temperature, weather_condition, humidity, windspeed) " +
                    "VALUES (weather_seq.NEXTVAL, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, (String) weatherData.get("location"));
            stmt.setDouble(2, (double) weatherData.get("temperature"));
            stmt.setString(3, (String) weatherData.get("weather_condition"));
            stmt.setLong(4, (long) weatherData.get("humidity"));
            stmt.setDouble(5, (double) weatherData.get("windspeed"));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Weather data saved successfully.");
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printAllWeatherData() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = "SELECT * FROM weather_data ORDER BY recorded_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("ID | Location | Temp | Condition | Humidity | Windspeed | Recorded At");
            System.out.println("---------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%d | %s | %.1f°C | %s | %d%% | %.1f km/h | %s\n",
                        rs.getInt("id"),
                        rs.getString("location"),
                        rs.getDouble("temperature"),
                        rs.getString("weather_condition"),
                        rs.getInt("humidity"),
                        rs.getDouble("windspeed"),
                        rs.getTimestamp("recorded_at").toString()
                );
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static DefaultTableModel getAllWeatherData() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Location");
        columnNames.add("Temperature");
        columnNames.add("Condition");
        columnNames.add("Humidity");
        columnNames.add("Windspeed");
        columnNames.add("Recorded At");

        Vector<Vector<Object>> data = new Vector<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM weather_data ORDER BY recorded_at DESC")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("location"));
                row.add(rs.getDouble("temperature") + " °C");
                row.add(rs.getString("weather_condition"));
                row.add(rs.getInt("humidity") + " %");
                row.add(rs.getDouble("windspeed") + " km/h");
                row.add(rs.getTimestamp("recorded_at").toString());
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new DefaultTableModel(data, columnNames);
    }
}
