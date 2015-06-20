package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene

import org.apache.lucene.analysis.en.EnglishAnalyzer

/**
 * @author Vyacheslav Rusakov 
 * @since 20.06.2015
 */
@CompositeLuceneIndex.List([
        @CompositeLuceneIndex(name = "test1", fields = ["foo", "bar"]),
        @CompositeLuceneIndex(name = "test2", fields = ["foo", "bar"], analyzer = EnglishAnalyzer)
])
class MultipleLuceneIndexesModel {
    String foo
    String bar
}
