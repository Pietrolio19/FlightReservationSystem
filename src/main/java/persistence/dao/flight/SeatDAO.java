package persistence.dao.flight;

import domain.flight.Flight;
import domain.flight.Seat;
import dto.flight.SeatAvailability;
import dto.flight.SeatState;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatDAO implements CrudDAO<Seat, Long> {

    @Override
    public Optional<Seat> findById(Long id) {
        String sql= """
                        SELECT id, flight_id, seat_row, letter,
                               type, classs, price
                        FROM Seat
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

    public List<Seat> findByFlightId(Long id) {
        String sql= """
                    SELECT *
                    FROM Seat
                    WHERE flight_id = ?
                    """;
        List<Seat> result = new ArrayList<>();
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    result.add(mapRow(rs));
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<SeatState> getSeatStateByFlightId(Long id) {
        String sql ="""
                    SELECT s.id, s.flight_id, s.seat_row, s.letter, s.type, s.class, s.price, sr.state AS reservation_state
                    FROM Seat s 
                        join SeatReservation sr on s.id=sr.seat_id
                    WHERE s.flight_id = ?
                    """;
        List<SeatState> result = new ArrayList<>();
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()) {
                    SeatState seatState = new SeatState();
                    seatState.setSeat(mapRow(rs));
                    seatState.setState(rs.getString("reservation_state"));
                    result.add(seatState);
                }
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public SeatAvailability getSeatAvailabilityByFlightId(Long id) {
        String sql= """                
                SELECT
                    COUNT(s.id) AS total_seats,
                    COUNT(sr.seat_id) AS confirmed_seats,
                    COUNT(s.id) - COUNT(sr.seat_id) AS available_seats
                FROM Seat s
                LEFT JOIN SeatReservation sr
                    ON sr.seat_id = s.id
                   AND sr.state = 'CONFIRMED'
                WHERE s.flight_id = ?;
                """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()){
                rs.next();

                return new SeatAvailability(
                        rs.getInt("total_seats"),
                        rs.getInt("confirmed_seats"),
                        rs.getInt("available_seats")
                );
            }
        } catch(SQLException e){
            throw new RuntimeException("Errore nel recupero disponibilità posti", e);
        }
    }

    @Override
    public List<Seat> findAll() {
        String sql = """
                        SELECT *
                        FROM Seat
                    """;
        List<Seat> result = new ArrayList<>();

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
    public void save(Seat entity) {
        if(entity.getSeatId() == null){
            insert(entity);
        }
        else{
            update(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                        DELETE FROM Seat WHERE id = ?
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
    public void insert(Seat entity) {
        String  sql = """
                        INSERT INTO Seat(flight_id, seat_row, letter, type, classs, price)
                        VALUES (?, ?, ?, ?, ?, ?)
                     """;

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, entity.getFlight().getFlightId());
            ps.setInt(2, entity.getRow());
            ps.setString(3, entity.getLetter());
            ps.setString(4, entity.getType());
            ps.setString(5, entity.getSeatClass());
            ps.setInt(6, entity.getPrice());

            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys();){
                if(keys.next()){
                    entity.setSeatId(keys.getLong(1));
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Seat entity) {
        String sql= """
                        UPDATE Seat
                        SET flight_id = ?, seat_row = ?, letter = ?, type = ?,
                            classs = ?, price = ?
                        WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, entity.getFlight().getFlightId());
            ps.setInt(2, entity.getRow());
            ps.setString(3, entity.getLetter());
            ps.setString(4, entity.getType());
            ps.setString(5, entity.getSeatClass());
            ps.setInt(6, entity.getPrice());
            ps.setLong(7, entity.getSeatId());

            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    private Seat mapRow(ResultSet rs) throws SQLException {
        Seat seat = new Seat();

        seat.setSeatId(rs.getLong("id"));

        Flight flight = new Flight();
        flight.setFlightId(rs.getLong("flight_id"));
        seat.setFlight(flight);

        seat.setRow(rs.getInt("seat_row"));
        seat.setLetter(rs.getString("letter"));
        seat.setType(rs.getString("type").toUpperCase());
        seat.setSeatClass(rs.getString("class").toUpperCase());
        seat.setPrice(rs.getInt("price"));

        return seat;
    }
}
