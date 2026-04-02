package UI.controller.flight;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import domain.flight.Flight;
import domain.flight.Seat;
import dto.flight.SeatState;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import service.flight.BookingService;
import service.flight.SeatReservationService;
import util.session.BookingSession;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class SeatReservationController implements NavigatorAware {
    //attributi
    private final SeatReservationService seatReservationService = new SeatReservationService();
    private final BookingService bookingService = new BookingService();
    private Navigator navigator;

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
    private Button continueButton;

    @FXML
    private Button backToMain;

    @FXML
    private void initialize() {
        createFlightCard();
        createSeatsGrid();
        leftBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.8));
        rightBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.2));
        continueButton.setOnAction(e -> continueToPassengerView());
        backToMain.setOnAction(e -> returnToMain());
    }

    private void createFlightCard() {
        Flight currentFlight = bookingService.getSessionFlight();
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
    }

    private void createSeatsGrid() {
        Flight currentFlight = bookingService.getSessionFlight();
        List<Seat> seats = seatReservationService.getSeatsList(currentFlight.getFlightId());
        List<SeatState> seatStates = bookingService.getSeatStates(currentFlight.getFlightId());
        for(Seat s: seats) {
            int column = getSeatColumn(s);
            seatsGrid.add(createToggleButton(s, seatStates), column, s.getRow());
        }
    }

    private int getSeatColumn(Seat seat) {
        return switch(seat.getLetter()) {
            case "A" -> 0;
            case "B" -> 1;
            case "C" -> 2;
            case "D" -> 4;
            case "E" -> 5;
            case "F" -> 6;
            default -> throw new IllegalArgumentException();
        };
    }

    private ToggleButton createToggleButton(Seat seat, List<SeatState> states) {
        ToggleButton button = new ToggleButton();
        button.setText(seat.getSeatCode() + "\n" + seat.getFormattedPrice());
        button.setUserData(seat.getSeatCode());

        for (SeatState s : states) {
            if (s.getSeat().getSeatId().equals(seat.getSeatId())) {
                String state = s.getState();

                if ("PENDING".equals(state) || "CONFIRMED".equals(state)) {
                    button.setDisable(true);
                    break;
                }
            }
        }

        button.getStyleClass().add("seat-button");

        button.setOnAction(e -> handleSeatSelection(seat, button));

        return button;
    }

    private void handleSeatSelection(Seat seat, ToggleButton button) {
        List<Seat> sessionSeats = bookingService.getSessionSeats();
        if (button.isSelected()) {
            if (sessionSeats.size() < bookingService.getTotalPassengers()) {
                bookingService.addSessionSeat(seat);
            } else {
                button.setSelected(false);
                return;
            }
        }
        else
            bookingService.removeSessionSeat(seat);
        refreshSelectedSeatsBox();
    }

    private void refreshSelectedSeatsBox() {
        selectedSeatsBox.getChildren().clear();
        List<Seat> sessionSeats = bookingService.getSessionSeats();
        int totalPrice = 0;

        for(Seat s: sessionSeats) {
            selectedSeatsBox.getChildren().add(createSelectedSeatEntry(s));
            totalPrice += s.getPrice();
        }

        resumeBox.setVisible(true);
        resumeBox.setManaged(true);
        if(sessionSeats.isEmpty()) {
            resumeBox.setVisible(false);
            resumeBox.setManaged(false);
        }

        int totalSeats = sessionSeats.size();
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

    private void continueToPassengerView() {
        navigator.loadView("passenger-view.fxml");
    }

    private void returnToMain() {
        bookingService.clearSessionSeats();
        navigator.loadView("flight-search.fxml");
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
