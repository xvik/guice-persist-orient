package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.fulltext

/**
 * @author Vyacheslav Rusakov 
 * @since 15.06.2015
 */
class FulltextIndexModel {

    @FulltextIndex
    String defaults

    @FulltextIndex(name = "all_options",
            indexRadix = false,
            ignoreChars = "'",
            separatorChars = "!?",
            minWordLength = 5,
            stopWords = ["of", "the"])
    String options
}
