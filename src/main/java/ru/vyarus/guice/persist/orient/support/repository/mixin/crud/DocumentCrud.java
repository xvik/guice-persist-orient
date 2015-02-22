package ru.vyarus.guice.persist.orient.support.repository.mixin.crud;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;

import java.util.Iterator;

/**
 * Crud mixin for document repositories.
 * Could be used by repository to avoid external dao requirement.
 *
 * @param <T> entity type (if not specified then getAll method will not work)
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
@Delegate(DocumentCrudDelegate.class)
public interface DocumentCrud<T> {

    /**
     * @param id entity id
     * @return found entity or null
     */
    ODocument get(String id);

    /**
     * @param id entity id
     * @return found entity or null
     */
    ODocument get(ORID id);

    /**
     * Note: for object entities returns object proxy. If you need to use saved object just after save,
     * use returned proxy instead of original object (original entity will not be updated with id or version).
     *
     * @param entity entity to save or update
     * @return saved entity instance
     */
    ODocument save(ODocument entity);

    /**
     * @param entity entity to remove
     */
    void delete(ODocument entity);

    /**
     * @param id entity id to remove
     */
    void delete(String id);

    /**
     * @param id entity id to remove
     */
    void delete(ORID id);

    /**
     * NOTE: works only if generic parameter set. Method can't be used in case when queried type doesn't have
     * class reference.
     *
     * @return all records of type
     */
    Iterator<ODocument> getAll();

    /**
     * Create new empty document.
     * Starting from orient 2.0 document creation requires database object bound to current thread.
     *
     * @return empty document
     */
    ODocument create();
}
