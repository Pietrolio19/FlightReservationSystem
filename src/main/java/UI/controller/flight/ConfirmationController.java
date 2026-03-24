package UI.controller.flight;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import domain.flight.Seat;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import service.BookingService;
import util.session.BookingSession;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;

public class ConfirmationController implements NavigatorAware {
    //attributi
    private final BookingSession session = BookingSession.getInstance();
    private final BookingService bookingService = new BookingService();
    private Navigator navigator;

    @FXML
    private VBox passengerCardsArea;

    @FXML
    private Label airlineLabel;

    @FXML
    private Label flightCodeLabel;

    @FXML
    private Label departureCityLabel;

    @FXML
    private Label departureIataLabel;

    @FXML
    private Label departureTimeLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label arrivalCityLabel;

    @FXML
    private Label arrivalIataLabel;

    @FXML
    private Label arrivalTimeLabel;

    @FXML
    private Label departureDateLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Label confirmLabel;

    @FXML
    private Button backToPassenger;

    @FXML
    private void initialize() {
        setFlightData();
        createPassengerCardList();
        confirmButton.setOnAction(e -> completeBooking());
        backToPassenger.setOnAction(e -> backToPassenger());
    }

    private void setFlightData() {
        airlineLabel.setText(session.getSelectedFlight().getAirline().getName());
        flightCodeLabel.setText(session.getSelectedFlight().getFlightCode());
        departureCityLabel.setText(session.getSelectedFlight().getDeparture().getCity());
        departureIataLabel.setText(session.getSelectedFlight().getDeparture().getIata());
        departureDateLabel.setText(session.getSelectedFlight().getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        departureTimeLabel.setText(session.getSelectedFlight().getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        durationLabel.setText(session.getSelectedFlight().formattedDuration());
        arrivalCityLabel.setText(session.getSelectedFlight().getArrival().getCity());
        arrivalIataLabel.setText(session.getSelectedFlight().getArrival().getIata());
        arrivalTimeLabel.setText(session.getSelectedFlight().getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void createPassengerCardList() {
        for(SeatReservation sr: session.getSeatReservations()) {
            passengerCardsArea.getChildren().add(createPassengerCard(sr.getSeat(), sr.getPassenger()));
        }
    }

    private Node createPassengerCard(Seat seat, Passenger passenger) {
        Label seatLabel = new Label("Posto " + seat.getSeatCode());

        Label nameLabel = new Label("Nome:");
        Label nameField = new Label(passenger.getName());
        HBox nameBox = new HBox(10, nameLabel, nameField);

        Label surnameLabel = new Label("Cognome:");
        Label surnameField = new Label(passenger.getSurname());
        HBox surnameBox = new HBox(10, surnameLabel, surnameField);

        Label birthDateLabel = new Label("Data di Nascita:");
        Label birthDateField  = new Label(passenger.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        HBox birthDateBox = new HBox(10, birthDateLabel, birthDateField);

        HBox firstRow = new HBox(100, nameBox, surnameBox, birthDateBox);

        Label addressLabel = new Label("Indirizzo:");
        Label addressField = new Label(passenger.getAddress());
        HBox secondRow = new HBox(10, addressLabel, addressField);

        Label cityLabel = new Label("Città:");
        Label cityField = new Label(passenger.getCity());
        HBox cityBox = new HBox(10, cityLabel, cityField);

        Label provinceLabel = new Label("Provincia:");
        Label provinceField = new Label(passenger.getProvince());
        HBox provinceBox = new HBox(10, provinceLabel, provinceField);

        Label countryLabel = new Label("Stato:");
        Label countryField = new Label(passenger.getCountry());
        HBox countryBox = new HBox(10, countryLabel, countryField);

        HBox thirdRow = new HBox(100, cityBox, provinceBox, countryBox);

        Label codFiscLabel = new Label("Codice Fiscale:");
        Label codFiscField = new Label(passenger.getCodFisc());
        HBox codFiscBox = new HBox(10, codFiscLabel, codFiscField);

        Label codIdLabel = new Label("Codice Carta Identità:");
        Label codIdField = new Label(passenger.getCodId());
        HBox codIdBox = new HBox(10, codIdLabel, codIdField);

        Label phoneLabel = new Label("Cellulare:");
        Label phoneField = new Label(passenger.getPhoneNumber());
        HBox phoneBox = new HBox(10, phoneLabel, phoneField);

        HBox lastRow = new HBox(100, codFiscBox, codIdBox, phoneBox);

        VBox card = new VBox(10, seatLabel, firstRow, secondRow, thirdRow, lastRow);

        //Associazione CSS
        seatLabel.getStyleClass().add("passenger-card-text");
        nameLabel.getStyleClass().add("passenger-card-text");
        surnameLabel.getStyleClass().add("passenger-card-text");
        birthDateLabel.getStyleClass().add("passenger-card-text");
        addressLabel.getStyleClass().add("passenger-card-text");
        cityLabel.getStyleClass().add("passenger-card-text");
        provinceLabel.getStyleClass().add("passenger-card-text");
        countryLabel.getStyleClass().add("passenger-card-text");
        codFiscLabel.getStyleClass().add("passenger-card-text");
        codIdLabel.getStyleClass().add("passenger-card-text");
        phoneLabel.getStyleClass().add("passenger-card-text");

        nameField.getStyleClass().add("passenger-card-field");
        surnameField.getStyleClass().add("passenger-card-field");
        birthDateField.getStyleClass().add("search-field");
        addressField.getStyleClass().add("passenger-card-field");
        cityField.getStyleClass().add("passenger-card-field");
        provinceField.getStyleClass().add("passenger-card-field");
        countryField.getStyleClass().add("passenger-card-field");
        codFiscField.getStyleClass().add("passenger-card-field");
        codIdField.getStyleClass().add("passenger-card-field");
        phoneField.getStyleClass().add("passenger-card-field");

        card.getStyleClass().add("passenger-card-wrapper");

        return card;
    }

    private void completeBooking() {
        try {
            bookingService.saveBookingData();
            confirmLabel.setText("Prenotazione completata, verrai riindirizzato alla finestra principale");

            PauseTransition pause = new PauseTransition(Duration.seconds(3)); //simulazione attesa nel salvataggio sul DB
            pause.setOnFinished(e -> navigator.loadView("flight-search.fxml"));
            pause.play();

        } catch (Exception e) {
            confirmLabel.setText("Errore durante il salvataggio della prenotazione");
            e.printStackTrace();
        }
    }

    private void backToPassenger() {
        BookingSession.getInstance().clearPassengers();
        navigator.loadView("passenger-view.fxml");
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
