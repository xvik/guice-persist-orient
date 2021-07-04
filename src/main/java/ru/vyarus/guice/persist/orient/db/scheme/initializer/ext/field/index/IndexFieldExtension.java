package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;

import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link Index} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class IndexFieldExtension implements FieldExtension<Index> {
    private final Logger logger = LoggerFactory.getLogger(IndexFieldExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Field field, final Index annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Field field, final Index annotation) {
        final String property = field.getName();
        final String model = descriptor.schemeClass;
        final String name = MoreObjects.firstNonNull(
                Strings.emptyToNull(annotation.name().trim()), model + '.' + property);
        final OClass clazz = db.getMetadata().getSchema().getClass(model);
        final OIndex classIndex = clazz.getClassIndex(name);
        final OClass.INDEX_TYPE type = annotation.value();
        if (!descriptor.initialRegistration && classIndex != null) {
            final IndexValidationSupport support = new IndexValidationSupport(classIndex, logger);

            support.checkFieldsCompatible(property);

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
        clazz.createIndex(name, type.name(), null, metadata, new String[]{property});
        logger.info("Index '{}' ({} [{}]) {} created", name, model, property, type);
    }
}
