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
 * <p>Defaults taken from implementation class {@link com.orientechnologies.orient.core.index.OIndexFullText}.</p>
 * <p>To make index
 * <a href="http://orientdb.com/docs/last/orientdb.wiki/Indexes.html#case-insensitive-match">case insensitive</a>
 * use {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci.CaseInsensitive} annotation
 * on field (in orient index ci and property ci flags are the same).</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://orientdb.com/docs/last/orientdb.wiki/FullTextIndex.html">docs</a>
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
    String separatorChars() default " \\r\\n\\t:;,.|+*/\\\\=!?[]()";

    /**
     * @return minimum word length to index
     */
    int minWordLength() default 3;

    /**
     * @return stop words excluded from indexing
     */
    String[] stopWords() default {"the", "in", "a", "at", "as", "and", "or", "for", "his", "her", "him",
            "this", "that", "what", "which", "while", "up", "with", "be", "was", "were", "is"};

    /**
     * @return true for hash index, otherwise sb-tree will be used (default)
     * @see com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE#FULLTEXT
     * @see com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE#FULLTEXT_HASH_INDEX
     */
    boolean useHashIndex() default false;
}
