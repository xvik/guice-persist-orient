package ru.vyarus.guice.persist.orient.finder.executor;

import com.google.inject.Provider;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.command.CommandBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Object connection query executor.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class ObjectFinderExecutor extends AbstractFinderExecutor {

    private final Provider<OObjectDatabaseTx> provider;

    @Inject
    public ObjectFinderExecutor(final Provider<OObjectDatabaseTx> provider, final CommandBuilder commandBuilder) {
        super(commandBuilder);
        this.provider = provider;
    }

    @Override
    public boolean accept(final Class<?> returnType) {
        return provider.get().getEntityManager().getRegisteredEntities().contains(returnType);
    }

    @Override
    protected OCommandRequest wrapCommand(final OCommandRequest command) {
        return provider.get().command(command);
    }

    @Override
    public Object getConnection() {
        return provider.get();
    }

    @Override
    public DbType getType() {
        return DbType.OBJECT;
    }
}
