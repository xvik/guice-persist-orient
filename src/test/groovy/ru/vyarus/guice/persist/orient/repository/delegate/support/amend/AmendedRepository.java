package ru.vyarus.guice.persist.orient.repository.delegate.support.amend;

import ru.vyarus.guice.persist.orient.repository.delegate.Delegate;
import ru.vyarus.guice.persist.orient.repository.delegate.support.amend.ext.DummyAmend;

/**
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
@DummyAmend("type")
@Delegate(AmendedDelegate.class)
public interface AmendedRepository {
    @DummyAmend("method")
    void select1();

    void select2();
}
