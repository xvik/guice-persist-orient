package ru.vyarus.guice.persist.orient.util.uniquedb

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.runtime.model.Tag

/**
 * @author Vyacheslav Rusakov 
 * @since 10.10.2015
 */
class UniqueDbExtension extends AbstractAnnotationDrivenExtension<UniqueDb> {

    @Override
    void visitSpecAnnotation(UniqueDb annotation, SpecInfo spec) {

    }

    @Override
    void visitSpec(SpecInfo spec) {
        spec.addTag(new Tag("memory"))
        def interceptor = new UniqueDbInterceptor()
        spec.sharedInitializerInterceptors.add(0, interceptor)
        spec.addCleanupSpecInterceptor(interceptor)
    }
}
