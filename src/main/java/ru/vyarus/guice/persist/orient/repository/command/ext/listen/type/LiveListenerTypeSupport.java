package ru.vyarus.guice.persist.orient.repository.command.ext.listen.type;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.sql.query.OLiveQuery;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.ListenerTypeSupport;
import ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * Handler for {@link Listen} parameters within {@link LiveQuery}.
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
public class LiveListenerTypeSupport implements ListenerTypeSupport<OLiveResultListener> {

    @Override
    public void checkParameter(final String query, final ParamInfo<Listen> param, final Class<?> returnType) {
        check(int.class.equals(returnType) || Integer.class.equals(returnType),
                "Live query method must have int return type to receive subscription token");
        check(OLiveResultListener.class.isAssignableFrom(param.type), "Only %s can be used as live listener",
                OLiveResultListener.class.getName());
    }

    @Override
    public OCommandResultListener processListener(final OCommandRequest command, final OLiveResultListener listener) {
        checkExec(command instanceof OLiveQuery,
                "Live listener (@%s parameter) can only be used with @%s",
                Listen.class.getSimpleName(), LiveQuery.class.getSimpleName());
        return new OLiveListenerAdapter(listener);
    }
}
