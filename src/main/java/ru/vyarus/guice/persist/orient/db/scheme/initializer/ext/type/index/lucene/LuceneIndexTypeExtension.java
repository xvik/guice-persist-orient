package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.lucene;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
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
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.IndexValidationSupport;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.lucene.LuceneIndexFieldExtension;

import javax.inject.Singleton;

/**
 * {@link CompositeLuceneIndex} scheme model type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 20.06.2015
 */
@Singleton
public class LuceneIndexTypeExtension implements TypeExtension<CompositeLuceneIndex> {
    private final Logger logger = LoggerFactory.getLogger(LuceneIndexTypeExtension.class);

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final CompositeLuceneIndex annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final CompositeLuceneIndex annotation) {
        final String name = Strings.emptyToNull(annotation.name().trim());
        Preconditions.checkArgument(name != null, "Index name required");
        final String model = descriptor.schemeClass;
        final OClass clazz = db.getMetadata().getSchema().getClass(model);
        final OIndex<?> classIndex = clazz.getClassIndex(name);
        final OClass.INDEX_TYPE type = OClass.INDEX_TYPE.FULLTEXT;
        final String[] fields = annotation.fields();
        if (!descriptor.initialRegistration && classIndex != null) {
            final IndexValidationSupport support = new IndexValidationSupport(classIndex, logger);

            support.checkTypeCompatible(type);
            support.checkFieldsCompatible(fields);


            final boolean correct = support
                    .isIndexSigns(classIndex.getConfiguration().field("algorithm"), getAnalyzer(classIndex))
                    .matchRequiredSigns(type, OLuceneIndexFactory.LUCENE_ALGORITHM, annotation.analyzer().getName());
            if (!correct) {
                support.dropIndex(db);
            } else {
                // index ok
                return;
            }
        }
        final ODocument metadata = createMetadata(annotation);
        clazz.createIndex(name, type.name(), null, metadata, OLuceneIndexFactory.LUCENE_ALGORITHM, fields);
        logger.info("Lucene fulltext index '{}' ({} [{}]) created", name, model, Joiner.on(',').join(fields));
    }

    private ODocument createMetadata(final CompositeLuceneIndex annotation) {
        final ODocument metadata = new ODocument();
        metadata.field(LuceneIndexFieldExtension.ANALYZER, annotation.analyzer().getName());
        return metadata;
    }

    private String getAnalyzer(final OIndex classIndex) {
        // analyzer is stored only in metadata and there is no way to get default analyzer.. just assume it
        final ODocument metadata = classIndex.getMetadata();
        final String analyzer = metadata != null ? metadata.<String>field(LuceneIndexFieldExtension.ANALYZER) : null;
        return Objects.firstNonNull(analyzer, StandardAnalyzer.class.getName());
    }
}
