package ru.vyarus.guice.persist.orient.repository.delegate.support.amend

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.delegate.support.amend.ext.DummyAmend

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
@DummyAmend("root")
interface RootAmendRepo extends AmendedRepository, AmendRepo2 {
}
