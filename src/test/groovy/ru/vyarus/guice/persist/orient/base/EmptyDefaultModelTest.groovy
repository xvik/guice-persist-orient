package ru.vyarus.guice.persist.orient.base

import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.base.modules.EmptySimpleModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(EmptySimpleModule.class)
class EmptyDefaultModelTest extends AbstractTest{

    def "Check empty model"(){
        expect: "no errors - no model is not error"
        true
    }
}
