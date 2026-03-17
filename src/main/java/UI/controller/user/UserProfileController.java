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
        //per styling e test TODO rimuovere dopo DB
        User user = new User("Pietro", "Madioni", LocalDate.of(2003, 5, 11),
                            "Via francesco girolami ferdinando, 53", "Roma",
                            "RO", "Italia", "MFJDOSPAJDB",
                            "CA546HF", "3333333333", 1L,
                            "Pietrolio19", "pietromadi...@gmail.com", "123");

        Passenger passenger = new Passenger("Pietro", "Madioni", LocalDate.of(2003, 5, 11),
                                            "Via francesco girolami ferdinando, 53", "Roma",
                                            "RO", "Italia", "MFJDOSPAJDB",
                                            "CA546HF", "3333333333", 1L, user);

        Airport airport = new Airport(2L,"FCO", "Roma", "Italia", "Roma Fiumicino");
        Airport airport1 = new Airport(1L, "MIA", "Monaco", "Germania", "Aeroporto Internazionale Franz Josef Strauss");
        Airport airport2 = new Airport(3L,"HTW", "Londra", "Inghilterra", " Londra Heathrow");
        Airport airport3 = new Airport(4L,"DBX", "Dubai", " Emirati Arabi","Aeroporto Internazionale di Dubai" );
        Airline airline = new Airline(1L, "WizzAir", "34FF", "34FCF", "Lussemburgo");
        Aircraft aircraft = new Aircraft(1L, "A350", "Airbus", 550);

        Flight flight = new Flight(1L,"AZ123", airport, airport2, LocalDate.now(), LocalDate.now(), Time.valueOf("21:20:00"), Time.valueOf("23:30:00"), 130, 12, airline, aircraft);
        Flight flight1 = new Flight(2L, "AZ4312", airport1, airport3, LocalDate.now(), LocalDate.now(), Time.valueOf("11:45:00"), Time.valueOf("16:20:00"), 275, 15, airline, aircraft);
        Reservation reservation = new Reservation(1L, user, flight);
        Reservation reservation1 = new Reservation(2L, user, flight1);
        loadUserInfo(user);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        reservations.add(reservation1);
        loadReservation(reservations);
        user.addCompanion(passenger);
        user.addCompanion(passenger);
        user.addCompanion(passenger);
        loadCompanions(user);
        //rimuovere fino a qui
    }

    //funzioni per creare la parte anagrafica
    private void loadUserInfo(User user){
        userInfo.getChildren().clear();
        userInfo.getChildren().addAll(createUserInfo(user));
    }

    private VBox createUserInfo(User user){
        VBox vbox = new VBox(7);
        vbox.getChildren().addAll(
                new HBox(1, new Label("Username: "),       new Label(user.getUsername())),
                new HBox(1, new Label("Email: "),          new Label(user.getEmail())),
                new HBox(1, new Label("Nome: "),           new Label(user.getName())),
                new HBox(1, new Label("Cognome: "),        new Label(user.getSurname())),
                new HBox(1, new Label("Data di nascita: "),new Label(user.getDateOfBirth().toString())),
                new HBox(1, new Label("Indirizzo: "),      new Label(user.getAddress())),
                new HBox(1, new Label("Città: "),          new Label(user.getCity())),
                new HBox(1, new Label("Provincia: "),      new Label(user.getProvince())),
                new HBox(1, new Label("Paese: "),          new Label(user.getCountry())),
                new HBox(1, new Label("Codice fiscale: "), new Label(user.getCodFisc())),
                new HBox(1, new Label("Cod. ID: "),        new Label(user.getCodId())),
                new HBox(1, new Label("Telefono: "),       new Label(user.getPhoneNumber()))
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
