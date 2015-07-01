package ru.vyarus.guice.persist.orient.support.repository.mixin.graph.delegate;

import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import ru.vyarus.guice.persist.orient.db.util.RidUtils;
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.ObjectVertexCrud;

import javax.inject.Inject;

/**
 * Object vertex crud mixin delegate.
 *
 * @param <T> object type
 * @author Vyacheslav Rusakov
 * @since 21.06.2015
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class ObjectVertexCrudDelegate<T> implements ObjectVertexCrud<T> {

    private final Provider<OrientBaseGraph> graphDb;

    @Inject
    public ObjectVertexCrudDelegate(final Provider<OrientBaseGraph> graphDb) {
        this.graphDb = graphDb;
    }

    @Override
    public void delete(final ORID id) {
        final OrientVertex vertex = graphDb.get().getVertex(id);
        if (vertex != null) {
            vertex.remove();
        }
    }

    @Override
    public void delete(final String id) {
        final OrientVertex vertex = graphDb.get().getVertex(id);
        if (vertex != null) {
            vertex.remove();
        }
    }

    @Override
    public void delete(final T entity) {
        delete(RidUtils.getRid(entity));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Vertex> V objectToVertex(final Object vertex) {
        final ODocument doc = objectToDocument(vertex);
        return (V) graphDb.get().getVertex(doc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T vertexToObject(final Vertex vertex) {
        final OrientVertex orientVertex = (OrientVertex) vertex;
        return documentToObject(orientVertex.getRecord());
    }
}
