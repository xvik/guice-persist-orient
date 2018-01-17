package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.fulltext

import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.sql.OCommandSQL
import com.orientechnologies.orient.core.sql.OCommandSQLParsingException
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.AbstractSchemeExtensionTest

/**
 * Special test which will fail when https://github.com/orientechnologies/orientdb/issues/4837
 * blocking issue will be fixed. After that remove test and enable (unignore) FulltextIndexRemoteTest
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2016
 */
class RecoveryDetector extends AbstractSchemeExtensionTest {

    @Override
    String getModelPackage() {
        return "ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.fulltext"
    }

    def "Fix detector"() {
        when:
        def clazz = db.getMetadata().getSchema().createClass("Sample")
        clazz.createProperty("name", OType.STRING)
        db.getUnderlying().command(new OCommandSQL("insert into Sample set name = \"\rbla\"")).execute()
        then:
        thrown(OCommandSQLParsingException)
    }
}