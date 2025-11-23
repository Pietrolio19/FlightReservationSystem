package UI.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class FlightSearchController {
        @FXML
        private TextField departureField;

        @FXML
        private TextField arrivalField;

        @FXML
        private DatePicker datePickerOutward;

        @FXML
        private DatePicker datePickerReturn;

        @FXML
        private Button searchButton;

        @FXML
        private TableView<?> flightsTable;

        @FXML
        private ComboBox<String> journeyType = new ComboBox<>();

        @FXML
        private CheckBox checkDirect;

        @FXML
        private void initialize() {
            journeyType.getItems().addAll("Andata e Ritorno", "Solo Andata", "Solo Ritorno");
            journeyType.setValue("Andata e Ritorno");
            checkDirect.setSelected(false);
        }
}


