package ru.vyarus.guice.persist.orient.repository.command.ext.listen.query;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandRequestAbstract;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryListener;
import ru.vyarus.guice.persist.orient.repository.command.async.mapper.QueryResultMapper;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.support.ListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * Handler for {@link Listen} parameters within {@link ru.vyarus.guice.persist.orient.repository.command.query.Query}
 * or {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery}.
 * <p>
 * If transaction is required (through {@link Listen}) then listener will be wrapped to apply transaction.
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
public class QueryListenerTypeSupport implements ListenerTypeSupport {

    private static final Key<PersistentContext<ODatabaseDocumentTx>> CONTEXT_KEY =
            Key.get(new TypeLiteral<PersistentContext<ODatabaseDocumentTx>>() {
            });

    @Override
    public void checkParameter(final String query, final ParamInfo<Listen> param,
                               final Class<?> returnType) {
        check(query.startsWith("select"), "Listener could be applied only for select queries");
        check(void.class.equals(returnType) || Void.class.equals(returnType),
                "Method with listener must be void, because no results returned from query "
                        + "when listener used");
        check(OCommandResultListener.class.isAssignableFrom(param.type)
                        || QueryListener.class.isAssignableFrom(param.type),
                "Only %s or %s can be used as result listener",
                OCommandResultListener.class.getName(), QueryListener.class.getName());
    }

    @Override
    public OCommandResultListener processListener(final OCommandRequest command,
                                                  final Object listener,
                                                  final Injector injector,
                                                  final boolean transactional,
                                                  final Class<?> conversionTarget) {
        checkExec(command instanceof OCommandRequestAbstract,
                "@%s can't be applied to query, because command object %s doesn't support it",
                Listen.class.getSimpleName(), command.getClass().getName());
        return wrap(listener, injector, transactional, conversionTarget);
    }

    private OCommandResultListener wrap(final Object listener,
                                        final Injector injector,
                                        final boolean transactional,
                                        final Class<?> targetType) {
        final OCommandResultListener res = listener instanceof QueryListener
                ? wrap((QueryListener) listener, injector, targetType) : (OCommandResultListener) listener;
        // wrap listener with transaction if required
        return transactional ? new TransactionalAsyncAdapter(injector.getInstance(CONTEXT_KEY), res) : res;
    }

    private OCommandResultListener wrap(final QueryListener listener, final Injector injector,
                                        final Class<?> targetType) {
        final RecordConverter converter = injector.getInstance(RecordConverter.class);
        return new QueryResultMapper(converter, listener, targetType);
    }

}
