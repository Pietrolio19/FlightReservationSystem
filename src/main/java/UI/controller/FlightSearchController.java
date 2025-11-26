package UI.controller;

import domain.flight.Flight;
import domain.flight.Airport;
import domain.flight.Aircraft;
import domain.flight.Airline;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FlightSearchController {
    //attributi FXML
    //campi per la ricerca
    @FXML private TextField departureField;

    @FXML private TextField arrivalField;

    @FXML private VBox returnFieldsWrapper;

    @FXML private DatePicker datePickerOutward;

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

    @FXML
    private void initialize() {
        journeyType.getItems().addAll("Andata e Ritorno", "Solo Andata");
        journeyType.setValue("Andata e Ritorno");
        journeyType.valueProperty().addListener((ov, oldV, newV) -> checkFlightType(newV));

        checkDirect.setSelected(false);

        passengerSelector.setOnMouseClicked(e -> openPassengerMenu());
        Adults.addListener((obs, oldV, newV) -> updatePassengerSummary());
        Children.addListener((obs, oldV, newV) -> updatePassengerSummary());
        Newborns.addListener((obs, oldV, newV) -> updatePassengerSummary());

        animalSelector.getItems().addAll("Nessuno","1 Cane", "1 Gatto");
        animalSelector.setValue("Nessuno");

        //Codice per testare e stylare correttamente la tabella dei risultati TODO rimuovere post DB
        Airport airport = new Airport(2, "Roma", "Italia", "Roma Fiumicino");
        Airport airport2 = new Airport(3, "Londra", "Inghilterra", "Heathrow");
        Airline airline = new Airline(1, "WizzAir", "34FF", "34FCF", "Lussemburgo");
        Aircraft aircraft = new Aircraft(1, "A350", "Airbus", 550);

        flightsTable.getItems().add(new Flight(1,"AZ123", airport, airport2, LocalDate.now(), LocalDate.now(), Time.valueOf("21:20:00"), Time.valueOf("23:30:00"), 130, airline, aircraft));

        actionsColumn.setCellFactory(col -> new TableCell<>() {

            private final Label airline = new Label("WizzAir");
            private final Label flightCode = new Label("WZ1324");
            private final Label departure = new Label("Roma");
            private final Label arrival = new Label("Londra");
            private final Label departureDate = new Label("25/11/2025");
            private final Label minPrice = new Label("A partire da 30 euro");
            private final Button flightReservation = new Button("Prenota");
            private final HBox box = new HBox(150, airline, flightCode, departure, arrival, departureDate, minPrice, flightReservation);
            {
                //Associazione CSS
                box.getStyleClass().add("flight-row");
                airline.getStyleClass().add("airline-label");
                flightCode.getStyleClass().add("flight-code-label");
                departure.getStyleClass().add("departure-label");
                arrival.getStyleClass().add("arrival-label");
                departureDate.getStyleClass().add("date-label");
                minPrice.getStyleClass().add("price-label");
                flightReservation.getStyleClass().add("primary-button");

                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                }

                else {
                    setGraphic(box);
                }
                //else {
                //    Flight flight = getTableView().getItems().get(getIndex());

                //    airline.setText(flight.getAirline().getName());
                //    flightCode.setText(flight.getFlightId());
                //    departure.setText(flight.getDeparture().getCity());
                //    arrival.setText(flight.getArrival().getCity());
                //    departureDate.setText(flight.getDepartureDate().toString());
                //    }
            }
        });
        actionsColumn.prefWidthProperty().bind(flightsTable.widthProperty().multiply(0.9785));
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
}


