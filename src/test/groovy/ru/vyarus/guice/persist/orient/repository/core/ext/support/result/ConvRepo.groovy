package ru.vyarus.guice.persist.orient.repository.core.ext.support.result
/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@DummyConverter("type")
interface ConvRepo {

    @DummyConverter("method")
    void select1()

    void select2()

    // two converters not allowed
    @DummyConverter('dsfd')
    @DummyConverter2
    void illegal()
}