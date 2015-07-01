package ru.vyarus.guice.persist.orient.support.repository.mixin.graph.delegate;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic;
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.EdgeTypeSupport;
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.EdgesSupport;

import javax.inject.Inject;

/**
 * Edge type support mixin delegate implementation.
 *
 * @param <E> edge type
 * @author Vyacheslav Rusakov
 * @since 23.06.2015
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class EdgeTypeSupportDelegate<E> implements EdgeTypeSupport<E, Object, Object> {

    private final EdgesSupport edgesSupport;

    @Inject
    public EdgeTypeSupportDelegate(final EdgesSupport edgesSupport) {
        this.edgesSupport = edgesSupport;
    }

    public E createEdge(@Generic("E") final Class<E> edgeClass, final Object from, final Object to) {
        return edgesSupport.createEdge(edgeClass, from, to);
    }

    @Override
    public E createEdge(final Object from, final Object to, final E edge) {
        return edgesSupport.createEdge(from, to, edge);
    }

    public int deleteEdge(@Generic("E") final Class<E> edgeClass, final Object from, final Object to) {
        return edgesSupport.deleteEdge(edgeClass, from, to);
    }

    @Override
    public void deleteEdge(final E edge) {
        edgesSupport.deleteEdge(edge);
    }

    @Override
    public void deleteEdge(final String id) {
        edgesSupport.deleteEdge(id);
    }

    public E findEdge(@Generic("E") final Class<E> edgeClass, final Object from, final Object to) {
        return edgesSupport.findEdge(edgeClass, from, to);
    }

    public E findEdgeBetween(@Generic("E") final Class<E> edgeClass, final Object first, final Object second) {
        return edgesSupport.findEdgeBetween(edgeClass, first, second);
    }

    @Override
    public E getEdge(final String id) {
        return edgesSupport.getEdge(id);
    }

    @Override
    public void updateEdge(final E edge) {
        edgesSupport.updateEdge(edge);
    }
}
