package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils;

import javax.inject.Singleton;
import java.util.HashSet;

import static ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException.check;

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
        final String name = Strings.emptyToNull(annotation.name().trim());
        Preconditions.checkArgument(name != null, "Index name required");
        final OClass clazz = db.getMetadata().getSchema().getClass(descriptor.schemeClass);
        final OIndex<?> classIndex = clazz.getClassIndex(name);
        final OClass.INDEX_TYPE type = annotation.type();
        final String[] fields = annotation.fields();
        if (!descriptor.initialRegistration && classIndex != null) {
            final HashSet<String> indexFields = Sets.newHashSet(classIndex.getDefinition().getFields());
            check(indexFields.equals(Sets.newHashSet(fields)),
                    "Existing index '%s' fields '%s' are different from '%s'.",
                    name, Joiner.on(',').join(indexFields), Joiner.on(',').join(fields));
            if (!classIndex.getType().equalsIgnoreCase(type.toString())) {
                logger.debug("Dropping current index {}, because of type mismatch: {}, when required {}",
                        name, classIndex.getType(), type);
                SchemeUtils.dropIndex(db, name);
            } else if (classIndex.getDefinition().isNullValuesIgnored() != annotation.ignoreNullValues()) {
                logger.debug("Dropping current index {}, because of ignore nulls setting mismatch: current {}",
                        name, classIndex.getDefinition().isNullValuesIgnored());
                SchemeUtils.dropIndex(db, name);
            } else {
                // index ok
                return;
            }
        }
        clazz.createIndex(name, type, fields)
                .getDefinition().setNullValuesIgnored(annotation.ignoreNullValues());
        logger.debug("Index {} ({}) {} created", name, Joiner.on(',').join(fields), type);
    }
}
