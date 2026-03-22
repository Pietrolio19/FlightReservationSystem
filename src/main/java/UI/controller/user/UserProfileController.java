package UI.controller.user;

import domain.flight.Aircraft;
import domain.flight.Airline;
import domain.flight.Airport;
import domain.flight.Flight;
import domain.reservation.Reservation;
import domain.user.Passenger;
import domain.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserProfileController {
    //attributi FXML
    @FXML private VBox userInfo;

    @FXML private VBox reservations;

    @FXML private VBox companions;

    @FXML
    private void initialize() {
    }

    //funzioni per creare la parte anagrafica
    private void loadUserInfo(User user){
        userInfo.getChildren().clear();
        userInfo.getChildren().addAll(createUserInfo(user));
    }

    private VBox createUserInfo(User user){
        VBox vbox = new VBox(7);
        vbox.getChildren().addAll(
                //new HBox(1, new Label("Username: "),       new Label(user.getUsername())),
                //new HBox(1, new Label("Email: "),          new Label(user.getEmail())),
                //new HBox(1, new Label("Nome: "),           new Label(user.getName())),
                //new HBox(1, new Label("Cognome: "),        new Label(user.getSurname())),
                //new HBox(1, new Label("Data di nascita: "),new Label(user.getDateOfBirth().toString())),
                //new HBox(1, new Label("Indirizzo: "),      new Label(user.getAddress())),
                //new HBox(1, new Label("Città: "),          new Label(user.getCity())),
                //new HBox(1, new Label("Provincia: "),      new Label(user.getProvince())),
                //new HBox(1, new Label("Paese: "),          new Label(user.getCountry())),
                //new HBox(1, new Label("Codice fiscale: "), new Label(user.getCodFisc())),
                //new HBox(1, new Label("Cod. ID: "),        new Label(user.getCodId())),
                //new HBox(1, new Label("Telefono: "),       new Label(user.getPhoneNumber()))
        );

        return vbox;
    }

    //funzioni per creare la parte delle prenotazioni
    private void loadReservation(List<Reservation> reservation){
        reservations.getChildren().clear();
        for(Reservation r : reservation)
            reservations.getChildren().addAll(createReservation(r));
    }

    private VBox createReservation(Reservation reservation){
        VBox vbox = new VBox(7);
        vbox.getChildren().addAll(
                new HBox(3, new Label("Volo: "), new Label(reservation.getFlight().getFlightCode())),
                new HBox(3, new Label("Da: "), new Label(reservation.getFlight().getDeparture().getName())),
                new HBox(3, new Label("A: "), new Label(reservation.getFlight().getArrival().getName())),
                new HBox(3, new Label("Data Partenza-Arrivo: "), new Label(reservation.getFlight().getDepartureDate().toString()
                                                                                + "->"
                                                                                + reservation.getFlight().getArrivalDate().toString())),
                new HBox(3, new Label("Orario Partenza-Arrivo: "), new Label(reservation.getFlight().getDepartureTime().toString()
                                                                                + "->"
                                                                                + reservation.getFlight().getArrivalTime().toString())),
                new HBox(3, new Label("Durata Totale: "), new Label(reservation.getFlight().formattedDuration())),
                new HBox(3, new Label("Operato da: "), new Label(reservation.getFlight().getAirline().getName())),
                new HBox(3, new Label("Velivolo: "), new Label(reservation.getFlight().getAircraft().getModel())),
                new HBox(3, new Label("Numero Prenotazione: "), new Label(String.valueOf(reservation.getReservationId()))),
                new HBox(3, new Label("Stato Prenotazione: "), new Label(reservation.getState())),
                new Separator()
        );

        return vbox;
    }
    //funzioni per creare la parte Companions
    private void loadCompanions(User user){
        List<Passenger> comp = user.getCompanions();
        companions.getChildren().clear();
        companions.getChildren().addAll(createCompanions(comp));
    }

    private VBox createCompanions(List<Passenger> comp){
        VBox vbox = new VBox(7);
        int i = 1;
        for(Passenger p : comp){
            VBox passenger = new VBox(7);
            passenger.getStyleClass().add("passenger-vbox");

            Label passengerMarkerLabel = new Label("Companion " + i);
            passengerMarkerLabel.getStyleClass().add("companion-label");

            passenger.getChildren().addAll(
                    passengerMarkerLabel,
                    new HBox(1, new Label("Nome: "),           new Label(p.getName())),
                    new HBox(1, new Label("Cognome: "),        new Label(p.getSurname())),
                    new HBox(1, new Label("Data di nascita: "),new Label(p.getDateOfBirth().toString())),
                    new HBox(1, new Label("Indirizzo: "),      new Label(p.getAddress())),
                    new HBox(1, new Label("Città: "),          new Label(p.getCity())),
                    new HBox(1, new Label("Provincia: "),      new Label(p.getProvince())),
                    new HBox(1, new Label("Paese: "),          new Label(p.getCountry())),
                    new HBox(1, new Label("Codice fiscale: "), new Label(p.getCodFisc())),
                    new HBox(1, new Label("Cod. ID: "),        new Label(p.getCodId())),
                    new HBox(1, new Label("Telefono: "),       new Label(p.getPhoneNumber()))
            );
            vbox.getChildren().addAll(passenger);
            i++;
        }
        return vbox;
    }
}
