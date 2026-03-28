package persistence.user;

import domain.user.Passenger;
import domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerDAOTest {
    private PassengerDAO passengerDAO;
    private UserDAO userDAO;

    private User user;
    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passengerDAO = new PassengerDAO();
        userDAO = new UserDAO();

        user = createTestUser();
        userDAO.insert(user);
    }

    @AfterEach
    void cleanTestData() {
        if (passenger != null && passenger.getPassengerId() != null) {
            passengerDAO.deleteById(passenger.getPassengerId());
        }
        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
    }

    @Test
    void findById_return_null() {
        Optional<Passenger> testPassenger = passengerDAO.findById(9999L);
        assertTrue(testPassenger.isEmpty());
    }

    @Test
    void findById_return_correct_mapped_object() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        Passenger testPassenger = passengerDAO.findById(passenger.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));

        assertEquals("Mario", testPassenger.getName());
        assertEquals("Rossi", testPassenger.getSurname());
        assertEquals(LocalDate.of(1995, 1, 10), testPassenger.getDateOfBirth());
        assertEquals("Via Roma 1", testPassenger.getAddress());
        assertEquals("Milano", testPassenger.getCity());
        assertEquals("MI", testPassenger.getProvince());
        assertEquals("Italia", testPassenger.getCountry());
        assertEquals("RSSMRA95A10F205Z", testPassenger.getCodFisc());
        assertEquals("ID123456", testPassenger.getCodId());
        assertEquals("3331234567", testPassenger.getPhoneNumber());
        assertEquals(user.getUserId(), testPassenger.getCompanionOwner().getUserId());
    }

    @Test
    void findByCodFiscOrCodId_return_correct_passenger() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        Passenger testPassenger = passengerDAO.findByCodFiscOrCodId(passenger.getCodFisc(), passenger.getCodId())
                .orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));

        assertEquals(passenger.getCodFisc(), testPassenger.getCodFisc());
        assertEquals(passenger.getCodId(), testPassenger.getCodId());
    }

    @Test
    void findByCodFiscOrCodId_return_empty_with_not_existing_codFisc_and_codId() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        Optional<Passenger> testPassengerWrongCodFisc = passengerDAO.findByCodFiscOrCodId("RSSMRA95A10F205C", passenger.getCodId());
        Optional<Passenger> testPassengerWrongCodId = passengerDAO.findByCodFiscOrCodId(passenger.getCodFisc(), "ID1234567");

        assertTrue(testPassengerWrongCodFisc.isEmpty());
        assertTrue(testPassengerWrongCodId.isEmpty());
    }

    @Test
    void findByCompanionOwner_return_correct_passengers() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        List<Passenger> testPassengers = passengerDAO.findByCompanionOwner(user.getUserId());

        assertFalse(testPassengers.isEmpty());
        assertEquals(1, testPassengers.size());
        for(Passenger p : testPassengers) {
            assertEquals(passenger.getCodFisc(), p.getCodFisc());
            assertEquals(passenger.getCodId(), p.getCodId());
            assertEquals(user.getUserId(), p.getCompanionOwner().getUserId());
        }
    }

    @Test
    void findByCompanionOwner_return_empty_with_wrong_parameters() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        List<Passenger> testPassengers = passengerDAO.findByCompanionOwner(9999L);

        assertTrue(testPassengers.isEmpty());
    }

    @Test
    void findAll_return_a_list_of_passengers() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        List<Passenger> testPassengers = passengerDAO.findAll();
        assertTrue(testPassengers.stream().anyMatch(p -> p.getPassengerId().equals(passenger.getPassengerId())));
        assertFalse(testPassengers.isEmpty());
    }

    @Test
    void insert_should_create_a_new_table_row() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        Passenger testPassenger = passengerDAO.findById(passenger.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));

        assertEquals("Mario", testPassenger.getName());
    }

    @Test
    void update_should_modify_target_passenger() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        passenger.setPhoneNumber("3339999999");
        passengerDAO.update(passenger);

        Passenger updatedPassenger = passengerDAO.findById(passenger.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));

        assertEquals("3339999999", updatedPassenger.getPhoneNumber());
    }

    @Test
    void deleteById_should_delete_correct_passenger() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);
        passengerDAO.deleteById(passenger.getPassengerId());

        Optional<Passenger> deletedPassenger = passengerDAO.findById(passenger.getPassengerId());
        assertTrue(deletedPassenger.isEmpty());
    }

    @Test
    void save_should_insert_when_id_is_null() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.save(passenger);

        assertNotNull(passenger.getPassengerId());
    }

    @Test
    void save_should_update_when_id_is_not_null() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);

        passenger.setPhoneNumber("3339999999");
        passengerDAO.save(passenger);

        Passenger testPassenger = passengerDAO.findById(passenger.getPassengerId())
                .orElseThrow(() -> new IllegalArgumentException("Passeggero non trovato"));

        assertEquals("3339999999", testPassenger.getPhoneNumber());
    }

    @Test
    void crudFlow_should_insert_update_and_delete_passenger_correctly() {
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );

        passengerDAO.insert(passenger);
        assertNotNull(passenger.getPassengerId());

        Passenger testPassenger = passengerDAO.findById(passenger.getPassengerId())
                .orElseThrow(() -> new AssertionError("Passeggero non trovato dopo insert"));
        assertEquals("Mario", testPassenger.getName());

        testPassenger.setPhoneNumber("3339999999");
        passengerDAO.update(testPassenger);

        Passenger updatedPassenger = passengerDAO.findById(passenger.getPassengerId())
                .orElseThrow(() -> new AssertionError("Passeggero non trovato dopo update"));
        assertEquals("3339999999", updatedPassenger.getPhoneNumber());

        passengerDAO.deleteById(passenger.getPassengerId());

        Optional<Passenger> deletedPassenger = passengerDAO.findById(passenger.getPassengerId());
        assertTrue(deletedPassenger.isEmpty());

        passenger = null;
    }

    private User createTestUser() {
        User user = new User();

        user.setUsername("user_test_passenger");
        user.setEmail("user_test_passenger@example.com");
        user.setHashPassword("hashed_password_test");

        return user;
    }
}