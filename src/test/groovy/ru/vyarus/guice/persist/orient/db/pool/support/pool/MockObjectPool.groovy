package ru.vyarus.guice.persist.orient.db.pool.support.pool

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import ru.vyarus.guice.persist.orient.db.DbType

/**
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
class MockObjectPool extends AbstractMockPool<ODatabaseObject>{

    @Override
    DbType getType() {
        return DbType.OBJECT
    }
}
