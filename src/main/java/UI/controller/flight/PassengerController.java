package UI.controller.flight;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import domain.flight.Seat;
import domain.user.Passenger;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import service.flight.BookingService;
import util.session.BookingSession;
import util.session.SessionHandler;

import java.util.*;

public class PassengerController implements NavigatorAware {
    //attributi
    private Navigator navigator;
    private final Map<String, Map<String, Control>> cardFields = new HashMap<>(); //mappa per tenere traccia dei Fields nelle card
    private final BookingService bookingService = new BookingService();
    private final ToggleGroup toggleGroup = new ToggleGroup(); // oggetto per tenere traccia dei RadioButton nelle card
    private final Map<String, ToggleButton> saveCompanionButtonMap = new HashMap<>(); //mappa per tenere traccia dei ToggleButton per i companion nelle card
    private final Map<String, Button> companionButtonMap = new HashMap<>();
    private final Map<String, Passenger> selectedCompanionBySeat = new HashMap<>();

    @FXML
    private VBox passengerCardsArea;

    @FXML
    private Button continueButton;

    @FXML
    private Button backToSeatReservation;

    @FXML
    private void initialize() {
        createPassengerCardList();
        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if(newToggle != null) {
                String seatCode = (String) newToggle.getUserData();
                selectedCompanionBySeat.remove(seatCode);
                saveCompanionButtonMap.get(seatCode).setVisible(false);
                saveCompanionButtonMap.get(seatCode).setManaged(false);
                companionButtonMap.get(seatCode).setVisible(false);
                companionButtonMap.get(seatCode).setManaged(false);
                autocompleteUserInfo(seatCode);
            }
            if(oldToggle != null) {
                String seatCode = (String) oldToggle.getUserData();
                saveCompanionButtonMap.get(seatCode).setVisible(true);
                saveCompanionButtonMap.get(seatCode).setManaged(true);
                companionButtonMap.get(seatCode).setVisible(true);
                companionButtonMap.get(seatCode).setManaged(true);
                removeAutocompleteData(seatCode);
            }
        });

        continueButton.setOnAction(e -> continueToConfirmView());
        backToSeatReservation.setOnAction(e -> backToSeatReservation());
    }

    private void createPassengerCardList() {
        List<Seat> selectedSeats = bookingService.getSessionSeats();
        for(Seat s: selectedSeats) {
            passengerCardsArea.getChildren().add(createPassengerCard(s));
        }
    }

    private Node createPassengerCard(Seat seat) {
        Map<String, Control> fields = new HashMap<>();

        Label seatLabel = new Label("Posto " + seat.getSeatCode());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        ImageView bookmark = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/UI/images/bookmark.png")).toExternalForm()));
        ImageView add = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/UI/images/add.png")).toExternalForm()));
        add.setFitHeight(20);
        add.setFitWidth(20);

        ToggleButton button = new ToggleButton("Salva Companion");
        button.setUserData(seat.getSeatCode());
        button.setGraphic(bookmark);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(8);
        saveCompanionButtonMap.put(seat.getSeatCode(), button);

        Button compButton = new Button("Aggiungi Companion");
        compButton.setUserData(seat.getSeatCode());
        compButton.setGraphic(add);
        compButton.setContentDisplay(ContentDisplay.LEFT);
        compButton.setGraphicTextGap(8);
        compButton.setOnAction(e -> handleAddCompanion(seat.getSeatCode()));
        companionButtonMap.put(seat.getSeatCode(), compButton);

        HBox buttonBox = new HBox(5, button, compButton);

        HBox seatRow = new HBox(seatLabel, spacer, buttonBox);

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

        Region radioSpacer = new Region();
        HBox.setHgrow(radioSpacer, Priority.ALWAYS);
        RadioButton radioButton = new RadioButton("Sono io");
        radioButton.setUserData(seat.getSeatCode());
        radioButton.setToggleGroup(toggleGroup);

        HBox buttonRow = new HBox(radioSpacer, radioButton);
        VBox card = new VBox(10, seatRow, firstRow, secondRow, thirdRow, lastRow, buttonRow);

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
        bookingService.createSeatReservations();
        navigator.loadView("confirm-view.fxml");
    }

    private void handleAddCompanion(String seatCode) {
        List<Passenger> companions = SessionHandler.getInstance().getCurrentUser().getCompanions();

        if (companions == null || companions.isEmpty()) {
            return;
        }

        Dialog<Passenger> dialog = new Dialog<>();
        dialog.setTitle("Seleziona Companion");

        ButtonType confirmButtonType = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        ListView<Passenger> listView = new ListView<>();
        listView.getItems().addAll(companions);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Passenger item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " " + item.getSurname());
                }
            }
        });

        dialog.getDialogPane().setContent(listView);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == confirmButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Optional<Passenger> result = dialog.showAndWait();
        result.ifPresent(passenger -> {
            if (isCompanionAlreadySelected(seatCode, passenger)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Companion già selezionato");
                alert.setHeaderText(null);
                alert.setContentText("Questo companion è già stato assegnato a un altro posto.");
                alert.showAndWait();
                return;
            }

            selectedCompanionBySeat.put(seatCode, passenger);
            fillPassengerCard(seatCode, passenger);
        });
    }

    private void fillPassengerCard(String seatCode, Passenger passenger) {
        Map<String, Control> fields = cardFields.get(seatCode);
        if (fields == null) return;

        ((TextField) fields.get("name")).setText(passenger.getName());
        ((TextField) fields.get("surname")).setText(passenger.getSurname());
        ((DatePicker) fields.get("birthDate")).setValue(passenger.getDateOfBirth());
        ((TextField) fields.get("address")).setText(passenger.getAddress());
        ((TextField) fields.get("city")).setText(passenger.getCity());
        ((TextField) fields.get("province")).setText(passenger.getProvince());
        ((TextField) fields.get("country")).setText(passenger.getCountry());
        ((TextField) fields.get("codFisc")).setText(passenger.getCodFisc());
        ((TextField) fields.get("codId")).setText(passenger.getCodId());
        ((TextField) fields.get("phone")).setText(passenger.getPhoneNumber());
    }

    private void savePassengersData() {
        Toggle selectedRadio = toggleGroup.getSelectedToggle();
        String selfSeatCode = selectedRadio != null ? (String) selectedRadio.getUserData() : null;
        Toggle selectedToggle;
        for(Map.Entry<String, Map<String, Control>> entry: cardFields.entrySet()){
            String seatCode = entry.getKey();
            Map<String, Control> fields = entry.getValue();
            selectedToggle = saveCompanionButtonMap.get(seatCode);

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
            if(selectedToggle.isSelected())
                passenger.setCompanionOwner(SessionHandler.getInstance().getCurrentUser());

            bookingService.mapPassengersAndSeats(seatCode, passenger);
            if(seatCode.equals(selfSeatCode))
                bookingService.saveSelfPassenger(passenger);
        }
        bookingService.saveSessionPassengers();
    }

    private void backToSeatReservation() {
        BookingSession.getInstance().clearSeats();
        BookingSession.getInstance().clearPassengers();
        navigator.loadView("seat-reservation-view.fxml");
    }

    private void autocompleteUserInfo(String seatCode) {
        Passenger currentSelfPassenger = SessionHandler.getInstance().getCurrentUser().getSelfPassenger();
        if (currentSelfPassenger == null) return;

        Map<String, Control> fields = cardFields.get(seatCode);
        if (fields == null) return;

        ((TextField) fields.get("name")).setText(currentSelfPassenger.getName());
        ((TextField) fields.get("surname")).setText(currentSelfPassenger.getSurname());
        ((DatePicker) fields.get("birthDate")).setValue(currentSelfPassenger.getDateOfBirth());
        ((TextField) fields.get("address")).setText(currentSelfPassenger.getAddress());
        ((TextField) fields.get("city")).setText(currentSelfPassenger.getCity());
        ((TextField) fields.get("province")).setText(currentSelfPassenger.getProvince());
        ((TextField) fields.get("country")).setText(currentSelfPassenger.getCountry());
        ((TextField) fields.get("codFisc")).setText(currentSelfPassenger.getCodFisc());
        ((TextField) fields.get("codId")).setText(currentSelfPassenger.getCodId());
        ((TextField) fields.get("phone")).setText(currentSelfPassenger.getPhoneNumber());
    }

    private void removeAutocompleteData(String seatCode) {
        Passenger currentSelfPassenger = SessionHandler.getInstance().getCurrentUser().getSelfPassenger();
        if (currentSelfPassenger == null) return;

        Map<String, Control> fields = cardFields.get(seatCode);
        if (fields == null) return;

        ((TextField) fields.get("name")).setText("");
        ((TextField) fields.get("surname")).setText("");
        ((DatePicker) fields.get("birthDate")).setValue(null);
        ((TextField) fields.get("address")).setText("");
        ((TextField) fields.get("city")).setText("");
        ((TextField) fields.get("province")).setText("");
        ((TextField) fields.get("country")).setText("");
        ((TextField) fields.get("codFisc")).setText("");
        ((TextField) fields.get("codId")).setText("");
        ((TextField) fields.get("phone")).setText("");

    }

    private boolean isCompanionAlreadySelected(String currentSeatCode, Passenger passenger) {
        for (Map.Entry<String, Passenger> entry : selectedCompanionBySeat.entrySet()) {
            String seatCode = entry.getKey();
            Passenger selectedPassenger = entry.getValue();

            if (!seatCode.equals(currentSeatCode) && isSamePassenger(selectedPassenger, passenger)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSamePassenger(Passenger p1, Passenger p2) {
        if (p1 == null || p2 == null) {
            return false;
        }

        if (p1.getPassengerId() != null && p2.getPassengerId() != null) {
            return Objects.equals(p1.getPassengerId(), p2.getPassengerId());
        }

        if (p1.getCodFisc() != null && p2.getCodFisc() != null) {
            return Objects.equals(p1.getCodFisc(), p2.getCodFisc());
        }

        if (p1.getCodId() != null && p2.getCodId() != null) {
            return Objects.equals(p1.getCodId(), p2.getCodId());
        }

        return false;
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
