package ru.vyarus.guice.persist.orient.repository.command.ext.timeout;

import com.orientechnologies.orient.core.command.OCommandContext;

/**
 * Command timeout descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class TimeoutDescriptor {

    /**
     * Timeout value in milliseconds (0 to ignore timeout).
     */
    public long timeout;

    /**
     * Timeout strategy.
     */
    public OCommandContext.TIMEOUT_STRATEGY strategy;

    public TimeoutDescriptor(final long timeout, final OCommandContext.TIMEOUT_STRATEGY strategy) {
        this.timeout = timeout;
        this.strategy = strategy;
    }
}
