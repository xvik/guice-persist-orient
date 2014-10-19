package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 18.10.2014
 */
@Transactional
class BeanPowerFinder extends BaseBeanFinder<Model> {
}
