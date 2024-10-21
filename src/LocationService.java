import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class responsible for determining the user's city based on IP address.
 */
public class LocationService {

    public String getUserCity() {
        String apiURL = "http://ip-api.com/json";  // Use HTTP instead of HTTPS

        try {
            URI uri = new URI(apiURL);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            // Set the User-Agent header as required by ip-api.com
            conn.setRequestProperty("User-Agent", "WeatherApp/1.0 (your-email@example.com)");
            conn.setConnectTimeout(5000); // Optional: Set timeout
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

            if ("success".equals(jsonObj.get("status"))) {
                return (String) jsonObj.get("city");
            } else {
                System.out.println("API Error: " + jsonObj.get("message"));
                return null;
            }

        } catch (Exception e) {
            System.out.println("Error fetching location: " + e.getMessage());
            return null;
        }
    }
}
