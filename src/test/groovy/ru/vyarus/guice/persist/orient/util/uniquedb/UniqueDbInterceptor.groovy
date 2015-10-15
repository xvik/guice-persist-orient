package ru.vyarus.guice.persist.orient.util.uniquedb

import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import ru.vyarus.guice.persist.orient.support.Config

/**
 * @author Vyacheslav Rusakov 
 * @since 11.10.2015
 */
class UniqueDbInterceptor extends AbstractMethodInterceptor {

    @Override
    void interceptSharedInitializerMethod(IMethodInvocation invocation) throws Throwable {
        if (invocation.spec.tags.find({ it.name == "memory" })) {
            Config.DB = "memory:${invocation.spec.name}"
        }
        invocation.proceed()
    }

    @Override
    void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed()
        if (invocation.spec.tags.find({ it.name == "memory" })) {
            Config.DB = "memory:test"
        }
    }
}
