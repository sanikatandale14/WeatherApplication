import javax.swing.*;
import java.awt.*;

public class WeatherDataViewer extends JFrame {
    public WeatherDataViewer() {
        setTitle("Stored Weather Data Viewer");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTable table = new JTable(WeatherDatabaseManager.getAllWeatherData());
        JScrollPane scrollPane = new JScrollPane(table);

        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JLabel title = new JLabel("Stored Weather Data", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(title, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherDataViewer viewer = new WeatherDataViewer();
            viewer.setVisible(true);
        });
    }
}
