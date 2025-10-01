import javax.swing.*;
import java.awt.*;

public class ForecastTabsFrame extends JFrame {
    private String location;

    public ForecastTabsFrame(String location) {
        // Null/empty check
        if (location == null || location.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid location provided!", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        this.location = location.trim();
        setTitle("Weather Forecast - " + this.location);

        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabbed pane setup with only "Next 4 Days"
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Next 4 Days", new ForecastPanel(this.location, "4day"));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
