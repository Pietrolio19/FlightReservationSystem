package UI.controller.flight;

import UI.navigator.Navigator;
import UI.navigator.NavigatorAware;
import domain.flight.Airport;
import domain.flight.Flight;
import dto.flight.FlightSearchRequest;
import dto.flight.FlightSearchResult;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import service.SeatReservationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class FlightSearchController implements NavigatorAware {
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
    public Navigator navigator;

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

            String text = newText == null ? "" : newText.trim();

            if (text.isEmpty()) {
                updating.set(true);
                comboBox.setValue(null);
                comboBox.getSelectionModel().clearSelection();
                comboBox.hide();
                updating.set(false);
                return;
            }

            String selected = comboBox.getValue();
            if (selected != null && selected.equals(newText)) return;

            List<String> filtered = createAirportsNames(text);
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

            //TODO migliorare immagini
            private final Label departureCity = new Label();
            private final Label departureIata = new Label();
            private final Label departureTime = new Label();
            private final ImageView takeoff = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/UI/images/planeTakeoff.png")).toExternalForm()));

            private final Label arrivalCity = new Label();
            private final Label arrivalIata = new Label();
            private final Label arrivalTime = new Label();
            private final ImageView land = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/UI/images/planeLand.png")).toExternalForm()));

            private final Label departureDate = new Label();

            private final Label duration = new Label();
            private final Region leftDashedLine = new Region();
            private final Region rightDashedLine = new Region();

            private final Label minPrice = new Label();
            private final Button flightReserve = new Button("Prenota");

            private final VBox airlineInfoBox = new VBox(10, airline, flightCode);
            private final HBox departureInfoBox = new HBox(5, departureCity, departureIata, departureTime, takeoff);
            private final HBox routeLineBox = new HBox(8, leftDashedLine, duration, rightDashedLine);
            private final VBox middleInfoBox = new VBox(5, routeLineBox);
            private final HBox arrivalInfoBox = new HBox(5, land, arrivalCity, arrivalIata, arrivalTime);

            private final HBox box1 = new HBox(10, departureInfoBox, middleInfoBox, arrivalInfoBox);
            private final HBox box2 = new HBox(departureDate);
            private final HBox box3 = new HBox(10, minPrice, flightReserve);
            private final Region spacer = new Region();
            private final HBox box = new HBox(120, airlineInfoBox, box1);

            private final HBox bottomHBox = new HBox(box2, spacer, box3);
            private final VBox vbox = new VBox(10, box, bottomHBox);

            {
                //Associazione CSS
                vbox.getStyleClass().add("flight-row");
                airlineInfoBox.getStyleClass().add("airline-box");
                departureInfoBox.getStyleClass().add("departure-box");
                arrivalInfoBox.getStyleClass().add("arrival-box");
                leftDashedLine.getStyleClass().add("dashed-line");
                rightDashedLine.getStyleClass().add("dashed-line");
                departureDate.getStyleClass().add("date-label");
                minPrice.getStyleClass().add("price-label");
                flightReserve.getStyleClass().add("primary-button");

                //Posizionamento
                box.setAlignment(Pos.CENTER);
                box2.setAlignment(Pos.CENTER_LEFT);
                box3.setAlignment(Pos.CENTER_RIGHT);
                bottomHBox.setAlignment(Pos.CENTER_LEFT);
                airlineInfoBox.setAlignment(Pos.CENTER);
                departureInfoBox.setAlignment(Pos.CENTER_LEFT);
                middleInfoBox.setAlignment(Pos.CENTER);
                arrivalInfoBox.setAlignment(Pos.CENTER_RIGHT);
                routeLineBox.setAlignment(Pos.CENTER);

                HBox.setHgrow(spacer, Priority.ALWAYS);

                leftDashedLine.setPrefWidth(300);
                rightDashedLine.setPrefWidth(300);
                leftDashedLine.setTranslateY(-15);
                rightDashedLine.setTranslateY(-15);

                leftDashedLine.setMaxWidth(700);
                rightDashedLine.setMaxWidth(700);

                    flightReserve.setOnAction(e -> {
                        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            Flight currentFlight = getTableView().getItems().get(getIndex());
                            navigator.loadSeatReservationView(currentFlight);
                        }
                    });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                Flight flight = getTableView().getItems().get(getIndex());

                airline.setText(flight.getAirline().getName());
                flightCode.setText(flight.getFlightCode());
                departureCity.setText(flight.getDeparture().getCity());
                departureIata.setText(flight.getDeparture().getIata());
                departureTime.setText(flight.getDepartureTime().toString());
                arrivalCity.setText(flight.getArrival().getCity());
                arrivalIata.setText(flight.getArrival().getIata());
                arrivalTime.setText(flight.getArrivalTime().toString());
                departureDate.setText(flight.getDepartureDate().toString());
                duration.setText(flight.formattedDuration());
                minPrice.setText(flight.formattedPrice());

                setGraphic(vbox);
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

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}


