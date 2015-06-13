package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scheme model type extension to create index on multiple fields.
 * If index with provided name exist, but with different type, index will be re-created.
 * If existing index build on different properties, error will be thrown.
 * <p>Use {@link CompositeIndex.List} to define more than one index.</p>
 * <p>Annotation doesn't cover all possible index options.</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/Indexes.html">docs</a>
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Create-Index.html">create index doc</a>
 * @since 09.03.2015
 */
@Target(TYPE)
@Retention(RUNTIME)
@SchemeTypeInit(IndexTypeExtension.class)
public @interface CompositeIndex {

    /**
     * @return index name
     */
    String name();

    /**
     * @return index type
     */
    OClass.INDEX_TYPE type();

    /**
     * @return index fields
     */
    String[] fields();

    /**
     * @return false to allow null values, true (by default) to ignore null values
     * @see <a href="http://orientdb.com/docs/last/orientdb.wiki/SQL-Create-Index.html#null-values">docs</a>
     */
    boolean ignoreNullValues() default true;

    /**
     * Scheme model type extension to group multiply composite index definitions.
     *
     * @author Vyacheslav Rusakov
     * @since 09.03.2015
     */
    @Target(TYPE)
    @Retention(RUNTIME)
    @SchemeTypeInit(MultipleIndexesTypeExtension.class)
    public @interface List {
        /**
         * @return index definitions
         */
        CompositeIndex[] value();
    }
}
