import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.LocalDate;
public class WeatherApp {

    // Fetch weather data for a given location
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: Location not found or API unavailable.");
            return null;
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?"
                + "latitude=" + latitude
                + "&longitude=" + longitude
                + "&current_weather=true"
                + "&hourly=temperature_2m,relative_humidity_2m,weathercode,windspeed_10m"
                + "&timezone=auto";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to weather API.");
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
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");

            double temperature = (double) temperatureData.get(index);
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));
            long humidity = (long) humidityData.get(index);
            double windspeed = (double) windspeedData.get(index);

                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);

            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Corrected geolocation API URL
    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName
                + "&count=1&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to geolocation API.");
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
            JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());

            return (JSONArray) resultsJsonObj.get("results");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString); // Use standard constructor for String argument
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0;
    }

    private static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDateTime.format(formatter);
    }

    private static String convertWeatherCode(long weathercode) {
        if (weathercode == 0L) return "Clear";
        else if (weathercode <= 3L) return "Cloudy";
        else if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)) return "Rain";
        else if (weathercode >= 71L && weathercode <= 77L) return "Snow";
        return "Unknown";
    }

    // Fetch forecast based on type
    public static String fetchForecastData(String locationName, String type) {
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null || locationData.isEmpty()) return "Location not found.";

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate;

        if ("4day".equalsIgnoreCase(type)) {
            endDate = startDate.plusDays(3);
        } else if ("weekly".equalsIgnoreCase(type)) {
            endDate = startDate.plusDays(6);
        } else if ("monthly".equalsIgnoreCase(type)) {
            endDate = startDate.plusDays(29);
        }

        String dailyUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude
                + "&longitude=" + longitude
                + "&daily=temperature_2m_max,temperature_2m_min,weathercode,sunrise,sunset,precipitation_probability_max,pressure_msl_max"
                + "&hourly=temperature_2m,weathercode,precipitation_probability"
                + "&timezone=auto"
                + "&start_date=" + startDate
                + "&end_date=" + endDate;

        String aqiUrl = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=" + latitude
                + "&longitude=" + longitude
                + "&hourly=pm10,pm2_5,us_aqi"
                + "&timezone=auto";

        try {
            HttpURLConnection conn = fetchApiResponse(dailyUrl);
            if (conn == null || conn.getResponseCode() != 200) return "Failed to fetch weather data.";
            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder weatherJson = new StringBuilder();
            while (scanner.hasNext()) weatherJson.append(scanner.nextLine());
            scanner.close();
            conn.disconnect();

            conn = fetchApiResponse(aqiUrl);
            StringBuilder aqiJson = new StringBuilder();
            if (conn != null && conn.getResponseCode() == 200) {
                scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNext()) aqiJson.append(scanner.nextLine());
                scanner.close();
                conn.disconnect();
            }

            JSONParser parser = new JSONParser();
            JSONObject weatherRoot = (JSONObject) parser.parse(weatherJson.toString());
            JSONObject daily = (JSONObject) weatherRoot.get("daily");
            JSONObject hourly = (JSONObject) weatherRoot.get("hourly");

            JSONArray dates = (JSONArray) daily.get("time");
            JSONArray maxTemps = (JSONArray) daily.get("temperature_2m_max");
            JSONArray minTemps = (JSONArray) daily.get("temperature_2m_min");
            JSONArray weatherCodes = (JSONArray) daily.get("weathercode");
            JSONArray sunriseTimes = (JSONArray) daily.get("sunrise");
            JSONArray sunsetTimes = (JSONArray) daily.get("sunset");
            JSONArray rainChances = (JSONArray) daily.get("precipitation_probability_max");
            JSONArray pressure = (JSONArray) daily.get("pressure_msl_max");

            JSONArray hourlyTimes = (JSONArray) hourly.get("time");
            JSONArray hourlyTemps = (JSONArray) hourly.get("temperature_2m");
            JSONArray hourlyCodes = (JSONArray) hourly.get("weathercode");
            JSONArray hourlyRain = (JSONArray) hourly.get("precipitation_probability");

            String aqiInfo = "";
            if (!aqiJson.isEmpty()) {
                JSONObject aqiRoot = (JSONObject) parser.parse(aqiJson.toString());
                JSONObject aqiHourly = (JSONObject) aqiRoot.get("hourly");
                JSONArray usAqi = (JSONArray) aqiHourly.get("us_aqi");

                if (usAqi != null && usAqi.size() > 0) {
                    aqiInfo = "\n🌿 AQI: " + usAqi.get(0).toString() + " (US)";
                }
            }

            StringBuilder forecast = new StringBuilder("📍 Forecast for " + location.get("name") + "\n");

            forecast.append("\n🗓️ 5-Day Forecast\n---------------------");
            for (int i = 0; i < dates.size(); i++) {
                forecast.append("\n📅 Date: ").append(dates.get(i))
                        .append("\n🌤️ Condition: ").append(convertWeatherCode((long) weatherCodes.get(i)))
                        .append(String.format("\n🌡️ Max: %.1f°C / Min: %.1f°C", (double) maxTemps.get(i), (double) minTemps.get(i)))
                        .append("\n☔ Rain Chance: ").append(rainChances.get(i)).append("%")
                        .append("\n🔵 Pressure: ").append(pressure.get(i)).append(" hPa")
                        .append("\n🌅 Sunrise: ").append(sunriseTimes.get(i))
                        .append(" | 🌇 Sunset: ").append(sunsetTimes.get(i))
                        .append("\n---------------------");
            }

            forecast.append("\n\n🕓 24-Hour Forecast Today\n---------------------");
            LocalDate today = LocalDate.now();
            for (int i = 0; i < hourlyTimes.size(); i++) {
                String timeStr = (String) hourlyTimes.get(i);
                if (timeStr.startsWith(today.toString())) {
                    double temp = (double) hourlyTemps.get(i);
                    long code = (long) hourlyCodes.get(i);
                    long rain = (long) hourlyRain.get(i);
                    forecast.append(String.format("\n%s → 🌡️ %.1f°C | %s | ☔ %d%%",
                            timeStr.substring(11), temp, convertWeatherCode(code), rain));
                }
            }

            forecast.append(aqiInfo);
            return forecast.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing forecast data.";
        }
    }



}
