package ru.vyarus.guice.persist.orient.support.finder.delegate

import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 22.10.2014
 */
public interface Finder extends Mixin<Model>, ComplexFinder<Model>, Errors {

    void countIndirect();

    void addIndirect(int a, int b);

    void mergeIndirect(Model model, int a);
}