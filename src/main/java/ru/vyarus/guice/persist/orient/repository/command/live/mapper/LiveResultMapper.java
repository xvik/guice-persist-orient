package ru.vyarus.guice.persist.orient.repository.command.live.mapper;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;
import ru.vyarus.java.generics.resolver.GenericsResolver;

/**
 * Adapter for {@link LiveResultListener} which performs actual result conversion. Adapter must also implement
 * orient command listener interface like {@link com.orientechnologies.orient.core.sql.query.OLocalLiveResultListener}.
 *
 * @author Vyacheslav Rusakov
 * @since 09.10.2017
 */
public class LiveResultMapper implements OLiveResultListener, OCommandResultListener {

    private final RecordConverter converter;
    private final LiveResultListener listener;

    public LiveResultMapper(final RecordConverter converter,
                            final LiveResultListener listener) {
        this.converter = converter;
        this.listener = listener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onLiveResult(final int iLiveToken, final ORecordOperation iOp) throws OException {
        final RecordOperation op = RecordOperation.forType(iOp.type);
        final ORecord rec = iOp.getRecord();
        final Class targetType = GenericsResolver.resolve(listener.getClass())
                .type(LiveResultListener.class).generic("T");
        try {
            listener.onLiveResult(iLiveToken, op, converter.convert(rec, targetType));
        } catch (Exception th) {
            final StringBuilder id = new StringBuilder(
                    rec instanceof ODocument ? ((ODocument) rec).getClassName() : rec.getClass().getSimpleName()
            ).append("(").append(rec.getIdentity()).append(")");
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

    // not used, but required methods

    @Override
    public boolean result(final Object iRecord) {
        return false;
    }

    @Override
    public void end() {
        // not needed
    }

    @Override
    public Object getResult() {
        return null;
    }
}
