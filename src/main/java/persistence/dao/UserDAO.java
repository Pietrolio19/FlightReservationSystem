package persistence.dao;

import domain.user.User;

import java.util.List;
import java.util.Optional;

public class UserDAO implements CrudDAO<User, Long> {

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void save(User entity) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
