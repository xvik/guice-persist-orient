package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene

import org.apache.lucene.analysis.en.EnglishAnalyzer

/**
 * @author Vyacheslav Rusakov 
 * @since 16.06.2015
 */
class LuceneIndexModel {

    @LuceneIndex
    String defaults

    @LuceneIndex(EnglishAnalyzer)
    String custom
}
