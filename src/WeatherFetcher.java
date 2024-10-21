import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

/**
 * Class responsible for fetching weather data from OpenWeatherMap API.
 */
public class WeatherFetcher {

    private final String API_KEY = "ad02e6c153a01ebe2968d5936ca5c8b1";

    public String getWeatherData(String city) {
        String apiURL = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + API_KEY + "&units=metric";

        try {
            URI uri = new URI(apiURL.replace(" ", "%20")); // Encode spaces in city names
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 seconds
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            InputStream inputStream;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream)
            );
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            // Parse JSON response
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(response.toString());

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successful response
                JSONObject main = (JSONObject) jsonObj.get("main");
                double temperature = ((Number) main.get("temp")).doubleValue();
                long humidity = ((Number) main.get("humidity")).longValue();

                JSONArray weatherArray = (JSONArray) jsonObj.get("weather");
                JSONObject weather = (JSONObject) weatherArray.get(0);
                String description = (String) weather.get("description");

                return temperature + "°C, " + description + ", Humidity: " + humidity + "%";
            } else {
                // Error response
                String errorMessage = (String) jsonObj.get("message");
                System.out.println("API Error: " + errorMessage);
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error fetching weather data: " + e.getMessage());
            return null;
        }
    }
}
