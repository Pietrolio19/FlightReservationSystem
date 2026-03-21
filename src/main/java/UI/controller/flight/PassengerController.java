package UI.controller.flight;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import domain.flight.Seat;
import domain.user.Passenger;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.BookingService;
import util.session.BookingSession;

import java.util.*;

public class PassengerController implements NavigatorAware {
    //attributi
    private Navigator navigator;
    private final Map<String, Map<String, Control>> cardFields = new HashMap<>();
    private final BookingService bookingService = new BookingService();
    private final Map<String, Passenger> passengersBySeatCode = new LinkedHashMap<>();
    private final BookingSession session = BookingSession.getInstance();

    @FXML
    private VBox passengerCardsArea;

    @FXML
    private Button continueButton;

    @FXML
    private void initialize() {
        createPassengerCardList();
        continueButton.setOnAction(e -> continueToConfirmView());
    }

    private void createPassengerCardList() {
        for(Seat s: session.getSelectedSeats()) {
            passengerCardsArea.getChildren().add(createPassengerCard(s));
        }
    }

    private Node createPassengerCard(Seat seat) {
        Map<String, Control> fields = new HashMap<>();

        Label seatLabel = new Label("Posto " + seat.getSeatCode());

        Label nameLabel = new Label("Nome:");
        TextField nameField = new TextField();
        HBox nameBox = new HBox(10, nameLabel, nameField);
        fields.put("name", nameField);

        Label surnameLabel = new Label("Cognome:");
        TextField surnameField = new TextField();
        HBox surnameBox = new HBox(10, surnameLabel, surnameField);
        fields.put("surname", surnameField);

        Label birthDateLabel = new Label("Data di Nascita:");
        DatePicker birthDateField  = new DatePicker();
        birthDateField.setPromptText("Scegli data");
        HBox birthDateBox = new HBox(10, birthDateLabel, birthDateField);
        fields.put("birthDate", birthDateField);

        HBox firstRow = new HBox(100, nameBox, surnameBox, birthDateBox);

        Label addressLabel = new Label("Indirizzo:");
        TextField addressField = new TextField();
        HBox secondRow = new HBox(10, addressLabel, addressField);
        fields.put("address", addressField);

        Label cityLabel = new Label("Città:");
        TextField cityField = new TextField();
        HBox cityBox = new HBox(10, cityLabel, cityField);
        fields.put("city", cityField);

        Label provinceLabel = new Label("Provincia:");
        TextField provinceField = new TextField();
        HBox provinceBox = new HBox(10, provinceLabel, provinceField);
        fields.put("province", provinceField);

        Label countryLabel = new Label("Stato:");
        TextField countryField = new TextField();
        HBox countryBox = new HBox(10, countryLabel, countryField);
        fields.put("country", countryField);

        HBox thirdRow = new HBox(100, cityBox, provinceBox, countryBox);

        Label codFiscLabel = new Label("Codice Fiscale:");
        TextField codFiscField = new TextField();
        HBox codFiscBox = new HBox(10, codFiscLabel, codFiscField);
        fields.put("codFisc", codFiscField);

        Label codIdLabel = new Label("Codice Carta Identità:");
        TextField codIdField = new TextField();
        HBox codIdBox = new HBox(10, codIdLabel, codIdField);
        fields.put("codId", codIdField);

        Label phoneLabel = new Label("Cellulare:");
        TextField phoneField = new TextField();
        HBox phoneBox = new HBox(10, phoneLabel, phoneField);
        fields.put("phone", phoneField);

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

        //Mappatura per recupero dati
        cardFields.put(seat.getSeatCode(), fields);

        return card;
    }

    private void continueToConfirmView(){
        savePassengersData();
        bookingService.createSeatReservations(passengersBySeatCode);
        navigator.loadView("confirm-view.fxml");
    }

    private void savePassengersData() {
        for(Map.Entry<String, Map<String, Control>> entry: cardFields.entrySet()){
            String seatCode = entry.getKey();
            Map<String, Control> fields = entry.getValue();

            Passenger passenger = new Passenger();
            passenger.setName(((TextField) fields.get("name")).getText());
            passenger.setSurname(((TextField) fields.get("surname")).getText());
            passenger.setDateOfBirth(((DatePicker) fields.get("birthDate")).getValue());
            passenger.setAddress(((TextField) fields.get("address")).getText());
            passenger.setCity(((TextField) fields.get("city")).getText());
            passenger.setProvince(((TextField) fields.get("province")).getText());
            passenger.setCountry(((TextField) fields.get("country")).getText());
            passenger.setCodFisc(((TextField) fields.get("codFisc")).getText());
            passenger.setCodId(((TextField) fields.get("codId")).getText());
            passenger.setPhoneNumber(((TextField) fields.get("phone")).getText());

            passengersBySeatCode.put(seatCode, passenger);
        }
        bookingService.saveSessionPassengers(passengersBySeatCode);
    }


    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
