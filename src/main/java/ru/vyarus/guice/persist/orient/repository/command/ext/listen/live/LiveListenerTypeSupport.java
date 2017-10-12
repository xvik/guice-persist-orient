package ru.vyarus.guice.persist.orient.repository.command.ext.listen.live;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OLiveQuery;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.ListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery;
import ru.vyarus.guice.persist.orient.repository.command.live.mapper.LiveResultListener;
import ru.vyarus.guice.persist.orient.repository.command.live.mapper.LiveResultMapper;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * Handler for {@link Listen} parameters within {@link LiveQuery}.
 * <p>
 * Listener may be {@link OLiveResultListener} (orient) or {@link LiveResultListener} if special result
 * conversions required.
 * <p>
 * If transaction is required (through {@link Listen}) then listener will be wrapped to apply transaction.
 * <p>
 * If no transaction is required and  listener implement {@link OCommandResultListener} directly then no wrapping
 * applied.
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
public class LiveListenerTypeSupport implements ListenerTypeSupport {

    private static final Key<PersistentContext<ODatabaseDocumentTx>> CONTEXT_KEY =
            Key.get(new TypeLiteral<PersistentContext<ODatabaseDocumentTx>>() {
            });

    @Override
    public void checkParameter(final String query, final ParamInfo<Listen> param, final Class<?> returnType) {
        check(int.class.equals(returnType) || Integer.class.equals(returnType),
                "Live query method must have int return type to receive subscription token");
        check(OLiveResultListener.class.isAssignableFrom(param.type)
                        || LiveResultListener.class.isAssignableFrom(param.type),
                "Only %s or %s can be used as live listener",
                OLiveResultListener.class.getName(), LiveResultListener.class.getName());
    }

    @Override
    public OCommandResultListener processListener(final OCommandRequest command,
                                                  final Object listener,
                                                  final Injector injector,
                                                  final boolean transactional) {
        checkExec(command instanceof OLiveQuery,
                "Live listener (@%s parameter) can only be used with @%s",
                Listen.class.getSimpleName(), LiveQuery.class.getSimpleName());
        return wrap(listener, injector, transactional);
    }

    private OCommandResultListener wrap(final Object listener,
                                        final Injector injector,
                                        final boolean transactional) {
        final OLiveResultListener adaptedListener = listener instanceof LiveResultListener
                // special listener with custom mapping
                ? wrap((LiveResultListener) listener, injector) : (OLiveResultListener) listener;

        final OCommandResultListener res;
        if (transactional) {
            // wrap with transaction
            res = new TransactionalLiveAdapter(injector.getInstance(CONTEXT_KEY), adaptedListener);
        } else if (adaptedListener instanceof OCommandResultListener) {
            // listener is already correct command object
            res = (OCommandResultListener) adaptedListener;
        } else {
            // simple wrapping
            res = new OLiveListenerAdapter(adaptedListener);
        }
        return res;
    }

    private OLiveResultListener wrap(final LiveResultListener listener, final Injector injector) {
        final RecordConverter converter = injector.getInstance(RecordConverter.class);
        return new LiveResultMapper(converter, listener);
    }

}
