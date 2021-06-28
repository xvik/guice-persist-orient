package ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;

/**
 * Adapter for {@link LiveQueryListener} which performs actual result conversion.
 * <p>
 * Applied by {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen} extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.10.2017
 */
public class LiveResultMapper implements OLiveResultListener {

    private final RecordConverter converter;
    private final LiveQueryListener listener;
    private final Class<?> targetType;

    public LiveResultMapper(final RecordConverter converter,
                            final LiveQueryListener listener,
                            final Class<?> targetType) {
        this.converter = converter;
        this.listener = listener;
        this.targetType = targetType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLiveResult(final int iLiveToken, final ORecordOperation iOp) throws OException {
        final RecordOperation op = RecordOperation.forType(iOp.type);
        final ORecord rec = iOp.getRecord();
        try {
            listener.onLiveResult(iLiveToken, op, converter.convert(rec, targetType));
        } catch (Exception th) {
            final StringBuilder id = new StringBuilder(
                    rec instanceof ODocument ? ((ODocument) rec).getClassName() : rec.getClass().getSimpleName()
            ).append('(').append(rec.getIdentity()).append(')');
            throw new LiveResultMappingException(
                    "Error calling live result listener " + iLiveToken + " for " + op + " record " + id, th);
        }
    }

    @Override
    public void onError(final int iLiveToken) {
        listener.onError(iLiveToken);
    }

    @Override
    public void onUnsubscribe(final int iLiveToken) {
        listener.onUnsubscribe(iLiveToken);
    }
}
