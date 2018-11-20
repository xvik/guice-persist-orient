package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link CompositeLuceneIndex.List} scheme model type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 20.06.2015
 */
@Singleton
public class MultipleLuceneIndexesTypeExtension implements TypeExtension<CompositeLuceneIndex.List> {

    private final LuceneIndexTypeExtension extension;

    @Inject
    public MultipleLuceneIndexesTypeExtension(final LuceneIndexTypeExtension extension) {
        this.extension = extension;
    }

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final CompositeLuceneIndex.List annotation) {
        for (CompositeLuceneIndex index : annotation.value()) {
            extension.beforeRegistration(db, descriptor, index);
        }
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final CompositeLuceneIndex.List annotation) {
        for (CompositeLuceneIndex index : annotation.value()) {
            extension.afterRegistration(db, descriptor, index);
        }
    }
}
