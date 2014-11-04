package ru.vyarus.guice.persist.orient.support.finder.mixin.crud;

import com.orientechnologies.orient.core.id.ORID;
import ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate;

import java.util.Iterator;

/**
 * Crud mixin for object finders.
 * Could be used by finder to avoid external dao requirement.
 *
 * @param <T> entity type
 * @author Vyacheslav Rusakov
 * @since 15.10.2014
 */
@FinderDelegate(ObjectCrudDelegate.class)
public interface ObjectCrudMixin<T> {

    /**
     * @param id entity id
     * @return found entity or null
     */
    T get(String id);

    /**
     * @param id entity id
     * @return found entity or null
     */
    T get(ORID id);

    /**
     * Note: for object entities returns object proxy. If you need to use saved object just after save,
     * use returned proxy instead of original object (original entity will not be updated with id or version).
     *
     * @param entity entity to save or update
     * @return saved entity instance
     */
    T save(T entity);

    /**
     * @param entity entity to remove
     */
    void delete(T entity);

    /**
     * @param id entity id to remove
     */
    void delete(String id);

    /**
     * @param id entity id to remove
     */
    void delete(ORID id);

    /**
     * @param entity entity to attach
     */
    void attach(T entity);

    /**
     * @param entity entity object (proxy)
     * @return detached row entity (unproxied)
     */
    T detach(T entity);

    /**
     * Create new empty proxy. Used if it's important to track instance:
     * if raw object used for save, then it will not be updated and you will
     * have to use returned proxy object. This allows to overcome limitation.
     *
     * @return empty object proxy
     */
    T create();

    /**
     * @return all records of type
     */
    Iterator<T> getAll();
}
