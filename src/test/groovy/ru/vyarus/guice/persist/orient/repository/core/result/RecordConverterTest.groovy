package ru.vyarus.guice.persist.orient.repository.core.result

import com.orientechnologies.orient.core.db.document.ODatabaseDocument
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConversionException
import ru.vyarus.guice.persist.orient.support.model.EdgeModel
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.VertexModel
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov
 * @since 06.10.2017
 */
@UseModules(PackageSchemeModule)
class RecordConverterTest extends AbstractTest {

    @Inject
    RecordConverter converter
    @Inject
    PersistentContext<ODatabaseDocument> docContext

    def "check simple conversions"() {

        when: "converting with projection"
        def res = converter.convert(new ODocument('length': 1), Integer)
        then: "projection recognized"
        res == 1

        when: "projection to primitive"
        res = converter.convert(new ODocument('length': 1), int)
        then: "projection recognized"
        res == 1

        when: "void conversion"
        res = converter.convert(new ODocument('length': 1), void)
        then: "null"
        res == null
    }

    def "Check object conversions"() {

        when: "request object conversion"
        docContext.doInTransaction({ db ->
            Model model = context.connection.save(new Model(name: "tst", nick: "test"))
            def rec = db.getRecord(new ORecordId(model.id))
            def res = converter.convert(rec, Model.class)

            assert res instanceof Model
            assert res.name == model.name
        } as SpecificTxAction<Void, ODatabaseDocument>)
        then: "converted to object"
        true

        when: "convert outside of tx"
        def doc = docContext.doInTransaction({ db ->
            db.browseClass(Model.simpleName).first()
        } as SpecificTxAction)
        // custom converter required because default will fail to do conversion
        def res = converter.convert(doc, Model, { ResultDescriptor descriptor, Object result ->
            assert result instanceof ODocument
            return new Model(name: result.field("name"))
        })
        then: "converted manually"
        res instanceof Model
        res.id == null
        res.name == doc.field("name")

        when: "impossible conversion"
        // default converter will no be able to convert it
        converter.convert(doc, Model)
        then: "fail"
        def ex = thrown(ResultConversionException)
        ex.message == "Failed to convert ODocument to Model"
    }

    def "Check object conversion with different entity type"() {
        when: "request object conversion"
        docContext.doInTransaction({ db ->
            VertexModel model = context.connection.save(new VertexModel(name: "tst", nick: "test"))
            def rec = db.getRecord(new ORecordId(model.id))
            def res = converter.convert(rec, VertexModel)

            assert res instanceof VertexModel
            assert res.name == model.name
        } as SpecificTxAction)
        then: "converted to object"
        true

        when: "converting to different type"
        docContext.doInTransaction({ db ->
            def doc = db.browseClass(VertexModel.simpleName).first()
            try {
                converter.convert(doc, Model)
                assert false
            } catch (ResultConversionException ex) {
                assert true
            }
        } as SpecificTxAction)
        then: "error was thrown"
        true
    }

    def "Check graph type conversion"() {

        when: "request vertex conversion"
        docContext.doInTransaction({ db ->
            VertexModel model = context.connection.save(new VertexModel(name: "tst", nick: "test"))
            def rec = db.getRecord(new ORecordId(model.id))
            def res = converter.convert(rec, Vertex)

            assert res instanceof Vertex
            assert res.getProperty("name") == model.name
        } as SpecificTxAction)
        then: "converted to vertex"
        true

        when: "request edge conversion"
        docContext.doInTransaction({ db ->
            EdgeModel model = context.connection.save(new EdgeModel(name: "tst", nick: "test"))
            def rec = db.getRecord(new ORecordId(model.id))
            def res = converter.convert(rec, Edge)

            assert res instanceof Edge
            assert res.getProperty("name") == model.name
        } as SpecificTxAction)
        then: "converted to edge"
        true

        when: "converting to wrong type"
        docContext.doInTransaction({ db ->
            def doc = db.browseClass(VertexModel.simpleName).first()
            try {
                converter.convert(doc, Edge)
                assert false
            } catch (ResultConversionException ex) {
                assert true
            }
        } as SpecificTxAction)
        then: "error"
        true
    }
}