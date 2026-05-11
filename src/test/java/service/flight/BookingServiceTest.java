package service.flight;

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

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {
    private BookingService bookingService;
    private final SessionHandler sessionHandler = SessionHandler.getInstance();
    private final BookingSession bookingSession = BookingSession.getInstance();

    private PassengerDAO passengerDAO;
    private ReservationDAO reservationDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;
    private SeatReservationDAO seatReservationDAO;
    private SeatDAO seatDAO;
    private UserDAO userDAO;
    private FlightDAO flightDAO;


    private Seat seat1;
    private Seat seat2;
    private Passenger passenger1;
    private Passenger passenger2;
    private User user;
    private Flight flight;
    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;


    @BeforeEach
    void setUp() {
        bookingSession.clear();
        bookingService = new BookingService();

        passengerDAO = new PassengerDAO();
        reservationDAO = new ReservationDAO();
        seatReservationDAO = new SeatReservationDAO();
        seatDAO = new SeatDAO();
        userDAO = new UserDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();
        flightDAO = new FlightDAO();

        departure = new Airport(null, "JFK", "New York", "Stati Uniti", "John F. Kennedy International Airport");
        arrival = new Airport(null, "NRT", "Tokyo", "Giappone", "Narita International Airport");
        airline = new Airline(null, "Japan Airlines", "JL", "JAL", "Giappone");
        aircraft = new Aircraft(null, "B787", "Boeing", 242);

        flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

        seat1 = new Seat();
        seat1.setSeatId(null);
        seat1.setFlight(flight);
        seat1.setRow(10);
        seat1.setLetter("B");
        seat1.setType("MIDDLE");
        seat1.setSeatClass("ECONOMY");
        seat1.setPrice(100);

        seat2 = new Seat();
        seat2.setSeatId(null);
        seat2.setFlight(flight);
        seat2.setRow(11);
        seat2.setLetter("B");
        seat2.setType("MIDDLE");
        seat2.setSeatClass("ECONOMY");
        seat2.setPrice(90);

        passenger1 = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, null
        );

        passenger2 = new Passenger(
                "Maria", "Rossi", LocalDate.of(1997, 1, 10),
                "Via Roma 80", "Torino", "TO", "Italia",
                "RSSMMM95A10F205Z", "ID124456", "3331234669",
                null, null
        );

        user = new User();

        user.setUserId(null);
        user.setUsername("user_test_passenger");
        user.setEmail("user_test_passenger@example.com");
        user.setHashPassword("hashed_password_test");
        user.setSelfPassenger(null);

        userDAO.insert(user);

        passenger1.setCompanionOwner(user);
        passenger2.setCompanionOwner(user);

        airportDAO.insert(departure);
        airportDAO.insert(arrival);
        airlineDAO.insert(airline);
        aircraftDAO.insert(aircraft);
        flightDAO.insert(flight);
        seatDAO.insert(seat1);
        seatDAO.insert(seat2);

        User loginUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        sessionHandler.login(loginUser);

        bookingSession.setSelectedFlight(flight);
        bookingSession.addSeat(seat1);
        bookingSession.addSeat(seat2);
        bookingSession.addPassenger(passenger1);
        bookingSession.addPassenger(passenger2);
        bookingSession.addMappedElement(seat1.getSeatCode(), passenger1);
        bookingSession.addMappedElement(seat2.getSeatCode(), passenger2);
    }

    @AfterEach
    void cleanTestData() {
        if(user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
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
        if (seat1 != null && seat1.getSeatId() != null) {
            seatDAO.deleteById(seat1.getSeatId());
        }
        if(seat2 != null && seat2.getSeatId() != null) {
            seatDAO.deleteById(seat2.getSeatId());
        }
        if(passenger1 != null && passenger1.getPassengerId() != null){
            passengerDAO.deleteById(passenger1.getPassengerId());
        }
        if(passenger2 != null && passenger2.getPassengerId() != null){
            passengerDAO.deleteById(passenger2.getPassengerId());
        }

        bookingSession.clear();
    }

    //test per lato "Dati Passeggeri"
    @Test
    void createSeatReservations_correctly_creates_and_save_seatReservations() {
        bookingService.createSeatReservations();
        List<SeatReservation> sessionReservations = bookingSession.getSeatReservations();

        assertFalse(sessionReservations.isEmpty());
        assertEquals(2, sessionReservations.size());

        SeatReservation sr1 = sessionReservations.stream()
                .filter(sr -> sr.getSeat().getSeatCode().equals(seat1.getSeatCode()))
                .findFirst()
                .orElseThrow();

        SeatReservation sr2 = sessionReservations.stream()
                .filter(sr -> sr.getSeat().getSeatCode().equals(seat2.getSeatCode()))
                .findFirst()
                .orElseThrow();

        assertEquals(passenger1.getCodFisc(), sr1.getPassenger().getCodFisc());
        assertEquals(passenger2.getCodFisc(), sr2.getPassenger().getCodFisc());
        assertNotNull(sr1.getSeat());
        assertNotNull(sr1.getPassenger());
        assertNotNull(sr2.getSeat());
        assertNotNull(sr2.getPassenger());
    }

    //test per lato "Finestra di conferma"
    @Test
    void saveBookingData_correctly_saves_all_booking_data() {
        int initialPoints = user.getFidelityPoints();

        bookingService.createSeatReservations();
        bookingService.saveBookingData();

        Reservation bookingReservation = bookingService.getBookingReservation();

        Passenger savedPassenger1 = passengerDAO.findById(passenger1.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("Passeggero 1 non trovato"));

        Passenger savedPassenger2 = passengerDAO.findById(passenger2.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("Passeggero 2 non trovato"));

        assertNotNull(bookingReservation.getReservationId());

        Reservation savedReservation = reservationDAO.findById(bookingReservation.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        List<SeatReservation> savedSeatReservations =
                seatReservationDAO.findByReservationId(savedReservation.getReservationId());

        SeatReservation savedSeatReservation1 = savedSeatReservations.stream()
                .filter(sr -> sr.getSeat().getSeatId().equals(seat1.getSeatId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("SeatReservation 1 non trovata"));

        SeatReservation savedSeatReservation2 = savedSeatReservations.stream()
                .filter(sr -> sr.getSeat().getSeatId().equals(seat2.getSeatId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("SeatReservation 2 non trovata"));

        User savedUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        int expectedPoints = initialPoints + seat1.getPrice() + seat2.getPrice();

        assertAll(
                () -> assertEquals(passenger1.getPassengerId(), savedPassenger1.getPassengerId()),
                () -> assertEquals(passenger1.getName(), savedPassenger1.getName()),
                () -> assertEquals(passenger1.getSurname(), savedPassenger1.getSurname()),
                () -> assertEquals(passenger1.getDateOfBirth(), savedPassenger1.getDateOfBirth()),
                () -> assertEquals(passenger1.getAddress(), savedPassenger1.getAddress()),
                () -> assertEquals(passenger1.getCity(), savedPassenger1.getCity()),
                () -> assertEquals(passenger1.getProvince(), savedPassenger1.getProvince()),
                () -> assertEquals(passenger1.getCountry(), savedPassenger1.getCountry()),
                () -> assertEquals(passenger1.getCodFisc(), savedPassenger1.getCodFisc()),
                () -> assertEquals(passenger1.getCodId(), savedPassenger1.getCodId()),
                () -> assertEquals(passenger1.getPhoneNumber(), savedPassenger1.getPhoneNumber()),

                () -> assertEquals(passenger2.getPassengerId(), savedPassenger2.getPassengerId()),
                () -> assertEquals(passenger2.getName(), savedPassenger2.getName()),
                () -> assertEquals(passenger2.getSurname(), savedPassenger2.getSurname()),
                () -> assertEquals(passenger2.getDateOfBirth(), savedPassenger2.getDateOfBirth()),
                () -> assertEquals(passenger2.getAddress(), savedPassenger2.getAddress()),
                () -> assertEquals(passenger2.getCity(), savedPassenger2.getCity()),
                () -> assertEquals(passenger2.getProvince(), savedPassenger2.getProvince()),
                () -> assertEquals(passenger2.getCountry(), savedPassenger2.getCountry()),
                () -> assertEquals(passenger2.getCodFisc(), savedPassenger2.getCodFisc()),
                () -> assertEquals(passenger2.getCodId(), savedPassenger2.getCodId()),
                () -> assertEquals(passenger2.getPhoneNumber(), savedPassenger2.getPhoneNumber()),

                () -> assertEquals(user.getUserId(), savedReservation.getUser().getUserId()),
                () -> assertEquals(flight.getFlightId(), savedReservation.getFlight().getFlightId()),
                () -> assertEquals(LocalDate.now(), savedReservation.getDate()),
                () -> assertEquals("CONFIRMED", savedReservation.getState()),

                () -> assertEquals(2, savedSeatReservations.size()),

                () -> assertEquals(passenger1.getPassengerId(), savedSeatReservation1.getPassenger().getPassengerId()),
                () -> assertEquals(savedReservation.getReservationId(), savedSeatReservation1.getReservation().getReservationId()),
                () -> assertEquals(seat1.getSeatId(), savedSeatReservation1.getSeat().getSeatId()),
                () -> assertEquals("CONFIRMED", savedSeatReservation1.getState()),

                () -> assertEquals(passenger2.getPassengerId(), savedSeatReservation2.getPassenger().getPassengerId()),
                () -> assertEquals(savedReservation.getReservationId(), savedSeatReservation2.getReservation().getReservationId()),
                () -> assertEquals(seat2.getSeatId(), savedSeatReservation2.getSeat().getSeatId()),
                () -> assertEquals("CONFIRMED", savedSeatReservation2.getState()),

                () -> assertEquals(user.getUserId(), savedUser.getUserId()),
                () -> assertEquals(user.getEmail(), savedUser.getEmail()),
                () -> assertEquals(user.getUsername(), savedUser.getUsername()),
                () -> assertEquals(expectedPoints, savedUser.getFidelityPoints())
        );
    }
}
