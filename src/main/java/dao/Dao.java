package dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {
    List<T> findAll();

    Optional<T> findById(K id);

    T getById(Integer id) throws Exception;

    boolean delete(K id);

    boolean update(T entity);

    T save(T entity);
}
