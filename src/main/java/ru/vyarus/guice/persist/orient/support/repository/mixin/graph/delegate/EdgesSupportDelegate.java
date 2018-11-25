package ru.vyarus.guice.persist.orient.support.repository.mixin.graph.delegate;

import com.google.common.base.Preconditions;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import ru.vyarus.guice.persist.orient.db.util.RidUtils;
import ru.vyarus.guice.persist.orient.support.repository.mixin.graph.EdgesSupport;

import javax.inject.Inject;
import java.util.Iterator;

/**
 * Edges support mixin delegate implementation.
 *
 * @author Vyacheslav Rusakov
 * @since 22.06.2015
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class EdgesSupportDelegate implements EdgesSupport {

    private final Provider<ODatabaseObject> objectDb;
    private final Provider<OrientBaseGraph> graphDb;

    @Inject
    public EdgesSupportDelegate(final Provider<ODatabaseObject> objectDb, final Provider<OrientBaseGraph> graphDb) {
        this.objectDb = objectDb;
        this.graphDb = graphDb;
    }

    @Override
    public <T> T createEdge(final Class<T> edgeClass, final Object from, final Object to) {
        final OrientEdge edgeImpl = createEdgeImpl(edgeClass, from, to);
        return objectDb.get().load(edgeImpl.getIdentity());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createEdge(final Object from, final Object to, final T edge) {
        final ODocument doc = (ODocument) objectDb.get().getRecordByUserObject(edge, true);
        return this.createEdge((Class<T>) edge.getClass(), from, to, doc);
    }

    @Override
    public <T> T createEdge(final Class<T> edgeClass, final Object from, final Object to, final ODocument edge) {
        final OrientEdge edgeImpl = createEdgeImpl(edgeClass, from, to);
        for (String key : edge.fieldNames()) {
            final Object val = edge.field(key);
            if (key.charAt(0) != '@' && val != null) {
                edgeImpl.setProperty(key, val);
            }
        }
        edgeImpl.save();
        return objectDb.get().load(edgeImpl.getIdentity());
    }

    @Override
    public void deleteEdge(final Object edge) {
        final OrientEdge edgeImpl = graphDb.get().getEdge(RidUtils.getRid(edge));
        if (edgeImpl != null) {
            edgeImpl.remove();
        }
    }

    @Override
    public <T> T findEdge(final Class<T> edgeClass, final Object from, final Object to) {
        return findEdgeImpl(edgeClass, from, to, Direction.OUT);
    }

    @Override
    public <T> T findEdgeBetween(final Class<T> edgeClass, final Object first, final Object second) {
        return findEdgeImpl(edgeClass, first, second, Direction.BOTH);
    }

    @Override
    public <T> T getEdge(final String id) {
        return objectDb.get().load(new ORecordId(id));
    }

    @Override
    public void updateEdge(final Object edge) {
        if (edge instanceof OrientEdge) {
            ((OrientEdge) edge).save();
        } else {
            objectDb.get().save(edge);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T edgeToObject(final Edge edge) {
        return (T) objectDb.get().getUserObjectByRecord((OrientEdge) edge, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Edge> T objectToEdge(final Object edge) {
        final ORecord doc = objectDb.get().getRecordByUserObject(edge, true);
        return (T) graphDb.get().getEdge(doc);
    }

    private OrientEdge createEdgeImpl(final Class edgeClass, final Object from, final Object to) {
        final OrientBaseGraph graph = graphDb.get();
        return graph.addEdge("class:" + edgeClass.getSimpleName(),
                getVertex(from),
                getVertex(to), null);
    }

    @SuppressWarnings("unchecked")
    private <T> T findEdgeImpl(final Class<T> edgeClass, final Object first, final Object second,
                               final Direction direction) {
        final Iterator<Edge> it = getVertex(first)
                .getEdges(getVertex(second), direction,
                        edgeClass.getSimpleName()).iterator();
        T res = null;
        if (it.hasNext()) {
            res = objectDb.get().load(((OrientEdge) it.next()).getIdentity());
        }
        return res;
    }

    private OrientVertex getVertex(final Object object) {
        final String rid = RidUtils.getRid(object);
        return Preconditions.checkNotNull(graphDb.get().getVertex(rid), "No vertex found for rid %s", rid);
    }
}
