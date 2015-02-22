package ru.vyarus.guice.persist.orient.repository.core.ext.support

import com.google.common.collect.Lists
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
class MixinDelegate {

    List<Model> selectSometh() {
        return Lists.newArrayList()
    }
}
