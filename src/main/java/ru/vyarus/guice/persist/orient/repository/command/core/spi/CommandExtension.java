package ru.vyarus.guice.persist.orient.repository.command.core.spi;

import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension;

/**
 * Query specific extensions. Query execution process consists of two phases: prepare descriptor and create
 * orient command object and execute.
 * <p>
 * On descriptor creation extensions could modify command string, parse parameters from arguments and
 * supply el variable values.
 * <p>
 * On command creation phase extension could modify orient command object.
 *
 * @param <T> descriptor type
 * @author Vyacheslav Rusakov
 * @since 05.02.2015
 */
public interface CommandExtension<T extends CommandMethodDescriptor> extends AmendExecutionExtension {

    /**
     * Called after sql descriptor creation. Use it to modify query, parse parameters and add el variables.
     *
     * @param sql        sql command descriptor
     * @param descriptor repository method descriptor
     * @param instance   repository instance
     * @param arguments  method execution arguments
     */
    void amendCommandDescriptor(SqlCommandDescriptor sql, T descriptor, Object instance, Object... arguments);

    /**
     * Called after query object creation. Use it to modify request command object.
     * <p>
     * Note: this is raw command object, not tied to connection. After extension command attached to
     * exact connection type, which wraps command object with another command (to apply security,
     * result conversions etc).
     *
     * @param query      query command request
     * @param descriptor repository method descriptor
     * @param instance   repository instance
     * @param arguments  method execution arguments
     */
    void amendCommand(OCommandRequest query, T descriptor, Object instance, Object... arguments);
}
