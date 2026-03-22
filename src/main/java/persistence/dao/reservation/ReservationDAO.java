package persistence.dao.reservation;

import domain.flight.Flight;
import domain.reservation.Reservation;
import domain.user.User;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAO implements CrudDAO<Reservation, Long> {
    @Override
    public Optional<Reservation> findById(Long id) {
        String sql= """
                        SELECT *
                        FROM Reservation
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

    @Override
    public List<Reservation> findAll() {
        String sql = """
                        SELECT *
                        FROM Reservation
                    """;

        List<Reservation> result = new ArrayList<>();

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

    public List<Reservation> findByUser(Long userId) {
        String sql= """
                    SELECT *
                    FROM Reservation
                    WHERE user_id = ?
                    """;
        List<Reservation> result = new ArrayList<>();

        try(Connection conn  = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, userId);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch(SQLException e){
          e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(Reservation entity) {
        if(entity.getReservationId() == null){
            insert(entity);
        }
        else{
            update(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                        DELETE FROM Reservation WHERE id = ?
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
    public void insert(Reservation entity) {
        String  sql = """
                        INSERT INTO Reservation(user_id, flight_id, date, state)
                        VALUES (?, ?, ?, ?)
                     """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, entity.getUser().getUserId());
            ps.setLong(2, entity.getFlight().getFlightId());
            ps.setDate(3, Date.valueOf(entity.getCreatedAt().toLocalDate()));
            ps.setString(4, entity.getState());

            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys();){
                if(keys.next()){
                    entity.setReservationId(keys.getLong(1));
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Reservation entity) {
        String sql= """
                        UPDATE Reservation
                        SET user_id = ?, flight_id = ?, date = ?, state = ?
                        WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, entity.getUser().getUserId());
            ps.setLong(2, entity.getFlight().getFlightId());
            //TODO rivedere classe Reservation e DB per consistenza
            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private Reservation mapRow(ResultSet rs) throws SQLException {//TODO aggiungere service per User e Flight
        Reservation reservation = new Reservation();

        reservation.setReservationId(rs.getLong("id"));

        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        reservation.setUser(user);

        Flight flight = new Flight();
        flight.setFlightId(rs.getLong("flight_id"));
        reservation.setFlight(flight);

        reservation.setDate(rs.getDate("date").toLocalDate());

        reservation.setState(rs.getString("state"));

        return reservation;

    }
}
