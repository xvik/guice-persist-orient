package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.IndexValidationSupport;

import javax.inject.Singleton;

/**
 * {@link CompositeIndex} scheme model type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class IndexTypeExtension implements TypeExtension<CompositeIndex> {
    private final Logger logger = LoggerFactory.getLogger(IndexTypeExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final CompositeIndex annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final CompositeIndex annotation) {
        // single field index definition intentionally allowed (no check)
        final String name = Strings.emptyToNull(annotation.name().trim());
        Preconditions.checkArgument(name != null, "Index name required");
        final String model = descriptor.schemeClass;
        final OClass clazz = db.getMetadata().getSchema().getClass(model);
        final OIndex classIndex = clazz.getClassIndex(name);
        final OClass.INDEX_TYPE type = annotation.type();
        final String[] fields = annotation.fields();
        if (!descriptor.initialRegistration && classIndex != null) {
            final IndexValidationSupport support = new IndexValidationSupport(classIndex, logger);

            support.checkFieldsCompatible(fields);

            final boolean correct = support
                    .isIndexSigns(classIndex.getDefinition().isNullValuesIgnored())
                    .matchRequiredSigns(type, annotation.ignoreNullValues());
            if (!correct) {
                support.dropIndex(db);
            } else {
                // index ok
                return;
            }
        }
        final ODocument metadata = new ODocument()
                .field("ignoreNullValues", annotation.ignoreNullValues());
        clazz.createIndex(name, type.name(), null, metadata, fields);
        logger.info("Index '{}' ({} [{}]) {} created", name, model, Joiner.on(',').join(fields), type);
    }
}
