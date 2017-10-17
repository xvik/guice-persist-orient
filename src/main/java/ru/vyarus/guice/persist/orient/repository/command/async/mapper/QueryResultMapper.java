package ru.vyarus.guice.persist.orient.repository.command.async.mapper;

import com.google.common.base.MoreObjects;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;

/**
 * Adapter for {@link QueryListener} which performs actual result conversion.
 * <p>
 * Applied by {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen} extension.
 *
 * @author Vyacheslav Rusakov
 * @since 14.10.2017
 */
public class QueryResultMapper implements OCommandResultListener {

    private final RecordConverter converter;
    private final QueryListener listener;
    private final Class<?> targetType;

    public QueryResultMapper(final RecordConverter converter,
                             final QueryListener listener,
                             final Class<?> targetType) {
        this.converter = converter;
        this.listener = listener;
        this.targetType = targetType;
    }

    @Override
    @SuppressWarnings({"unchecked", "PMD.ConsecutiveLiteralAppends"})
    public boolean result(final Object rec) {
        // most likely (suppose in all cases) rec will be ORecord, but any other type would also be handled properly
        try {
            return listener.onResult(converter.convert(rec, targetType));
        } catch (Exception th) {
            final StringBuilder id = new StringBuilder(
                    rec instanceof ODocument
                            // ODocument may not have class if its a wrapper around simple value (select t from Model)
                            ? MoreObjects.firstNonNull(((ODocument) rec).getClassName(), "ODocument")
                            : rec.getClass().getSimpleName()
            ).append("(").append(rec instanceof ORecord ? ((ORecord) rec).getIdentity() : rec.toString()).append(")");
            throw new QueryResultMappingException(
                    "Error calling query result listener for record " + id, th);
        }
    }

    @Override
    public void end() {
        listener.onEnd();
    }

    @Override
    public Object getResult() {
        // method is not useful for queries
        return null;
    }
}
