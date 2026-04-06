package persistence.user;

import domain.user.Passenger;
import domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import persistence.dao.user.PassengerDAO;
import persistence.dao.user.UserDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private final UserDAO userDAO = new UserDAO();
    private final PassengerDAO passengerDAO = new PassengerDAO();
    private User user;
    private Passenger passenger;

    @AfterEach
    void cleanTestData() {
        if (user != null && user.getUserId() != null) {
            userDAO.deleteById(user.getUserId());
        }
        if (passenger != null && passenger.getPassengerId() != null){
            passengerDAO.deleteById(passenger.getPassengerId());
        }
    }

    @Test
    void findById_return_null() {
        Optional<User> testUser = userDAO.findById(9999L);
        assertTrue(testUser.isEmpty());
    }

    @Test
    void findById_return_correct_mapped_object() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        User testUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        assertEquals("TestUser", testUser.getUsername());
        assertEquals("testuser@gmail.com", testUser.getEmail());
        assertEquals("testuserpw", testUser.getHashPassword());
    }

    @Test
    void findByEmail_return_correct_user() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        User testUser = userDAO.findByEmail(user.getEmail()).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        assertEquals(user.getEmail(), testUser.getEmail());
    }

    @Test
    void findByEmail_return_empty_with_not_existing_email() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        Optional<User> testUser = userDAO.findByEmail("testuserwrongemail@gmail.com");
        assertTrue(testUser.isEmpty());
    }

    @Test void findByUsername_return_correct_user() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        User testUser = userDAO.findByUsername(user.getUsername()).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        assertEquals(user.getUsername(), testUser.getUsername());
    }

    @Test
    void findByEmail_return_empty_with_not_existing_username() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        Optional<User> testUser = userDAO.findByUsername("TestUserWrongUsername");
        assertTrue(testUser.isEmpty());
    }

    @Test
    void findAll_return_a_list_of_users() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        List<User> testUsers = userDAO.findAll();

        assertFalse(testUsers.isEmpty());
        assertTrue(testUsers.stream().anyMatch(u -> u.getUserId().equals(user.getUserId())));
    }

    @Test
    void insert_should_create_a_new_table_row() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        User testUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        assertEquals("TestUser", testUser.getUsername());
    }

    @Test
    void update_should_modify_target_user() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        user.setUsername("UpdatedUser");
        userDAO.update(user);

        User updatedUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        assertEquals("UpdatedUser", updatedUser.getUsername());
    }

    @Test
    void deleteById_should_delete_correct_user() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        userDAO.deleteById(user.getUserId());

        Optional<User> deletedUser = userDAO.findById(user.getUserId());
        assertTrue(deletedUser.isEmpty());

        user = null;
    }

    @Test
    void save_should_insert_when_id_is_null() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.save(user);

        assertNotNull(user.getUserId());
    }

    @Test
    void save_should_update_when_id_is_not_null() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        userDAO.insert(user);

        user.setUsername("UpdatedUser");
        userDAO.save(user);

        User testUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        assertEquals("UpdatedUser", testUser.getUsername());
    }

    @Test
    void crudFlow_should_insert_update_and_delete_user_correctly() {
        user = new User(null, "TestUser", "testuser@gmail.com", "testuserpw");
        passenger = new Passenger(
                "Mario", "Rossi", LocalDate.of(1995, 1, 10),
                "Via Roma 1", "Milano", "MI", "Italia",
                "RSSMRA95A10F205Z", "ID123456", "3331234567",
                null, user
        );
        passengerDAO.insert(passenger);
        user.setSelfPassenger(passenger);
        userDAO.insert(user);
        assertNotNull(user.getUserId());

        User testUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new AssertionError("Utente non trovato dopo insert"));
        assertEquals("TestUser", testUser.getUsername());

        testUser.setUsername("UpdatedUser");
        userDAO.update(testUser);

        User updatedUser = userDAO.findById(user.getUserId())
                .orElseThrow(() -> new AssertionError("Utente non trovato dopo update"));
        assertEquals("UpdatedUser", updatedUser.getUsername());

        userDAO.deleteById(user.getUserId());

        Optional<User> deletedUser = userDAO.findById(user.getUserId());
        assertTrue(deletedUser.isEmpty());

        user = null;
    }
}