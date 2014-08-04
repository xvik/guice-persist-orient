package ru.vyarus.guice.persist.orient.finder.executor;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.command.CommandBuilder;
import ru.vyarus.guice.persist.orient.finder.command.SqlCommandDesc;

/**
 * @author Vyacheslav Rusakov
 * @since 02.08.2014
 */
public abstract class AbstractFinderExecutor implements FinderExecutor {

    private CommandBuilder commandBuilder;

    public AbstractFinderExecutor(CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    @Override
    public Object executeQuery(SqlCommandDesc desc) {
        OCommandRequest command = commandBuilder.buildCommand(desc);
        command = wrapCommand(command);

        Object result;
        if (desc.useNamedParams) {
            result = desc.namedParams.size() > 0 ?
                    command.execute(desc.namedParams) : command.execute();
        } else {
            result = desc.params.length > 0 ?
                    command.execute(desc.params) : command.execute();
        }
        return result;
    }

    protected abstract OCommandRequest wrapCommand(OCommandRequest command);
}
