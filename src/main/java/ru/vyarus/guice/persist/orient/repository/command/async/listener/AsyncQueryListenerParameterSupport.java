package ru.vyarus.guice.persist.orient.repository.command.async.listener;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandRequestAbstract;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery;
import ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener;
import ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncResultMapper;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.support.ListenerParameterSupport;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import java.lang.annotation.Annotation;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * Handler for {@link Listen} parameters within {@link ru.vyarus.guice.persist.orient.repository.command.query.Query}
 * or {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery}.
 * <p>
 * * Listener wrapped with an external transaction to be able to use thread bound listener connection through guice.
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
public class AsyncQueryListenerParameterSupport implements ListenerParameterSupport {

    private static final Key<PersistentContext<ODatabaseDocument>> CONTEXT_KEY =
            Key.get(new TypeLiteral<PersistentContext<ODatabaseDocument>>() {
            });

    @Override
    public boolean accept(final Class<? extends Annotation> extension) {
        return AsyncQuery.class.equals(extension);
    }

    @Override
    public void checkParameter(final String query, final ParamInfo<Listen> param,
                               final Class<?> returnType) {
        check(query.startsWith("select"), "Listener could be applied only for select queries");
        check(OCommandResultListener.class.isAssignableFrom(param.type)
                        || AsyncQueryListener.class.isAssignableFrom(param.type),
                "Only %s or %s can be used as result listener",
                OCommandResultListener.class.getName(), AsyncQueryListener.class.getName());
    }

    @Override
    public OCommandResultListener processListener(final OCommandRequest command,
                                                  final Object listener,
                                                  final Injector injector,
                                                  final Class<?> conversionTarget) {
        checkExec(command instanceof OCommandRequestAbstract,
                "@%s can't be applied to query, because command object %s doesn't support it",
                Listen.class.getSimpleName(), command.getClass().getName());
        return wrap(listener, injector, conversionTarget, ((OSQLQuery) command).getText());
    }

    private OCommandResultListener wrap(final Object listener,
                                        final Injector injector,
                                        final Class<?> targetType,
                                        final String queryContext) {
        final OCommandResultListener res = listener instanceof AsyncQueryListener
                ? wrap((AsyncQueryListener) listener, injector, targetType) : (OCommandResultListener) listener;
        // wrap listener with external transaction
        return new TransactionalAsyncAdapter(injector.getInstance(CONTEXT_KEY), res, queryContext);
    }

    private OCommandResultListener wrap(final AsyncQueryListener listener, final Injector injector,
                                        final Class<?> targetType) {
        final RecordConverter converter = injector.getInstance(RecordConverter.class);
        return new AsyncResultMapper(converter, listener, targetType);
    }

}
