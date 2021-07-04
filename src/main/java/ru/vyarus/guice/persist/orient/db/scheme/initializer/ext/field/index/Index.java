package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model field extension to create index on field. If index name not set, className.fieldName will be used.
 * If index with provided name exist, but with different type, index will be re-created.
 * If existing index build on different properties, error will be thrown.
 * <p>
 * Use {@link Index.List} to define more than one index.
 * <p>
 * Annotation doesn't cover all possible index options. Use specific annotations for advanced index types:
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.fulltext.FulltextIndex} and
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene.LuceneIndex}.
 * <p>
 * To make index
 * <a href="https://orientdb.org/docs/3.1.x/indexing/Indexes.html#case-insensitive-matching-with-indexes">
 * case insensitive</a> use {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci.CaseInsensitive}
 * annotation on field (in orient index ci and property ci flags are the same).
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/indexing/Indexes.html">docs</a>
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Create-Index.html">create index doc</a>
 * @since 09.03.2015
 */
@Target(FIELD)
@Retention(RUNTIME)
@SchemeFieldInit(IndexFieldExtension.class)
public @interface Index {

    /**
     * @return index type
     */
    OClass.INDEX_TYPE value();

    /**
     * @return index name (if not set className.fieldName used)
     */
    String name() default "";

    /**
     * @return false to allow null values, true (by default) to ignore null values
     * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Create-Index.html">docs</a>
     */
    boolean ignoreNullValues() default true;

    /**
     * Scheme model field extension to group multiple index definitions.
     * <p>
     * Don't forget to assign different names, because default names will be the same.
     *
     * @author Vyacheslav Rusakov
     * @since 09.03.2015
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    @SchemeFieldInit(MultipleIndexesFiledExtension.class)
    @interface List {

        /**
         * @return index definitions
         */
        Index[] value();
    }
}
