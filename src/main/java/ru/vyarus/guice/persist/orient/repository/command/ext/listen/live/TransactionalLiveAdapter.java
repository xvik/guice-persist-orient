package ru.vyarus.guice.persist.orient.repository.command.ext.listen.live;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;

/**
 * Wraps live listener ({@link OLiveResultListener}) with a transaction.
 *
 * @author Vyacheslav Rusakov
 * @since 11.10.2017
 */
public class TransactionalLiveAdapter extends OLiveListenerAdapter {

    private final PersistentContext<ODatabaseDocumentTx> context;
    private final OLiveResultListener underlying;

    public TransactionalLiveAdapter(final PersistentContext<ODatabaseDocumentTx> context,
                                    final OLiveResultListener underlying) {
        super(underlying);
        this.context = context;
        this.underlying = underlying;
    }

    @Override
    public void onLiveResult(final int iLiveToken, final ORecordOperation iOp) throws OException {
        if (context.getTransactionManager().isTransactionActive()) {
            // avoid additional calls on stack
            underlying.onLiveResult(iLiveToken, iOp);
        } else {
            // wrapping in transaction
            context.doInTransaction(new TxAction<Void>() {
                @Override
                public Void execute() throws Throwable {
                    underlying.onLiveResult(iLiveToken, iOp);
                    return null;
                }
            });
        }

    }
}
