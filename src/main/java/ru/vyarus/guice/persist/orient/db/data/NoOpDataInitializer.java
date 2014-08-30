package ru.vyarus.guice.persist.orient.db.data;

/**
 * Default no-op data initializer implementation, used if no custom implementation defined.
 *
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
public class NoOpDataInitializer implements DataInitializer {

    @Override
    public void initializeData() {
        // no-op impl
    }
}
