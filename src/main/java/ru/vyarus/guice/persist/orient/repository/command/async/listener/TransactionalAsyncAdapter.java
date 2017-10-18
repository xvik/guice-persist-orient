package ru.vyarus.guice.persist.orient.repository.command.async.listener;

import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;

/**
 * Wraps {@link OCommandResultListener} with a transaction.
 * <p>
 * Do not use for live queries! Live query listener use different method to handle result.
 *
 * @author Vyacheslav Rusakov
 * @since 11.10.2017
 */
public class TransactionalAsyncAdapter implements OCommandResultListener {

    private final PersistentContext<ODatabaseDocumentTx> context;
    private final OCommandResultListener listener;

    public TransactionalAsyncAdapter(final PersistentContext<ODatabaseDocumentTx> context,
                                     final OCommandResultListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public boolean result(final Object iRecord) {
        if (context.getTransactionManager().isTransactionActive()) {
            // avoid additional calls on stack (listener will be called in separate thread only for remote connection)
            return listener.result(iRecord);
        } else {
            // wrapping in transaction
            return context.doInTransaction(new TxAction<Boolean>() {
                @Override
                public Boolean execute() throws Throwable {
                    return listener.result(iRecord);
                }
            });
        }
    }

    @Override
    public void end() {
        listener.end();
    }

    @Override
    public Object getResult() {
        return listener.getResult();
    }
}
