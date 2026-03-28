package persistence.flight;

import domain.flight.*;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import domain.user.User;
import dto.flight.SeatAvailability;
import dto.flight.SeatState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.*;
import persistence.dao.reservation.ReservationDAO;
import persistence.dao.reservation.SeatReservationDAO;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;

import java.time.LocalDate;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SeatDAOTest {
    private SeatDAO seatDAO;
    private FlightDAO flightDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;
    private SeatReservationDAO seatReservationDAO;
    private PassengerDAO passengerDAO;
    private ReservationDAO reservationDAO;
    private UserDAO userDAO;

    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;
    private Flight flight;
    private Seat seat;
    private SeatReservation seatReservation;
    private Passenger passenger;
    private Reservation reservation;
    private User user;

    @BeforeEach
    void setUp() {
        seatDAO = new SeatDAO();
        flightDAO = new FlightDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();
        seatReservationDAO = new SeatReservationDAO();
        passengerDAO = new PassengerDAO();
        reservationDAO = new ReservationDAO();
        userDAO = new UserDAO();

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

        flight = new Flight(null, "ST234", departure, arrival,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"),
                80, airline, aircraft);
        flightDAO.insert(flight);
    }

    @AfterEach
    void cleanTestData() {
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
        if (seat != null && seat.getSeatId() != null) {
            seatDAO.deleteById(seat.getSeatId());
        }
        if (seatReservation != null && seatReservation.getSeatReservationId() != null) {
            seatReservationDAO.deleteById(seatReservation.getSeatReservationId());
        }
        if (reservation != null && reservation.getReservationId() != null) {
            reservationDAO.deleteById(reservation.getReservationId());
        }
        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
        if (passenger != null && passenger.getPassengerId() != null) {
            passengerDAO.deleteById(passenger.getPassengerId());
        }
    }

    @Test
    void findById_return_null() {
        Optional<Seat> testSeat = seatDAO.findById(9999L);
        assertTrue(testSeat.isEmpty());
    }

    @Test
    void findById_return_correct_mapped_object() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        Seat testSeat = seatDAO.findById(seat.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("Posto non trovato"));

        assertEquals(flight.getFlightId(), testSeat.getFlight().getFlightId());
        assertEquals(1, testSeat.getRow());
        assertEquals("A", testSeat.getLetter());
        assertEquals("WINDOW", testSeat.getType());
        assertEquals("ECONOMY", testSeat.getSeatClass());
        assertEquals(80, testSeat.getPrice());
    }

    @Test
    void findByFlightId_return_correct_seats() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        List<Seat> testSeats = seatDAO.findByFlightId(flight.getFlightId());

        assertFalse(testSeats.isEmpty());
        for(Seat s : testSeats) {
            assertEquals(flight.getFlightId(), s.getFlight().getFlightId());
        }
        assertTrue(testSeats.stream().anyMatch(s -> s.getSeatCode().equals(seat.getSeatCode())));
        assertTrue(testSeats.stream().anyMatch(s -> s.getFlight().getFlightId().equals(seat.getFlight().getFlightId())));

    }

    @Test
    void getSeatStateByFlightId_return_correct_states() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        reservation = new Reservation(null, user, flight);
        reservation.confirm();
        reservationDAO.insert(reservation);

        passenger = new Passenger("Mario", "Rossi", LocalDate.of(1995, 3, 15), "Via Roma 10",
                "Milano", "MI", "Italia", "RSSMRA95C15F205X", "CA1234567", "3331234567", null, user);
        passengerDAO.insert(passenger);

        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        List<SeatState> testSeatState = seatDAO.getSeatStateByFlightId(seat.getFlight().getFlightId());
        assertFalse(testSeatState.isEmpty());
        assertEquals(1, testSeatState.size());
        for(SeatState state : testSeatState) {
            assertEquals(seat.getSeatCode(), state.getSeat().getSeatCode());
            assertEquals(seat.getFlight().getFlightId(), state.getSeat().getFlight().getFlightId());
            assertEquals(seatReservation.getState(), state.getState());
        }
    }

    @Test
    void getSeatAvailabilityByFlightId_return_correct_values_with_no_seatReservations() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        SeatAvailability seatAvailability = seatDAO.getSeatAvailabilityByFlightId(seat.getFlight().getFlightId());

        assertEquals(1, seatAvailability.getTotalSeats());
        assertEquals(1, seatAvailability.getAvailableSeats());
        assertEquals(0, seatAvailability.getConfirmedSeats());
    }

    @Test
    void getSeatAvailabilityByFlightId_return_correct_values_with_seatReservations() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        reservation = new Reservation(null, user, flight);
        reservation.confirm();
        reservationDAO.insert(reservation);

        passenger = new Passenger("Mario", "Rossi", LocalDate.of(1995, 3, 15), "Via Roma 10",
                "Milano", "MI", "Italia", "RSSMRA95C15F205X", "CA1234567", "3331234567", null, user);
        passengerDAO.insert(passenger);

        seatReservation = new SeatReservation(null, passenger, reservation, seat);
        seatReservation.setState("CONFIRMED");
        seatReservationDAO.insert(seatReservation);

        SeatAvailability seatAvailability = seatDAO.getSeatAvailabilityByFlightId(seat.getFlight().getFlightId());

        assertEquals(1, seatAvailability.getTotalSeats());
        assertEquals(0, seatAvailability.getAvailableSeats());
        assertEquals(1, seatAvailability.getConfirmedSeats());
    }

    @Test
    void findAll_return_a_list_of_seats() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        List<Seat> testSeats = seatDAO.findAll();
        assertTrue(testSeats.stream().anyMatch(s -> s.getSeatId().equals(seat.getSeatId())));
        assertFalse(testSeats.isEmpty());
    }

    @Test
    void insert_should_create_a_new_table_row() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        Seat testSeat = seatDAO.findById(seat.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("Posto non trovato"));

        assertEquals("A", testSeat.getLetter());
    }

    @Test
    void update_should_modify_target_seat() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        seat.setPrice(95);
        seatDAO.update(seat);

        Seat updatedSeat = seatDAO.findById(seat.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("Posto non trovato"));

        assertEquals(95, updatedSeat.getPrice());
    }

    @Test
    void deleteById_should_delete_correct_seat() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);
        seatDAO.deleteById(seat.getSeatId());

        Optional<Seat> deletedSeat = seatDAO.findById(seat.getSeatId());
        assertTrue(deletedSeat.isEmpty());
    }

    @Test
    void save_should_insert_when_id_is_null() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.save(seat);

        assertNotNull(seat.getSeatId());
    }

    @Test
    void save_should_update_when_id_is_not_null() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);

        seat.setPrice(95);
        seatDAO.save(seat);

        Seat testSeat = seatDAO.findById(seat.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("Posto non trovato"));

        assertEquals(95, testSeat.getPrice());
    }

    @Test
    void crudFlow_should_insert_update_and_delete_seat_correctly() {
        seat = new Seat(null, flight, 1, "A", 80);
        seat.setType("WINDOW");
        seat.setSeatClass("ECONOMY");

        seatDAO.insert(seat);
        assertNotNull(seat.getSeatId());

        Seat testSeat = seatDAO.findById(seat.getSeatId())
                .orElseThrow(() -> new AssertionError("Posto non trovato dopo insert"));
        assertEquals("A", testSeat.getLetter());

        testSeat.setPrice(95);
        seatDAO.update(testSeat);

        Seat updatedSeat = seatDAO.findById(seat.getSeatId())
                .orElseThrow(() -> new AssertionError("Posto non trovato dopo update"));
        assertEquals(95, updatedSeat.getPrice());

        seatDAO.deleteById(seat.getSeatId());

        Optional<Seat> deletedSeat = seatDAO.findById(seat.getSeatId());
        assertTrue(deletedSeat.isEmpty());

        seat = null;
    }
}
