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
 * <p>Use {@link Index.List} to define more than one index.</p>
 * <p>Annotation doesn't cover all possible index options.</p>
 *
 * @author Vyacheslav Rusakov
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/Indexes.html">docs</a>
 * @see <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/SQL-Create-Index.html">create index doc</a>
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
     * Scheme model field extension to group multiply index definitions.
     * <p>Don't forget to assign different names, because default names will be the same.</p>
     *
     * @author Vyacheslav Rusakov
     * @since 09.03.2015
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    @SchemeFieldInit(MultipleIndexesFiledExtension.class)
    public @interface List {

        /**
         * @return index definitions
         */
        Index[] value();
    }
}
