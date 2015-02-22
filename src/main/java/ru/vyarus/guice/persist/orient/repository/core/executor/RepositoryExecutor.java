package ru.vyarus.guice.persist.orient.repository.core.executor;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.db.DbType;

/**
 * Provides queries support for particular database type.
 * Implementations must be registered in accordance with registered pools (e.g. if graph registered then graph
 * executor can be registered too)
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
public interface RepositoryExecutor {

    /**
     * Called to detect connection to use for repository method. If executor accepts return type,
     * then it will be called for execution.
     *
     * @param returnType repository return type (in case of collection it will be generic class)
     * @return true if pool recognize return type, false otherwise
     */
    boolean accept(Class<?> returnType);

    /**
     * @return database connection object obtained from pool
     */
    Object getConnection();

    /**
     * @return reference connection type
     */
    DbType getType();

    /**
     * Called to bind command to connection.
     * Query will work without wrapping in document mode. Wrapped commend ties result to connection specific objects.
     *
     * @param command command
     * @return command bound to connection
     */
    OCommandRequest wrapCommand(OCommandRequest command);
}
