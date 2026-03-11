package persistence.dao.flight;

import domain.model.flight.Airport;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirportDAO implements CrudDAO<Airport, Long> {

    @Override
    public Optional<Airport> findById(Long id) {
        String sql =""" 
                        SELECT id, iata, country, city, name
                        From Airport
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

    @Override
    public List<Airport> findAll() {
        String sql ="""
                         SELECT id, iata, country, city, name
                         From Airport
                     """;

        List<Airport> result = new ArrayList<>();

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(Airport airport) {
        if (airport.getAirportId() == null) {
            insert(airport);
        } else {
            update(airport);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Airport WHERE id = ?";

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Airport airport) {
        String sql = """
            INSERT INTO Airport(
                iata, city, country, name
            )
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, airport.getIata());
            ps.setString(2, airport.getCity());
            ps.setString(3, airport.getCountry());
            ps.setString(4, airport.getName());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    airport.setAirportId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Airport airport) {
        String sql = """
                        UPDATE Airport
                        SET iata = ?, city = ?, country = ?, name = ?
                        WHERE id = ?
                    """;

        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, airport.getIata());
            ps.setString(2, airport.getCity());
            ps.setString(3, airport.getCountry());
            ps.setString(4, airport.getName());
            ps.setLong(5, airport.getAirportId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Airport mapRow(ResultSet rs) throws SQLException {
        Airport airport = new Airport();

        airport.setAirportId(rs.getLong("id"));
        airport.setIata(rs.getString("iata"));
        airport.setCity(rs.getString("city"));
        airport.setCountry(rs.getString("country"));
        airport.setName(rs.getString("name"));

        return airport;
    }
}
