package ru.vyarus.guice.persist.orient.repository.core.executor.impl;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.executor.RepositoryExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Document connection repository executor.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class DocumentRepositoryExecutor implements RepositoryExecutor {
    private static final List<Class> ACCEPT_TYPES = ImmutableList.<Class>of(ODocument.class);

    private final Provider<ODatabaseDocumentTx> provider;

    @Inject
    public DocumentRepositoryExecutor(final Provider<ODatabaseDocumentTx> provider) {
        this.provider = provider;
    }


    @Override
    public boolean accept(final Class<?> returnType) {
        return ACCEPT_TYPES.contains(returnType);
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
