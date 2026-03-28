package UI.controller.admin;

import domain.reservation.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.admin.AdminService;

import java.time.format.DateTimeFormatter;

public class AdminReservationController {
    private final AdminService adminService = new AdminService();

    @FXML
    private HBox mainBox;

    @FXML
    private VBox leftBox;

    @FXML
    private VBox rightBox;

    @FXML
    private Label idLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label flightInfoLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label stateLabel;

    @FXML
    private ListView<Reservation> reservationListView;

    @FXML
    private void initialize() {
        leftBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.4));
        rightBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.6));
        loadReservationInfo();
        reservationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showRecordContent(newValue);
            }
        });
    }

    private void loadReservationInfo() {
        ObservableList<Reservation> current = FXCollections.observableList(adminService.findAllReservation());
        reservationListView.setItems(current);
    }

    private void showRecordContent(Reservation reservation){
        idLabel.setText("Numero " + reservation.getReservationId());
        usernameLabel.setText("Utente: " + reservation.getUser().getUsername());
        flightInfoLabel.setText("Volo: " + reservation.getFlight().getFlightCode());
        dateLabel.setText("Data: " + reservation.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        stateLabel.setText("Stato: " + reservation.getState());
    }
}
