package persistence.dao.flight;

import domain.flight.Airline;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirlineDAO implements CrudDAO<Airline, Long> {

    @Override
    public Optional<Airline> findById(Long id) {
        String sql = """
                        SELECT id, iata, icao, name, country
                        FROM Airline
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
    public List<Airline> findAll() {
        String sql = """
                        SELECT *
                        FROM Airline
                    """;
        List<Airline> result = new ArrayList<>();
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
    public void save(Airline entity) {
        if(entity.getAirlineId() == null){
            insert(entity);
        }
        else{
            update(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                        DELETE FROM Airline WHERE id = ?
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
    public void insert(Airline entity) {
        String  sql = """
                        INSERT INTO Airline(iata, icao, name, country)
                        VALUES (?, ?, ?, ?)
                     """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, entity.getIata());
            ps.setString(2, entity.getIcao());
            ps.setString(3, entity.getName());
            ps.setString(4, entity.getCountry());

            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys();){
                if(keys.next()){
                    entity.setAirlineId(keys.getLong(1));
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Airline entity) {
        String sql= """
                        UPDATE Airline SET
                        iata = ?, icao = ?, name = ?, country = ?
                        WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, entity.getIata());
            ps.setString(2, entity.getIcao());
            ps.setString(3, entity.getName());
            ps.setString(4, entity.getCountry());
            ps.setLong(5, entity.getAirlineId());

            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private Airline mapRow(ResultSet rs) throws SQLException {
        Airline airline = new Airline();

        airline.setAirlineId(rs.getLong("id"));
        airline.setIata(rs.getString("iata"));
        airline.setIcao(rs.getString("icao"));
        airline.setName(rs.getString("name"));
        airline.setCountry(rs.getString("country"));

        return airline;
    }
}
