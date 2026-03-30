package service.admin;

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

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {
    private AdminService adminService;

    private UserDAO userDAO;
    private FlightDAO flightDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;
    private ReservationDAO reservationDAO;
    private PassengerDAO passengerDAO;
    private SeatDAO seatDAO;
    private SeatReservationDAO seatReservationDAO;

    private User user;
    private Flight flight;
    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;
    private Reservation reservation;
    private Passenger passenger;
    private Seat seat;
    private SeatReservation seatReservation;

    @BeforeEach
    void setUp() {
        adminService = new AdminService();
        userDAO = new UserDAO();
        flightDAO = new FlightDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();
        reservationDAO = new ReservationDAO();
        passengerDAO = new PassengerDAO();
        seatDAO = new SeatDAO();
        seatReservationDAO = new SeatReservationDAO();

        user = new User();
        user.setUserId(null);
        user.setUsername("TestUser");
        user.setEmail("testuser@gmail.com");
        user.setHashPassword("testuserpw");

        departure = new Airport(null, "JFK", "New York", "Stati Uniti", "John F. Kennedy International Airport");
        arrival = new Airport(null, "NRT", "Tokyo", "Giappone", "Narita International Airport");
        airline = new Airline(null, "Japan Airlines", "JL", "JAL", "Giappone");
        aircraft = new Aircraft(null, "B787", "Boeing", 242);

        flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

        reservation = new Reservation();
        reservation.setReservationId(null);
        reservation.setUser(user);
        reservation.setFlight(flight);
        reservation.setState("CONFIRMED");

        passenger = new Passenger();
        passenger.setPassengerId(null);
        passenger.setName("Mario");
        passenger.setSurname("Rossi");
        passenger.setDateOfBirth(LocalDate.of(2026, 10, 5));
        passenger.setAddress("Via Raffaello");
        passenger.setCity("Firenze");
        passenger.setCountry("Italia");
        passenger.setCodFisc("ABC123456789");
        passenger.setCodId("987654321");
        passenger.setPhoneNumber("1234567890");
        passenger.setCompanionOwner(user);

        seat = new Seat();
        seat.setSeatId(null);
        seat.setFlight(flight);
        seat.setRow(10);
        seat.setLetter("A");
        seat.setType("MIDDLE");
        seat.setSeatClass("ECONOMY");
        seat.setPrice(100);

        userDAO.insert(user);
        airportDAO.insert(departure);
        airportDAO.insert(arrival);
        airlineDAO.insert(airline);
        aircraftDAO.insert(aircraft);
        flightDAO.insert(flight);
        passengerDAO.insert(passenger);
        seatDAO.insert(seat);

        seatReservation = new SeatReservation();
        seatReservation.setPassenger(passenger);
        seatReservation.setReservation(reservation);
        seatReservation.setSeat(seat);
        seatReservation.setState("CONFIRMED");
    }

    @AfterEach
    void cleanTestData() {
        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
        if (flight != null && flight.getFlightId() != null) {
            flightDAO.deleteById(flight.getFlightId());
        }
        if (reservation != null && reservation.getReservationId() != null) {
            reservationDAO.deleteById(reservation.getReservationId());
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
        if (passenger != null && passenger.getPassengerId() != null) {
            passengerDAO.deleteById(passenger.getPassengerId());
        }
        if (seat != null && seat.getSeatId() != null) {
            seatDAO.deleteById(seat.getSeatId());
        }
        if (seatReservation != null && seatReservation.getSeatReservationId() != null) {
            seatReservationDAO.deleteById(seatReservation.getSeatReservationId());
        }
    }

    @Test
    void findAllReservation_return_correct_reservations() {
        reservationDAO.insert(reservation);
        List<Reservation> reservations = adminService.findAllReservation();

        User testUser = userDAO.findById(user.getUserId()).orElseThrow(() -> new IllegalArgumentException("User non trovato"));
        Flight testFlight = flightDAO.findById(flight.getFlightId()).orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));

        assertNotNull(reservations);
        assertFalse(reservations.isEmpty());
        Reservation foundReservation = reservations.stream()
                .filter(r -> r.getReservationId().equals(reservation.getReservationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals(testUser.getUserId(), foundReservation.getUser().getUserId());
        assertEquals(testUser.getUsername(), foundReservation.getUser().getUsername());
        assertEquals(testUser.getEmail(), foundReservation.getUser().getEmail());

        assertEquals(testFlight.getFlightId(), foundReservation.getFlight().getFlightId());
        assertEquals(testFlight.getFlightCode(), foundReservation.getFlight().getFlightCode());
        assertEquals(testFlight.getDeparture().getAirportId(), foundReservation.getFlight().getDeparture().getAirportId());
        assertEquals(testFlight.getArrival().getAirportId(), foundReservation.getFlight().getArrival().getAirportId());
        assertEquals(testFlight.getDepartureDate(), foundReservation.getFlight().getDepartureDate());
        assertEquals(testFlight.getArrivalDate(), foundReservation.getFlight().getArrivalDate());
        assertEquals(testFlight.getDepartureTime(), foundReservation.getFlight().getDepartureTime());
        assertEquals(testFlight.getArrivalTime(), foundReservation.getFlight().getArrivalTime());
        assertEquals(testFlight.getDuration(), foundReservation.getFlight().getDuration());
        assertEquals(testFlight.getAirline().getAirlineId(), foundReservation.getFlight().getAirline().getAirlineId());
        assertEquals(testFlight.getAircraft().getAircraftId(), foundReservation.getFlight().getAircraft().getAircraftId());

        assertEquals(reservation.getReservationId(), foundReservation.getReservationId());
        assertEquals(LocalDate.now(), foundReservation.getDate());
        assertEquals(reservation.getState(), foundReservation.getState());
    }

    //La funzione findAllFLights non necessita di test dato che è già stata testata nella classe FlightServiceTest

    @Test
    void findAllSeatReservation_return_correct_reservations() {
        reservationDAO.insert(reservation);
        seatReservationDAO.insert(seatReservation);
        List<SeatReservation> seatReservations = adminService.findAllSeatReservation();

        Passenger testPassenger = passengerDAO.findById(passenger.getPassengerId()).orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));
        Reservation testReservation = reservationDAO.findById(reservation.getReservationId()).orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
        Seat testSeat = seatDAO.findById(seat.getSeatId()).orElseThrow(() -> new IllegalArgumentException("Posto non trovato"));

        assertNotNull(seatReservations);
        assertFalse(seatReservations.isEmpty());
        SeatReservation foundReservation = seatReservations.stream()
                .filter(r -> r.getSeatReservationId().equals(seatReservation.getSeatReservationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        assertEquals(seatReservation.getSeatReservationId(), foundReservation.getSeatReservationId());

        assertEquals(testPassenger.getPassengerId(), foundReservation.getPassenger().getPassengerId());
        assertEquals(testPassenger.getName(), foundReservation.getPassenger().getName());
        assertEquals(testPassenger.getSurname(), foundReservation.getPassenger().getSurname());
        assertEquals(testPassenger.getCodFisc(), foundReservation.getPassenger().getCodFisc());
        assertEquals(testPassenger.getCodId(), foundReservation.getPassenger().getCodId());

        assertEquals(testReservation.getReservationId(), foundReservation.getReservation().getReservationId());
        assertEquals(testReservation.getUser().getUserId(), foundReservation.getReservation().getUser().getUserId());
        assertEquals(testReservation.getFlight().getFlightId(), foundReservation.getReservation().getFlight().getFlightId());
        assertEquals(testReservation.getState(), foundReservation.getReservation().getState());

        assertEquals(testSeat.getSeatId(), foundReservation.getSeat().getSeatId());
        assertEquals(testSeat.getFlight().getFlightId(), foundReservation.getSeat().getFlight().getFlightId());
        assertEquals(testSeat.getRow(), foundReservation.getSeat().getRow());
        assertEquals(testSeat.getLetter(), foundReservation.getSeat().getLetter());
        assertEquals(testSeat.getType(), foundReservation.getSeat().getType());
        assertEquals(testSeat.getSeatClass(), foundReservation.getSeat().getSeatClass());
        assertEquals(testSeat.getPrice(), foundReservation.getSeat().getPrice());
    }
}
