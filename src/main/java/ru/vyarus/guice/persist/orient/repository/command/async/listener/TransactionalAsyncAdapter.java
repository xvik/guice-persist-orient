package ru.vyarus.guice.persist.orient.repository.command.async.listener;

import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;

/**
 * Wraps {@link OCommandResultListener} with an external transaction (allows using thread bound connection
 * in guice).
 * <p>
 * IMPORTANT: current async queries realization did not properly expect exceptions inside the listener, so
 * adapter have to intercept all exceptions, log it and return stop processing signal. As a result, you may
 * know about error only from logs or because of incorrect result (empty or too short). You can always wrap your
 * code inside listener into try/catch and handle exception properly.
 * <p>
 * Do not use for live queries! Live query listener use different method to handle result.
 *
 * @author Vyacheslav Rusakov
 * @since 11.10.2017
 */
public class TransactionalAsyncAdapter implements OCommandResultListener {

    private final Logger logger = LoggerFactory.getLogger(TransactionalAsyncAdapter.class);

    private final PersistentContext<ODatabaseDocument> context;
    private final OCommandResultListener listener;
    // on error this is the only way to indicate problem context
    private final String queryContext;

    public TransactionalAsyncAdapter(final PersistentContext<ODatabaseDocument> context,
                                     final OCommandResultListener listener,
                                     final String queryContext) {
        this.context = context;
        this.listener = listener;
        this.queryContext = queryContext;
    }

    @Override
    public boolean result(final Object iRecord) {
        // avoid additional call on stack (blocking case)
        if (context.getTransactionManager().isTransactionActive()) {
            // note that this is blocking case (listener called inside query thread) and throwing exception
            // will not harm connection, but still "eating" exception for unified behaviour
            return safeResult(iRecord);
        } else {
            // wrapping in external transaction (non blocking case)
            return context.doInTransaction(TxConfig.external(), new TxAction<Boolean>() {
                @Override
                public Boolean execute() throws Throwable {
                    return safeResult(iRecord);
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

    private boolean safeResult(final Object iRecord) {
        // orient behave incorrectly in non blocking mode (with remote connection) after exception in the listener
        // so "eating" all exceptions; overall this almost unifies behaviour with live listener
        try {
            return listener.result(iRecord);
        } catch (Exception ex) {
            logger.error("Error processing listener for async query '" + queryContext + "'. Note that "
                    + "Exception is not propagated (in order to not put orient connection into invalid "
                    + "state) and async query execution is simply stopped (all already processed results "
                    + "are returned as final result). To manually handle exception, use try-catch inside "
                    + "your listener.", ex);
            return false;
        }
    }
}
