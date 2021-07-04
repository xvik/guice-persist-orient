package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model type extension to create composite lucene fulltext index on fields.
 * <p>
 * In order to use this index you will need to add
 * <a href="https://github.com/orientechnologies/orientdb-lucene/wiki">orient-lucene</a> dependency.
 * <p>
 * If index with the same name exists but created on different properties error will be thrown.
 * If existing index is not lucene, error will be thrown. Otherwise existing index will be dropped and new
 * one created.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/indexing/Full-Text-Index.html">docs</a>
 * @since 20.06.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(LuceneIndexTypeExtension.class)
public @interface CompositeLuceneIndex {
    /**
     * @return index name
     */
    String name();

    /**
     * @return index fields
     */
    String[] fields();

    /**
     * @return analyzer implementation to use
     */
    Class<? extends Analyzer> analyzer() default StandardAnalyzer.class;

    /**
     * Scheme model type extension to group multiple composite lucene index definitions.
     *
     * @author Vyacheslav Rusakov
     * @since 20.06.2015
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    @SchemeTypeInit(MultipleLuceneIndexesTypeExtension.class)
    @interface List {
        /**
         * @return lucene index definitions
         */
        CompositeLuceneIndex[] value();
    }
}
