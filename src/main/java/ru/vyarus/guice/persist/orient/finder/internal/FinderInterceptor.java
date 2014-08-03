package ru.vyarus.guice.persist.orient.finder.internal;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author Vyacheslav Rusakov
 * @since 03.08.2014
 */
public class FinderInterceptor implements MethodInterceptor {

    FinderInvocationHandler invocationHandler;

    public FinderInterceptor(FinderInvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return invocationHandler.invoke(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
    }
}
