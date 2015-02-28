package ru.vyarus.guice.persist.orient.db.transaction.template;

import ru.vyarus.guice.persist.orient.db.PersistException;

/**
 * Exception used in transaction templates to wrap checked exceptions.
 *
 * @author Vyacheslav Rusakov
 * @since 01.02.2015
 */
public class TemplateTransactionException extends PersistException {

    public TemplateTransactionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
