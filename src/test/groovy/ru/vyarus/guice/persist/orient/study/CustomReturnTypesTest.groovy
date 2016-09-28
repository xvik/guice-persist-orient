package ru.vyarus.guice.persist.orient.study

import com.google.inject.Inject
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxTemplate
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 05.08.2014
 */
@UseModules([PackageSchemeModule, BootstrapModule])
class CustomReturnTypesTest extends AbstractTest {

    @Inject
    SpecificTxTemplate<OrientBaseGraph> graphTemplate

    def "Check fields collection select"() {

        when: "selecting one field in object connection"
        def res = context.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select name from Model order by name"))
        } as SpecificTxAction)
        then: "receiving list of documents"
        res instanceof List<ODocument>
        res[0].field('name') == 'name0'

        when: "selecting few fields in object connection"
        res = context.doInTransaction({ db ->
            db.query(new OSQLSynchQuery<Object>("select name, nick from Model order by name"))
        } as SpecificTxAction)
        then: "receiving list of documents"
        res instanceof List<ODocument>
        res[0].field('nick') == 'nick0'
    }

    def "Check select fields in graph connection"() {

        when: "selecting one field in graph connection"
        def res = graphTemplate.doInTransaction({ db ->
            db.command(new OSQLSynchQuery<Object>("select name from Model order by name")).execute()
        } as SpecificTxAction)
        then: "receiving list of vertexes"
        res instanceof Iterable<Vertex>
        res.iterator().next().getProperty('name') == 'name0'
    }
}