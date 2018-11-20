package ru.vyarus.guice.persist.orient.db.pool.support.pool

import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import ru.vyarus.guice.persist.orient.db.DbType

/**
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
class MockDocumentPool extends AbstractMockPool<ODatabaseDocument>{

    @Override
    DbType getType() {
        return DbType.DOCUMENT
    }
}
