package ru.vyarus.guice.persist.orient.db.scheme.impl;

import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer;

/**
 * Default no-op initializer. Used if no specific implementation provided.
 *
 * @author Vyacheslav Rusakov
 * @since 26.07.2014
 */
public class NoOpSchemeInitializer implements SchemeInitializer {

    @Override
    public void initialize() {
        // no-op impl
    }
}
