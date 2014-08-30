package ru.vyarus.guice.persist.orient.finder.command;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.script.OCommandFunction;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.inject.Singleton;

/**
 * Default command builder implementation. Differentiate select/update queries by simply
 * looking for "select" at query start (should cover most cases).
 * <p>First result value appended to query as "SKIP num", max results value set directly to query object.
 * For function call first result is ignored (api not support this)</p>
 *
 * @author Vyacheslav Rusakov
 * @since 02.08.2014
 */
@Singleton
public class DefaultCommandBuilder implements CommandBuilder {

    @Override
    @SuppressWarnings("PMD.UseStringBufferForStringAppends")
    public OCommandRequest buildCommand(final SqlCommandDesc desc) {
        final boolean isFunction = desc.isFunctionCall;
        // skip can't be applied to function
        String query = isFunction
                ? desc.function
                : desc.query.trim();

        final boolean isQuery = !isFunction && query.toLowerCase().startsWith("select");
        if (isQuery) {
            query += desc.start > 0 ? " SKIP " + desc.start : "";
        }
        final OCommandRequest command = createCommandInstance(isFunction, isQuery, query);

        if (desc.max > 0 && (desc.isFunctionCall || isQuery)) {
            // must not be set for update command
            command.setLimit(desc.max);
        }

        return command;
    }

    private OCommandRequest createCommandInstance(final boolean isFunction,
                                                  final boolean isQuery,
                                                  final String query) {
        OCommandRequest command;
        if (isFunction) {
            command = new OCommandFunction(query);
        } else {
            command = isQuery ? new OSQLSynchQuery<Object>(query) : new OCommandSQL(query);
        }
        return command;
    }
}
