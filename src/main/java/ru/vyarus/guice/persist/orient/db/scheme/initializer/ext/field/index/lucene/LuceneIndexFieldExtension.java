package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.orientechnologies.lucene.OLuceneIndexFactory;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.IndexValidationSupport;

import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link LuceneIndex} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 14.06.2015
 */
@Singleton
public class LuceneIndexFieldExtension implements FieldExtension<LuceneIndex> {
    public static final String ANALYZER = "analyzer";
    private final Logger logger = LoggerFactory.getLogger(LuceneIndexFieldExtension.class);

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final Field field, final LuceneIndex annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final Field field, final LuceneIndex annotation) {
        final String property = field.getName();
        final String model = descriptor.schemeClass;
        final String name = Objects.firstNonNull(Strings.emptyToNull(annotation.name().trim()), model + '.' + property);
        final OClass clazz = db.getMetadata().getSchema().getClass(model);
        final OIndex<?> classIndex = clazz.getClassIndex(name);
        final OClass.INDEX_TYPE type = OClass.INDEX_TYPE.FULLTEXT;
        if (!descriptor.initialRegistration && classIndex != null) {
            final IndexValidationSupport support = new IndexValidationSupport(classIndex, logger);

            support.checkTypeCompatible(type);
            support.checkFieldsCompatible(property);


            final boolean correct = support
                    .isIndexSigns(classIndex.getConfiguration().field("algorithm"), getAnalyzer(classIndex))
                    .matchRequiredSigns(type, OLuceneIndexFactory.LUCENE_ALGORITHM, annotation.value().getName());
            if (!correct) {
                support.dropIndex(db);
            } else {
                // index ok
                return;
            }
        }
        final ODocument metadata = createMetadata(annotation);
        clazz.createIndex(name, type.name(), null, metadata, OLuceneIndexFactory.LUCENE_ALGORITHM,
                new String[]{property});
        logger.info("Lucene fulltext index '{}' ({} [{}]) created", name, model, property);
    }

    private ODocument createMetadata(final LuceneIndex annotation) {
        final ODocument metadata = new ODocument();
        metadata.field(ANALYZER, annotation.value().getName());
        return metadata;
    }

    private String getAnalyzer(final OIndex classIndex) {
        // analyzer is stored only in metadata and there is no way to get default analyzer.. just assume it
        final ODocument metadata = classIndex.getMetadata();
        final String analyzer = metadata != null ? metadata.<String>field(ANALYZER) : null;
        return Objects.firstNonNull(analyzer, StandardAnalyzer.class.getName());
    }
}
