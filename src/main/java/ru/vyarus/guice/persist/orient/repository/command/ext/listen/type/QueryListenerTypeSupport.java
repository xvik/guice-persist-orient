package ru.vyarus.guice.persist.orient.repository.command.ext.listen.type;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandRequestAbstract;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.ListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * Handler for {@link Listen} parameters within {@link ru.vyarus.guice.persist.orient.repository.command.query.Query}
 * or {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery}.
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
public class QueryListenerTypeSupport implements ListenerTypeSupport<OCommandResultListener> {

    @Override
    public void checkParameter(final String query, final ParamInfo<Listen> param,
                               final Class<?> returnType) {
        check(query.startsWith("select"), "Listener could be applied only for select queries");
        check(void.class.equals(returnType) || Void.class.equals(returnType),
                "Method with listener must be void, because no results returned from query "
                        + "when listener used");
        check(OCommandResultListener.class.isAssignableFrom(param.type), "Only %s can be used as result listener",
                OCommandResultListener.class.getName());
    }

    @Override
    public OCommandResultListener processListener(final OCommandRequest command,
                                                  final OCommandResultListener listener) {
        checkExec(command instanceof OCommandRequestAbstract,
                "@%s can't be applied to query, because command object %s doesn't support it",
                Listen.class.getSimpleName(), command.getClass().getName());
        return listener;
    }
}
