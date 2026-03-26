package persistence.dao.reservation;

import domain.flight.Seat;
import domain.reservation.Reservation;
import domain.reservation.SeatReservation;
import domain.user.Passenger;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatReservationDAO implements CrudDAO<SeatReservation, Long> {
    @Override
    public Optional<SeatReservation> findById(Long id) {
        String sql= """
                        SELECT passenger_id, reservation_id, seat_id, date, state
                        FROM SeatReservation
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
    public List<SeatReservation> findAll() {
        String sql = """
                        SELECT *
                        FROM SeatReservation
                    """;

        List<SeatReservation> result = new ArrayList<>();

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
    public void save(SeatReservation entity) {
        if(entity.getSeatReservationId() == null){
            insert(entity);
        }
        else{
            update(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                        DELETE FROM SeatReservation WHERE id = ?
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
    public void insert(SeatReservation entity) {
        String  sql = """
                        INSERT INTO SeatReservation(passenger_id, reservation_id, seat_id, date, state)
                        VALUES (?, ?, ?, ?, ?)
                     """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, entity.getPassenger().getPassengerId());
            ps.setLong(2, entity.getReservation().getReservationId());
            ps.setLong(3, entity.getSeat().getSeatId());
            ps.setDate(4, Date.valueOf(entity.getDate()));
            ps.setString(5, entity.getState());

            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys();){
                if(keys.next()){
                    entity.setSeatReservationId(keys.getLong(1));
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(SeatReservation entity) {
        String sql= """
                        UPDATE SeatReservation
                        SET passenger_id = ?, reservation = ?, seat_id = ?, date = ?, state = ?
                        WHERE id = ?
                    """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, entity.getPassenger().getPassengerId());
            ps.setLong(2, entity.getReservation().getReservationId());
            ps.setLong(3, entity.getSeat().getSeatId());
            ps.setDate(4, Date.valueOf(entity.getDate()));
            ps.setString(5, entity.getState());
            ps.setLong(6, entity.getSeatReservationId());
            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private SeatReservation mapRow(ResultSet rs) throws SQLException {//TODO aggiungere service per passenger reservation e seat
        SeatReservation seatReservation = new SeatReservation();

        seatReservation.setSeatReservationId(rs.getLong("id"));
        Passenger passenger = new Passenger();
        passenger.setPassengerId(rs.getLong("passenger_id"));
        seatReservation.setPassenger(passenger);

        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getLong("reservation_id"));
        seatReservation.setReservation(reservation);

        Seat seat = new Seat();
        seat.setSeatId(rs.getLong("seat_id"));
        seatReservation.setSeat(seat);

        Date date = rs.getDate("date");
        seatReservation.setDate(date.toLocalDate());
        seatReservation.setState(rs.getString("state"));

        return seatReservation;
    }
}
