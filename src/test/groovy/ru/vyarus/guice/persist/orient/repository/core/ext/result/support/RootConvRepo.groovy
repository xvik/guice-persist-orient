package ru.vyarus.guice.persist.orient.repository.core.ext.result.support

import ru.vyarus.guice.persist.orient.repository.core.ext.result.support.ext.DummyConverter

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@DummyConverter("root")
interface RootConvRepo extends ConvRepo, ConvRepo2{

}