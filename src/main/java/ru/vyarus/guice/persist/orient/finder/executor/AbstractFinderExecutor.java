package ru.vyarus.guice.persist.orient.finder.executor;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.command.CommandBuilder;
import ru.vyarus.guice.persist.orient.finder.command.SqlCommandDesc;

/**
 * Base class for finder executors.
 *
 * @author Vyacheslav Rusakov
 * @since 02.08.2014
 */
public abstract class AbstractFinderExecutor implements FinderExecutor {

    private CommandBuilder commandBuilder;

    public AbstractFinderExecutor(final CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    @Override
    public Object executeQuery(final SqlCommandDesc desc) {
        OCommandRequest command = commandBuilder.buildCommand(desc);
        command = wrapCommand(command);

        Object result;
        if (desc.useNamedParams) {
            result = desc.namedParams.size() > 0
                    ? command.execute(desc.namedParams) : command.execute();
        } else {
            result = desc.params.length > 0
                    ? command.execute(desc.params) : command.execute();
        }
        return result;
    }

    /**
     * Called to bind command to connection.
     *
     * @param command command
     * @return command bound to connection
     */
    protected abstract OCommandRequest wrapCommand(OCommandRequest command);
}
