package ru.vyarus.guice.persist.orient.support.finder.inheritance

import com.google.inject.persist.finder.Finder
import ru.vyarus.guice.persist.orient.finder.placeholder.Placeholder

/**
 * @author Vyacheslav Rusakov 
 * @since 18.10.2014
 */
class BaseBeanFinder<K> extends Lvl2BaseBeanFinder<K> {

    @Finder(query = 'select from ${K} where ${field} = ?')
    public List<K> findByField(@Placeholder("field") String field, Object value) {
        throw new UnsupportedOperationException()
    }

    @Finder(query = 'select from ${K}')
    public K[] selectCustom() {
        throw new UnsupportedOperationException()
    }
}
