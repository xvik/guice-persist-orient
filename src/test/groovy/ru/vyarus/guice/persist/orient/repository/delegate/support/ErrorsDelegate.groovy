package ru.vyarus.guice.persist.orient.repository.delegate.support

import ru.vyarus.guice.persist.orient.repository.delegate.ext.db.Connection
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic
import ru.vyarus.guice.persist.orient.repository.delegate.ext.instance.Repository
import ru.vyarus.guice.persist.orient.repository.mixin.support.CustomMixin

/**
 * @author Vyacheslav Rusakov 
 * @since 25.10.2014
 */
class ErrorsDelegate {

    // duplicate generic declaration
    void someth(@Generic("T") Class t, @Generic("T") Class p) {
    }

    // unknown generic name
    void someth2(@Generic("P") Class t) {
    }

    // bad type for generic
    void someth3(@Generic("T") Object t) {
    }

    // duplicate instance declaration
    void someth4(@Repository Object inst, @Repository Object inst2) {
    }

    // incompatible repository instance
    void someth5(@Repository CustomMixin inst) {
    }

    // duplicate connection param
    void someth6(@Connection Object inst, @Connection Object inst2) {
    }
}
