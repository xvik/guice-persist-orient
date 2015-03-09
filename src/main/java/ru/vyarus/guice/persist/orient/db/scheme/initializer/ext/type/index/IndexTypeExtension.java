package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;

import javax.inject.Singleton;

/**
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class IndexTypeExtension implements TypeExtension<CompositeIndex> {
    private final Logger logger = LoggerFactory.getLogger(IndexTypeExtension.class);

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final CompositeIndex annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final CompositeIndex annotation) {
        // single field index definition intentionally allowed (no check)
        final String model = descriptor.modelClass.getSimpleName();
        final String name = Strings.emptyToNull(annotation.name().trim());
        Preconditions.checkArgument(name != null, "Index name required");
        final OClass clazz = db.getMetadata().getSchema().getClass(model);
        final OIndex<?> classIndex = clazz.getClassIndex(name);
        final OClass.INDEX_TYPE type = annotation.type();
        if (!descriptor.initialRegistration && classIndex != null) {
            if (!classIndex.getType().equalsIgnoreCase(type.toString())) {
                logger.debug("Dropping current index {}, because of type mismatch: {}, when required {}",
                        name, classIndex.getType(), type);
                db.getMetadata().getIndexManager().dropIndex(name);
            } else {
                // index ok
                return;
            }
        }
        clazz.createIndex(name, type, annotation.fields());
        if (logger.isDebugEnabled()) {
            logger.debug("Index {} ({}) {} created", name, Joiner.on(',').join(annotation.fields()), type);
        }
    }
}
