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

    boolean accept(Class<?> returnType);

    Object executeQuery(SqlCommandDesc desc);

    DbType getType();
}
