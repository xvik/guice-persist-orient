package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.inject.ImplementedBy;
import ru.vyarus.guice.persist.orient.db.scheme.impl.NoOpSchemeInitializer;

/**
 * Initialize or update database schema just after database opening or creation.
 * To register custom implementation simply register implementation in guice context.
 * By default no-op implementation will be used.
 * <p>
 * There are two predefined implementations:
 * <ul>
 *     <li>{@link ru.vyarus.guice.persist.orient.db.scheme.impl.PackageSchemeInitializer} which update scheme from
 *     using all classes in defined package (more suitable for standard package by layer approach)</li>
 *     <li>{@link ru.vyarus.guice.persist.orient.db.scheme.impl.AutoScanSchemeInitializer} which update scheme from
 *     all annotated beans in classpath (more suitable for package by feature approach)</li>
 * </ul>
 * Schema modifications must be performed without transaction (orient requirement), and implementation will
 * be called under predefined notx unit of work.
 *
 * @see ru.vyarus.guice.persist.orient.db.data.DataInitializer for data migration or default data initialization
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
@ImplementedBy(NoOpSchemeInitializer.class)
public interface SchemeInitializer {

    /**
     * Called under predefined notx unit of work just after database opened or created to initialize or update scheme.
     * Connection object not provided because implementer may choose any type and so needs to use appropriate provider.
     */
    void initialize();
}
