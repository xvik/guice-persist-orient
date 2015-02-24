package ru.vyarus.guice.persist.orient.repository.delegate.support

import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 22.10.2014
 */
public interface Repository extends Mixin<Model>, ComplexRepository<Model> {

    void countIndirect();

    void addIndirect(int a, int b);

    void mergeIndirect(Model model, int a);
}