package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import com.google.common.primitives.Primitives;
import com.google.inject.Injector;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ResultUtils;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * Used to convert single record (1 entity) using default result converter mechanism.
 * This is required to apply conversion for orient listeners (async query or live query).
 * Service may be used directly (by injection).
 * <p>
 * Important difference with method result converter: this converter will also try to convert resulted document
 * into object model class or vertex/edge (only if there are underlying transaction in thread(!) to avoid additional
 * connections opening). Default converter is applied only if object or graph type conversion is not required
 * (then default converter will check if projection to simple value is required).
 *
 * @author Vyacheslav Rusakov
 * @see ResultConverter
 * @since 03.10.2017
 */
@Singleton
public class RecordConverter {

    private final ResultConverter defaultResultConverter;
    private final DatabaseManager databaseManager;
    private final TransactionManager transactionManager;
    private final Injector injector;

    @Inject
    public RecordConverter(final ResultConverter defaultResultConverter,
                           final DatabaseManager databaseManager,
                           final TransactionManager transactionManager,
                           final Injector injector) {
        this.defaultResultConverter = defaultResultConverter;
        this.databaseManager = databaseManager;
        this.transactionManager = transactionManager;
        this.injector = injector;
    }

    /**
     * Could convert document to object or graph (vertex/edge) object if there is an underlying connection
     * (converted does not open new connections!). Calls default result converter (used for repository methods)
     * to perform projection.
     * <p>
     * Converter tries to mimic the same logic as usual repository method call: in usual synchronous call
     * orient connection will perform automatic type conversion nad then custom result conversion is applied.
     * This service emulates the first part and calls result converter (so overall behaviour is near the same).
     *
     * @param result     orient record
     * @param targetType target conversion type
     * @param <T>        target conversion type
     * @return converted object
     * @throws ResultConversionException if conversion is impossible
     */
    public <T> T convert(final Object result, final Class<T> targetType) {
        return convert(result, targetType, defaultResultConverter);
    }

    /**
     * @param result     orient record
     * @param targetType target conversion type
     * @param converter  converter object
     * @param <T>        target conversion type
     * @return converted object
     * @throws ResultConversionException if conversion is impossible
     */
    @SuppressWarnings("unchecked")
    public <T> T convert(final Object result, final Class<T> targetType, final ResultConverter converter) {
        // wrap primitive, because result will always be object
        final Class<T> target = Primitives.wrap(targetType);
        // do manual conversion to other types if required (usually this will be done automatically by connection
        // object, but async and live callbacks will always return documents)
        T res = tryConversion(result, target);
        if (res != null) {
            return res;
        }

        // use converter
        final ResultDescriptor desc = new ResultDescriptor();
        // support void case for more universal usage
        desc.returnType = target.equals(Void.class) ? ResultType.VOID : ResultType.PLAIN;
        desc.entityType = target;
        desc.expectType = target;
        desc.entityDbType = ORecord.class.isAssignableFrom(target) ? DbType.DOCUMENT : DbType.UNKNOWN;
        res = (T) converter.convert(desc, result);
        ResultUtils.check(res, target);
        return res;
    }

    @SuppressWarnings("checkstyle:BooleanExpressionComplexity")
    private <T> T tryConversion(final Object result, final Class<T> targetType) {
        if (result == null
                // no need for conversion of raw types (e.g. Document)
                || ORecord.class.isAssignableFrom(targetType)
                // without ongoing transaction we will have to open new connections for conversion (waste of resources)
                || !transactionManager.isTransactionActive()
                // only document type could be converted
                || !(result instanceof ODocument)
                // for wrapper documents (e.g. select t from Model) no class will be set (projection case)
                || ((ODocument) result).getClassName() == null) {
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
            final ODatabaseObject db = injector.getInstance(ODatabaseObject.class);
            if (db.getEntityManager().getRegisteredEntities().contains(targetType)) {
                return (T) db.getUserObjectByRecord(doc, null);
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
