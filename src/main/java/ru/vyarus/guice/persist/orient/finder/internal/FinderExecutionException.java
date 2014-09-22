package ru.vyarus.guice.persist.orient.finder.internal;

/**
 * Thrown to indicate finder execution exception (orient execution error).
 *
 * @author Vyacheslav Rusakov
 * @since 22.09.2014
 */
public class FinderExecutionException extends RuntimeException {

    public FinderExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
