package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.FinderModule
import ru.vyarus.guice.persist.orient.db.DbType

/**
 * Defines object as default connection for ambiguous cases (instead of document).
 *
 * @author Vyacheslav Rusakov 
 * @since 02.08.2014
 */
class FinderDefaultObjectModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PackageSchemeModule())
        install(new FinderModule()
                .defaultConnectionType(DbType.OBJECT))
    }
}
