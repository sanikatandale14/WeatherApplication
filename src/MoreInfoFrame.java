import javax.swing.*;
import java.awt.*;

public class MoreInfoFrame extends JFrame {

    public MoreInfoFrame(String forecastText) {
        setTitle("4-Day Weather Forecast");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Panel with gradient background
        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Color color1 = new Color(0, 102, 204);
                Color color2 = new Color(153, 204, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridLayout(5, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Forecast split by lines
        String[] lines = forecastText.split("\\n");

        // Show each day as its own styled label
        for (String line : lines) {
            JLabel dayLabel = new JLabel(line);
            dayLabel.setOpaque(true);
            dayLabel.setBackground(new Color(255, 255, 255, 200));
            dayLabel.setForeground(Color.BLACK);
            dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            dayLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.add(dayLabel);
        }
    }
}
