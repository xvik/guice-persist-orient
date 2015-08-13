package ru.vyarus.guice.persist.orient.repository.core.executor.impl;

import com.google.inject.Provider;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.executor.RepositoryExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Graph connection repository executor.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class GraphRepositoryExecutor implements RepositoryExecutor {

    private final Provider<OrientBaseGraph> provider;

    @Inject
    public GraphRepositoryExecutor(final Provider<OrientBaseGraph> provider) {
        this.provider = provider;
    }

    @Override
    public boolean accept(final Class<?> returnType) {
        return Edge.class.isAssignableFrom(returnType) || Vertex.class.isAssignableFrom(returnType);
    }

    @Override
    public OCommandRequest wrapCommand(final OCommandRequest command) {
        return provider.get().command(command);
    }

    @Override
    public Object getConnection() {
        return provider.get();
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }
}
