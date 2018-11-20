package ru.vyarus.guice.persist.orient.repository.core.executor.impl;

import com.google.inject.Provider;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.ORecord;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.executor.RepositoryExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Document connection repository executor.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class DocumentRepositoryExecutor implements RepositoryExecutor {

    private final Provider<ODatabaseDocument> provider;

    @Inject
    public DocumentRepositoryExecutor(final Provider<ODatabaseDocument> provider) {
        this.provider = provider;
    }


    @Override
    public boolean accept(final Class<?> returnType) {
        return ORecord.class.isAssignableFrom(returnType);
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
        return DbType.DOCUMENT;
    }
}
