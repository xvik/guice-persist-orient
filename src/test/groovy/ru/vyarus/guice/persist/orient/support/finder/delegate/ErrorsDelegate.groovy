package ru.vyarus.guice.persist.orient.support.finder.delegate

import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderDb
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderGeneric
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderInstance
import ru.vyarus.guice.persist.orient.support.finder.CustomMixin

/**
 * @author Vyacheslav Rusakov 
 * @since 25.10.2014
 */
class ErrorsDelegate {

    // duplicate generic declaration
    void someth(@FinderGeneric("T") Class t, @FinderGeneric("T") Class p) {
    }

    // unknown generic name
    void someth2(@FinderGeneric("P") Class t) {
    }

    // bad type for generic
    void someth3(@FinderGeneric("T") Object t) {
    }

    // duplicate instance declaration
    void someth4(@FinderInstance Object inst, @FinderInstance Object inst2) {
    }

    // incompatible finder instance
    void someth5(@FinderInstance CustomMixin inst) {
    }

    // duplicate connection param
    void someth6(@FinderDb Object inst, @FinderDb Object inst2) {
    }
}
