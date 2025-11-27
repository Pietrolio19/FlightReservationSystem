package persistence.dao;

import domain.flight.Airport;
import persistence.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AirportDAO implements CrudDAO<Airport, Long> {


    @Override
    public Optional<Airport> findById(Long id) {
        String sql =""" 
                        SELECT name
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
        return List.of();
    }

    @Override
    public void save(Airport entity) {}

    @Override
    public void deleteById(Long id) {}

    private Airport mapRow(ResultSet rs) throws SQLException {
        Airport airport = new Airport();

    }
}
