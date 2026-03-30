package service.user;

import domain.flight.*;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.*;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;
import util.session.BookingSession;
import util.session.SessionHandler;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserProfileServiceTest {
    private UserProfileService userProfileService;
    private SessionHandler sessionHandler = SessionHandler.getInstance();

    private PassengerDAO passengerDAO;
    private ReservationDAO reservationDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;
    private FlightDAO flightDAO;
    private UserDAO userDAO;
    private SeatReservationDAO seatReservationDAO;
    private SeatDAO seatDAO;

    private User user;
    private Passenger passenger;
    private Reservation reservation;
    private Flight flight;
    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;
    private SeatReservation seatReservation;
    private Seat seat;

    @BeforeEach
    void setUp() {
        userProfileService = new UserProfileService();
        passengerDAO = new PassengerDAO();
        reservationDAO = new ReservationDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();
        flightDAO = new FlightDAO();
        userDAO = new UserDAO();
        seatReservationDAO = new SeatReservationDAO();
        seatDAO = new SeatDAO();

        user = new User();

        user.setUserId(null);
        user.setUsername("user_test_passenger");
        user.setEmail("user_test_passenger@example.com");
        user.setHashPassword("hashed_password_test");

        departure = new Airport(null, "JFK", "New York", "Stati Uniti", "John F. Kennedy International Airport");
        arrival = new Airport(null, "NRT", "Tokyo", "Giappone", "Narita International Airport");
        airline = new Airline(null, "Japan Airlines", "JL", "JAL", "Giappone");
        aircraft = new Aircraft(null, "B787", "Boeing", 242);

        flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.now());
        reservation.confirm();

        seat = new Seat(null, flight, 10, "A", 50);
        seat.setType("MIDDLE");
        seat.setSeatClass("ECONOMY");

        seatReservation = new SeatReservation(null, passenger, reservation, seat);

        user.setSelfPassenger(passenger);

        sessionHandler.login(user);
        airportDAO.insert(departure);
        airportDAO.insert(arrival);
        airlineDAO.insert(airline);
        aircraftDAO.insert(aircraft);
        flightDAO.insert(flight);
        userDAO.insert(user);
        passengerDAO.insert(passenger);
        reservationDAO.insert(reservation);
        seatDAO.insert(seat);
        seatReservationDAO.insert(seatReservation);
    }

    @AfterEach
    void cleanTestData() {
        if(passenger != null && passenger.getPassengerId() != null) {
            passengerDAO.deleteById(passenger.getPassengerId());
        }
        if(flight != null && flight.getFlightId() != null) {
            flightDAO.deleteById(flight.getFlightId());
        }
        if (departure != null && departure.getAirportId() != null) {
            airportDAO.deleteById(departure.getAirportId());
        }
        if (arrival != null && arrival.getAirportId() != null) {
            airportDAO.deleteById(arrival.getAirportId());
        }
        if (airline != null && airline.getAirlineId() != null) {
            airlineDAO.deleteById(airline.getAirlineId());
        }
        if (aircraft != null && aircraft.getAircraftId() != null) {
            aircraftDAO.deleteById(aircraft.getAircraftId());
        }
        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
        if (reservation != null && reservation.getReservationId() != null) {
            reservationDAO.deleteById(reservation.getReservationId());
        }
        if (seat != null && seat.getSeatId() != null) {
            seatDAO.deleteById(seat.getSeatId());
        }
        if (seatReservation != null && seatReservation.getSeatReservationId() != null) {
            seatReservationDAO.deleteById(seatReservation.getSeatReservationId());
        }
    }

    @Test
    void getSelfPassengerInfo_return_correct_passenger() {
        userProfileService.getSelfPassengerInfo();
        
        User testUser = sessionHandler.getCurrentUser();
        Passenger testPassenger = sessionHandler.getCurrentUser().getSelfPassenger();
        
        assertNotNull(testUser);
        assertNotNull(testPassenger);
        
        assertEquals(user.getUsername(), testUser.getUsername());
        assertEquals(user.getEmail(), testUser.getEmail());

        assertEquals(passenger.getPassengerId(), testPassenger.getPassengerId());
        assertEquals(passenger.getName(), testPassenger.getName());
        assertEquals(passenger.getSurname(), testPassenger.getSurname());
        assertEquals(passenger.getDateOfBirth(), testPassenger.getDateOfBirth());
        assertEquals(passenger.getAddress(), testPassenger.getAddress());
        assertEquals(passenger.getCity(), testPassenger.getCity());
        assertEquals(passenger.getProvince(), testPassenger.getProvince());
        assertEquals(passenger.getCountry(), testPassenger.getCountry());
        assertEquals(passenger.getCodFisc(), testPassenger.getCodFisc());
        assertEquals(passenger.getCodId(), testPassenger.getCodId());
        assertEquals(passenger.getPhoneNumber(), testPassenger.getPhoneNumber());
    }

    @Test
    void getUserReservations_return_correct_reservations() {
        List<Reservation> reservations = userProfileService.getUserReservations();

        assertEquals(1, reservations.size());
        Reservation foundReservation = reservations.stream().filter(r -> r.getReservationId().equals(reservation.getReservationId()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals(reservation.getUser().getUserId(), foundReservation.getUser().getUserId());
        assertEquals(reservation.getFlight().getFlightId(), foundReservation.getFlight().getFlightId());
        assertEquals(LocalDate.now(), foundReservation.getDate());
        assertEquals(reservation.getState(), foundReservation.getState());
    }

    @Test
    void getUserCompanions_return_correct_passengers() {
        List<Passenger> passengers = userProfileService.getUserCompanions();

        assertEquals(1, passengers.size());

        Passenger testPassenger = passengers.stream().filter(p -> p.getPassengerId().equals(passenger.getPassengerId()))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));

        assertEquals(passenger.getPassengerId(), testPassenger.getPassengerId());
        assertEquals(passenger.getName(), testPassenger.getName());
        assertEquals(passenger.getSurname(), testPassenger.getSurname());
        assertEquals(passenger.getDateOfBirth(), testPassenger.getDateOfBirth());
        assertEquals(passenger.getAddress(), testPassenger.getAddress());
        assertEquals(passenger.getCity(), testPassenger.getCity());
        assertEquals(passenger.getProvince(), testPassenger.getProvince());
        assertEquals(passenger.getCountry(), testPassenger.getCountry());
        assertEquals(passenger.getCodFisc(), testPassenger.getCodFisc());
        assertEquals(passenger.getCodId(), testPassenger.getCodId());
        assertEquals(passenger.getPhoneNumber(), testPassenger.getPhoneNumber());
    }

    @Test
    void getUserCompanions_correctly_saves_passengers_in_session() {
        userProfileService.getUserCompanions();

        List<Passenger> passengers = sessionHandler.getCurrentUser().getCompanions();

        assertEquals(1, passengers.size());

        Passenger testPassenger = passengers.stream().filter(p -> p.getPassengerId().equals(passenger.getPassengerId()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));

        assertEquals(passenger.getPassengerId(), testPassenger.getPassengerId());
        assertEquals(passenger.getName(), testPassenger.getName());
        assertEquals(passenger.getSurname(), testPassenger.getSurname());
        assertEquals(passenger.getDateOfBirth(), testPassenger.getDateOfBirth());
        assertEquals(passenger.getAddress(), testPassenger.getAddress());
        assertEquals(passenger.getCity(), testPassenger.getCity());
        assertEquals(passenger.getProvince(), testPassenger.getProvince());
        assertEquals(passenger.getCountry(), testPassenger.getCountry());
        assertEquals(passenger.getCodFisc(), testPassenger.getCodFisc());
        assertEquals(passenger.getCodId(), testPassenger.getCodId());
        assertEquals(passenger.getPhoneNumber(), testPassenger.getPhoneNumber());
    }

    @Test
    void removeReservation_correctly_cancels_reservation() {
        Reservation testReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovato"));

        assertEquals(reservation.getReservationId(), testReservation.getReservationId());
        assertEquals(reservation.getUser().getUserId(), testReservation.getUser().getUserId());
        assertEquals(reservation.getFlight().getFlightId(), testReservation.getFlight().getFlightId());
        assertEquals(LocalDate.now(), testReservation.getDate());
        assertEquals("CONFIRMED", testReservation.getState());

        userProfileService.removeReservation(reservation);

        testReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovato"));

        assertEquals(reservation.getReservationId(), testReservation.getReservationId());
        assertEquals(reservation.getUser().getUserId(), testReservation.getUser().getUserId());
        assertEquals(reservation.getFlight().getFlightId(), testReservation.getFlight().getFlightId());
        assertEquals(LocalDate.now(), testReservation.getDate());
        assertEquals("CANCELED", testReservation.getState());
    }

    @Test
    void removeReservation_correctly_cancels_seatReservation() {
        userProfileService.removeReservation(reservation);

        List<SeatReservation> seatReservations = seatReservationDAO.findByReservationId(reservation.getReservationId());

        SeatReservation testSeatReservation = seatReservations.stream().filter(
                sr -> sr.getReservation().getReservationId().equals(reservation.getReservationId()))
                        .findFirst().orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals(1, seatReservations.size());

        assertEquals(seatReservation.getSeatReservationId(), testSeatReservation.getSeatReservationId());
        assertEquals(seatReservation.getSeat().getSeatId(), testSeatReservation.getSeat().getSeatId());
        assertEquals(seatReservation.getPassenger().getPassengerId(), testSeatReservation.getPassenger().getPassengerId());
        assertEquals(seatReservation.getReservation().getReservationId(), testSeatReservation.getReservation().getReservationId());
        assertEquals(seatReservation.getDate(), testSeatReservation.getDate());
        assertEquals("CANCELED", testSeatReservation.getState());

    }
}
