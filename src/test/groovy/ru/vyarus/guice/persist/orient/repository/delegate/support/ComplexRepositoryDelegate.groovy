package ru.vyarus.guice.persist.orient.repository.delegate.support

import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic
import ru.vyarus.guice.persist.orient.repository.delegate.ext.instance.Repository

/**
 * @author Vyacheslav Rusakov 
 * @since 25.10.2014
 */
class ComplexRepositoryDelegate {

    void someth() {
    }

    void someth(@Generic("T") Class t, @Repository ComplexRepository repository) {
    }

    // additional parameters placement not important (only base params order important)
    void add(int a, @Generic("T") Class t, int b) {
    }
}
