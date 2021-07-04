package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to create lucene fulltext index on field. If index name not set, className.fieldName
 * will be used.
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
 * @since 09.06.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(LuceneIndexFieldExtension.class)
public @interface LuceneIndex {

    /**
     * @return index name (if not set className.fieldName used)
     */
    String name() default "";

    /**
     * @return analyzer implementation to use
     */
    Class<? extends Analyzer> value() default StandardAnalyzer.class;
}
