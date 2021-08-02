package it.unipd.stage.sl.springrest.services;

/**
 * CRUD interface for any type of object: exposes the usual CRUD methods
 * @param <T>
 */
public interface CRUDService<T> {

    T getById(Long id);

    T create(T obj);

    T update(T obj);

    boolean delete(Long id);
}
