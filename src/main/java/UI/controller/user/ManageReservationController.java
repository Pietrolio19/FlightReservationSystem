package UI.controller.user;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import domain.flight.Flight;
import domain.reservation.Reservation;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.UserProfileService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageReservationController implements NavigatorAware {
    private final UserProfileService userProfileService = new UserProfileService();
    private Navigator navigator;

    @FXML
    private VBox reservationsContainer;

    @FXML
    private Button allButton;

    @FXML
    private Button activeButton;

    @FXML
    private Button removedButton;

    @FXML
    private void initialize() {
        loadReservations();
        allButton.setOnAction(e -> applyFilter(allButton.getText()));
        activeButton.setOnAction(e -> applyFilter(activeButton.getText()));
        removedButton.setOnAction(e -> applyFilter(removedButton.getText()));
    }

    private void loadReservations() {
        reservationsContainer.getChildren().clear();
        List<Reservation> reservations = userProfileService.getUserReservations();

        for (Reservation reservation : reservations) {
            reservationsContainer.getChildren().add(createReservationCard(reservation));
        }
    }

    private VBox createReservationCard(Reservation reservation) {
        VBox card = new VBox(10);
        card.getStyleClass().add("reservation-card");
        card.setUserData(reservation);

        Flight flight = reservation.getFlight();

        Label idLabel = new Label("Prenotazione #" + reservation.getReservationId());
        idLabel.getStyleClass().add("reservation-id-label");

        Label statusLabel = new Label("Stato: " + reservation.getState());
        Label flightLabel = new Label(flight.getAirline().getName() + " - " + flight.getFlightCode());
        Label routeLabel = new Label(
                flight.getDeparture().getCity() + " (" + flight.getDeparture().getIata() + ") → " +
                        flight.getArrival().getCity() + " (" + flight.getArrival().getIata() + ")"
        );

        Label dateTimeLabel = new Label(
                flight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " - " +
                        flight.getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );

        VBox summaryBox = new VBox(4, idLabel, statusLabel, flightLabel, routeLabel, dateTimeLabel);

        Button detailsButton = new Button("Dettagli");
        Button cancelButton = new Button("Cancella");

        VBox detailsBox = createReservationDetailsBox(reservation);
        detailsBox.setVisible(false);
        detailsBox.setManaged(false);

        detailsButton.setOnAction(e -> toggleDetails(detailsBox, detailsButton));

        if (reservation.getState().equals("CANCELLED")) {
            cancelButton.setDisable(true);
        } else {
            cancelButton.setOnAction(e -> handleCancelReservation(reservation));
        }

        HBox actionRow = new HBox(10, detailsButton, cancelButton);
        actionRow.getStyleClass().add("reservation-action-row");

        card.getChildren().addAll(summaryBox, actionRow, detailsBox);

        return card;
    }

    private VBox createReservationDetailsBox(Reservation reservation) {
        VBox detailsBox = new VBox(6);
        detailsBox.getStyleClass().add("reservation-details-box");

        Flight flight = reservation.getFlight();

        Label detailTitle = new Label("Dettagli prenotazione");
        detailTitle.getStyleClass().add("reservation-details-title");

        Label airlineLabel = new Label("Compagnia: " + flight.getAirline().getName());
        Label flightCodeLabel = new Label("Codice volo: " + flight.getFlightCode());
        Label departureLabel = new Label("Partenza: " +
                flight.getDeparture().getName() + " - " +
                flight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " +
                flight.getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        Label arrivalLabel = new Label("Arrivo: " +
                flight.getArrival().getName() + " - " +
                flight.getArrivalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " +
                flight.getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        detailsBox.getChildren().addAll(
                detailTitle,
                airlineLabel,
                flightCodeLabel,
                departureLabel,
                arrivalLabel
        );

        return detailsBox;
    }

    private void toggleDetails(VBox detailsBox, Button detailsButton) {
        boolean isVisible = detailsBox.isVisible();

        detailsBox.setVisible(!isVisible);
        detailsBox.setManaged(!isVisible);

        detailsButton.setText(isVisible ? "Dettagli" : "Nascondi");
    }

    private void handleCancelReservation(Reservation reservation) {
        userProfileService.removeReservation(reservation);
        navigator.loadView("manage-reservation.fxml");
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    private void applyFilter(String filter) {
        for(Node node : reservationsContainer.getChildren()) {
            VBox card = (VBox) node;
            Reservation reservation = (Reservation) card.getUserData();

            boolean show = switch (filter) {
                case "Attive" -> !reservation.getState().equals("CANCELED");
                case "Cancellate" -> reservation.getState().equals("CANCELED");
                default -> true;
            };

            card.setVisible(show);
            card.setManaged(show);
        }
    }
}
