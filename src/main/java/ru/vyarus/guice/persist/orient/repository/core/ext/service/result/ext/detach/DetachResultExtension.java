package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConversionException;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.result.ResultExtension;

import javax.inject.Singleton;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * {@link DetachResult} result extension.
 *
 * @author Vyacheslav Rusakov
 * @since 02.03.2015
 */
@Singleton
public class DetachResultExtension implements ResultExtension<DetachResult> {

    @Override
    public void handleAnnotation(final RepositoryMethodDescriptor descriptor, final DetachResult annotation) {
        // not needed
    }

    @Override
    public Object convert(final RepositoryMethodDescriptor descriptor, final Object result) {
        checkExec(DbType.OBJECT.equals(descriptor.executor.getType()), "Detach may be performed only on "
                        + "objects from OBJECT connection, but current connection is %s",
                descriptor.executor.getType());
        final OObjectDatabaseTx connection = (OObjectDatabaseTx) descriptor.executor.getConnection();
        Object res;
        switch (descriptor.result.returnType) {
            case PLAIN:
                res = connection.detachAll(result, true);
                break;
            case COLLECTION:
                res = handleCollection(result, connection);
                break;
            case ARRAY:
                res = handleArray(result, connection);
                break;
            default:
                throw new ResultConversionException("Unsupported return type " + descriptor.result.returnType);
        }
        return res;
    }

    private Object handleCollection(final Object result, final OObjectDatabaseTx connection) {
        final boolean isIterator = result instanceof Iterator;
        @SuppressWarnings("unchecked")
        final Collection<Object> col = isIterator ? Lists.newArrayList((Iterator) result)
                : (Collection<Object>) result;
        final Collection res = detachCollection(col, connection);
        return isIterator ? res.iterator() : res;
    }

    @SuppressWarnings("unchecked")
    private Collection detachCollection(final Collection result, final OObjectDatabaseTx connection) {
        final List<Object> tmp = Lists.newArrayList();
        for (Object obj : result) {
            tmp.add(obj == null ? null : connection.detachAll(obj, true));
        }
        result.clear();
        for (Object obj : tmp) {
            result.add(obj);
        }
        return result;
    }

    private Object handleArray(final Object result, final OObjectDatabaseTx connection) {
        for (int i = 0; i < Array.getLength(result); i++) {
            final Object elt = Array.get(result, i);
            if (elt != null) {
                Array.set(result, i, connection.detachAll(elt, true));
            }
        }
        return result;
    }
}