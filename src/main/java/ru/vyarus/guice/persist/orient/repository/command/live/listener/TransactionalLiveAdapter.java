package ru.vyarus.guice.persist.orient.repository.command.live.listener;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import com.orientechnologies.orient.core.sql.query.OLocalLiveResultListener;
import ru.vyarus.guice.persist.orient.db.PersistentContext;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction;

/**
 * Wraps live listener ({@link OLiveResultListener}) with an external transaction (allows using thread bound
 * connection in guice).
 *
 * @author Vyacheslav Rusakov
 * @since 11.10.2017
 */
public class TransactionalLiveAdapter extends OLocalLiveResultListener {

    private final PersistentContext<ODatabaseDocument> context;
    private final OLiveResultListener underlying;

    public TransactionalLiveAdapter(final PersistentContext<ODatabaseDocument> context,
                                    final OLiveResultListener underlying) {
        super(underlying);
        this.context = context;
        this.underlying = underlying;
    }

    @Override
    public void onLiveResult(final int iLiveToken, final ORecordOperation iOp) throws OException {
        // wrapping in external transaction (live query thread will always have thread bound connection)
        context.doInTransaction(TxConfig.external(), new TxAction<Void>() {
            @Override
            public Void execute() throws Throwable {
                underlying.onLiveResult(iLiveToken, iOp);
                return null;
            }
        });
    }
}
