import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui(){

        super("Weather App");

        // configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set the size of our gui (in pixels)
        setSize(450, 650);

        // load our gui at the center of the screen
//        setLocationRelativeTo(null);

        // make our layout manager null to manually position our components within the gui
        setLayout(null);

        // prevent any resize of our gui
        setResizable(true);

        addGuiComponents();
        //setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
    }

    private void addGuiComponents(){
        // search field
        JTextField searchTextField = new JTextField();

        // set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        // change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);
        // button to open detailed forecast page
        JButton moreInfoButton = new JButton("More Info");
        moreInfoButton.setBounds(160, 570, 120, 30);
        moreInfoButton.setFont(new Font("Dialog", Font.BOLD, 16));
        moreInfoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        moreInfoButton.addActionListener(e -> {
////            if (weatherData != null) {
//                String location = (String) weatherData.get("location");
//                String forecast = WeatherApp.fetchForecastData(location, "4day");
//
//                JTextArea forecastArea = new JTextArea(forecast);
//                forecastArea.setEditable(false);
//                forecastArea.setFont(new Font("Dialog", Font.PLAIN, 16));
//                forecastArea.setLineWrap(true);
//                forecastArea.setWrapStyleWord(true);
//
//                JScrollPane scrollPane = new JScrollPane(forecastArea);
//                scrollPane.setPreferredSize(new Dimension(350, 200));
//
//                JOptionPane.showMessageDialog(null, scrollPane, "4-Day Forecast", JOptionPane.INFORMATION_MESSAGE);
////            } else {
////                JOptionPane.showMessageDialog(null, "Please search for a location first.");
////            }
//        });
        moreInfoButton.addActionListener(e -> {
            if (weatherData != null && weatherData.get("location") != null) {
                String location = (String) weatherData.get("location");
                String forecast = WeatherApp.fetchForecastData(location, "4day");

                JTextArea forecastArea = new JTextArea(forecast);
                forecastArea.setEditable(false);
                forecastArea.setFont(new Font("Dialog", Font.PLAIN, 16));
                forecastArea.setLineWrap(true);
                forecastArea.setWrapStyleWord(true);

                JScrollPane scrollPane = new JScrollPane(forecastArea);
                scrollPane.setPreferredSize(new Dimension(750, 600));

                JOptionPane.showMessageDialog(null, scrollPane, "4-Day Forecast", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Please search for a location first.");
         }

        });

        add(moreInfoButton);
// current date and time
        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setBounds(10, 70, 400, 20);
        dateTimeLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        add(dateTimeLabel);

        Timer timer = new Timer(1000, e -> {
            dateTimeLabel.setText("Today: " + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy | hh:mm a")));
        });
        timer.start();


        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        // continue in WeatherAppGui class
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = searchTextField.getText().trim();
                if (location.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a location.");
                    return;
                }

                JSONObject data = WeatherApp.getWeatherData(location);
                if (data == null) {
                    JOptionPane.showMessageDialog(null, "Weather data not found for: " + location);
                    return;
                }

                // Store location in weatherData for "More Info" button
                data.put("location", location);
                    data.put("location", location);
                weatherData = data;
                WeatherDatabaseManager.saveWeatherData(weatherData);


                // Update GUI components with real data
                double temperature = (double) data.get("temperature");
                String condition = (String) data.get("weather_condition");
                long humidity = (long) data.get("humidity");
                double windspeed = (double) data.get("windspeed");

                temperatureText.setText(String.format("%.1f°C", temperature));
                weatherConditionDesc.setText(condition);
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + " km/h</html>");

                // Update image based on condition
                switch (condition.toLowerCase()) {
                    case "clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                    default:
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                }
            }

        });

        add(searchButton);

        add(searchButton);


    }

    // u
    //);sed to create images in our gui components
    private ImageIcon loadImage(String resourcePath){
        try{
            // read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // returns an image icon so that our component can render it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}









