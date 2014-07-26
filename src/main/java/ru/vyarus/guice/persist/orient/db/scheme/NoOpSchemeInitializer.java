package ru.vyarus.guice.persist.orient.db.scheme;

/**
 * Default no-op initializer. Used if no specific implementation provided.
 *
 * @author Vyacheslav Rusakov
 * @since 26.07.2014
 */
public class NoOpSchemeInitializer implements SchemeInitializer {

    @Override
    public void initialize() {
    }
}
