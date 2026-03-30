package service.flight;

import domain.flight.*;
import domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.*;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeatReservationServiceTest {
    private SeatReservationService seatReservationService;

    private SeatDAO seatDAO;
    private AirportDAO airportDAO;
    private AirlineDAO airlineDAO;
    private AircraftDAO aircraftDAO;
    private FlightDAO flightDAO;

    private Seat seat;
    private Flight flight;
    private Airport departure;
    private Airport arrival;
    private Airline airline;
    private Aircraft aircraft;

    @BeforeEach
    void setUp() {
        seatReservationService = new SeatReservationService();

        seatDAO = new SeatDAO();
        aircraftDAO = new AircraftDAO();
        airlineDAO = new AirlineDAO();
        airportDAO = new AirportDAO();
        flightDAO = new FlightDAO();

        departure = new Airport(null, "JFK", "New York", "Stati Uniti", "John F. Kennedy International Airport");
        arrival = new Airport(null, "NRT", "Tokyo", "Giappone", "Narita International Airport");
        airline = new Airline(null, "Japan Airlines", "JL", "JAL", "Giappone");
        aircraft = new Aircraft(null, "B787", "Boeing", 242);

        flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

        seat = new Seat(null, flight, 10, "A", 50);
        seat.setType("MIDDLE");
        seat.setSeatClass("ECONOMY");

        airportDAO.insert(departure);
        airportDAO.insert(arrival);
        airlineDAO.insert(airline);
        aircraftDAO.insert(aircraft);
        flightDAO.insert(flight);
        seatDAO.insert(seat);
    }

    @AfterEach
    void cleanTestData() {
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
        if (seat != null && seat.getSeatId() != null) {
            seatDAO.deleteById(seat.getSeatId());
        }
    }

    @Test
    void getSeatsList_return_correct_seats() {
        List<Seat> seatList = seatReservationService.getSeatsList(flight.getFlightId());

        assertEquals(1, seatList.size());

        Seat foundSeat = seatList.stream().filter(s -> s.getSeatId().equals(seat.getSeatId()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Posto non trovato"));

        Flight testFlight = flightDAO.findById(foundSeat.getFlight().getFlightId())
                .orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));

        assertEquals(seat.getSeatId(), foundSeat.getSeatId());
        assertEquals(seat.getFlight().getFlightId(), foundSeat.getFlight().getFlightId());
        assertEquals(seat.getRow(), foundSeat.getRow());
        assertEquals(seat.getLetter(), foundSeat.getLetter());
        assertEquals(seat.getType(), foundSeat.getType());
        assertEquals(seat.getSeatClass(), foundSeat.getSeatClass());
        assertEquals(seat.getPrice(), foundSeat.getPrice());

        assertEquals(flight.getFlightCode(), testFlight.getFlightCode());
    }
}
