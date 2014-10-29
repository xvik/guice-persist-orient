package ru.vyarus.guice.persist.orient.support.finder.delegate

import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderGeneric
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderInstance

/**
 * @author Vyacheslav Rusakov 
 * @since 25.10.2014
 */
class ComplexFinderDelegate {

    void someth() {
    }

    void someth(@FinderGeneric("T") Class t, @FinderInstance ComplexFinder finder) {
    }

    // additional parameters placement not important (only base params order important)
    void add(int a, @FinderGeneric("T") Class t, int b) {
    }
}
