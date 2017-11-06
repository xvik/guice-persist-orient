package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ResultUtils;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType;

import javax.inject.Singleton;
import java.util.Collection;

/**
 * Default converter implementation.
 * May be substituted for another one by simply defining new converter instance in guice context.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
@Singleton
public class DefaultResultConverter implements ResultConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(final ResultDescriptor desc, final Object result) {
        final Class<?> returnClass = desc.expectType;

        Object res = null;
        if (result != null && !ResultType.VOID.equals(desc.returnType)) {
            res = isCompatible(result, returnClass, desc)
                    ? result : convertResult(
                    desc.returnType,
                    returnClass,
                    desc.entityType,
                    result,
                    desc.entityDbType == DbType.UNKNOWN);
        }
        // converted result may not match required return type! this is important for cases with custom
        // result converter extensions which will use default conversion as a source for final conversion
        return res;
    }

    @SuppressWarnings("unchecked")
    private boolean isCompatible(final Object result, final Class<?> returnClass, final ResultDescriptor desc) {
        boolean compatible = false;
        if (returnClass.isAssignableFrom(result.getClass())) {
            // type compatibility checked only for collections when target entity type is specified
            // and its not native orient type (connection will already return required object).
            compatible = desc.returnType != ResultType.COLLECTION
                    || desc.entityDbType != DbType.UNKNOWN
                    || Object.class.equals(desc.entityType)
                    // In case of graph connection, there could be an iterator which should be ignored
                    // to avoid premature database load (this is possible only if connection hint was manually set,
                    // because otherwise document connection will be used)
                    || !Collection.class.isAssignableFrom(result.getClass());
        }
        return compatible;
    }

    private Object convertResult(final ResultType type, final Class returnClass,
                                 final Class entityClass, final Object result, final boolean projection) {
        final Object converted;
        switch (type) {
            case COLLECTION:
                converted = ResultUtils.convertToCollection(result, returnClass, entityClass, projection);
                break;
            case ARRAY:
                converted = ResultUtils.convertToArray(result, entityClass, projection);
                break;
            case PLAIN:
                converted = ResultUtils.convertToPlain(result, returnClass, entityClass, projection);
                break;
            default:
                throw new ResultConversionException("Unsupported return type " + type);
        }
        return converted;
    }
}
