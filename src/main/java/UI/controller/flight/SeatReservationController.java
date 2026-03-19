package UI.controller.flight;

import domain.flight.Flight;
import domain.flight.Seat;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import service.SeatReservationService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class SeatReservationController {
    //attributi
    private final SeatReservationService seatReservationService = new SeatReservationService();
    private Flight currentFlight;
    private List<Seat> selectedSeats = new ArrayList<>();

    @FXML
    public ScrollPane contentArea;

    @FXML
    private HBox mainBox;

    @FXML
    private VBox leftBox;

    @FXML
    private VBox rightBox;

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
    private GridPane seatsGrid;

    @FXML
    private VBox selectedSeatsBox;

    @FXML
    private Label totalSeats;

    @FXML
    private Label totalPrice;

    @FXML
    private HBox resumeBox;

    @FXML
    private void initialize() {
        leftBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.8));
        rightBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.2));
    }

    public Flight getCurrentFlight() {
        return currentFlight;
    }

    public void setCurrentFlight(Flight currentFlight) {
        this.currentFlight = currentFlight;
        createFlightCard();
        createSeatsGrid();
    }

    private void createFlightCard() {
        airlineLabel.setText(currentFlight.getAirline().getName());
        flightCodeLabel.setText(currentFlight.getFlightCode());
        departureCityLabel.setText(currentFlight.getDeparture().getCity());
        departureIataLabel.setText(currentFlight.getDeparture().getIata());
        departureTimeLabel.setText(currentFlight.getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        arrivalCityLabel.setText(currentFlight.getArrival().getCity());
        arrivalIataLabel.setText(currentFlight.getArrival().getIata());
        arrivalTimeLabel.setText(currentFlight.getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        departureDateLabel.setText(currentFlight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        durationLabel.setText(currentFlight.formattedDuration());
        minPriceLabel.setText(currentFlight.formattedPrice());
    }

    private void createSeatsGrid() {
        List<Seat> seats = seatReservationService.getSeatsList(currentFlight.getFlightId());
        for(Seat s: seats) {
            int column = getSeatColumn(s);
            seatsGrid.add(createToggleButton(s), column, s.getRow());
        }
    }

    private int getSeatColumn(Seat seat) {
        return switch(seat.getLetter()) {
            case "A" -> 0;
            case "B" -> 1;
            case "C" -> 2;
            case "D" -> 3;
            case "E" -> 4;
            case "F" -> 5;
            default -> throw new IllegalArgumentException();
        };
    }

    private ToggleButton createToggleButton(Seat seat) {
        ToggleButton button = new ToggleButton(seat.getSeatCode());
        //TODO applicare styleClass

        button.setOnAction(e -> handleSeatSelection(seat, button));

        return button;
    }

    private void handleSeatSelection(Seat seat, ToggleButton button) {
        if(button.isSelected())
            selectedSeats.add(seat);
        else
            selectedSeats.remove(seat);
        refreshSelectedSeatsBox();
    }

    private void refreshSelectedSeatsBox() {
        selectedSeatsBox.getChildren().clear();
        int totalPrice = 0;

        for(Seat s: selectedSeats) {
            selectedSeatsBox.getChildren().add(createSelectedSeatEntry(s));
            totalPrice += s.getPrice();
        }

        resumeBox.setVisible(true);
        resumeBox.setManaged(true);
        if(selectedSeats.isEmpty()) {
            resumeBox.setVisible(false);
            resumeBox.setManaged(false);
        }

        int totalSeats = selectedSeats.size();
        this.totalSeats.setText("Totale Posti: " + totalSeats);
        this.totalPrice.setText("Totale: " + totalPrice + "€");
    }

    private Node createSelectedSeatEntry(Seat seat) {
        Label seatLabel = new Label("Posto " + seat.getSeatCode() + "-" + seat.getSeatClass());

        Label seatType = new Label(seat.getType());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label priceLabel = new Label(seat.getFormattedPrice());
        HBox lower = new HBox(seatType, spacer, priceLabel);

        VBox entry = new VBox(seatLabel, lower);

        //Associazione CSS
        entry.getStyleClass().add("seat-reservation-resume-wrapper");
        lower.getStyleClass().add("seat-reservation-resume-text");
        seatType.getStyleClass().add("seat-reservation-resume-type");
        seatLabel.getStyleClass().add("seat-reservation-resume-text");

        return entry;
    }
}
