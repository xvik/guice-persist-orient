package ru.vyarus.guice.persist.orient.template;

import ru.vyarus.guice.persist.orient.internal.OrientPersistService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransactionTemplate {


    private OrientPersistService persist;

    @Inject
    public TransactionTemplate(OrientPersistService persist) {
        this.persist = persist;
    }

    public <T> T doWithTransaction(final TransactionalAction<T> action) throws Throwable {
        if (persist.isTransactionActive()) {
            // execution inside of other transaction
            return action.execute(persist.get());
        }

        try {
            persist.begin();
            T res = action.execute(persist.get());
            persist.end();
            return res;
        } catch (Throwable th) {
            if (persist.isTransactionActive()) {
                // calling once for nested transactions (or in case it was done manually
                persist.rollback();
            }
            throw th;
        }
    }
}
