package ru.vyarus.guice.persist.orient.repository.delegate.support.amend

import ru.vyarus.guice.persist.orient.repository.delegate.Delegate

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@Delegate(AmendedDelegate)
interface AmendRepo2 {

    void select3();
}
