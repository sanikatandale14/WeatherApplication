import javax.swing.*;
import java.awt.*;

public class ForecastPanel extends JPanel {
    public ForecastPanel(String location, String type) {
        setLayout(new BorderLayout());

        // Handle null or empty location or type
        if (location == null || location.trim().isEmpty()) {
            location = "Unknown";
        }
        if (type == null || type.trim().isEmpty()) {
            type = "Unknown";
        }

        JLabel heading = new JLabel(type.toUpperCase() + " Forecast for " + location);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setFont(new Font("Dialog", Font.BOLD, 20));

        JTextArea forecastArea = new JTextArea("Fetching " + type + " forecast...");
        forecastArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        forecastArea.setEditable(false);

        // Fetch API data in a new thread
        String finalLocation = location;
        String finalType = type;
        new Thread(() -> {
            String forecastData;
            try {
                forecastData = WeatherApp.fetchForecastData(finalLocation, finalType);
            } catch (Exception e) {
                forecastData = "Error fetching " + finalType + " forecast for " + finalLocation + ":\n" + e.getMessage();
            }
            String finalForecastData = forecastData;
            SwingUtilities.invokeLater(() -> forecastArea.setText(finalForecastData));
        }).start();

        add(heading, BorderLayout.NORTH);
        add(new JScrollPane(forecastArea), BorderLayout.CENTER);
    }
}
