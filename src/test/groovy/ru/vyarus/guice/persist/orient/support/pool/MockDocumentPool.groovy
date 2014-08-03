package ru.vyarus.guice.persist.orient.support.pool

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import ru.vyarus.guice.persist.orient.db.DbType

/**
 * @author Vyacheslav Rusakov 
 * @since 01.08.2014
 */
class MockDocumentPool extends AbstractMockPool<ODatabaseDocumentTx>{

    @Override
    DbType getType() {
        return DbType.DOCUMENT
    }
}
