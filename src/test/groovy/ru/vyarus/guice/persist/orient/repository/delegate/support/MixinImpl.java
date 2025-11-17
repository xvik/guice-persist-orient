package ru.vyarus.guice.persist.orient.repository.delegate.support;

import ru.vyarus.guice.persist.orient.support.model.Model;

/**
 * @author Vyacheslav Rusakov
 * @since 22.10.2014
 */
public class MixinImpl implements Mixin<Model> {
    @Override
    public void count() {
    }

    @Override
    public void add(int a, int b) {
    }

    @Override
    public void merge(Model model, int a) {
    }

}
