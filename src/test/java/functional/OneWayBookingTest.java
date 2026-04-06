package functional;

import domain.flight.Aircraft;
import domain.flight.Airline;
import domain.flight.Airport;
import domain.flight.Flight;
import domain.flight.Seat;
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
import service.flight.BookingService;
import util.security.PasswordHasher;
import util.session.BookingSession;
import util.session.SessionHandler;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OneWayBookingTest {

    private final BookingService bookingService = new BookingService();
    private final BookingSession bookingSession = BookingSession.getInstance();
    private final SessionHandler sessionHandler = SessionHandler.getInstance();

    private final AirportDAO airportDAO = new AirportDAO();
    private final AirlineDAO airlineDAO = new AirlineDAO();
    private final AircraftDAO aircraftDAO = new AircraftDAO();
    private final FlightDAO flightDAO = new FlightDAO();
    private final SeatDAO seatDAO = new SeatDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final SeatReservationDAO seatReservationDAO = new SeatReservationDAO();

    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;
    private Flight flight;
    private Seat seat1;
    private Seat seat2;
    private User user;

    @BeforeEach
    void setUp() {
        bookingSession.clear();
        bookingSession.clearTotal();
        sessionHandler.logout();

        departure = new Airport(null, "MCP", "Milano", "Italia", "Malpensa");
        arrival = new Airport(null, "BCN", "Barcellona", "Spagna", "Barcelona El Prat");
        airline = new Airline(null, "Iberia", "IS", "ISE", "Spagna");
        aircraft = new Aircraft(null, "A320", "Airbus", 180);

        airportDAO.insert(departure);
        airportDAO.insert(arrival);
        airlineDAO.insert(airline);
        aircraftDAO.insert(aircraft);

        flight = new Flight(
                null,
                "IB1234",
                departure,
                arrival,
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 10),
                Time.valueOf("09:30:00"),
                Time.valueOf("11:15:00"),
                105,
                airline,
                aircraft
        );
        flightDAO.insert(flight);

        seat1 = new Seat(null, flight, 12, "A", 80);
        seat1.setType("WINDOW");
        seat1.setSeatClass("ECONOMY");

        seat2 = new Seat(null, flight, 12, "B", 75);
        seat2.setType("MIDDLE");
        seat2.setSeatClass("ECONOMY");

        seatDAO.insert(seat1);
        seatDAO.insert(seat2);

        user = new User();
        user.setUsername("functional_user");
        user.setEmail("functional_user@test.com");
        user.setHashPassword(PasswordHasher.hash("Password123"));

        userDAO.insert(user);
        sessionHandler.login(user);
    }

    @AfterEach
    void tearDown() {
        bookingSession.clear();
        bookingSession.clearTotal();
        sessionHandler.logout();

        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }

        if (flight != null && flight.getFlightId() != null) {
            flightDAO.deleteById(flight.getFlightId());
        }
        if (airline != null && airline.getAirlineId() != null) {
            airlineDAO.deleteById(airline.getAirlineId());
        }
        if (aircraft != null && aircraft.getAircraftId() != null) {
            aircraftDAO.deleteById(aircraft.getAircraftId());
        }
        if (departure != null && departure.getAirportId() != null) {
            airportDAO.deleteById(departure.getAirportId());
        }
        if (arrival != null && arrival.getAirportId() != null) {
            airportDAO.deleteById(arrival.getAirportId());
        }
    }

    @Test
    void oneWayBookingFlow_should_create_reservation_and_related_seat_reservations() {
        Passenger passenger1 = buildPassenger(
                "Mario", "Rossi",
                "RSSMRA80A01F205X",
                "DOC001"
        );

        Passenger passenger2 = buildPassenger(
                "Luigi", "Verdi",
                "VRDLGU85B02F205Y",
                "DOC002"
        );

        bookingSession.setTotalPassengers(2);
        bookingService.setJourneyType("Solo Andata");
        bookingService.setOutwardFlight(flight);

        bookingService.addSessionSeat(seat1);
        bookingService.addSessionSeat(seat2);

        bookingService.mapPassengersAndSeats(seat1.getSeatCode(), passenger1);
        bookingService.mapPassengersAndSeats(seat2.getSeatCode(), passenger2);

        bookingService.saveSessionPassengers();
        bookingService.createSeatReservations();
        bookingService.saveBookingData();

        List<Reservation> reservations = reservationDAO.findByUser(user.getUserId());
        Reservation savedReservation = reservations.stream()
                .filter(r -> r.getFlight().getFlightId().equals(flight.getFlightId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Prenotazione non trovata"));

        assertEquals(user.getUserId(), savedReservation.getUser().getUserId());
        assertEquals(flight.getFlightId(), savedReservation.getFlight().getFlightId());
        assertEquals("CONFIRMED", savedReservation.getState());

        List<SeatReservation> savedSeatReservations =
                seatReservationDAO.findByReservationId(savedReservation.getReservationId());

        assertEquals(2, savedSeatReservations.size());
        assertTrue(savedSeatReservations.stream().allMatch(sr -> "CONFIRMED".equals(sr.getState())));

        SeatReservation firstSeatReservation = savedSeatReservations.stream()
                .filter(sr -> sr.getSeat().getSeatId().equals(seat1.getSeatId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("SeatReservation del primo posto non trovata"));

        SeatReservation secondSeatReservation = savedSeatReservations.stream()
                .filter(sr -> sr.getSeat().getSeatId().equals(seat2.getSeatId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("SeatReservation del secondo posto non trovata"));

        Passenger persistedPassenger1 = passengerDAO
                .findByCodFiscOrCodId("RSSMRA80A01F205X", "DOC001")
                .orElseThrow(() -> new AssertionError("Passeggero 1 non trovato"));

        Passenger persistedPassenger2 = passengerDAO
                .findByCodFiscOrCodId("VRDLGU85B02F205Y", "DOC002")
                .orElseThrow(() -> new AssertionError("Passeggero 2 non trovato"));

        assertEquals(persistedPassenger1.getPassengerId(), firstSeatReservation.getPassenger().getPassengerId());
        assertEquals(persistedPassenger2.getPassengerId(), secondSeatReservation.getPassenger().getPassengerId());

        User updatedUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new AssertionError("Utente non trovato dopo il salvataggio"));

        assertEquals(seat1.getPrice() + seat2.getPrice(), updatedUser.getFidelityPoints());
        assertEquals("BRONZE", updatedUser.getFidelityStatus());
    }

    private Passenger buildPassenger(String name, String surname, String codFisc, String codId) {
        Passenger passenger = new Passenger();
        passenger.setName(name);
        passenger.setSurname(surname);
        passenger.setDateOfBirth(LocalDate.of(1990, 1, 1));
        passenger.setCodFisc(codFisc);
        passenger.setCodId(codId);
        passenger.setCompanionOwner(user);
        return passenger;
    }
}
