package persistence.dao.flight;

import domain.flight.Aircraft;
import persistence.DBManager;
import persistence.dao.CrudDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AircraftDAO implements CrudDAO<Aircraft, Long> {

    @Override
    public Optional<Aircraft> findById(Long id) {
        String sql= """
                        SELECT id, model, producer, capacity
                        FROM Aircraft
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
    public List<Aircraft> findAll() {
        String sql = """
                        SELECT *
                        FROM Aircraft
                    """;
        List<Aircraft> result = new ArrayList<>();

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
    public void save(Aircraft entity) {
        if(entity.getAircraftId() == null){
            insert(entity);
        }
        else{
            update(entity);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                        DELETE FROM Aircraft WHERE id = ?
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
    public void insert(Aircraft entity) {
        String  sql = """
                        INSERT INTO Aircraft(model, producer, capacity)
                        VALUES (?, ?, ?)
                     """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, entity.getModel());
            ps.setString(2, entity.getProducer());
            ps.setInt(3, entity.getCapacity());

            ps.executeUpdate();

            try(ResultSet keys = ps.getGeneratedKeys();){
                if(keys.next()){
                    entity.setAircraftId(keys.getLong(1));
                }
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Aircraft entity) {
        String sql= """
                        UPDATE Aircraft
                        SET model = ?, producer = ?, capacity = ?
                        WHERE id = ?
                    """;
        try(Connection conn = DBManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, entity.getModel());
            ps.setString(2, entity.getProducer());
            ps.setInt(3, entity.getCapacity());
            ps.setLong(4, entity.getAircraftId());

            ps.executeUpdate();

        } catch(SQLException e){
            e.printStackTrace();
        }
    }


    private Aircraft mapRow(ResultSet rs) throws SQLException {
        Aircraft aircraft = new Aircraft();

        aircraft.setAircraftId(rs.getLong("id"));
        aircraft.setModel(rs.getString("model"));
        aircraft.setProducer(rs.getString("producer"));
        aircraft.setCapacity(rs.getInt("capacity"));

        return aircraft;
    }
}
