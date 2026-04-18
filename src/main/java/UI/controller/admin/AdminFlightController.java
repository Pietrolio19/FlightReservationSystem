package UI.controller.admin;

import domain.flight.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.admin.AdminService;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
    private Button flightUpdateButton;

    @FXML
    private Button flightDeleteButton;

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
        flightUpdateButton.setOnAction(e -> handleUpdate());
        flightDeleteButton.setOnAction(e -> {
            if(flightListView.getSelectionModel().getSelectedItem() != null) {
                adminService.deleteFlight(
                        flightListView.getSelectionModel().getSelectedItem().getFlightId());
                loadFlightInfo();
            }
        });
    }

    @FXML
    private void handleUpdate() {
        Flight selectedFlight = flightListView.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Aggiorna volo");
        dialog.setHeaderText("Modifica i dati del volo selezionato");

        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField codeField = new TextField(selectedFlight.getFlightCode());
        DatePicker departureDatePicker = new DatePicker(selectedFlight.getDepartureDate());
        DatePicker arrivalDatePicker = new DatePicker(selectedFlight.getArrivalDate());
        TextField departureTimeField = new TextField(String.valueOf(selectedFlight.getDepartureTime()));
        TextField arrivalTimeField = new TextField(String.valueOf(selectedFlight.getArrivalTime()));
        TextField durationField = new TextField(String.valueOf(selectedFlight.getDuration()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Codice volo:"), 0, 0);
        grid.add(codeField, 1, 0);

        grid.add(new Label("Data partenza:"), 0, 1);
        grid.add(departureDatePicker, 1, 1);

        grid.add(new Label("Data arrivo:"), 0, 2);
        grid.add(arrivalDatePicker, 1, 2);

        grid.add(new Label("Orario partenza:"), 0, 3);
        grid.add(departureTimeField, 1, 3);

        grid.add(new Label("Orario arrivo:"), 0, 4);
        grid.add(arrivalTimeField, 1, 4);

        grid.add(new Label("Durata:"), 0, 5);
        grid.add(durationField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButtonType) {
            selectedFlight.setFlightCode(codeField.getText());
            selectedFlight.setDepartureDate(departureDatePicker.getValue());
            selectedFlight.setArrivalDate(arrivalDatePicker.getValue());
            selectedFlight.setDuration(Integer.parseInt(durationField.getText()));
            selectedFlight.setDepartureTime(Time.valueOf(LocalTime.parse(departureTimeField.getText())));
            selectedFlight.setArrivalTime(Time.valueOf(LocalTime.parse(arrivalTimeField.getText())));

            adminService.updateFlight(selectedFlight);

            flightListView.refresh();
        }
    }

    private void loadFlightInfo() {
        ObservableList<Flight> current = FXCollections.observableList(adminService.findAllFlights());
        flightListView.setItems(current);
        flightListView.setCellFactory(lv -> new ListCell<Flight>(){
            @Override
            protected void updateItem(Flight flight, boolean empty) {
                super.updateItem(flight, empty);

                if (empty || flight == null) {
                    setText(null);
                } else {
                    setText(flight.getFlightCode());
                }
            }
        });
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
