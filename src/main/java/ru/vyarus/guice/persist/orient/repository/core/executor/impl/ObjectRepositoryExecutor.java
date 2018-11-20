package ru.vyarus.guice.persist.orient.repository.core.executor.impl;

import com.google.inject.Provider;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.executor.RepositoryExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Object connection repository executor.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class ObjectRepositoryExecutor implements RepositoryExecutor {

    private final Provider<ODatabaseObject> provider;

    @Inject
    public ObjectRepositoryExecutor(final Provider<ODatabaseObject> provider) {
        this.provider = provider;
    }

    @Override
    public boolean accept(final Class<?> returnType) {
        return provider.get().getEntityManager().getRegisteredEntities().contains(returnType);
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
        return DbType.OBJECT;
    }
}
