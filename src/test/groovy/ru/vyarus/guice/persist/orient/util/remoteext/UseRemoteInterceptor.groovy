package ru.vyarus.guice.persist.orient.util.remoteext

import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import ru.vyarus.guice.persist.orient.util.ServerRule

/**
 * @author Vyacheslav Rusakov 
 * @since 03.05.2015
 */
class UseRemoteInterceptor extends AbstractMethodInterceptor {

    ServerRule serverRule = new ServerRule()

    @Override
    void interceptSharedInitializerMethod(IMethodInvocation invocation) throws Throwable {
        // important for guice to use correct urls
        ServerRule.setRemoteConf('_' + invocation.spec.name.replace(' ', '_'))
        invocation.proceed()
    }

    @Override
    void interceptSetupSpecMethod(IMethodInvocation invocation) throws Throwable {
        ServerRule.setRemoteConf('_' + invocation.spec.name.replace(' ', '_'))
        serverRule.startServer()
        invocation.proceed();
    }

    @Override
    void interceptCleanupSpecMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed()
        serverRule.stopServer()
        sleep(100)
    }

    @Override
    void interceptSetupMethod(IMethodInvocation invocation) throws Throwable {
        invocation.proceed()
    }


}
