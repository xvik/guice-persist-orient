package ru.vyarus.guice.persist.orient.db.pool.support.pool

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.db.DbType

/**
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
class MockObjectPool extends AbstractMockPool<OObjectDatabaseTx>{

    @Override
    DbType getType() {
        return DbType.OBJECT
    }
}
