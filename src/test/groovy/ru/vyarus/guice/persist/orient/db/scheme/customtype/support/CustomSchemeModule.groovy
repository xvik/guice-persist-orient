package ru.vyarus.guice.persist.orient.db.scheme.customtype.support

import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer
import ru.vyarus.guice.persist.orient.support.AbstractSchemeModule

/**
 * @author Vyacheslav Rusakov
 * @since 30.03.2017
 */
class CustomSchemeModule extends AbstractSchemeModule {

    CustomSchemeModule() {
        super([CustomSchemeModule.package.name] as String[])
    }

    @Override
    protected void bindSchemeInitializer() {
        bind(SecurityRoleSerializer)
        bind(SchemeInitializer).to(CustomSchemeInitializer)
    }
}
