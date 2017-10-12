package ru.vyarus.guice.persist.orient.repository.command.live.mapper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ORecordOperation;

/**
 * Enum representation for orient record operation constants ({@link ORecordOperation}).
 * <p>
 * NOTE: not all operations are sent to live query.
 *
 * @author Vyacheslav Rusakov
 * @see ORecordOperation
 * @since 03.10.2017
 */
public enum RecordOperation {
    /**
     * New record created.
     */
    CREATED(ORecordOperation.CREATED),
    /**
     * Record updated.
     */
    UPDATED(ORecordOperation.UPDATED),
    /**
     * Record deleted.
     */
    DELETED(ORecordOperation.DELETED),

    /**
     * Most likely, record is loaded from db (means its state is synchronized with db aka no changes).
     * Should not be called for live query.
     */
    LOADED(ORecordOperation.LOADED),
    /**
     * Meaning is almost like CREATED (most of the orient logic treat both events as the same).
     * The difference in meaning is: the place of old record taken by some new record.
     * Should not be called for live query.
     *
     * @see ODatabaseDocumentTx#recycle(com.orientechnologies.orient.core.record.ORecord)
     */
    RECYCLED(ORecordOperation.RECYCLED);

    private byte otype;

    RecordOperation(final byte otype) {
        this.otype = otype;
    }

    /**
     * @return relative orient constant
     */
    public byte getOtype() {
        return otype;
    }

    /**
     * @param otype orient constant
     * @return relative enum type
     */
    public static RecordOperation forType(final byte otype) {
        for (RecordOperation op : RecordOperation.values()) {
            if (op.otype == otype) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown orient record type: " + otype);
    }
}
