package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene

import com.orientechnologies.lucene.OLuceneIndexPlugin
import ru.vyarus.guice.persist.orient.util.remoteext.UseRemote
import spock.lang.Ignore

/**
 * @author Vyacheslav Rusakov 
 * @since 20.06.2015
 */
@Ignore // https://github.com/orientechnologies/orientdb/issues/3863
@UseRemote
class CompositeLuceneIndexesRemoteTest extends CompositeLuceneIndexTest {

    @Override
    void setup() {
        new OLuceneIndexPlugin().startup()
    }
}