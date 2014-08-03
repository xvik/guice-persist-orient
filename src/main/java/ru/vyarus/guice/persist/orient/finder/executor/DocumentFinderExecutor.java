package ru.vyarus.guice.persist.orient.finder.executor;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
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
public class DocumentFinderExecutor extends AbstractFinderExecutor {
    private static final List<Class> ACCEPT_TYPES = ImmutableList.<Class>of(ODocument.class);

    private Provider<ODatabaseDocumentTx> provider;

    @Inject
    public DocumentFinderExecutor(Provider<ODatabaseDocumentTx> provider, CommandBuilder commandBuilder) {
        super(commandBuilder);
        this.provider = provider;
    }


    @Override
    public boolean accept(Class<?> returnType) {
        return ACCEPT_TYPES.contains(returnType);
    }

    @Override
    protected OCommandRequest wrapCommand(OCommandRequest command) {
        return provider.get().command(command);
    }

    @Override
    public DbType getType() {
        return DbType.DOCUMENT;
    }
}
