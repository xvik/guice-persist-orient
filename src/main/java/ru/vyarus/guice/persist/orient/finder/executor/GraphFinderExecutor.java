package ru.vyarus.guice.persist.orient.finder.executor;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.command.CommandBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class GraphFinderExecutor extends AbstractFinderExecutor {

    private static final List<Class> ACCEPT_TYPES = ImmutableList.<Class>of(Vertex.class, Edge.class);
    private final Provider<OrientBaseGraph> provider;

    @Inject
    public GraphFinderExecutor(final Provider<OrientBaseGraph> provider, final CommandBuilder commandBuilder) {
        super(commandBuilder);
        this.provider = provider;
    }

    @Override
    public boolean accept(final Class<?> returnType) {
        return ACCEPT_TYPES.contains(returnType);
    }

    @Override
    protected OCommandRequest wrapCommand(final OCommandRequest command) {
        return provider.get().command(command);
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }
}
