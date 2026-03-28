package persistence.reservation;

import domain.flight.Aircraft;
import domain.flight.Airline;
import domain.flight.Airport;
import domain.flight.Flight;
import domain.reservation.Reservation;
import domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.AircraftDAO;
import persistence.dao.flight.AirlineDAO;
import persistence.dao.flight.AirportDAO;
import persistence.dao.flight.FlightDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.user.UserDAO;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationDAOTest {
    private ReservationDAO reservationDAO;
    private UserDAO userDAO;
    private FlightDAO flightDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;

    private User user;
    private Reservation reservation;
    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;
    private Flight flight;

    @BeforeEach
    void setUp() {
        reservationDAO = new ReservationDAO();
        userDAO = new UserDAO();
        flightDAO = new FlightDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();

        user = new User(null, "TestUserReservation", "reservation_test@example.com", "testpw");
        userDAO.insert(user);

        departure = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");
        arrival = new Airport(null, "NRT", "Tokyo",
                "Giappone", "Narita International Airport");
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        airportDAO.insert(departure);
        airportDAO.insert(arrival);
        airlineDAO.insert(airline);
        aircraftDAO.insert(aircraft);

        flight = new Flight(null, "RS234", departure, arrival,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"),
                80, airline, aircraft);
        flightDAO.insert(flight);
    }

    @AfterEach
    void cleanTestData() {
        if (reservation != null && reservation.getReservationId() != null) {
            reservationDAO.deleteById(reservation.getReservationId());
        }
        if (flight != null && flight.getFlightId() != null) {
            flightDAO.deleteById(flight.getFlightId());
        }
        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
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
    }

    @Test
    void findById_return_null() {
        Optional<Reservation> testReservation = reservationDAO.findById(9999L);
        assertTrue(testReservation.isEmpty());
    }

    @Test
    void findById_return_correct_mapped_object() {
        reservation = new Reservation(null, user, flight);
        reservation.confirm();

        reservationDAO.insert(reservation);

        Reservation testReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals(user.getUserId(), testReservation.getUser().getUserId());
        assertEquals(flight.getFlightId(), testReservation.getFlight().getFlightId());
        assertEquals(LocalDate.now(), testReservation.getDate());
        assertEquals("CONFIRMED", testReservation.getState());
    }

    @Test
    void findByUser_return_correct_reservation() {
        reservation = new Reservation(null, user, flight);
        reservation.confirm();

        reservationDAO.insert(reservation);
        List<Reservation> testReservation = reservationDAO.findByUser(reservation.getUser().getUserId());

        assertFalse(testReservation.isEmpty());
        assertEquals(1, testReservation.size());
        for(Reservation r : testReservation) {
            assertEquals(reservation.getUser().getUserId(), r.getUser().getUserId());
            assertEquals(reservation.getFlight().getFlightId(), r.getFlight().getFlightId());
            assertEquals(reservation.getState(), r.getState());
        }
    }

    @Test
    void findByUser_return_empty_with_not_existing_user() {
        reservation = new Reservation(null, user, flight);
        reservation.confirm();

        reservationDAO.insert(reservation);
        List<Reservation> testReservation = reservationDAO.findByUser(9999L);

        assertTrue(testReservation.isEmpty());
    }

    @Test
    void findAll_return_a_list_of_reservations() {
        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));
        reservation.confirm();

        reservationDAO.insert(reservation);

        List<Reservation> testReservations = reservationDAO.findAll();

        assertFalse(testReservations.isEmpty());
        assertTrue(testReservations.stream().anyMatch(r -> r.getReservationId().equals(reservation.getReservationId())));
    }

    @Test
    void insert_should_create_a_new_table_row() {
        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));
        reservation.confirm();

        reservationDAO.insert(reservation);

        Reservation testReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals("CONFIRMED", testReservation.getState());
    }

    @Test
    void update_should_modify_target_reservation() {
        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));

        reservationDAO.insert(reservation);

        reservation.confirm();
        reservationDAO.update(reservation);

        Reservation updatedReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals("CONFIRMED", updatedReservation.getState());
    }

    @Test
    void deleteById_should_delete_correct_reservation() {
        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));
        reservation.confirm();

        reservationDAO.insert(reservation);
        reservationDAO.deleteById(reservation.getReservationId());

        Optional<Reservation> deletedReservation = reservationDAO.findById(reservation.getReservationId());
        assertTrue(deletedReservation.isEmpty());

        reservation = null;
    }

    @Test
    void save_should_insert_when_id_is_null() {
        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));
        reservation.confirm();

        reservationDAO.save(reservation);

        assertNotNull(reservation.getReservationId());
    }

    @Test
    void save_should_update_when_id_is_not_null() {
        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));

        reservationDAO.insert(reservation);

        reservation.confirm();
        reservationDAO.save(reservation);

        Reservation testReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals("CONFIRMED", testReservation.getState());
    }

    @Test
    void crudFlow_should_insert_update_and_delete_reservation_correctly() {
        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));

        reservationDAO.insert(reservation);
        assertNotNull(reservation.getReservationId());

        Reservation testReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new AssertionError("Prenotazione non trovata dopo insert"));
        assertEquals("PENDING", testReservation.getState());

        testReservation.confirm();
        reservationDAO.update(testReservation);

        Reservation updatedReservation = reservationDAO.findById(reservation.getReservationId())
                .orElseThrow(() -> new AssertionError("Prenotazione non trovata dopo update"));
        assertEquals("CONFIRMED", updatedReservation.getState());

        reservationDAO.deleteById(reservation.getReservationId());

        Optional<Reservation> deletedReservation = reservationDAO.findById(reservation.getReservationId());
        assertTrue(deletedReservation.isEmpty());

        reservation = null;
    }
}