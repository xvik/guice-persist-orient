package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import com.google.inject.Injector;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Used to convert single result (1 entity) using default result converter mechanism.
 * This is required to apply conversion for orient listeners (async query or live query).
 * Service may be used directly (by injection).
 * <p>
 * Important difference with method result converter: this converter will also try to convert resulted document
 * into object model class or vertex/edge. Default converter is applied only if object or graph type conversion is
 * not required (then default converter will check if projection to simple value is required).
 * <p>
 * Performs type conversion only from ODocument! It is not intended to be a universal converter for all types, only
 * for document (raw type). For example, you can't expect it to convert Vertex to ODocument or Model to Vertex.
 *
 * @author Vyacheslav Rusakov
 * @since 03.10.2017
 */
@Singleton
public class PlainResultConverter {

    private final ResultConverter defaultConverter;
    private final DatabaseManager databaseManager;
    private final TransactionManager transactionManager;
    private final Injector injector;

    @Inject
    public PlainResultConverter(final ResultConverter defaultConverter,
                                final DatabaseManager databaseManager,
                                final TransactionManager transactionManager,
                                final Injector injector) {
        this.defaultConverter = defaultConverter;
        this.databaseManager = databaseManager;
        this.transactionManager = transactionManager;
        this.injector = injector;
    }

    public <T> T convert(final Object result, final Class<T> targetType) {
        return convert(result, targetType, defaultConverter);
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(final Object result, final Class<T> targetType, final ResultConverter converter) {
        // do manual conversion to other types if required (usually this will be done automatically by connection
        // object, but async and live callbacks will always return documents)
        T res = tryConversion(result, targetType);
        if (res != null) {
            return res;
        }

        // use converter
        final ResultDescriptor desc = new ResultDescriptor();
        // support void case for more universal usage
        desc.returnType = targetType.equals(Void.class) || targetType.equals(void.class)
                ? ResultType.VOID : ResultType.PLAIN;
        desc.entityType = targetType;
        desc.expectType = targetType;
        return (T) converter.convert(desc, result);
    }

    private <T> T tryConversion(final Object result, final Class<T> targetType) {
        if (result == null
                // no need for conversion of raw types
                || ORecord.class.isAssignableFrom(targetType)
                // without ongoing transaction we will have to open new connections for conversion (waste of resources)
                || !transactionManager.isTransactionActive()
                // only document type could be converted
                || !(result instanceof ODocument)) {
            return null;
        }

        final ODocument doc = (ODocument) result;
        final Set<DbType> supportedTypes = databaseManager.getSupportedTypes();
        T res = null;
        // try object conversion (if supported)
        if (supportedTypes.contains(DbType.OBJECT)) {
            res = tryObjectConversion(doc, targetType);
        }
        // try graph conversion (if supported)
        if (res == null && supportedTypes.contains(DbType.GRAPH)) {
            res = tryGraphConversion(doc, targetType);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private <T> T tryObjectConversion(final ODocument doc, final Class<T> targetType) {
        // Possible case: custom result converter required. For example, targetType may be even mapped object
        // class but returned document does not represent this class (assumed manual conversion)
        if (doc.getClassName().equals(targetType.getSimpleName())) {
            final OObjectDatabaseTx db = injector.getInstance(OObjectDatabaseTx.class);
            if (db.getEntityManager().getRegisteredEntities().contains(targetType)) {
                final T pojo = db.newInstance(targetType);
                return (T) db.stream2pojo(doc, pojo, null, true);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T tryGraphConversion(final ODocument doc, final Class<T> targetType) {
        T res = null;
        final boolean requireVertex = Vertex.class.isAssignableFrom(targetType);
        if (requireVertex || Edge.class.isAssignableFrom(targetType)) {
            final OrientBaseGraph db = injector.getInstance(OrientBaseGraph.class);
            // explicitly check object compatibility to avoid wrong conversions (allowed by orient)
            if (requireVertex && doc.getSchemaClass().isSubClassOf("V")) {
                res = (T) db.getVertex(doc);
            } else if (doc.getSchemaClass().isSubClassOf("E")) {
                res = (T) db.getEdge(doc);
            }
        }
        return res;
    }
}
