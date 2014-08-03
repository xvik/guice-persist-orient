package ru.vyarus.guice.persist.orient.support.modules

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import ru.vyarus.guice.persist.orient.FinderModule
import ru.vyarus.guice.persist.orient.finder.FinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.DocumentFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.GraphFinderExecutor
import ru.vyarus.guice.persist.orient.finder.executor.ObjectFinderExecutor
import ru.vyarus.guice.persist.orient.support.finder.InterfaceFinder

/**
 * @author Vyacheslav Rusakov 
 * @since 31.07.2014
 */
class TestFinderModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new PackageSchemeModule())
        Multibinder<FinderExecutor> support = Multibinder.newSetBinder(binder(), FinderExecutor)
        support.addBinding().to(DocumentFinderExecutor);
        support.addBinding().to(ObjectFinderExecutor);
        support.addBinding().to(GraphFinderExecutor);
        install(new FinderModule().addFinder(InterfaceFinder))
    }
}
