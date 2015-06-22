package ru.vyarus.guice.persist.orient.support.repository.mixin.graph.delegate;

import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.id.ORID;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
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

    private final Provider<OrientBaseGraph> dbProvider;

    @Inject
    public ObjectVertexCrudDelegate(final Provider<OrientBaseGraph> dbProvider) {
        this.dbProvider = dbProvider;
    }

    @Override
    public void delete(final ORID id) {
        dbProvider.get().getVertex(id).remove();
    }

    @Override
    public void delete(final String id) {
        dbProvider.get().getVertex(id).remove();
    }

    @Override
    public void delete(final T entity) {
        delete(RidUtils.getRid(entity));
    }
}
