package ru.vyarus.guice.persist.orient.repository.core.ext.support.result

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@DummyConverter("root")
interface RootConvRepo extends ConvRepo, ConvRepo2{

}