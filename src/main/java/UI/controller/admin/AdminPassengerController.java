package UI.controller.admin;

import domain.user.Passenger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import persistence.dao.user.PassengerDAO;

import java.time.format.DateTimeFormatter;

public class AdminPassengerController {
    private final PassengerDAO passengerDAO = new PassengerDAO();

    @FXML
    private ListView<Passenger> passengerListView;

    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label dateOfBirthLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label cityLabel;

    @FXML
    private Label provinceLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label codFiscLabel;

    @FXML
    private Label codIDLabel;

    @FXML
    private Label phoneNumberLabel;


    @FXML
    private void initialize() {
        loadPassengerInfo();
    }

    private void loadPassengerInfo() {
        ObservableList<Passenger> current = FXCollections.observableList(passengerDAO.findAll());
        passengerListView.setItems(current);
        passengerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showRecordContent(newValue);
            }
        });
    }

    private void showRecordContent(Passenger passenger) {
        idLabel.setText("Numero Passeggero: " + passenger.getPassengerId());
        nameLabel.setText("Nome: " + passenger.getName());
        surnameLabel.setText("Cognome: " + passenger.getSurname());
        dateOfBirthLabel.setText("Data di Nascita: " + passenger.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        addressLabel.setText("Indirizzo: " + passenger.getAddress());
        cityLabel.setText("Città: " + passenger.getCity());
        provinceLabel.setText("Provincia: " + passenger.getProvince());
        countryLabel.setText("Stato: " + passenger.getCountry());
        codFiscLabel.setText("Codice Fiscale: " + passenger.getCodFisc());
        codIDLabel.setText("Codice ID: " + passenger.getCodId());
        phoneNumberLabel.setText("Cellulare: " + passenger.getPhoneNumber());
    }
}
