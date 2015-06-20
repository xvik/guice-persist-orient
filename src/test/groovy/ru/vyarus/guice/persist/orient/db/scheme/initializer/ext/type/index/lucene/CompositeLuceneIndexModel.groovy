package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene

import org.apache.lucene.analysis.en.EnglishAnalyzer

/**
 * @author Vyacheslav Rusakov 
 * @since 20.06.2015
 */
@CompositeLuceneIndex(name = "test", fields = ["foo", "bar"], analyzer = EnglishAnalyzer)
class CompositeLuceneIndexModel {
    String foo
    String bar
}
