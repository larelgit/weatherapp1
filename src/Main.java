import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalTime;

/**
 * The entry point of the Weather App.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("weather_app.fxml"));
        Scene scene = new Scene(loader.load());

        // Determine current time
        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();

        // Define day time (6 AM to 6 PM)
        boolean isDayTime = currentHour >= 6 && currentHour < 18;

        // Apply the appropriate CSS stylesheet
        if (isDayTime) {
            scene.getStylesheets().add(getClass().getResource("styles_light.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("styles_dark.css").toExternalForm());
        }

        primaryStage.setTitle("Minimalistic Weather App");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Make the window non-resizable
        primaryStage.show();

        // Set the default city to the user's current location
        WeatherAppController controller = loader.getController();
        LocationService locationService = new LocationService();

        // Fetch the user's city in a background thread
        new Thread(() -> {
            String city = locationService.getUserCity();
            if (city != null) {
                // Update the ComboBox with the user's city on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    controller.setCity(city);
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
