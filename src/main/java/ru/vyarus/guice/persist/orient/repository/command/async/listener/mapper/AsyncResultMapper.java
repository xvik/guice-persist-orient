package ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper;

import com.google.common.base.MoreObjects;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for {@link AsyncQueryListener} which performs actual result conversion.
 * <p>
 * Applied by {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen} extension.
 *
 * @author Vyacheslav Rusakov
 * @since 14.10.2017
 */
public class AsyncResultMapper implements OCommandResultListener {

    private final RecordConverter converter;
    private final AsyncQueryListener listener;
    private final Class<?> targetType;
    // collect results
    private final List results = new ArrayList();

    public AsyncResultMapper(final RecordConverter converter,
                             final AsyncQueryListener listener,
                             final Class<?> targetType) {
        this.converter = converter;
        this.listener = listener;
        this.targetType = targetType;
    }

    @Override
    @SuppressWarnings({"unchecked", "PMD.ConsecutiveLiteralAppends"})
    public boolean result(final Object rec) {
        // in all cases rec will be ORecord, but any other type would also be handled properly (just in case)
        try {
            final Object converted = converter.convert(rec, targetType);
            final boolean res = listener.onResult(converted);
            if (res) {
                results.add(converted);
            }
            return res;
        } catch (Exception th) {
            final StringBuilder id = new StringBuilder(
                    rec instanceof ODocument
                            // ODocument may not have class if its a wrapper around simple value (select t from Model)
                            ? MoreObjects.firstNonNull(((ODocument) rec).getClassName(), "ODocument")
                            : rec.getClass().getSimpleName()
            ).append('(').append(rec instanceof ORecord ? ((ORecord) rec).getIdentity() : rec.toString()).append(')');
            throw new AsyncResultMappingException(
                    "Error calling query result listener for record " + id, th);
        }
    }

    @Override
    public void end() {
        listener.onEnd();
    }

    @Override
    public Object getResult() {
        // this is only useful for non blocking queries, when Future is returned, so user can do
        // result = repository.select(listener).get()
        return results;
    }
}
