package ru.vyarus.guice.persist.orient.db.util;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.enhancement.OObjectEntitySerializer;
import javassist.util.proxy.Proxy;
import ru.vyarus.guice.persist.orient.db.PersistException;

import java.lang.reflect.Field;

/**
 * Rid utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 03.06.2015
 */
public final class RidUtils {

    private RidUtils() {
    }

    /**
     * Resolve rid from almost all possible objects.
     * Even if simple string provided, value will be checked for correctness.
     * <p>Note: not saved object proxy, document or vertex will contain fake id and will be accepted
     * (but query result against such id will be empty).</p>
     *
     * @param value value may be a mapped object (proxy or raw), document, vertex, ORID or simple string
     * @return correct rid string
     * @throws ru.vyarus.guice.persist.orient.db.PersistException if rid couldn't be resolved
     * @throws NullPointerException if value is null
     */
    public static String getRid(final Object value) {
        Preconditions.checkNotNull(value, "Not null value required");
        String res;
        if (value instanceof ORID) {
            res = value.toString();
        } else if (value instanceof OIdentifiable) {
            // ODocument, Vertex support
            res = ((OIdentifiable) value).getIdentity().toString();
        } else if (value instanceof String) {
            res = checkRid(value);
        } else if (value instanceof Proxy) {
            // object proxy
            res = OObjectEntitySerializer.getRid((Proxy) value).toString();
        } else {
            // raw (non proxy) object
            res = resolveIdFromObject(value);
        }
        return res;
    }

    private static String resolveIdFromObject(final Object value) {
        final Field idField = OObjectEntitySerializer.getIdField(value.getClass());
        final String className = value.getClass().getSimpleName();
        if (idField == null) {
            throw new PersistException(String.format("Class %s doesn't contain id field", className));
        }
        String res;
        try {
            final Object id = OObjectEntitySerializer.getFieldValue(idField, value);
            if (id == null) {
                throw new PersistException(String.format(
                        "Object of type %s does not contains id", className));
            }
            res = checkRid(id.toString());
        } catch (IllegalAccessException e) {
            throw new PersistException(String.format("Error resolving object %s id value", className), e);
        }
        return res;
    }

    private static String checkRid(final Object strVal) {
        final String val = (String) strVal;
        if (!ORecordId.isA(val)) {
            throw new PersistException(String.format("String '%s' is not rid", val));
        }
        return val;
    }
}
