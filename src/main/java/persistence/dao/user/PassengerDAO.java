package persistence.dao.user;

import domain.user.Passenger;
import domain.user.User;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PassengerDAO implements CrudDAO<Passenger, Long> {

    @Override
    public Optional<Passenger> findById(Long id) {
        String sql= """
                        SELECT *
                        FROM Passenger
                        WHERE id = ?
                    """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(mapRow(rs));
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Passenger> findByCodFiscOrCodId(String codFisc, String codId) {
        String sql= """
                    SELECT *
                    FROM Passenger
                    WHERE cod_fisc = ? AND cod_id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, codFisc);
            ps.setString(2, codId);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(mapRow(rs));
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Passenger> findByCompanionOwner(Long id) {
        String sql= """
                    SELECT *
                    FROM Passenger p
                    WHERE companion_owner = ?
                    """;
        List<Passenger> result = new ArrayList<>();
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    result.add(mapRow(rs));
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Passenger> findAll() {
        String sql = """
                        SELECT *
                        FROM Passenger
                    """;

        List<Passenger> result = new ArrayList<>();

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
    public void save(Passenger entity) {
        if(entity.getPassengerId() == null){
            insert(entity);
        }
        else{
            update(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                        DELETE FROM Passenger WHERE id = ?
                    """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Passenger entity) {
        String sql = """
            INSERT INTO Passenger(name, surname, date_of_birth, address,
                                  city, province, country, cod_fisc, cod_id,
                                  phone_number, companion_owner)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getSurname());
            ps.setDate(3, Date.valueOf(entity.getDateOfBirth()));
            ps.setString(4, entity.getAddress());
            ps.setString(5, entity.getCity());
            ps.setString(6, entity.getProvince());
            ps.setString(7, entity.getCountry());
            ps.setString(8, entity.getCodFisc());
            ps.setString(9, entity.getCodId());
            ps.setString(10, entity.getPhoneNumber());

            if (entity.getCompanionOwner() != null && entity.getCompanionOwner().getUserId() != null) {
                ps.setLong(11, entity.getCompanionOwner().getUserId());
            } else {
                ps.setNull(11, Types.BIGINT);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    entity.setPassengerId(rs.getLong("id"));
                } else {
                    throw new SQLException("Insert Passenger riuscita ma nessun id restituito.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore nel salvataggio Passenger", e);
        }
    }

    @Override
    public void update(Passenger entity) {
        String sql= """
                        UPDATE Passenger
                        SET name = ?, surname = ?, date_of_birth = ?, address = ?,
                            city = ?, province = ?, country = ?,
                            cod_fisc = ?, cod_id = ?, phone_number = ?,
                            companion_owner = ?
                        WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getSurname());
            ps.setDate(3, Date.valueOf(entity.getDateOfBirth()));
            ps.setString(4, entity.getAddress());
            ps.setString(5, entity.getCity());
            ps.setString(6, entity.getProvince());
            ps.setString(7, entity.getCountry());
            ps.setString(8, entity.getCodFisc());
            ps.setString(9, entity.getCodId());
            ps.setString(10, entity.getPhoneNumber());
            if (entity.getCompanionOwner() != null && entity.getCompanionOwner().getUserId() != null) {
                ps.setLong(11, entity.getCompanionOwner().getUserId());
            } else {
                ps.setNull(11, Types.BIGINT);
            }
            ps.setLong(12, entity.getPassengerId());

            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private Passenger mapRow(ResultSet rs) throws SQLException {
        Passenger passenger = new Passenger();

        passenger.setPassengerId(rs.getLong("id"));
        passenger.setName(rs.getString("name"));
        passenger.setSurname(rs.getString("surname"));
        Date dateOfBirth = rs.getDate("date_of_birth");
        passenger.setDateOfBirth(dateOfBirth.toLocalDate());
        passenger.setAddress(rs.getString("address"));
        passenger.setCity(rs.getString("city"));
        passenger.setProvince(rs.getString("province"));
        passenger.setCountry(rs.getString("country"));
        passenger.setCodFisc(rs.getString("cod_fisc"));
        passenger.setCodId(rs.getString("cod_id"));
        passenger.setPhoneNumber(rs.getString("phone_number"));

        Long companionOwnerId = rs.getObject("companion_owner", Long.class);

        if (companionOwnerId != null) {
            User user = new User();
            user.setUserId(companionOwnerId);
            passenger.setCompanionOwner(user);
        } else {
            passenger.setCompanionOwner(null);
        }

        return passenger;
    }
}
