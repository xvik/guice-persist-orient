package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.fulltext;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to create fulltext index on field. If index name not set, className.fieldName
 * will be used. In contrast to {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.Index}
 * it allows to configure fulltext specific options.
 * <p>
 * Defaults taken from implementation class {@link com.orientechnologies.orient.core.index.OIndexFullText}.
 * <p>
 * If index with the same name exists but created on different properties error will be thrown.
 * If existing index is not fulltext, error will be thrown. Otherwise existing index will be dropped and new
 * one created.
 * <p>
 * To make index
 * <a href="https://orientdb.org/docs/3.1.x/indexing/Indexes.html#case-insensitive-matching-with-indexes">case
 * insensitive</a> use {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci.CaseInsensitive}
 * annotation on field (in orient index ci and property ci flags are the same).
 * <p>
 * Composite fulltext index is not supported in orient. Use
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene.CompositeLuceneIndex} to
 * create composite fulltext index.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/indexing/FullTextIndex.html">docs</a>
 * @since 09.06.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(FulltextIndexFieldExtension.class)
public @interface FulltextIndex {

    /**
     * @return index name (if not set className.fieldName used)
     */
    String name() default "";

    /**
     * @return true to index word prefixes (default), false to ignore prefixes
     */
    boolean indexRadix() default true;

    /**
     * @return chars to skip when indexing
     */
    String ignoreChars() default "'\"";

    /**
     * @return separator characters
     */
    String separatorChars() default " \r\n\t:;,.|+*/\\=!?[]()";

    /**
     * @return minimum word length to index
     */
    int minWordLength() default 3;

    /**
     * @return stop words excluded from indexing
     */
    String[] stopWords() default {"the", "in", "a", "at", "as", "and", "or", "for", "his", "her", "him",
            "this", "that", "what", "which", "while", "up", "with", "be", "was", "were", "is"};
}
