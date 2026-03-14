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
                        SELECT id, username, email,
                            hashed_password, name, surname,
                            date_of_birth, address, city, province,
                            country,  cod_fisc, codId, phoneNumber,
                            fidelity_points, fidelity_status,
                            self_passenger_id
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
                username, email,
                hashed_password, name, surname,
                date_of_birth, address, city, province,
                country, cod_fisc, codId, phoneNumber,
                fidelity_points, fidelity_status,
                self_passenger_id
            )
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashPassword());
            ps.setString(4, user.getName());
            ps.setString(5, user.getSurname());
            if (user.getDateOfBirth() != null) { //null-safe
                ps.setDate(6, Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getCity());
            ps.setString(9, user.getProvince());
            ps.setString(10, user.getCountry());
            ps.setString(11, user.getCodFisc());
            ps.setString(12, user.getCodId());
            ps.setString(13, user.getPhoneNumber());
            ps.setLong(14, user.getFidelityPoints());
            ps.setString(15, user.getFidelityStatus());
            if (user.getSelfPassenger() != null && user.getSelfPassenger().getPassengerId() != null) {
                ps.setLong(16, user.getSelfPassenger().getPassengerId());
            } else {
                ps.setNull(16, Types.BIGINT);
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

    public void insertBasicUser(User user) {
        String sql= """
                    INSERT INTO Utente(
                    username, email, hashed_password, name, surname
                    )
                    VALUES(?,?,?,?,?)
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashPassword());
            ps.setString(4, user.getName());
            ps.setString(5, user.getSurname());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getLong(1));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {
        String sql = """
                        UPDATE Utente
                        SET username = ?, email = ?, hashed_password = ?,
                        name = ?, surname = ?, date_of_birth = ?, address = ?,
                        city = ?, province = ?, country = ?,
                        cod_fisc = ?, codId = ?, phoneNumber = ?,
                        fidelity_points = ?, fidelity_status = ?,
                        self_passenger_id = ?
                        WHERE id = ?
                    """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashPassword());
            ps.setString(4, user.getName());
            ps.setString(5, user.getSurname());
            if (user.getDateOfBirth() != null) { //null-safe
                ps.setDate(6, Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getCity());
            ps.setString(9, user.getProvince());
            ps.setString(10, user.getCountry());
            ps.setString(11, user.getCodFisc());
            ps.setString(12, user.getCodId());
            ps.setString(13, user.getPhoneNumber());
            ps.setLong(14, user.getFidelityPoints());
            ps.setString(15, user.getFidelityStatus());
            if (user.getSelfPassenger() != null && user.getSelfPassenger().getPassengerId() != null) { //null-safe
                ps.setLong(16, user.getSelfPassenger().getPassengerId());
            } else {
                ps.setNull(16, Types.BIGINT);
            }
            ps.setLong(17, user.getUserId());

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
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        Date dateOFBirth = rs.getDate("date_of_birth");
        if(dateOFBirth != null) {user.setDateOfBirth(dateOFBirth.toLocalDate());}
        user.setAddress(rs.getString("address"));
        user.setCity(rs.getString("city"));
        user.setProvince(rs.getString("province"));
        user.setCountry(rs.getString("country"));
        user.setCodFisc(rs.getString("cod_fisc"));
        user.setCodId(rs.getString("codId"));
        user.setPhoneNumber(rs.getString("phoneNumber"));
        user.setFidelityPoints(rs.getInt("fidelity_points"));
        user.setFidelityStatus(rs.getString("fidelity_status"));

        Passenger passenger = new Passenger();
        passenger.setPassengerId(rs.getLong("passenger_id"));
        user.setSelfPassenger(passenger);
        return user;
    }
}
