package UI.controller.admin;

import domain.flight.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.AdminService;
import service.FlightService;
import java.time.format.DateTimeFormatter;

public class AdminFlightController {
    private final AdminService adminService = new AdminService();

    @FXML
    private ListView<Flight> flightListView;

    @FXML
    private HBox mainBox;

    @FXML
    private VBox leftBox;

    @FXML
    private VBox rightBox;

    @FXML
    private Label idLabel;

    @FXML
    private Label flightCodeLabel;

    @FXML
    private Label departureLabel;

    @FXML
    private Label arrivalLabel;

    @FXML
    private Label departureDateLabel;

    @FXML
    private Label arrivalDateLabel;

    @FXML
    private Label departureTimeLabel;

    @FXML
    private Label arrivalTimeLabel;

    @FXML
    private Label airlineIdLabel;

    @FXML
    private Label aircraftIdLabel;

    @FXML
    private void initialize() {
        leftBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.4));
        rightBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.6));
        loadFlightInfo();
        flightListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showRecordContent(newValue);
            }
        });
    }

    private void loadFlightInfo() {
        ObservableList<Flight> current = FXCollections.observableList(adminService.findAllFlights());
        flightListView.setItems(current);
    }

    private void showRecordContent(Flight flight) {
        idLabel.setText("Numero Volo: " + flight.getFlightId());
        flightCodeLabel.setText("Codice Volo: " + flight.getFlightCode());
        departureLabel.setText("Partenza: " + flight.getDeparture().getCity());
        arrivalLabel.setText("Arrivo: " + flight.getArrival().getCity());
        departureDateLabel.setText("Data Partenza: " + flight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        arrivalDateLabel.setText("Data Arrivo: " + flight.getArrivalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        departureTimeLabel.setText("Orario Partenza: " + flight.getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        arrivalTimeLabel.setText("Orario Arrivo: " + flight.getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        airlineIdLabel.setText("Compagnia: " + flight.getAirline().getName());
        aircraftIdLabel.setText("Veivolo: " + flight.getAircraft().getModel());
    }

}
