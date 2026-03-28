package persistence.reservation;

import domain.flight.*;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.AircraftDAO;
import persistence.dao.flight.AirlineDAO;
import persistence.dao.flight.AirportDAO;
import persistence.dao.flight.FlightDAO;
import persistence.dao.flight.SeatDAO;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SeatReservationDAOTest {
    private SeatReservationDAO seatReservationDAO;
    private PassengerDAO passengerDAO;
    private ReservationDAO reservationDAO;
    private SeatDAO seatDAO;
    private UserDAO userDAO;
    private FlightDAO flightDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;

    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;
    private Flight flight;
    private Seat seat;
    private User user;
    private Reservation reservation;
    private Passenger passenger;
    private SeatReservation seatReservation;

    @BeforeEach
    void setUp() {
        seatReservationDAO = new SeatReservationDAO();
        passengerDAO = new PassengerDAO();
        reservationDAO = new ReservationDAO();
        seatDAO = new SeatDAO();
        userDAO = new UserDAO();
        flightDAO = new FlightDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();

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

        flight = new Flight(null, "SR234", departure, arrival,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"),
                80, airline, aircraft);
        flightDAO.insert(flight);

        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");
        seatDAO.insert(seat);

        user = new User(null, "TestUserSeatRes", "seatreservation_test@example.com", "testpw");
        userDAO.insert(user);

        reservation = new Reservation(null, user, flight);
        reservation.setDate(LocalDate.of(2026, 5, 1));
        reservation.confirm();
        reservationDAO.insert(reservation);

        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );
        passengerDAO.insert(passenger);
    }

    @AfterEach
    void cleanTestData() {
        if (seatReservation != null && seatReservation.getSeatReservationId() != null) {
            seatReservationDAO.deleteById(seatReservation.getSeatReservationId());
        }
        if (passenger != null && passenger.getPassengerId() != null) {
            passengerDAO.deleteById(passenger.getPassengerId());
        }
        if (reservation != null && reservation.getReservationId() != null) {
            reservationDAO.deleteById(reservation.getReservationId());
        }
        if (seat != null && seat.getSeatId() != null) {
            seatDAO.deleteById(seat.getSeatId());
        }
        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
        if (flight != null && flight.getFlightId() != null) {
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
    }

    @Test
    void findById_return_null() {
        Optional<SeatReservation> testSeatReservation = seatReservationDAO.findById(9999L);
        assertTrue(testSeatReservation.isEmpty());
    }

    @Test
    void findById_return_correct_mapped_object() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        SeatReservation testSeatReservation = seatReservationDAO.findById(seatReservation.getSeatReservationId())
                .orElseThrow(() -> new IllegalArgumentException("SeatReservation non trovata"));

        assertEquals(passenger.getPassengerId(), testSeatReservation.getPassenger().getPassengerId());
        assertEquals(reservation.getReservationId(), testSeatReservation.getReservation().getReservationId());
        assertEquals(seat.getSeatId(), testSeatReservation.getSeat().getSeatId());
        assertEquals("CONFIRMED", testSeatReservation.getState());
    }

    @Test
    void findByReservationId_return_correct_seatReservation() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        List<SeatReservation> testSeatReservation = seatReservationDAO.findByReservationId(seatReservation.getReservation().getReservationId());

        assertFalse(testSeatReservation.isEmpty());
        assertEquals(1, testSeatReservation.size());
        for(SeatReservation sr : testSeatReservation) {
            assertEquals(passenger.getPassengerId(), sr.getPassenger().getPassengerId());
            assertEquals(seatReservation.getSeatReservationId(), sr.getSeatReservationId());
            assertEquals(seat.getSeatId(), sr.getSeat().getSeatId());
            assertEquals(seatReservation.getState(), sr.getState());
        }
    }

    @Test
    void findByReservationId_return_empty_with_not_existing_reservation() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        List<SeatReservation> testSeatReservation = seatReservationDAO.findByReservationId(9999L);

        assertTrue(testSeatReservation.isEmpty());
    }

    @Test
    void findAll_return_a_list_of_seatReservations() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        List<SeatReservation> testSeatReservations = seatReservationDAO.findAll();

        assertFalse(testSeatReservations.isEmpty());
        assertTrue(testSeatReservations.stream()
                .anyMatch(sr -> sr.getSeatReservationId().equals(seatReservation.getSeatReservationId())));
    }

    @Test
    void insert_should_create_a_new_table_row() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        SeatReservation testSeatReservation = seatReservationDAO.findById(seatReservation.getSeatReservationId())
                .orElseThrow(() -> new IllegalArgumentException("SeatReservation non trovata"));

        assertEquals("CONFIRMED", testSeatReservation.getState());
    }

    @Test
    void update_should_modify_target_seatReservation() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservationDAO.insert(seatReservation);

        seatReservation.confirm();
        seatReservationDAO.update(seatReservation);

        SeatReservation updatedSeatReservation = seatReservationDAO.findById(seatReservation.getSeatReservationId())
                .orElseThrow(() -> new IllegalArgumentException("SeatReservation non trovata"));

        assertEquals("CONFIRMED", updatedSeatReservation.getState());
    }

    @Test
    void deleteById_should_delete_correct_seatReservation() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        seatReservationDAO.deleteById(seatReservation.getSeatReservationId());

        Optional<SeatReservation> deletedSeatReservation =
                seatReservationDAO.findById(seatReservation.getSeatReservationId());

        assertTrue(deletedSeatReservation.isEmpty());

        seatReservation = null;
    }

    @Test
    void save_should_insert_when_id_is_null() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");

        seatReservationDAO.save(seatReservation);

        assertNotNull(seatReservation.getSeatReservationId());
    }

    @Test
    void save_should_update_when_id_is_not_null() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservationDAO.insert(seatReservation);

        seatReservation.confirm();
        seatReservationDAO.save(seatReservation);

        SeatReservation testSeatReservation = seatReservationDAO.findById(seatReservation.getSeatReservationId())
                .orElseThrow(() -> new IllegalArgumentException("SeatReservation non trovata"));

        assertEquals("CONFIRMED", testSeatReservation.getState());
    }

    @Test
    void crudFlow_should_insert_update_and_delete_seatReservation_correctly() {
        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservationDAO.insert(seatReservation);
        assertNotNull(seatReservation.getSeatReservationId());

        SeatReservation testSeatReservation = seatReservationDAO.findById(seatReservation.getSeatReservationId())
                .orElseThrow(() -> new AssertionError("SeatReservation non trovata dopo insert"));
        assertEquals("PENDING", testSeatReservation.getState());

        testSeatReservation.confirm();
        seatReservationDAO.update(testSeatReservation);

        SeatReservation updatedSeatReservation = seatReservationDAO.findById(seatReservation.getSeatReservationId())
                .orElseThrow(() -> new AssertionError("SeatReservation non trovata dopo update"));
        assertEquals("CONFIRMED", updatedSeatReservation.getState());

        seatReservationDAO.deleteById(seatReservation.getSeatReservationId());

        Optional<SeatReservation> deletedSeatReservation =
                seatReservationDAO.findById(seatReservation.getSeatReservationId());

        assertTrue(deletedSeatReservation.isEmpty());

        seatReservation = null;
    }
}