package persistence.flight;

import domain.flight.Airline;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.AirlineDAO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AirlineDAOTest {
    private final AirlineDAO airlineDAO = new AirlineDAO();
    private Airline airline;

    @AfterEach
    void cleanTestData() {
        if (airline != null && airline.getAirlineId() != null) {
            airlineDAO.deleteById(airline.getAirlineId());
        }
    }

    @Test
    void findById_return_null() {
        Optional<Airline> testAirline = airlineDAO.findById(9999L);
        assertTrue(testAirline.isEmpty());
    }

    @Test
    void findById_return_correct_mapped_object() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.insert(airline);
        Airline testAirline = airlineDAO.findById(airline.getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnia aerea non trovata"));

        assertEquals("Test Sky", testAirline.getName());
        assertEquals("TS", testAirline.getIata());
        assertEquals("TSK", testAirline.getIcao());
        assertEquals("Testland", testAirline.getCountry());
    }

    @Test
    void findAll_return_a_list_of_airlines() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.insert(airline);

        List<Airline> testAirlines = airlineDAO.findAll();
        assertTrue(testAirlines.stream().anyMatch(a -> a.getAirlineId().equals(airline.getAirlineId())));
        assertFalse(testAirlines.isEmpty());
    }

    @Test
    void insert_should_create_a_new_table_row() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.insert(airline);
        Airline testAirline = airlineDAO.findById(airline.getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnia aerea non trovata"));

        assertEquals("Test Sky", testAirline.getName());
    }

    @Test
    void update_should_modify_target_airline() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.insert(airline);
        airline.setIata("TX");
        airlineDAO.update(airline);

        airline = airlineDAO.findById(airline.getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnia aerea non trovata"));

        assertEquals("TX", airline.getIata());
    }

    @Test
    void deleteById_should_delete_correct_airline() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.insert(airline);
        airlineDAO.deleteById(airline.getAirlineId());

        Optional<Airline> deletedAirline = airlineDAO.findById(airline.getAirlineId());
        assertTrue(deletedAirline.isEmpty());
    }

    @Test
    void save_should_insert_when_id_is_null() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.save(airline);

        assertNotNull(airline.getAirlineId());
    }

    @Test
    void save_should_update_when_id_is_not_null() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.insert(airline);
        airline.setIata("TX");
        airlineDAO.save(airline);

        Airline testAirline = airlineDAO.findById(airline.getAirlineId())
                .orElseThrow(() -> new IllegalArgumentException("Compagnia aerea non trovata"));

        assertEquals("TX", testAirline.getIata());
    }

    @Test
    void crudFlow_should_insert_update_and_delete_airline_correctly() {
        airline = new Airline(null, "Test Sky", "TS", "TSK", "Testland");

        airlineDAO.insert(airline);
        assertNotNull(airline.getAirlineId());

        Airline testAirline = airlineDAO.findById(airline.getAirlineId())
                .orElseThrow(() -> new AssertionError("Compagnia aerea non trovata dopo insert"));
        assertEquals("Test Sky", testAirline.getName());

        testAirline.setIata("TX");
        airlineDAO.update(testAirline);

        Airline updatedAirline = airlineDAO.findById(airline.getAirlineId())
                .orElseThrow(() -> new AssertionError("Compagnia aerea non trovata dopo update"));
        assertEquals("TX", updatedAirline.getIata());

        airlineDAO.deleteById(airline.getAirlineId());

        Optional<Airline> deletedAirline = airlineDAO.findById(airline.getAirlineId());
        assertTrue(deletedAirline.isEmpty());

        airline = null;
    }
}
