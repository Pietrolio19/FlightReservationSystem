package persistence.flight;

import domain.flight.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.*;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FlightDAOTest {
     private FlightDAO flightDAO;
     private AirportDAO airportDAO;
     private AirlineDAO airlineDAO;
     private AircraftDAO aircraftDAO;
     private SeatDAO seatDAO;
     private Flight flight;
     private Airport departure;
     private Airport arrival;
     private Airline airline;
     private Aircraft aircraft;


     @BeforeEach void setUp() {
         flightDAO = new FlightDAO();
         aircraftDAO = new AircraftDAO();
         airportDAO = new AirportDAO();
         airlineDAO = new AirlineDAO();
         seatDAO = new SeatDAO();
         departure = new Airport(null, "JFK", "New York", "Stati Uniti", "John F. Kennedy International Airport");
         arrival = new Airport(null, "NRT", "Tokyo", "Giappone", "Narita International Airport");
         airline = new Airline(null, "Japan Airlines", "JL", "JAL",  "Giappone");
         aircraft = new Aircraft(null, "B787", "Boeing", 242);

         airportDAO.insert(departure);
         airportDAO.insert(arrival);
         airlineDAO.insert(airline);
         aircraftDAO.insert(aircraft);
     }

     @AfterEach void cleanTestData() {
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

     @Test void findById_return_null() {
         Optional<Flight> testFlight = flightDAO.findById(9999L);
         assertTrue(testFlight.isEmpty());
     }

     @Test void findById_return_correct_mapped_object() {
         flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                 Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

         flightDAO.insert(flight);
         Flight flightTest = flightDAO.findById(flight.getFlightId()).orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));
         assertEquals("AB234", flightTest.getFlightCode());
         assertEquals(departure.getAirportId(), flightTest.getDeparture().getAirportId());
         assertEquals(arrival.getAirportId(), flightTest.getArrival().getAirportId());
         assertEquals(LocalDate.of(2026, 5, 1), flightTest.getDepartureDate());
         assertEquals(LocalDate.of(2026, 5, 1), flightTest.getArrivalDate());
         assertEquals(Time.valueOf("08:00:00"), flightTest.getDepartureTime());
         assertEquals(Time.valueOf("09:20:00"), flightTest.getArrivalTime());
         assertEquals(80, flightTest.getDuration());
         assertEquals(airline.getAirlineId(), flightTest.getAirline().getAirlineId());
         assertEquals(aircraft.getAircraftId(), flightTest.getAircraft().getAircraftId());
     }

     @Test void findByMinPrice_return_correct_flight() {
         flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                 Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);
         flightDAO.insert(flight);

         Seat seat1 = new Seat(null, flight, 1, "A", 80);
         Seat seat2 = new Seat(null, flight, 1, "B", 65);

         seat1.setType("WINDOW");
         seat1.setSeatClass("ECONOMY");
         seat2.setType("AISLE");
         seat2.setSeatClass("ECONOMY");

         seatDAO.insert(seat1);
         seatDAO.insert(seat2);

         Integer minimum = flightDAO.findMinPriceAvailable(flight.getFlightId()).orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));
         assertEquals(65, minimum);
     }

     @Test void findAll_return_a_list_of_flights() {
         flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                 Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);
         flightDAO.insert(flight);

         List<Flight> testFlights = flightDAO.findAll();
         assertTrue(testFlights.stream().anyMatch(f -> f.getFlightId().equals(flight.getFlightId())));
         assertFalse(testFlights.isEmpty());
     }

     @Test void insert_should_create_a_new_table_row() {
         flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                 Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);
         flightDAO.insert(flight);

         assertNotNull(flight.getFlightId());

         Flight savedFlight = flightDAO.findById(flight.getFlightId())
                 .orElseThrow(() -> new AssertionError("Volo non trovato"));

         assertEquals("AB234", savedFlight.getFlightCode());
     }

     @Test void update_should_modify_target_flight() {
         flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                 Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);
         flightDAO.insert(flight);
         flight.setFlightCode("AB333");
         flightDAO.update(flight);
         flight = flightDAO.findById(flight.getFlightId()).orElseThrow(() -> new IllegalArgumentException("Volo non trovato"));
         assertEquals("AB333", flight.getFlightCode());
     }

     @Test void deleteById_should_delete_correct_flight() {
         flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                 Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

         flightDAO.insert(flight);
         flightDAO.deleteById(flight.getFlightId());
         Flight deletedFlight = flightDAO.findById(flight.getFlightId()).orElse(null);

         assertNull(deletedFlight);
     }

     @Test void save_should_insert_when_id_is_null() {
        flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

        flightDAO.save(flight);

        assertNotNull(flight.getFlightId());
    }

     @Test void save_should_update_when_id_is_not_null() {
        flight = new Flight(null, "AB234", departure, arrival, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                Time.valueOf("08:00:00"), Time.valueOf("09:20:00"), 80, airline, aircraft);

        flightDAO.insert(flight);

        flight.setFlightCode("AB333");
        flightDAO.save(flight);

        Flight updatedFlight = flightDAO.findById(flight.getFlightId())
                .orElseThrow(() -> new AssertionError("Volo non trovato"));

        assertEquals("AB333", updatedFlight.getFlightCode());
    }

     @Test void oneWayFlightSearch_should_return_matching_flights() {
        List<Flight> flights = flightDAO.oneWayFlightSearch(3L, 4L, LocalDate.of(2026, 4, 10));

        assertFalse(flights.isEmpty());

        for(Flight flight : flights){
            assertEquals(3L, flight.getDeparture().getAirportId());
            assertEquals(4L, flight.getArrival().getAirportId());
            assertEquals(LocalDate.of(2026, 4, 10), flight.getDepartureDate());
        }
    }

     @Test void twoWayFlightSearch_should_return_matching_flights() {
        List<Flight> flights = flightDAO.twoWayFlightSearch(3L, 4L,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 10));

        assertFalse(flights.isEmpty());

        for (Flight flight : flights) {
            boolean matchesFirstDirection =
                    flight.getDeparture().getAirportId().equals(3L) &&
                            flight.getArrival().getAirportId().equals(4L) &&
                            flight.getDepartureDate().equals(LocalDate.of(2026, 4, 10));

            boolean matchesSecondDirection =
                    flight.getDeparture().getAirportId().equals(4L) &&
                            flight.getArrival().getAirportId().equals(3L) &&
                            flight.getDepartureDate().equals(LocalDate.of(2026, 4, 10));

            assertTrue(matchesFirstDirection || matchesSecondDirection);
        }

    }

     @Test void crudFlow_should_insert_update_and_delete_flight_correctly() {
         flight = new Flight(null, "AB234", departure, arrival,
                 LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1),
                 Time.valueOf("08:00:00"), Time.valueOf("09:20:00"),
                 80, airline, aircraft);

         flightDAO.insert(flight);
         assertNotNull(flight.getFlightId());

         Flight savedFlight = flightDAO.findById(flight.getFlightId())
                 .orElseThrow(() -> new AssertionError("Volo non trovato dopo insert"));
         assertEquals("AB234", savedFlight.getFlightCode());

         savedFlight.setFlightCode("AB333");
         flightDAO.update(savedFlight);

         Flight updatedFlight = flightDAO.findById(flight.getFlightId())
                 .orElseThrow(() -> new AssertionError("Volo non trovato dopo update"));
         assertEquals("AB333", updatedFlight.getFlightCode());

         flightDAO.deleteById(flight.getFlightId());

         Optional<Flight> deletedFlight = flightDAO.findById(flight.getFlightId());
         assertTrue(deletedFlight.isEmpty());

         flight = null;
     }
}
