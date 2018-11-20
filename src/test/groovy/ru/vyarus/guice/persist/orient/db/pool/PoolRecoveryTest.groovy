package ru.vyarus.guice.persist.orient.db.pool

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.OCommandExecutor
import com.orientechnologies.orient.core.command.OCommandRequestText
import com.orientechnologies.orient.core.db.ODatabase
import com.orientechnologies.orient.core.db.ODatabaseListener
import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

/**
 * Checks pools recovery when connection improperly close during previous transaction
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(PackageSchemeModule)
class PoolRecoveryTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<ODatabaseDocument> documentTemplate

    def "Check pool recovery"() {

        when: "closing connection after transaction close (while connection already back to pool)"
        documentTemplate.doInTransaction({ db ->
            db.registerListener(new ODatabaseListener() {
                @Override
                void onCreate(ODatabase iDatabase) {
                }

                @Override
                void onDelete(ODatabase iDatabase) {
                }

                @Override
                void onOpen(ODatabase iDatabase) {
                }

                @Override
                void onBeforeTxBegin(ODatabase iDatabase) {
                }

                @Override
                void onBeforeTxRollback(ODatabase iDatabase) {
                }

                @Override
                void onAfterTxRollback(ODatabase iDatabase) {
                }

                @Override
                void onBeforeTxCommit(ODatabase iDatabase) {
                }

                @Override
                void onAfterTxCommit(ODatabase iDatabase) {
                    // "corrupt" pool connection
                    iDatabase.rollback(true)
                }

                @Override
                void onClose(ODatabase iDatabase) {
                }

                @Override
                void onBeforeCommand(OCommandRequestText iCommand, OCommandExecutor executor) {

                }

                @Override
                void onAfterCommand(OCommandRequestText iCommand, OCommandExecutor executor, Object result) {

                }

                @Override
                boolean onCorruptionRepairDatabase(ODatabase iDatabase, String iReason, String iWhatWillbeFixed) {
                    return false
                }
            });
        } as SpecificTxAction)
        documentTemplate.doInTransaction({ db ->
            // this transaction will cause pool restart
            // it's not reproduced with new document pool (2.0), and I don't know how to simulate it.
        } as SpecificTxAction)
        then: "everything ok, pool recovered"
        // see logs for restart warning message (not shown in 2.0)
        true
    }
}