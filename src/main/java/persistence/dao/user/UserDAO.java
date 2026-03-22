package persistence.dao.user;

import domain.user.Passenger;
import domain.user.User;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements CrudDAO<User, Long> {

    @Override
    public Optional<User> findById(Long id) {
        String sql = """
                        SELECT *
                        FROM Utente
                        WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<User> findByEmail(String email){
        String sql ="""
                    SELECT *
                    FROM Utente
                    WHERE email=?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        String sql = """
                    SELECT *
                    FROM Utente
                    WHERE username=?
                    """;
        try {
            Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            {
                ps.setString(1, username);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = """
                        SELECT *
                        FROM Utente
                    """;
        List<User> result = new ArrayList<>();
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                result.add(mapRow(rs));
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(User user) {
        if(user.getUserId() == null){
            insert(user);
        }
        else{
            update(user);
        }
    }

    @Override
    public void deleteById(Long id){
        String sql = """
                        DELETE FROM Utente WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1,id);
            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void insert(User user) {
        String sql = """
            INSERT INTO Utente(
                username, email, hashed_password,
                fidelity_points, fidelity_status,
                self_passenger_id
            )
            VALUES(?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashPassword());
            ps.setLong(4, user.getFidelityPoints());
            ps.setString(5, user.getFidelityStatus());
            if (user.getSelfPassenger() != null && user.getSelfPassenger().getPassengerId() != null) {
                ps.setLong(6, user.getSelfPassenger().getPassengerId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {
        String sql = """
                        UPDATE Utente
                        SET username = ?, email = ?, hashed_password = ?,
                        fidelity_points = ?, fidelity_status = ?,
                        self_passenger_id = ?
                        WHERE id = ?
                    """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashPassword());
            ps.setLong(4, user.getFidelityPoints());
            ps.setString(5, user.getFidelityStatus());
            if (user.getSelfPassenger() != null && user.getSelfPassenger().getPassengerId() != null) { //null-safe
                ps.setLong(6, user.getSelfPassenger().getPassengerId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            ps.setLong(7, user.getUserId());

            ps.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException { //TODO aggiungere service per passenger
        User user = new User();

        user.setUserId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setHashPassword(rs.getString("hashed_password"));
        user.setFidelityPoints(rs.getInt("fidelity_points"));
        user.setFidelityStatus(rs.getString("fidelity_status"));

        Passenger passenger = new Passenger();
        passenger.setPassengerId(rs.getLong("self_passenger_id"));
        user.setSelfPassenger(passenger);
        return user;
    }
}
