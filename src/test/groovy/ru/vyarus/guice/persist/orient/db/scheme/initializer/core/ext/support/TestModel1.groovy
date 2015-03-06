package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.FieldExt1
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.FieldExt2
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.FieldExt3
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.TypeExt1
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.TypeExt2
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support.ext.TypeExt3

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
@TypeExt1
@TypeExt2
@TypeExt3
class TestModel1 {

    @FieldExt1
    @FieldExt2
    @FieldExt3
    String foo

    String bar
}
