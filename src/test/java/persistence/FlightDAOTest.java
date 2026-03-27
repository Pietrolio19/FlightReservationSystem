package persistence;

import domain.flight.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.FlightDAO;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FlightDAOTest {
     private FlightDAO flightDAO;

     @BeforeEach void setUp() {
         flightDAO = new FlightDAO();
     }

     @Test void findById_return_correct_flight() {
         Flight testFlight = flightDAO.findById(7L).orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));
         assertNotNull(testFlight);
         assertEquals(7L, testFlight.getFlightId());
     }

     @Test void findById_return_null() {
         Optional<Flight> testFlight = flightDAO.findById(9999L);
         assertTrue(testFlight.isEmpty());
     }

     @Test void findById_return_correct_mapped_object() {
         Flight testFlight = flightDAO.findById(7L).orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));
         assertEquals(7L, testFlight.getFlightId());
         assertEquals("AZ201", testFlight.getFlightCode());
         assertEquals(3L, testFlight.getDeparture().getAirportId());
         assertEquals(4L, testFlight.getArrival().getAirportId());
         assertEquals(LocalDate.of(2026, 4, 10), testFlight.getDepartureDate());
         assertEquals(LocalDate.of(2026, 4, 10), testFlight.getArrivalDate());
         assertEquals(Time.valueOf("08:30:00"), testFlight.getDepartureTime());
         assertEquals(Time.valueOf("09:40:00"), testFlight.getArrivalTime());
         assertEquals(70, testFlight.getDuration());
         assertEquals(2L, testFlight.getAirline().getAirlineId());
         assertEquals(2L, testFlight.getAircraft().getAircraftId());
     }
}
