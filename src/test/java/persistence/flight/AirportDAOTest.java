package persistence.flight;

import domain.flight.Airport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.AirportDAO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AirportDAOTest {
    private AirportDAO airportDAO = new AirportDAO();
    private Airport airport;

    @AfterEach void cleanTestData() {
        if (airport != null && airport.getAirportId() != null) {
            airportDAO.deleteById(airport.getAirportId());
        }
    }

    @Test void findById_return_null() {
        Optional<Airport> testFlight = airportDAO.findById(9999L);
        assertTrue(testFlight.isEmpty());
    }

    @Test void findById_return_correct_mapped_object() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.insert(airport);
        Airport testAirport = airportDAO.findById(airport.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto non trovato"));

        assertEquals("JFK", testAirport.getIata());
        assertEquals("New York", testAirport.getCity());
        assertEquals("Stati Uniti", testAirport.getCountry());
        assertEquals("John F. Kennedy International Airport", testAirport.getName());
    }

    @Test void findAll_return_a_list_of_airports() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.insert(airport);

        List<Airport> testAirports = airportDAO.findAll();
        assertTrue(testAirports.stream().anyMatch(a -> a.getAirportId().equals(airport.getAirportId())));
        assertFalse(testAirports.isEmpty());
    }

    @Test void insert_should_create_a_new_table_row() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.insert(airport);
        Airport testAirport = airportDAO.findById(airport.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto non trovato"));

        assertEquals("JFK", testAirport.getIata());
    }

    @Test void update_should_modify_target_airport() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.insert(airport);
        airport.setIata("ABC");
        airportDAO.update(airport);

        airportDAO.findById(airport.getAirportId());
        airport = airportDAO.findById(airport.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto non trovato"));
        assertEquals("ABC", airport.getIata());
    }

    @Test void deleteById_should_delete_correct_airport() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.insert(airport);
        airportDAO.deleteById(airport.getAirportId());
        Airport deletedAirport = airportDAO.findById(airport.getAirportId()).orElse(null);

        assertNull(deletedAirport);
    }

    @Test void save_should_insert_when_id_is_null() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.save(airport);

        assertNotNull(airport.getAirportId());
    }

    @Test void save_should_update_when_id_is_not_null() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.insert(airport);
        airport.setIata("ABC");
        airportDAO.save(airport);

        Airport testAirport = airportDAO.findById(airport.getAirportId())
                .orElseThrow(() -> new IllegalArgumentException("Aeroporto non trovato"));

        assertEquals("ABC", testAirport.getIata());
    }

    @Test void crudFlow_should_insert_update_and_delete_flight_correctly() {
        airport = new Airport(null, "JFK", "New York",
                "Stati Uniti", "John F. Kennedy International Airport");

        airportDAO.insert(airport);
        assertNotNull(airport.getAirportId());

        Airport testAirport = airportDAO.findById(airport.getAirportId())
                .orElseThrow(() -> new AssertionError("Aeroporto non trovato dopo insert"));
        assertEquals("JFK", testAirport.getIata());

        testAirport.setIata("ABC");
        airportDAO.update(testAirport);

        Airport updatedAirport = airportDAO.findById(airport.getAirportId())
                .orElseThrow(() -> new AssertionError("Aeroporto non trovato dopo update"));
        assertEquals("ABC", updatedAirport.getIata());

        airportDAO.deleteById(airport.getAirportId());

        Optional<Airport> deletedAirport = airportDAO.findById(airport.getAirportId());
        assertTrue(deletedAirport.isEmpty());

        airport = null;
    }
}
