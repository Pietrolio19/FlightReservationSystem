package service.flight;

import domain.flight.*;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import domain.user.User;
import dto.flight.FlightSearchRequest;
import dto.flight.FlightSearchResult;
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

public class FlightServiceTest {
    private FlightService flightService;
    private FlightDAO flightDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;
    private SeatDAO seatDAO;
    private PassengerDAO passengerDAO;
    private ReservationDAO reservationDAO;
    private SeatReservationDAO seatReservationDAO;
    private UserDAO userDAO;

    private Flight flight;
    private Flight returnFlight;
    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;
    private Seat seat1;
    private Seat seat2;
    private Seat returnSeat;
    private Passenger passenger;
    private Reservation reservation;
    private SeatReservation seatReservation;
    private User user;
    private FlightSearchRequest flightSearchRequest;

    @BeforeEach
    void setUp() {
        flightService = new FlightService();

        flightDAO = new FlightDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();
        userDAO = new UserDAO();
        passengerDAO = new PassengerDAO();
        reservationDAO = new ReservationDAO();
        seatReservationDAO = new SeatReservationDAO();
        seatDAO = new SeatDAO();

        departure = new Airport(null, "JFK", "New York", "Stati Uniti", "John F. Kennedy International Airport");
        arrival = new Airport(null, "NRT", "Tokyo", "Giappone", "Narita International Airport");
        airline = new Airline(null, "Japan Airlines", "JL", "JAL", "Giappone");
        aircraft = new Aircraft(null, "B787", "Boeing", 242);

        flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

        returnFlight = new Flight(null, "AB235", arrival, departure, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
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

        returnSeat = new Seat();
        returnSeat.setSeatId(null);
        returnSeat.setFlight(returnFlight);
        returnSeat.setRow(10);
        returnSeat.setLetter("C");
        returnSeat.setType("WINDOW");
        returnSeat.setSeatClass("ECONOMY");
        returnSeat.setPrice(120);

        user = new User();
        user.setUsername("user_test_passenger");
        user.setEmail("user_test_passenger@example.com");
        user.setHashPassword("hashed_password_test");

        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        reservation = new Reservation(null, user, flight);
        reservation.setState("CONFIRMED");

        seatReservation = new SeatReservation(null, passenger, reservation, seat1);
        seatReservation.setState("CONFIRMED");

        airportDAO.insert(departure);
        airportDAO.insert(arrival);
        airlineDAO.insert(airline);
        aircraftDAO.insert(aircraft);
        userDAO.insert(user);
        passengerDAO.insert(passenger);
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
        if (seat1.getSeatId() != null && seat1 != null) {
            seatDAO.deleteById(seat1.getSeatId());
        }
        if (seat2 != null && seat2.getSeatId() != null) {
            seatDAO.deleteById(seat2.getSeatId());
        }
        if (returnSeat != null && returnSeat.getSeatId() != null) {
            seatDAO.deleteById(returnSeat.getSeatId());
        }
        if(user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
        if(passenger != null && passenger.getPassengerId() != null) {
            passengerDAO.deleteById(passenger.getPassengerId());
        }
        if(reservation != null && reservation.getReservationId() != null) {
            reservationDAO.deleteById(reservation.getReservationId());
        }
        if(seatReservation != null && seatReservation.getSeatReservationId() != null) {
            seatReservationDAO.deleteById(seatReservation.getSeatReservationId());
        }
    }

    @Test
    void getFlightList_return_correct_and_mapped_flights() {
        flightDAO.insert(flight);
        List<Flight> flights = flightService.getFlightList();

        Airport testDeparture = airportDAO.findById(flight.getDeparture().getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto non trovato"));

        Airport testArrival = airportDAO.findById(flight.getArrival().getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto non trovato"));

        Airline testAirline = airlineDAO.findById(flight.getAirline().getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnia non trovata"));

        Aircraft testAircraft = aircraftDAO.findById(flight.getAircraft().getAircraftId())
                .orElseThrow(() -> new IllegalArgumentException("Veivolo non trovato"));

        assertNotNull(flights);
        assertFalse(flights.isEmpty());
        Flight foundFlight = flights.stream()
                .filter(r -> r.getFlightId().equals(flight.getFlightId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));

        assertEquals(flight.getFlightId(), foundFlight.getFlightId());
        assertEquals(flight.getFlightCode(), foundFlight.getFlightCode());
        assertEquals(flight.getDeparture().getAirportId(), foundFlight.getDeparture().getAirportId());
        assertEquals(flight.getArrival().getAirportId(), foundFlight.getArrival().getAirportId());
        assertEquals(flight.getDepartureDate(), foundFlight.getDepartureDate());
        assertEquals(flight.getArrivalDate(), foundFlight.getArrivalDate());
        assertEquals(flight.getDepartureTime(), foundFlight.getDepartureTime());
        assertEquals(flight.getArrivalTime(), foundFlight.getArrivalTime());
        assertEquals(flight.getDuration(), foundFlight.getDuration());
        assertEquals(flight.getAirline().getAirlineId(), foundFlight.getAirline().getAirlineId());
        assertEquals(flight.getAircraft().getAircraftId(), foundFlight.getAircraft().getAircraftId());

        assertEquals(departure.getAirportId(), testDeparture.getAirportId());
        assertEquals(departure.getIata(), testDeparture.getIata());
        assertEquals(departure.getCity(), testDeparture.getCity());
        assertEquals(departure.getCountry(), testDeparture.getCountry());
        assertEquals(departure.getName(), testDeparture.getName());

        assertEquals(arrival.getAirportId(), testArrival.getAirportId());
        assertEquals(arrival.getIata(), testArrival.getIata());
        assertEquals(arrival.getCity(), testArrival.getCity());
        assertEquals(arrival.getCountry(), testArrival.getCountry());
        assertEquals(arrival.getName(), testArrival.getName());

        assertEquals(airline.getAirlineId(), testAirline.getAirlineId());
        assertEquals(airline.getIata(), testAirline.getIata());
        assertEquals(airline.getIcao(), testAirline.getIcao());
        assertEquals(airline.getName(), testAirline.getName());
        assertEquals(airline.getCountry(), testAirline.getCountry());

        assertEquals(aircraft.getAircraftId(), testAircraft.getAircraftId());
        assertEquals(aircraft.getModel(), testAircraft.getModel());
        assertEquals(aircraft.getProducer(), testAircraft.getProducer());
        assertEquals(aircraft.getCapacity(), testAircraft.getCapacity());
    }

    @Test
    void getMinPriceAvailable_return_a_flight_with_existing_seats() {
        flightDAO.insert(flight);
        seatDAO.insert(seat1);
        seatDAO.insert(seat2);
        int minimum = flightService.getMinPriceAvailable(flight.getFlightId());

        assertEquals(seat2.getPrice(), minimum);
    }

    @Test
    void testGetMinPriceAvailable_throwsException_whenFlightNotFound() {
        assertThrows(IllegalArgumentException.class, () -> flightService.getMinPriceAvailable(9999L)); // verifica che venga sollevata l'eccezione corretta
    }

    @Test
    void airportsFilter_return_correct_airports() {
        List<Airport> departureAirports = flightService.airportsFilter("John");
        List<Airport> arrivalAirports = flightService.airportsFilter("Narita");

        assertNotNull(departureAirports);
        assertNotNull(arrivalAirports);

        assertFalse(departureAirports.isEmpty());
        assertFalse(arrivalAirports.isEmpty());

        assertTrue(departureAirports.stream()
                .anyMatch(a -> a.getAirportId().equals(departure.getAirportId())));

        assertTrue(arrivalAirports.stream()
                .anyMatch(a -> a.getAirportId().equals(arrival.getAirportId())));
    }

    @Test
    void searchFlights_select_correct_branch_with_outward_only_and_seats_available() {
        flightDAO.insert(flight);
        reservationDAO.insert(reservation);
        seatDAO.insert(seat1);

        flightSearchRequest = new FlightSearchRequest(flight.getDeparture().getName().trim().toLowerCase(),
                flight.getArrival().getName().trim().toLowerCase(),1, flight.getDepartureDate(),
                flight.getArrivalDate(), "Solo Andata");
        FlightSearchResult result = flightService.searchFlights(flightSearchRequest);

        assertNotNull(result);
        assertFalse(result.getOutwardFlights().isEmpty());
        assertTrue(result.getOutwardFlights().stream().anyMatch(f -> f.getFlightId().equals(flight.getFlightId())));
    }

    @Test
    void searchFlights_select_correct_branch_with_outward_only_and_no_seats_available() {
        flightDAO.insert(flight);
        reservationDAO.insert(reservation);
        seatDAO.insert(seat1);
        seatReservationDAO.insert(seatReservation);

        flightSearchRequest = new FlightSearchRequest(flight.getDeparture().getName(),flight.getArrival().getName(),
                1, flight.getDepartureDate(), flight.getArrivalDate(), "Solo Andata");
        FlightSearchResult result = flightService.searchFlights(flightSearchRequest);

        assertTrue(result.getOutwardFlights().isEmpty());
    }

    @Test
    void searchFlights_select_correct_branch_with_outward_and_return_and_seats_available() {
        flightDAO.insert(flight);
        flightDAO.insert(returnFlight);

        seatDAO.insert(seat1);
        seatDAO.insert(returnSeat);

        flightSearchRequest = new FlightSearchRequest(
                flight.getDeparture().getName().trim().toLowerCase(),
                flight.getArrival().getName().trim().toLowerCase(),
                1,
                flight.getDepartureDate(),
                flight.getArrivalDate(),
                "Andata e Ritorno"
        );

        FlightSearchResult result = flightService.searchFlights(flightSearchRequest);

        assertFalse(result.getOutwardFlights().isEmpty());
        assertFalse(result.getReturnFlights().isEmpty());
        assertTrue(result.getOutwardFlights().stream()
                .anyMatch(f -> f.getFlightId().equals(flight.getFlightId())));
        assertTrue(result.getReturnFlights().stream()
                .anyMatch(f -> f.getFlightId().equals(returnFlight.getFlightId())));
    }

    @Test
    void searchFlights_select_correct_branch_with_outward_and_return_and_no_seats_available() {
        flightDAO.insert(flight);
        flightDAO.insert(returnFlight);
        reservationDAO.insert(reservation);
        seatDAO.insert(seat1);
        seatReservationDAO.insert(seatReservation);

        flightSearchRequest = new FlightSearchRequest(flight.getDeparture().getName().trim().toLowerCase(),
                flight.getArrival().getName().trim().toLowerCase(),1, flight.getDepartureDate(),
                flight.getArrivalDate(), "Andata e Ritorno");
        FlightSearchResult result = flightService.searchFlights(flightSearchRequest);

        assertTrue(result.getOutwardFlights().isEmpty());
        assertTrue(result.getReturnFlights().isEmpty());
    }

    @Test
    void isAvailable_return_false_with_no_seats_available() {
        flightDAO.insert(flight);
        reservationDAO.insert(reservation);
        seatDAO.insert(seat1);
        seatReservationDAO.insert(seatReservation);

        assertFalse(flightService.isAvailable(flight));
    }

    @Test
    void isAvailable_return_true_with_seats_available() {
        flightDAO.insert(flight);
        reservationDAO.insert(reservation);
        seatDAO.insert(seat1);

        assertTrue(flightService.isAvailable(flight));
    }
}
