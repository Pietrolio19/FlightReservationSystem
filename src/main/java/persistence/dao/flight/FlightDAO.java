package persistence.dao.flight;

import domain.flight.Aircraft;
import domain.flight.Airline;
import domain.flight.Airport;
import domain.flight.Flight;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDAO implements CrudDAO<Flight, Long> {

    @Override
    public Optional<Flight> findById(Long id) {
        String sql =""" 
                        SELECT *
                        From Flight
                        WHERE id = ?
                        """;

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Integer> findMinPriceAvailable(Long id) {
        String sql ="""
                SELECT min(price) as start_price
                FROM seat s LEFT JOIN seatreservation sr ON s.id = sr.seat_id
                WHERE flight_id=? AND sr.seat_id IS NULL
                """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    int value = rs.getInt("start_price");
                    return Optional.of(value);
                }
                if(rs.wasNull()) {
                    return Optional.empty();
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Flight> findAll() {
        String sql = """
                        SELECT *
                        FROM Flight
                    """;
        List<Flight> result = new ArrayList<>();

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
    public void save(Flight entity) {
        if(entity.getFlightId() == null){
            insert(entity);
        }
        else{
            update(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                        DELETE FROM Flight WHERE id = ?
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
    public void insert(Flight entity) {
        String  sql = """
                        INSERT INTO Flight(flight_code, departure, arrival,
                                           departure_date, arrival_date, departure_time,
                                           arrival_time, duration, airline_id, aircraft_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, entity.getFlightCode());
            ps.setLong(2, entity.getDeparture().getAirportId());
            ps.setLong(3, entity.getArrival().getAirportId());
            ps.setDate(4, Date.valueOf(entity.getDepartureDate()));
            ps.setDate(5, Date.valueOf(entity.getArrivalDate()));
            ps.setTime(6, entity.getDepartureTime());
            ps.setTime(7, entity.getArrivalTime());
            ps.setInt(8, entity.getDuration());
            ps.setLong(9, entity.getAirline().getAirlineId());
            ps.setLong(10, entity.getAircraft().getAircraftId());

            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys();){
                if(keys.next()){
                    entity.setFlightId(keys.getLong(1));
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Flight entity) {
        String sql= """
                        UPDATE Flight
                        SET flight_code = ?, departure = ?, arrival = ?,
                            departure_date = ?, arrival_date = ?,
                            departure_time = ?, arrival_time = ?,
                            duration = ?, airline_id = ?, aircraft_id = ?
                        WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, entity.getFlightCode());
            ps.setLong(2, entity.getDeparture().getAirportId());
            ps.setLong(3, entity.getArrival().getAirportId());
            ps.setDate(4, Date.valueOf(entity.getDepartureDate()));
            ps.setDate(5, Date.valueOf(entity.getArrivalDate()));
            ps.setTime(6, entity.getDepartureTime());
            ps.setTime(7, entity.getArrivalTime());
            ps.setInt(8, entity.getDuration());
            ps.setLong(9, entity.getAirline().getAirlineId());
            ps.setLong(10, entity.getAircraft().getAircraftId());
            ps.setLong(11, entity.getFlightId());

            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    //query per ricerca di voli solo andata
    public List<Flight> oneWayFlightSearch(Long departureId, Long arrivalId, LocalDate departureDate) {
        String sql = """
                    SELECT *
                    FROM Flight
                    WHERE departure = ? AND arrival = ? AND departure_date = ?
                """;
        List<Flight> result = new ArrayList<>();

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, departureId);
            ps.setLong(2, arrivalId);
            ps.setDate(3, java.sql.Date.valueOf(departureDate));
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    //query per ricerca di voli andata e ritorno
    public List<Flight> twoWayFlightSearch(Long departureId, Long arrivalId, LocalDate departureDate, LocalDate returnDate) {
        String sql = """
                        SELECT *
                        FROM Flight
                        WHERE (departure = ? AND arrival = ? AND departure_date = ?) OR (departure = ? AND arrival = ? AND departure_date = ?)
                    """;
        List<Flight> result = new ArrayList<>();

        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, departureId);
            ps.setLong(2, arrivalId);
            ps.setDate(3, java.sql.Date.valueOf(departureDate));

            ps.setLong(4, arrivalId);
            ps.setLong(5, departureId);
            ps.setDate(6, java.sql.Date.valueOf(returnDate));
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    private Flight mapRow(ResultSet rs) throws SQLException {
        Flight flight = new Flight();

        flight.setFlightId(rs.getLong("id"));
        flight.setFlightCode(rs.getString("flight_code"));

        Airport departureAirport = new Airport();
        departureAirport.setAirportId(rs.getLong("departure"));
        flight.setDeparture(departureAirport);

        Airport arrivalAirport = new Airport();
        arrivalAirport.setAirportId(rs.getLong("arrival"));
        flight.setArrival(arrivalAirport);

        Date departureDate = rs.getDate("departure_date");
        Date arrivalDate = rs.getDate("arrival_date");

        Time departureTime = rs.getTime("departure_time");
        Time arrivalTime = rs.getTime("arrival_time");

        flight.setDepartureDate(departureDate.toLocalDate());
        flight.setArrivalDate(arrivalDate.toLocalDate());

        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDuration(rs.getInt("duration"));

        Airline airline = new Airline();
        airline.setAirlineId(rs.getLong("airline_id"));
        flight.setAirline(airline);

        Aircraft aircraft = new Aircraft();
        aircraft.setAircraftId(rs.getLong("aircraft_id"));
        flight.setAircraft(aircraft);

        return flight;
    }
}
