import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WeatherAppLauncher extends JFrame {
    public WeatherAppLauncher() {
        setTitle("Weather Forecast Launcher");
        setSize(960, 540); // match your image size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Load and scale the background image to fit the frame
        ImageIcon backgroundIcon = new ImageIcon("src/assets/background.jpg");
        Image scaledImage = backgroundIcon.getImage().getScaledInstance(960, 540, Image.SCALE_SMOOTH);
        backgroundIcon = new ImageIcon(scaledImage);

        // JLabel with background image
        JLabel background = new JLabel(backgroundIcon);
        background.setLayout(new GridBagLayout()); // for center alignment

        // Create transparent panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton checkWeatherButton = new JButton("Check Weather Forecast of Your Location");
        checkWeatherButton.setFont(new Font("Arial", Font.BOLD, 16));
        checkWeatherButton.setFocusPainted(false);
        checkWeatherButton.setBackground(new Color(255, 255, 255, 220));
        buttonPanel.add(checkWeatherButton);

        // Add the button panel to the center of the background
        background.add(buttonPanel, new GridBagConstraints());

        // Add background to frame
        setContentPane(background);
        setResizable(true);

        // Button click opens main weather app
        checkWeatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // close launcher
                SwingUtilities.invokeLater(() -> new WeatherAppGui().setVisible(true));

            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherAppLauncher().setVisible(true));

    }
}
