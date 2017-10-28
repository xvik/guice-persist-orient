package ru.vyarus.guice.persist.orient.support.repository.mixin.crud;

import com.orientechnologies.orient.core.id.ORID;
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.delegate.ObjectCrudDelegate;

/**
 * Crud mixin for object repositories.
 * Could be used by repository to avoid external dao requirement.
 * <p>
 * Warning: be careful with graph entities (e.g. classes annotated with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.VertexType} because when entity is
 * removed with object (or document) api graph consistency is not checked (as a result you will have
 * stale edges in db). Use
 * {@link ru.vyarus.guice.persist.orient.support.repository.mixin.graph.ObjectVertexCrud} for graph nodes.
 *
 * @param <T> entity type
 * @author Vyacheslav Rusakov
 * @since 15.10.2014
 */
@Delegate(ObjectCrudDelegate.class)
public interface ObjectCrud<T> extends BaseObjectCrud<T> {

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
}
