package UI.controller.flight;

import domain.flight.Seat;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.w3c.dom.Text;
import util.session.BookingSession;

import java.awt.print.Book;
import java.util.Date;

public class PassengerController {
    //attributi

    @FXML
    private VBox passengerCardsArea;

    @FXML
    private void initialize() {
        createPassengerCardList();
    }

    private void createPassengerCardList() {
        for(Seat s: BookingSession.getInstance().getSelectedSeats()) {
            passengerCardsArea.getChildren().add(createPassengerCard(s));
        }
    }

    private Node createPassengerCard(Seat seat) {
        Label seatLabel = new Label("Posto " + seat.getSeatCode());

        Label nameLabel = new Label("Nome:");
        TextField nameField = new TextField();
        HBox nameBox = new HBox(10, nameLabel, nameField);

        Label surnameLabel = new Label("Cognome:");
        TextField surnameField = new TextField();
        HBox surnameBox = new HBox(10, surnameLabel, surnameField);

        Label birthDateLabel = new Label("Data di Nascita:");
        DatePicker birthDateField  = new DatePicker();
        birthDateField.setPromptText("Scegli data");
        HBox birthDateBox = new HBox(10, birthDateLabel, birthDateField);

        HBox firstRow = new HBox(100, nameBox, surnameBox, birthDateBox);
        HBox.setHgrow(firstRow, Priority.ALWAYS);

        Label addressLabel = new Label("Indirizzo:");
        TextField addressField = new TextField();
        HBox secondRow = new HBox(10, addressLabel, addressField);
        HBox.setHgrow(secondRow, Priority.ALWAYS);

        Label cityLabel = new Label("Città:");
        TextField cityField = new TextField();
        HBox cityBox = new HBox(10, cityLabel, cityField);

        Label provinceLabel = new Label("Provincia:");
        TextField provinceField = new TextField();
        HBox provinceBox = new HBox(10, provinceLabel, provinceField);

        Label countryLabel = new Label("Stato:");
        TextField countryField = new TextField();
        HBox countryBox = new HBox(10, countryLabel, countryField);

        HBox thirdRow = new HBox(cityBox, provinceBox, countryBox);

        Label codFiscLabel = new Label("Codice Fiscale:");
        TextField codFiscField = new TextField();
        HBox codFiscBox = new HBox(10, codFiscLabel, codFiscField);

        Label codIdLabel = new Label("Codice Carta Identità:");
        TextField codIdField = new TextField();
        HBox codIdBox = new HBox(10, codIdLabel, codIdField);

        Label phoneLabel = new Label("Cellulare:");
        TextField phoneField = new TextField();
        HBox phoneBox = new HBox(10, phoneLabel, phoneField);

        HBox lastRow = new HBox(codFiscBox, codIdBox, phoneBox);

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

        return card;
    }
}
