package UI.controller.admin;

import domain.user.Passenger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import persistence.dao.user.PassengerDAO;
import service.admin.AdminService;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdminPassengerController {
    private final AdminService adminService = new AdminService();

    @FXML
    private ListView<Passenger> passengerListView;

    @FXML
    private HBox mainBox;

    @FXML
    private VBox leftBox;

    @FXML
    private VBox rightBox;

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
    private Button passengerUpdateButton;

    @FXML
    private Button passengerDeleteButton;

    @FXML
    private void initialize() {
        leftBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.4));
        rightBox.prefWidthProperty().bind(mainBox.widthProperty().multiply(0.6));

        loadPassengerInfo();

        passengerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showRecordContent(newValue);
            }
        });

        passengerUpdateButton.setOnAction(e -> handleUpdate());

        passengerDeleteButton.setOnAction(e -> {
            Passenger selectedPassenger = passengerListView.getSelectionModel().getSelectedItem();
            if (selectedPassenger != null) {
                adminService.deletePassenger(selectedPassenger.getPassengerId());
                loadPassengerInfo();
            }
        });
    }

    @FXML
    private void handleUpdate() {
        Passenger selectedPassenger = passengerListView.getSelectionModel().getSelectedItem();

        if (selectedPassenger == null) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Aggiorna passeggero");
        dialog.setHeaderText("Modifica i dati del passeggero selezionato");

        ButtonType saveButtonType = new ButtonType("Salva", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(selectedPassenger.getName());
        TextField surnameField = new TextField(selectedPassenger.getSurname());
        DatePicker dateOfBirthPicker = new DatePicker(selectedPassenger.getDateOfBirth());
        TextField addressField = new TextField(selectedPassenger.getAddress());
        TextField cityField = new TextField(selectedPassenger.getCity());
        TextField provinceField = new TextField(selectedPassenger.getProvince());
        TextField countryField = new TextField(selectedPassenger.getCountry());
        TextField codFiscField = new TextField(selectedPassenger.getCodFisc());
        TextField codIDField = new TextField(selectedPassenger.getCodId());
        TextField phoneNumberField = new TextField(selectedPassenger.getPhoneNumber());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Cognome:"), 0, 1);
        grid.add(surnameField, 1, 1);

        grid.add(new Label("Data di nascita:"), 0, 2);
        grid.add(dateOfBirthPicker, 1, 2);

        grid.add(new Label("Indirizzo:"), 0, 3);
        grid.add(addressField, 1, 3);

        grid.add(new Label("Città:"), 0, 4);
        grid.add(cityField, 1, 4);

        grid.add(new Label("Provincia:"), 0, 5);
        grid.add(provinceField, 1, 5);

        grid.add(new Label("Stato:"), 0, 6);
        grid.add(countryField, 1, 6);

        grid.add(new Label("Codice fiscale:"), 0, 7);
        grid.add(codFiscField, 1, 7);

        grid.add(new Label("Codice ID:"), 0, 8);
        grid.add(codIDField, 1, 8);

        grid.add(new Label("Cellulare:"), 0, 9);
        grid.add(phoneNumberField, 1, 9);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == saveButtonType) {
            selectedPassenger.setName(nameField.getText());
            selectedPassenger.setSurname(surnameField.getText());
            selectedPassenger.setDateOfBirth(dateOfBirthPicker.getValue());
            selectedPassenger.setAddress(addressField.getText());
            selectedPassenger.setCity(cityField.getText());
            selectedPassenger.setProvince(provinceField.getText());
            selectedPassenger.setCountry(countryField.getText());
            selectedPassenger.setCodFisc(codFiscField.getText());
            selectedPassenger.setCodId(codIDField.getText());
            selectedPassenger.setPhoneNumber(phoneNumberField.getText());

            adminService.updatePassenger(selectedPassenger);
            loadPassengerInfo();
        }
    }

    private void loadPassengerInfo() {
        ObservableList<Passenger> current = FXCollections.observableList(adminService.findAllPassengers());
        passengerListView.setItems(current);

        passengerListView.setCellFactory(lv -> new ListCell<Passenger>() {
            @Override
            protected void updateItem(Passenger passenger, boolean empty) {
                super.updateItem(passenger, empty);

                if (empty || passenger == null) {
                    setText(null);
                } else {
                    setText(passenger.getName() + " " + passenger.getSurname());
                }
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
