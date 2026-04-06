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

public class TwoWayBookingTest {

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

    private Flight outwardFlight;
    private Flight returnFlight;

    private Seat outwardSeat1;
    private Seat outwardSeat2;
    private Seat returnSeat1;
    private Seat returnSeat2;

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

        outwardFlight = new Flight(
                null,
                "IB2001",
                departure,
                arrival,
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 10),
                Time.valueOf("09:30:00"),
                Time.valueOf("11:15:00"),
                105,
                airline,
                aircraft
        );
        flightDAO.insert(outwardFlight);

        returnFlight = new Flight(
                null,
                "IB2002",
                arrival,
                departure,
                LocalDate.of(2026, 7, 17),
                LocalDate.of(2026, 7, 17),
                Time.valueOf("18:20:00"),
                Time.valueOf("20:05:00"),
                105,
                airline,
                aircraft
        );
        flightDAO.insert(returnFlight);

        outwardSeat1 = new Seat(null, outwardFlight, 12, "A", 80);
        outwardSeat1.setType("WINDOW");
        outwardSeat1.setSeatClass("ECONOMY");

        outwardSeat2 = new Seat(null, outwardFlight, 12, "B", 75);
        outwardSeat2.setType("MIDDLE");
        outwardSeat2.setSeatClass("ECONOMY");

        returnSeat1 = new Seat(null, returnFlight, 14, "A", 90);
        returnSeat1.setType("WINDOW");
        returnSeat1.setSeatClass("ECONOMY");

        returnSeat2 = new Seat(null, returnFlight, 14, "B", 85);
        returnSeat2.setType("MIDDLE");
        returnSeat2.setSeatClass("ECONOMY");

        seatDAO.insert(outwardSeat1);
        seatDAO.insert(outwardSeat2);
        seatDAO.insert(returnSeat1);
        seatDAO.insert(returnSeat2);

        user = new User();
        user.setUsername("roundtrip_user");
        user.setEmail("roundtrip_user@test.com");
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
        if (outwardFlight != null && outwardFlight.getFlightId() != null) {
            flightDAO.deleteById(outwardFlight.getFlightId());
        }
        if (returnFlight != null && returnFlight.getFlightId() != null) {
            flightDAO.deleteById(returnFlight.getFlightId());
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
    void twoWayBookingFlow_should_create_two_confirmed_reservations_and_reuse_same_passengers() {
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
        bookingService.setJourneyType("Andata e Ritorno");

        // ANDATA
        bookingService.setOutwardFlight(outwardFlight);
        bookingService.addSessionSeat(outwardSeat1);
        bookingService.addSessionSeat(outwardSeat2);
        bookingService.mapPassengersAndSeats(outwardSeat1.getSeatCode(), passenger1);
        bookingService.mapPassengersAndSeats(outwardSeat2.getSeatCode(), passenger2);
        bookingService.saveSessionPassengers();
        bookingService.createSeatReservations();
        bookingService.saveBookingData();

        // RITORNO
        bookingService.setReturnFlight(returnFlight);
        bookingService.addSessionSeat(returnSeat1);
        bookingService.addSessionSeat(returnSeat2);
        bookingService.mapPassengersAndSeats(returnSeat1.getSeatCode(), passenger1);
        bookingService.mapPassengersAndSeats(returnSeat2.getSeatCode(), passenger2);
        bookingService.saveSessionPassengers();
        bookingService.createSeatReservations();
        bookingService.saveBookingData();

        List<Reservation> reservations = reservationDAO.findByUser(user.getUserId());
        assertEquals(2, reservations.size());

        Reservation outwardReservation = reservations.stream()
                .filter(r -> r.getFlight().getFlightId().equals(outwardFlight.getFlightId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Prenotazione di andata non trovata"));

        Reservation returnReservation = reservations.stream()
                .filter(r -> r.getFlight().getFlightId().equals(returnFlight.getFlightId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Prenotazione di ritorno non trovata"));

        assertEquals("CONFIRMED", outwardReservation.getState());
        assertEquals("CONFIRMED", returnReservation.getState());

        List<SeatReservation> outwardSeatReservations =
                seatReservationDAO.findByReservationId(outwardReservation.getReservationId());

        List<SeatReservation> returnSeatReservations =
                seatReservationDAO.findByReservationId(returnReservation.getReservationId());

        assertEquals(2, outwardSeatReservations.size());
        assertEquals(2, returnSeatReservations.size());

        assertTrue(outwardSeatReservations.stream().allMatch(sr -> "CONFIRMED".equals(sr.getState())));
        assertTrue(returnSeatReservations.stream().allMatch(sr -> "CONFIRMED".equals(sr.getState())));

        Passenger persistedPassenger1 = passengerDAO
                .findByCodFiscOrCodId("RSSMRA80A01F205X", "DOC001")
                .orElseThrow(() -> new AssertionError("Passeggero 1 non trovato"));

        Passenger persistedPassenger2 = passengerDAO
                .findByCodFiscOrCodId("VRDLGU85B02F205Y", "DOC002")
                .orElseThrow(() -> new AssertionError("Passeggero 2 non trovato"));

        assertTrue(outwardSeatReservations.stream()
                .anyMatch(sr ->
                        sr.getSeat().getSeatId().equals(outwardSeat1.getSeatId()) &&
                                sr.getPassenger().getPassengerId().equals(persistedPassenger1.getPassengerId())
                ));

        assertTrue(outwardSeatReservations.stream()
                .anyMatch(sr ->
                        sr.getSeat().getSeatId().equals(outwardSeat2.getSeatId()) &&
                                sr.getPassenger().getPassengerId().equals(persistedPassenger2.getPassengerId())
                ));

        assertTrue(returnSeatReservations.stream()
                .anyMatch(sr ->
                        sr.getSeat().getSeatId().equals(returnSeat1.getSeatId()) &&
                                sr.getPassenger().getPassengerId().equals(persistedPassenger1.getPassengerId())
                ));

        assertTrue(returnSeatReservations.stream()
                .anyMatch(sr ->
                        sr.getSeat().getSeatId().equals(returnSeat2.getSeatId()) &&
                                sr.getPassenger().getPassengerId().equals(persistedPassenger2.getPassengerId())
                ));

        User updatedUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new AssertionError("Utente non trovato dopo il salvataggio"));

        int expectedPoints = outwardSeat1.getPrice() + outwardSeat2.getPrice()
                + returnSeat1.getPrice() + returnSeat2.getPrice();

        assertEquals(expectedPoints, updatedUser.getFidelityPoints());
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