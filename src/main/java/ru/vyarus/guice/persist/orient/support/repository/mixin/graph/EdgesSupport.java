package ru.vyarus.guice.persist.orient.support.repository.mixin.graph;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Edge;
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar;
import ru.vyarus.guice.persist.orient.repository.command.ext.ridelvar.RidElVar;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.delegate.EdgesSupportDelegate;

/**
 * Generic object edge support mixin. Used for edge-objects annotated with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge.EdgeType} (or extends E in scheme).
 * <p>
 * Supposed to be used as supplement to {@link ObjectVertexCrud}.
 * <p>
 * Mix object api (for comfortable work with properties) and graph api.
 * <p>
 * Also, may be used directly (like repository).
 *
 * @author Vyacheslav Rusakov
 * @since 12.06.2015
 */
@ProvidedBy(DynamicSingletonProvider.class)
@Delegate(EdgesSupportDelegate.class)
public interface EdgesSupport {

    /**
     * Creates new edge. Useful for edges without properties.
     *
     * @param edgeClass edge entity class
     * @param from      from node (orid, vertex, document or object)
     * @param to        from node (orid, vertex, document or object)
     * @param <T>       edge entity type
     * @return edge instance (using object api)
     */
    <T> T createEdge(Class<T> edgeClass, Object from, Object to);

    /**
     * Creates new edge from edge object. Useful for edges with properties.
     * Note: returned edge instance will be different from provided edge object
     * (provided edge object is just value holder).
     *
     * @param from from node (orid, vertex, document or object)
     * @param to   from node (orid, vertex, document or object)
     * @param edge edge object
     * @param <T>  edge entity type
     * @return edge instance (using object api)
     */
    <T> T createEdge(Object from, Object to, T edge);

    /**
     * Creates new edge from document. Useful for edges with properties.
     *
     * @param edgeClass edge entity class
     * @param from      from node (orid, vertex, document or object)
     * @param to        from node (orid, vertex, document or object)
     * @param edge      edge document (its fields will be used to init edge properties)
     * @param <T>       edge entity type
     * @return edge instance (using object api)
     */
    <T> T createEdge(Class<T> edgeClass, Object from, Object to, ODocument edge);

    /**
     * Deletes edge.
     *
     * @param edgeClass edge entity class
     * @param from      from node (orid, vertex, document or object)
     * @param to        from node (orid, vertex, document or object)
     * @return count of removed edges
     */
    @Query("delete edge from ${from} to ${to} where @class='${cls}'")
    int deleteEdge(@ElVar("cls") Class edgeClass, @RidElVar("from") Object from, @RidElVar("to") Object to);

    /**
     * @param edge edge instance (object api), document, vertex or rid
     */
    void deleteEdge(Object edge);

    /**
     * Selects edge by connected nodes.
     * NOTE: if more than one edge of required type exist in db only first edge will be returned.
     *
     * @param edgeClass edge entity class
     * @param from      from node (orid, vertex, document or object)
     * @param to        to node (orid, vertex, document or object)
     * @param <T>       edge entity type
     * @return edge instance (using object api) or null if not found
     */
    <T> T findEdge(Class<T> edgeClass, Object from, Object to);

    /**
     * Selects edge between specified nodes (any direction).
     * NOTE: if more than one edge of required type exist in db only first edge will be returned.
     *
     * @param edgeClass edge entity class
     * @param first     first node (orid, vertex, document or object)
     * @param second    second node (orid, vertex, document or object)
     * @param <T>       edge entity type
     * @return edge instance (using object api) or null if not found
     */
    <T> T findEdgeBetween(Class<T> edgeClass, Object first, Object second);

    /**
     * @param id  edge id
     * @param <T> edge type
     * @return found entity or null
     */
    <T> T getEdge(String id);

    /**
     * Updates edge properties.
     *
     * @param edge object (object api) or orient edge
     */
    void updateEdge(Object edge);

    /**
     * Converts object api instance into orient edge (preserving state).
     *
     * @param edge edge object instance
     * @param <T>  edge type (simply Edge or OrientEdge)
     * @return orient edge instance
     */
    <T extends Edge> T objectToEdge(Object edge);

    /**
     * Converts orient edge into pojo (preserving state).
     *
     * @param edge orient edge instance
     * @param <T>  type of expected pojo
     * @return object api pojo instance
     */
    <T> T edgeToObject(Edge edge);
}
