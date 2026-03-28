package persistence.flight;

import domain.flight.Aircraft;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import persistence.dao.flight.AircraftDAO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftDAOTest {
    private final AircraftDAO aircraftDAO = new AircraftDAO();
    private Aircraft aircraft;

    @AfterEach
    void cleanTestData() {
        if (aircraft != null && aircraft.getAircraftId() != null) {
            aircraftDAO.deleteById(aircraft.getAircraftId());
        }
    }

    @Test
    void findById_return_null() {
        Optional<Aircraft> testAircraft = aircraftDAO.findById(9999L);
        assertTrue(testAircraft.isEmpty());
    }

    @Test
    void findById_return_correct_mapped_object() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.insert(aircraft);
        Aircraft testAircraft = aircraftDAO.findById(aircraft.getAircraftId())
                .orElseThrow(() -> new IllegalArgumentException("Aereo non trovato"));

        assertEquals("B787-TEST", testAircraft.getModel());
        assertEquals("Boeing", testAircraft.getProducer());
        assertEquals(242, testAircraft.getCapacity());
    }

    @Test
    void findAll_return_a_list_of_aircrafts() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.insert(aircraft);

        List<Aircraft> testAircrafts = aircraftDAO.findAll();
        assertTrue(testAircrafts.stream().anyMatch(a -> a.getAircraftId().equals(aircraft.getAircraftId())));
        assertFalse(testAircrafts.isEmpty());
    }

    @Test
    void insert_should_create_a_new_table_row() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.insert(aircraft);
        Aircraft testAircraft = aircraftDAO.findById(aircraft.getAircraftId())
                .orElseThrow(() -> new IllegalArgumentException("Aereo non trovato"));

        assertEquals("B787-TEST", testAircraft.getModel());
    }

    @Test
    void update_should_modify_target_aircraft() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.insert(aircraft);
        aircraft.setModel("A350-TEST");
        aircraftDAO.update(aircraft);

        aircraft = aircraftDAO.findById(aircraft.getAircraftId())
                .orElseThrow(() -> new IllegalArgumentException("Aereo non trovato"));

        assertEquals("A350-TEST", aircraft.getModel());
    }

    @Test
    void deleteById_should_delete_correct_aircraft() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.insert(aircraft);
        aircraftDAO.deleteById(aircraft.getAircraftId());

        Optional<Aircraft> deletedAircraft = aircraftDAO.findById(aircraft.getAircraftId());
        assertTrue(deletedAircraft.isEmpty());
    }

    @Test
    void save_should_insert_when_id_is_null() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.save(aircraft);

        assertNotNull(aircraft.getAircraftId());
    }

    @Test
    void save_should_update_when_id_is_not_null() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.insert(aircraft);
        aircraft.setModel("A350-TEST");
        aircraftDAO.save(aircraft);

        Aircraft testAircraft = aircraftDAO.findById(aircraft.getAircraftId())
                .orElseThrow(() -> new IllegalArgumentException("Aereo non trovato"));

        assertEquals("A350-TEST", testAircraft.getModel());
    }

    @Test
    void crudFlow_should_insert_update_and_delete_aircraft_correctly() {
        aircraft = new Aircraft(null, "B787-TEST", "Boeing", 242);

        aircraftDAO.insert(aircraft);
        assertNotNull(aircraft.getAircraftId());

        Aircraft testAircraft = aircraftDAO.findById(aircraft.getAircraftId())
                .orElseThrow(() -> new AssertionError("Aereo non trovato dopo insert"));
        assertEquals("B787-TEST", testAircraft.getModel());

        testAircraft.setModel("A350-TEST");
        aircraftDAO.update(testAircraft);

        Aircraft updatedAircraft = aircraftDAO.findById(aircraft.getAircraftId())
                .orElseThrow(() -> new AssertionError("Aereo non trovato dopo update"));
        assertEquals("A350-TEST", updatedAircraft.getModel());

        aircraftDAO.deleteById(aircraft.getAircraftId());

        Optional<Aircraft> deletedAircraft = aircraftDAO.findById(aircraft.getAircraftId());
        assertTrue(deletedAircraft.isEmpty());

        aircraft = null;
    }
}