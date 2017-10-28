package ru.vyarus.guice.persist.orient.support;

import com.google.common.base.Joiner;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistService;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.ExtensionsDescriptorFactory;

/**
 * Base class for provided scheme initializers.
 * If no package specified then assuming root package should be used.
 * <p>
 * Initialization performed with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer}, which extends default orient
 * registration abilities. Custom plugins may be used.
 *
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
public abstract class AbstractSchemeModule extends AbstractModule {

    private final String pkgs;

    public AbstractSchemeModule(final String... pkgs) {
        this.pkgs = pkgs.length == 0 ? "" : Joiner.on(',').join(pkgs);
    }

    @Override
    protected void configure() {
        // prevent usage without main OrientModule
        requireBinding(PersistService.class);

        // if package not provided empty string will mean root package (search all classpath)
        // not required if provided scheme initializers not used
        bindConstant().annotatedWith(Names.named("orient.model.package")).to(pkgs);

        // required explicit binding to inject correct injector instance (instead of always root injector)
        bind(ExtensionsDescriptorFactory.class);

        bindSchemeInitializer();
    }

    /**
     * Bind scheme initializer implementation.
     *
     * @see ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializer
     */
    protected abstract void bindSchemeInitializer();
}
