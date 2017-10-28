package ru.vyarus.guice.persist.orient.support.repository.mixin.graph;

import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.delegate.EdgeTypeSupportDelegate;

/**
 * Edge type support mixin. Used for edge-object annotated with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge.EdgeType} (or extends E in scheme).
 * <p>
 * Supposed to be used as supplement to {@link ObjectVertexCrud}.
 * <p>
 * Mix object api (for comfortable work with properties) and graph api.
 * <p>
 * If more then one edge support required use {@link EdgesSupport}.
 *
 * @param <E> edge type
 * @param <F> from node type (may be exact type, Vertex, ODocument, ORID, string or Object to support all cases)
 * @param <T> to node type (may be exact type, Vertex, ODocument, ORID, string or Object to support all cases)
 * @author Vyacheslav Rusakov
 * @since 12.06.2015
 */
@Delegate(EdgeTypeSupportDelegate.class)
public interface EdgeTypeSupport<E, F, T> {

    /**
     * Creates new edge. Useful for edge without properties.
     *
     * @param from from node
     * @param to   to node
     * @return edge instance (using object api)
     */
    E createEdge(F from, T to);

    /**
     * Creates new edge from edge object. Useful for edge with properties.
     * Note: returned edge instance will be different from provided edge object
     * (provided edge object is just value holder).
     *
     * @param from from node
     * @param to   to node
     * @param edge edge object
     * @return edge instance (using object api)
     */
    E createEdge(F from, T to, E edge);

    /**
     * Deletes edge.
     *
     * @param from from node
     * @param to   to node
     * @return count of removed edges
     */
    int deleteEdge(F from, T to);

    /**
     * Deletes edge from object instance.
     *
     * @param edge edge instance (object api)
     */
    void deleteEdge(E edge);

    /**
     * Delete edge by id.
     *
     * @param id edge id
     */
    void deleteEdge(String id);

    /**
     * Selects edge between connected nodes (any direction).
     * NOTE: if more than one edge of required type exist in db only first edge will be returned.
     *
     * @param from from node
     * @param to   to node
     * @return edge instance (using object api) or null if not found
     */
    E findEdge(F from, T to);

    /**
     * Selects edge by connected nodes.
     * NOTE: if more than one edge of required type exist in db only first edge will be returned.
     *
     * @param first  first node
     * @param second second node
     * @return edge instance (using object api) or null if not found
     */
    E findEdgeBetween(F first, T second);

    /**
     * @param id edge id
     * @return found entity or null
     */
    E getEdge(String id);

    /**
     * Updates edge properties.
     *
     * @param edge object (object api)
     */
    void updateEdge(E edge);
}
