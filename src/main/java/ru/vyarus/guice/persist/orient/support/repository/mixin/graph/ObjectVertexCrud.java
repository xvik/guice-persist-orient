package ru.vyarus.guice.persist.orient.support.repository.mixin.graph;

import com.orientechnologies.orient.core.id.ORID;
import com.tinkerpop.blueprints.Vertex;
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.BaseObjectCrud;
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.delegate.ObjectVertexCrudDelegate;

/**
 * Crud mixin for object repositories.
 * Note that {@link ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud}
 * is used for pure objects and this crud is intended to be used for graph nodes (vertex).
 * Most likely such objects are annotated with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.VertexType}
 * (or simply extend V in general).
 * <p>
 * It is important to use this mixin for vertex types, because delete methods use graph api, which
 * grants consistency checks.
 * <p>
 * Use with {@link EdgesSupport} to add edges support methods. Or {@link EdgeTypeSupport} if only
 * one edge type support required.
 *
 * @param <T> entity type
 * @author Vyacheslav Rusakov
 * @since 12.06.2015
 */
@Delegate(ObjectVertexCrudDelegate.class)
public interface ObjectVertexCrud<T> extends BaseObjectCrud<T> {

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
     * Converts object api instance into orient vertex (preserving state).
     *
     * @param vertex vertex object instance
     * @param <V>    vertex type (Vertex or OrientVertex)
     * @return orient vertex instance
     */
    <V extends Vertex> V objectToVertex(Object vertex);

    /**
     * Converts orient vertex into pojo (preserving state).
     *
     * @param vertex orient vertex instance
     * @return object api pojo instance
     */
    T vertexToObject(Vertex vertex);
}
