package ru.vyarus.guice.persist.orient.db.util;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.OIdentityChangeListener;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.enhancement.OObjectEntitySerializer;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;
import javassist.util.proxy.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.PersistException;

import java.lang.reflect.Field;

/**
 * Rid utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 03.06.2015
 */
public final class RidUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RidUtils.class);

    private RidUtils() {
    }

    /**
     * Resolve rid from almost all possible objects.
     * Even if simple string provided, value will be checked for correctness.
     * <p>
     * Note: not saved object proxy, document or vertex will contain fake id and will be accepted
     * (but query result against such id will be empty).
     *
     * @param value value may be a mapped object (proxy or raw), document, vertex, ORID or simple string
     * @return correct rid string
     * @throws ru.vyarus.guice.persist.orient.db.PersistException if rid couldn't be resolved
     * @throws NullPointerException                               if value is null
     */
    public static String getRid(final Object value) {
        Preconditions.checkNotNull(value, "Not null value required");
        final String res;
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

    /**
     * Shortcut for {@link #trackIdChange(ODocument, Object)}.
     * Used when detaching pojo into pure object to fix temporal id in resulted pojo after commit.
     *
     * @param proxy object proxy
     * @param pojo  detached pure pojo
     */
    public static void trackIdChange(final Proxy proxy, final Object pojo) {
        final ODocument doc = OObjectEntitySerializer.getDocument(proxy);
        if (doc != null) {
            trackIdChange(doc, pojo);
        }
    }

    /**
     * When just created object is detached to pure pojo it gets temporary id.
     * Real id is assigned only after transaction commit. This method tracks original
     * document, intercepts its id change and sets correct id and version into pojo.
     * So it become safe to use such pojo id outside of transaction.
     *
     * @param document original document
     * @param pojo     pojo
     */
    @SuppressWarnings("checkstyle:IllegalIdentifierName")
    public static void trackIdChange(final ODocument document, final Object pojo) {
        if (document.getIdentity().isNew()) {
            ORecordInternal.addIdentityChangeListener(document, new OIdentityChangeListener() {
                @Override
                public void onBeforeIdentityChange(final ORecord record) {
                    // not needed
                }

                @Override
                public void onAfterIdentityChange(final ORecord record) {
                    OObjectSerializerHelper.setObjectID(record.getIdentity(), pojo);
                    OObjectSerializerHelper.setObjectVersion(record.getVersion(), pojo);
                }
            });
        }
    }

    private static String resolveIdFromObject(final Object value) {
        final Field idField = findIdField(value);
        final String className = value.getClass().getSimpleName();
        if (idField == null) {
            throw new PersistException(String.format("Class %s doesn't contain id field", className));
        }
        final String res;
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

    /**
     * Core orient field resolve method relies on bound connection, but it may be required to resolve
     * id outside of transaction. So we use orient method under transaction and do manual scan outside
     * of transaction.
     *
     * @param value object instance (non proxy)
     * @return object id field or null if not found
     */
    private static Field findIdField(final Object value) {
        Field res = null;
        final Class<?> type = value.getClass();
        if (ODatabaseRecordThreadLocal.instance().isDefined()) {
            res = OObjectEntitySerializer.getIdField(type);
        } else {
            final String idField = OObjectSerializerHelper.getObjectIDFieldName(value);
            if (idField != null) {
                try {
                    res = type.getDeclaredField(idField);
                } catch (NoSuchFieldException e) {
                    LOGGER.warn(String
                            .format("Id field '%s' not found in class '%s'.", idField, type.getSimpleName()), e);
                }
            }
        }
        return res;
    }
}
