package ru.vyarus.guice.persist.orient.finder;

import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.command.SqlCommandDesc;

/**
 * Provides queries support for particular database type.
 * Implementations must be registered in accordance with registered pools (e.g. if graph registered then graph
 * finder support can be registered too)
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
public interface FinderExecutor {

    /**
     * Called to detect connection to use for finder method. If executor accepts return type,
     * then it will be called for execution.
     *
     * @param returnType finder return type (in case of collection it will be generic class)
     * @return true if pool recognize return type, false otherwise
     */
    boolean accept(Class<?> returnType);

    /**
     * Called to execute finder query.
     * Implementation may use {@code ru.vyarus.guice.persist.orient.finder.command.CommandBuilder}
     * for actual query building.
     *
     * @param desc query description
     * @return query execution result
     */
    Object executeQuery(SqlCommandDesc desc);

    /**
     * @return database connection object obtained from pool
     */
    Object getConnection();

    /**
     * @return reference connection type
     */
    DbType getType();
}
