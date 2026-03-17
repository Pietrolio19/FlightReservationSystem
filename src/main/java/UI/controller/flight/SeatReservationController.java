package UI.controller.flight;

import domain.flight.Flight;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import service.SeatReservationService;

import java.util.HashMap;
import java.util.Map;

public class SeatReservationController {
    //attributi
    private Map<String, Button> seats = new HashMap<>();
    private final SeatReservationService seatReservationService = new SeatReservationService();
    private Flight currentFlight;

    public Flight getCurrentFlight() {
        return currentFlight;
    }

    public void setCurrentFlight(Flight currentFlight) {
        this.currentFlight = currentFlight;
        createFlightCard();
    }

    @FXML
    public ScrollPane contentArea;

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
    private Label arrivalCityLabel;

    @FXML
    private Label arrivalIataLabel;
    @FXML

    private Label arrivalTimeLabel;

    @FXML
    private Label departureDateLabel;

    @FXML
    private Label durationLabel;

    @FXML
    private Label minPriceLabel;

    @FXML
    private void initialize() {
    }

    private void createFlightCard() {
        airlineLabel.setText(currentFlight.getAirline().getName());
        flightCodeLabel.setText(currentFlight.getFlightCode());
        departureCityLabel.setText(currentFlight.getDeparture().getCity());
        departureIataLabel.setText(currentFlight.getDeparture().getIata());
        departureTimeLabel.setText(currentFlight.getDepartureTime().toString());
        arrivalCityLabel.setText(currentFlight.getArrival().getCity());
        arrivalIataLabel.setText(currentFlight.getArrival().getIata());
        arrivalTimeLabel.setText(currentFlight.getArrivalTime().toString());
        departureDateLabel.setText(currentFlight.getDepartureDate().toString());
        durationLabel.setText(currentFlight.formattedDuration());
        minPriceLabel.setText(currentFlight.formattedPrice());
    }


}
