package ru.vyarus.guice.persist.orient.db

import com.google.inject.Inject
import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(PackageSchemeModule)
class ManualConnectionCloseTest extends AbstractTest {
    @Inject
    SpecificTxTemplate<ODatabaseDocument> documentTemplate;
    @Inject
    SpecificTxTemplate<OrientGraph> graphTemplate;

    def "Check manual connection close"() {

        when: "manually closing document connection in the middle of transaction"
        documentTemplate.doInTransaction({ db ->
            db.close()
        } as SpecificTxAction)
        then: "consistency check will fail on commit"
        thrown(IllegalStateException)

        when: "manually closing object connection in the middle of transaction"
        context.doInTransaction({db ->
            db.close()
        } as SpecificTxAction)
        then: "consistency check will fail on commit"
        thrown(IllegalStateException)

        when: "manually closing graph connection in the middle of transaction"
        graphTemplate.doInTransaction({db ->
            db.getRawGraph().close()
        } as SpecificTxAction)
        then: "consistency check will fail on commit"
        thrown(IllegalStateException)
    }
}