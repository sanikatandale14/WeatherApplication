// WeatherDataFetcher.java

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

public class WeatherDataFetcher {

    // Replace with your OpenWeatherMap API key
    private static final String OPENWEATHER_API_KEY = "2919ac5e23b3aca281a48e32dd51d37f";

    // Fetch weather and AQI data for a given location
    public static JSONObject getWeatherData(String locationName) {
        try {
            // Get coordinates for the location
            JSONObject location = getLocationCoordinates(locationName);
            if (location == null) {
                System.out.println("Location not found.");
                return null;
            }

            double latitude = (double) location.get("latitude");
            double longitude = (double) location.get("longitude");

            // Store the searched location
            storeLocation(locationName, latitude, longitude);

            // Fetch weather forecast data
            JSONObject weatherForecast = fetchWeatherForecast(latitude, longitude);

            // Fetch air quality data
            JSONObject airQuality = fetchAirQualityData(latitude, longitude);

            // Combine data into a single JSON object
            JSONObject combinedData = new JSONObject();
            combinedData.put("location", locationName);
            combinedData.put("weather", weatherForecast);
            combinedData.put("air_quality", airQuality);

            return combinedData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get coordinates for a location using Open-Meteo Geocoding API
    private static JSONObject getLocationCoordinates(String locationName) {
        if (locationName == null || locationName.trim().isEmpty()) {
            System.out.println("Error: locationName is null or empty.");
            return null;
        }

        try {
            String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                    locationName.replaceAll(" ", "+") + "&count=1&language=en&format=json";

            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            JSONArray results = (JSONArray) resultsJsonObj.get("results");
            if (results.isEmpty()) {
                return null;
            }
            return (JSONObject) results.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Fetch weather forecast data from Open-Meteo API
    private static JSONObject fetchWeatherForecast(double latitude, double longitude) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(7); // 7-day forecast

            String urlString = "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=" + latitude +
                    "&longitude=" + longitude +
                    "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m" +
                    "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum" +
                    "&start_date=" + today +
                    "&end_date=" + endDate +
                    "&timezone=auto";

            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(String.valueOf(resultJson));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fetch air quality data from OpenWeatherMap API
    private static JSONObject fetchAirQualityData(double latitude, double longitude) {
        try {
            String urlString = "http://api.openweathermap.org/data/2.5/air_pollution?" +
                    "lat=" + latitude +
                    "&lon=" + longitude +
                    "&appid=" + OPENWEATHER_API_KEY;

            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(String.valueOf(resultJson));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Store searched location permanently
    private static void storeLocation(String locationName, double latitude, double longitude) {
        try {
            File file = new File("locations.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(locationName + "," + latitude + "," + longitude);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fetch API response
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
