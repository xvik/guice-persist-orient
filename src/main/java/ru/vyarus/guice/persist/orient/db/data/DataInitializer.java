package ru.vyarus.guice.persist.orient.db.data;

import com.google.inject.ImplementedBy;

/**
 * Update database data just after schema update (see SchemeInitializer).
 * Useful for db initialization in development or data migration after schema changes.
 * To register custom implementation simply register implementation in guice context.
 * By default no-op implementation will be used.
 * <p>
 * NOTE: Will be called WITHOUT transaction context, because you may need different transaction types,
 * so its up to implementer to define unit of work (e.g. by annotating implementation bean
 * with @Transactional or using transaction template).
 * More than one unit of work may be used (again, it's up to implementer).
 *
 * @see ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
@ImplementedBy(NoOpDataInitializer.class)
public interface DataInitializer {

    /**
     * Called after database open and schema update to update or prepare database data.
     * Connection object not provided because implementer may choose any type and so needs to use appropriate provider.
     *
     * @see ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer for schema initialization
     */
    void initializeData();
}
