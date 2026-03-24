package UI.controller.user;

import domain.reservation.Reservation;
import domain.user.Passenger;
import domain.user.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.UserProfileService;
import util.session.SessionHandler;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserProfileController {
    //attributi
    UserProfileService userProfileSerivce = new UserProfileService();

    //attributi FXML
    @FXML private VBox userInfo;

    @FXML private VBox reservations;

    @FXML private VBox companions;

    @FXML
    private void initialize() {
        loadUserInfo();
        loadReservation();
        loadCompanions();
    }

    //funzioni per creare la parte anagrafica
    private void loadUserInfo(){
        userProfileSerivce.getSelfPassengerInfo();
        userInfo.getChildren().clear();
        userInfo.getChildren().addAll(createUserInfo(SessionHandler.getInstance().getCurrentUser()));
    }

    private VBox createUserInfo(User user){
        VBox vbox = new VBox(7);
        vbox.getChildren().addAll(
                new HBox(1, new Label("Username: "),       new Label(user.getUsername())),
                new HBox(1, new Label("Email: "),          new Label(user.getEmail())),
                new HBox(1, new Label("Nome: "),           new Label(user.getSelfPassenger().getName())),
                new HBox(1, new Label("Cognome: "),        new Label(user.getSelfPassenger().getSurname())),
                new HBox(1, new Label("Data di nascita: "),new Label(user.getSelfPassenger().getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))),
                new HBox(1, new Label("Indirizzo: "),      new Label(user.getSelfPassenger().getAddress())),
                new HBox(1, new Label("Città: "),          new Label(user.getSelfPassenger().getCity())),
                new HBox(1, new Label("Provincia: "),      new Label(user.getSelfPassenger().getProvince())),
                new HBox(1, new Label("Paese: "),          new Label(user.getSelfPassenger().getCountry())),
                new HBox(1, new Label("Codice fiscale: "), new Label(user.getSelfPassenger().getCodFisc())),
                new HBox(1, new Label("Cod. ID: "),        new Label(user.getSelfPassenger().getCodId())),
                new HBox(1, new Label("Telefono: "),       new Label(user.getSelfPassenger().getPhoneNumber())),
                new HBox(1, new Label("Punti Fedeltà:" ), new Label(String.valueOf(user.getFidelityPoints()))),
                new HBox(1, new Label("Status Fedeltà:" ), new Label(String.valueOf(user.getFidelityStatus())))
        );

        return vbox;
    }

    //funzioni per creare la parte delle prenotazioni
    private void loadReservation(){
        reservations.getChildren().clear();
        List<Reservation> reservationsList = userProfileSerivce.getUserReservations();
        for(Reservation r : reservationsList)
            reservations.getChildren().addAll(createReservation(r));
    }

    private VBox createReservation(Reservation reservation){
        VBox vbox = new VBox(7);
        vbox.getChildren().addAll(
                new HBox(3, new Label("Volo: "), new Label(reservation.getFlight().getFlightCode())),
                new HBox(3, new Label("Da: "), new Label(reservation.getFlight().getDeparture().getName())),
                new HBox(3, new Label("A: "), new Label(reservation.getFlight().getArrival().getName())),
                new HBox(3, new Label("Data Partenza-Arrivo: "), new Label(reservation.getFlight().getDepartureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                                                + "->"
                                                                                + reservation.getFlight().getArrivalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))),
                new HBox(3, new Label("Orario Partenza-Arrivo: "), new Label(reservation.getFlight().getDepartureTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                                                                                + "->"
                                                                                + reservation.getFlight().getArrivalTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))),
                new HBox(3, new Label("Durata Totale: "), new Label(reservation.getFlight().formattedDuration())),
                new HBox(3, new Label("Operato da: "), new Label(reservation.getFlight().getAirline().getName())),
                new HBox(3, new Label("Velivolo: "), new Label(reservation.getFlight().getAircraft().getModel())),
                new HBox(3, new Label("Numero Prenotazione: "), new Label(String.valueOf(reservation.getReservationId()))),
                new HBox(3, new Label("Stato Prenotazione: "), new Label(reservation.getFormattedState())),
                new Separator()
        );

        return vbox;
    }

    //funzioni per creare la parte Companions
    private void loadCompanions(){
        List<Passenger> comp = userProfileSerivce.getUserCompanions();
        if(comp.isEmpty())
            companions.setVisible(false);
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
                    new HBox(1, new Label("Data di nascita: "),new Label(p.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))),
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
