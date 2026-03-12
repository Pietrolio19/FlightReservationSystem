package UI.controller;

import domain.flight.Airport;
import domain.flight.Flight;
import dto.FlightSearchRequest;
import dto.FlightSearchResult;
import service.FlightService;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlightSearchController {
    //attributi FXML
    //campi per la ricerca
    @FXML private ComboBox<String> departureField;

    @FXML private ComboBox<String> arrivalField;

    @FXML private VBox returnFieldsWrapper;

    @FXML private DatePicker datePickerDeparture;

    @FXML private DatePicker datePickerReturn;

    @FXML private Button searchButton;

    @FXML private ComboBox<String> journeyType;

    @FXML private CheckBox checkDirect;

    //menu a tendina per la selzione dei passeggeri
    @FXML private HBox passengerSelector;

    @FXML private Label passengerSummary;

    @FXML private ComboBox<String> animalSelector;

    @FXML private TableView<Flight> flightsTable;

    @FXML private TableColumn<Flight, Void> actionsColumn;

    //attributi non FXML
    private final IntegerProperty Adults = new SimpleIntegerProperty(1);
    private final IntegerProperty Children = new SimpleIntegerProperty(0);
    private final IntegerProperty Newborns = new SimpleIntegerProperty(0);
    private final FlightService flightService = new FlightService();

    @FXML
    private void initialize() {
        createSearchForm();
        createFlightsTable();
        updateFlights();
    }

    //metodi per la creazione del layout della finestra
    private void createSearchForm(){
        journeyType.getItems().addAll("Andata e Ritorno", "Solo Andata");
        journeyType.setValue("Andata e Ritorno");
        journeyType.valueProperty().addListener((ov, oldV, newV) -> checkFlightType(newV));

        checkDirect.setSelected(false);

        initializeComboBox();

        passengerSelector.setOnMouseClicked(e -> openPassengerMenu());
        Adults.addListener((obs, oldV, newV) -> updatePassengerSummary());
        Children.addListener((obs, oldV, newV) -> updatePassengerSummary());
        Newborns.addListener((obs, oldV, newV) -> updatePassengerSummary());

        animalSelector.getItems().addAll("Nessuno", "1 Cane", "1 Gatto");
        animalSelector.setValue("Nessuno");
    }

    private void initializeComboBox() {
        setupAirportComboBox(departureField);
        setupAirportComboBox(arrivalField);
    }

    private void setupAirportComboBox(ComboBox<String> comboBox) {
        comboBox.setEditable(true);
        comboBox.setPrefWidth(260);
        comboBox.setMinWidth(260);
        comboBox.setMaxWidth(260);

        final AtomicBoolean updating = new AtomicBoolean(false); //semaforo che impedisce a tendina e contenuto della combo di aggiornarsi contemporaneamente

        comboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (updating.get()) return;

            String selected = comboBox.getValue();
            if (selected != null && selected.equals(newText)) return;

            List<String> filtered = createAirportsNames(newText);
            comboBox.setItems(FXCollections.observableArrayList(filtered));

            if (!filtered.isEmpty() && comboBox.isFocused()) {
                comboBox.show();
            } else {
                comboBox.hide();
            }
        });

        comboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                updating.set(true);
                comboBox.getEditor().setText(newValue);
                comboBox.hide();
                updating.set(false);
            }
        });
    }

    private void createFlightsTable() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {

            private final Label airline = new Label();
            private final Label flightCode = new Label();
            private final Label departure = new Label();
            private final Label arrival = new Label();
            private final Label departureDate = new Label();
            private final Label minPrice = new Label();
            private final Button flightReserve = new Button();
            private final HBox box = new HBox(150, airline, flightCode, departure, arrival, departureDate, minPrice, flightReserve);
            {
                //Associazione CSS
                box.getStyleClass().add("flight-row");
                airline.getStyleClass().add("airline-label");
                flightCode.getStyleClass().add("flight-code-label");
                departure.getStyleClass().add("departure-label");
                arrival.getStyleClass().add("arrival-label");
                departureDate.getStyleClass().add("date-label");
                minPrice.getStyleClass().add("price-label");
                flightReserve.getStyleClass().add("primary-button");

                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                }
                else {
                    Flight flight = getTableView().getItems().get(getIndex());

                    airline.setText(flight.getAirline().getName());
                    flightCode.setText(flight.getFlightCode());
                    departure.setText(flight.getDeparture().getCity());
                    arrival.setText(flight.getArrival().getCity());
                    departureDate.setText(flight.getDepartureDate().toString());
                    minPrice.setText("A partire da 30 euro");
                    flightReserve.setText("Prenota");

                    setGraphic(box);
                }
            }
        });
        actionsColumn.prefWidthProperty().bind(flightsTable.widthProperty().multiply(0.9785));
    }

    private void updateFlights() {
        updateFlightsTable(flightService.getFlightList());
    }

    private void updateFlightsTable(List<Flight> flights) {
        ObservableList<Flight> obsFlights = FXCollections.observableArrayList(flights);
        flightsTable.setItems(obsFlights);
    }

    //funzione di ripristino e rimozione dei campi "Andata" e "Ritorno"
    private void checkFlightType(String flightType) {
        if(flightType.equals("Solo Andata")) {
            returnFieldsWrapper.setVisible(false);
            returnFieldsWrapper.setManaged(false);
        }
        else{
            returnFieldsWrapper.setVisible(true);
            returnFieldsWrapper.setManaged(true);
        }
    }

    //funzione per aggiornare il contenuto del campo "Passeggeri"
    private void updatePassengerSummary() {
        int adults = Adults.get();
        int children = Children.get();
        int newborns = Newborns.get();

        StringBuilder sb = new StringBuilder();

        if(adults == 1)
            sb.append("1 Adulto");
        else
            sb.append(adults).append(" Adulti");

        if(children > 0){
            sb.append(" ,");
            sb.append(children).append(children == 1 ? " Bambino" : " Bambini");
        }

        if(newborns > 0) {
            sb.append(" ,");
            sb.append(newborns).append(newborns == 1 ? " Neonato" : " Neonati");
        }

        passengerSummary.setText(sb.toString());
    }

    //funzione di creazione delle righe del passengerMenu
    private HBox createPassengerMenuRow(String labelText, IntegerProperty passengerCount) {
        Label passengerLabel = new Label(labelText);

        Button minus =  new Button("Minus");
        Button plus = new Button("Plus");
        Label passengerCountLabel = new Label();

        passengerCountLabel.textProperty().bind(passengerCount.asString());

        //logica dei bottoni
        minus.setOnAction((event) -> {
            if(passengerCount.get() > 0) {
                passengerCount.set(passengerCount.get() - 1);
            }
        });
        plus.setOnAction((event) -> {
            if(passengerCount.get() < 10) {
                passengerCount.set(passengerCount.get() + 1);
            }
        });

        HBox passengerMenuRow = new HBox(10, passengerLabel, minus, passengerCountLabel, plus);
        passengerMenuRow.getStyleClass().add("passenger-row");
        
        return passengerMenuRow;
    }

    //funzione di creazione del passengerMenu
    private CustomMenuItem createPassengerMenuItem() {
        VBox root = new VBox(12);

        root.getChildren().add(createPassengerMenuRow("Adulti", Adults));
        root.getChildren().add(createPassengerMenuRow("Bambini", Children));
        root.getChildren().add(createPassengerMenuRow("Neonati", Newborns));

        CustomMenuItem item = new CustomMenuItem(root);
        item.setHideOnClick(false);

        return item;
    }

    //funzione per la visualizzazione del menu a tendina
    private void openPassengerMenu() {

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(createPassengerMenuItem());

        menu.show(passengerSelector,
                passengerSelector.localToScreen(0, passengerSelector.getHeight()).getX(),
                passengerSelector.localToScreen(0, passengerSelector.getHeight()).getY());
    }

    private List<String> createAirportsNames(String newText) {
        List<Airport> filteredAirports = flightService.airportsFilter(newText);
        List<String> filtered = new ArrayList<>();
        for(Airport a: filteredAirports)
            filtered.add(a.getName() + " - " + a.getCity() + " (" + a.getIata() + ")");

        return filtered;
    }

    //funzione che crea la ricerca
    @FXML
    private void onSearchClicked() {
        FlightSearchRequest request = new FlightSearchRequest();

        request.setDepartureAirport(departureField.getEditor().getText().trim().toLowerCase());
        request.setArrivalAirport(arrivalField.getEditor().getText().trim().toLowerCase());
        request.setDepartureDate(datePickerDeparture.getValue());
        request.setReturnDate(datePickerReturn.getValue());
        request.setJourneyType(journeyType.getValue());

        FlightSearchResult result = flightService.searchFlights(request);
        if(!result.getReturnFlights().isEmpty())
            updateFlightsTable(result.getReturnFlights()); //TODO chiamata per la query andata e ritorno, aggiungere logica
        else
            updateFlightsTable(result.getOutwardFlights()); //query per l'andata
    }
}


