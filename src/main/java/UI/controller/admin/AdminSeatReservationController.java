package UI.controller.admin;

import domain.reservation.SeatReservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import service.admin.AdminService;

import java.time.format.DateTimeFormatter;

public class AdminSeatReservationController {
    private final AdminService adminService = new AdminService();

    @FXML
    private ListView<SeatReservation> seatListView;

    @FXML
    private Label idLabel;

    @FXML
    private Label passengerLabel;

    @FXML
    private Label reservationLabel;

    @FXML
    private Label seatLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label stateLabel;

    @FXML
    private void initialize() {
        loadSeatInfo();
    }

    private void loadSeatInfo() {
        ObservableList<SeatReservation> current = FXCollections.observableList(adminService.findAllSeatReservation());
        seatListView.setItems(current);
        seatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showRecordContent(newValue);
            }
        });
    }

    private void showRecordContent(SeatReservation seatReservation) {
        idLabel.setText("Numero: " + seatReservation.getSeatReservationId());
        passengerLabel.setText("Passeggero: " + seatReservation.getPassenger().getName() + " " + seatReservation.getPassenger().getSurname());
        reservationLabel.setText("Numero Prenotazione: " + seatReservation.getReservation().getReservationId());
        seatLabel.setText("Posto: " + seatReservation.getSeat().getSeatCode());
        dateLabel.setText("Data Prenotazione: " + seatReservation.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        stateLabel.setText("Stato: " + seatReservation.getState());
    }
}
