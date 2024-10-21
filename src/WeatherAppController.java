import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controller class for the Weather App GUI.
 */
public class WeatherAppController {

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private Button fetchButton;

    @FXML
    private Button locationButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label temperatureLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    private final LocationService locationService = new LocationService();

    public void initialize() {
        progressIndicator.setVisible(false);

        // Populate the ComboBox with a list of major cities
        ObservableList<String> cities = FXCollections.observableArrayList(
                "New York", "London", "Paris", "Tokyo", "Sydney", "Moscow", "Beijing",
                "Berlin", "Dubai", "Singapore", "Los Angeles", "Mumbai", "São Paulo"
        );
        cityComboBox.setItems(cities);
        cityComboBox.setEditable(true); // Allow users to enter custom city names
        cityComboBox.setPromptText("Select or enter city name");

        // Open dropdown when text field is clicked
        cityComboBox.getEditor().setOnMouseClicked(event -> {
            if (!cityComboBox.isShowing()) {
                cityComboBox.show();
            }
        });

        // Ensure arrow is visible
        cityComboBox.setVisibleRowCount(5);
    }

    @FXML
    public void fetchWeather() {
        String city = cityComboBox.getEditor().getText().trim();

        if (!city.isEmpty()) {
            statusLabel.setText("Fetching weather data...");
            temperatureLabel.setText("");
            descriptionLabel.setText("");
            humidityLabel.setText("");
            progressIndicator.setVisible(true);

            Task<Void> task = new Task<Void>() {
                private String temperature;
                private String description;
                private String humidity;
                private String errorMessage;

                @Override
                protected Void call() {
                    WeatherFetcher weatherFetcher = new WeatherFetcher();
                    String weatherInfo = weatherFetcher.getWeatherData(city);

                    if (weatherInfo != null) {
                        String[] infoParts = weatherInfo.split(",");
                        temperature = infoParts[0].trim();
                        description = infoParts[1].trim();
                        humidity = infoParts[2].trim();
                    } else {
                        errorMessage = "Unable to fetch weather data for " + city + ".";
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    progressIndicator.setVisible(false);
                    if (errorMessage == null) {
                        temperatureLabel.setText(temperature);
                        descriptionLabel.setText(description);
                        humidityLabel.setText(humidity);
                        statusLabel.setText("Weather data for " + city + ":");
                    } else {
                        statusLabel.setText(errorMessage);
                    }
                }

                @Override
                protected void failed() {
                    progressIndicator.setVisible(false);
                    statusLabel.setText("An error occurred while fetching weather data.");
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        } else {
            statusLabel.setText("Please select or enter a city name.");
        }
    }

    @FXML
    private void fetchCurrentLocationWeather() {
        statusLabel.setText("Detecting current location...");
        progressIndicator.setVisible(true);

        Task<String> task = new Task<String>() {
            @Override
            protected String call() {
                return locationService.getUserCity();
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                String city = getValue();
                if (city != null) {
                    setCity(city);
                    fetchWeather();
                } else {
                    statusLabel.setText("Unable to determine your location.");
                }
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                statusLabel.setText("An error occurred while detecting your location.");
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void setCity(String city) {
        cityComboBox.getEditor().setText(city);
    }
}
