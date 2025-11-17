package ru.vyarus.guice.persist.orient.repository.delegate.support

import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 22.10.2014
 */
class MixinImpl implements Mixin<Model> {
    @Override
    void count() {
    }

    @Override
    void add(int a, int b) {
    }

    @Override
    void merge(Model model, int a) {
    }
}
