package ru.vyarus.guice.persist.orient.finder.command;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.script.OCommandFunction;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.inject.Singleton;

/**
 * @author Vyacheslav Rusakov
 * @since 02.08.2014
 */
@Singleton
public class DefaultCommandBuilder implements CommandBuilder {

    @Override
    public OCommandRequest buildCommand(final SqlCommandDesc desc) {
        String query = desc.isFunctionCall ? desc.function : desc.query;
        if (!desc.isFunctionCall) {
            // no support for paging in functions
            if (desc.start > 0) {
                query += " SKIP " + desc.start;
            }
        }
        query = query.trim().intern();

        OCommandRequest command = null;
        if (desc.isFunctionCall) {
            command = new OCommandFunction(desc.function);
        } else {
            boolean isQuery = query.toLowerCase().startsWith("select");
            command = isQuery ? new OSQLSynchQuery<Object>(query) : new OCommandSQL(query);
        }

        if (desc.max > 0) {
            command.setLimit(desc.max);
        }

        return command;
    }
}
