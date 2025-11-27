package persistence.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    void save(T entity);      // insert o update

    void deleteById(ID id);
}
