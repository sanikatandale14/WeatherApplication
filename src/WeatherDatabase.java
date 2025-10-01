import org.json.simple.JSONObject;
import java.sql.*;

class WeatherDatabase {
    public static void saveWeatherDataToDB(String location, JSONObject data) {
        String url = "jdbc:oracle:thin:@localhost:1521:xe"; // your Oracle DB URL
        String user = "system"; // change this
        String password = "@Sanika"; // change this

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String query = "INSERT INTO weather_data (location, weather_json) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, location);
            ps.setString(2, data.toJSONString());
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}