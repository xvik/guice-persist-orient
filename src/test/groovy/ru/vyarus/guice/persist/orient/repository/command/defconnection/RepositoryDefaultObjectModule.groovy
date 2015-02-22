package ru.vyarus.guice.persist.orient.repository.command.defconnection

import com.google.inject.AbstractModule
import ru.vyarus.guice.persist.orient.RepositoryModule
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule

/**
 * Defines object as default connection for ambiguous cases (instead of document).
 *
 * @author Vyacheslav Rusakov 
 * @since 02.08.2014
 */
class RepositoryDefaultObjectModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PackageSchemeModule())
        install(new RepositoryModule()
                .defaultConnectionType(DbType.OBJECT))
    }
}
