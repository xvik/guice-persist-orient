package ru.vyarus.guice.persist.orient.repository.command.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach.DetachResult
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * Checks connection type recognition from expected result
 *
 * @author Vyacheslav Rusakov 
 * @since 13.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface DbRecognitionCases {

    // object db
    @Query("select from Model")
    List<Model> selectAll()

    // object db
    @Query("select from Model")
    Model[] selectAllAsArray()

    // single element select
    @Query("select from Model")
    @DetachResult
    Model selectUnique()

    // document db
    @Query("select from Model")
    List<ODocument> selectAllAsDocument()

    // graph db
    @Query("select from Model")
    List<Vertex> selectAllAsVertex()

    // check derivative types recognition
    @Query("select from Model")
    List<OrientVertex> selectAllAsOrientVertex()

    // document db by default
    @Query("update Model set name='changed'")
    void update()

    // explicitly define to use object connection instead of default document one
    @Query(value = "update Model set name='changed'", connection = DbType.OBJECT)
    void updateUsingObjectConnection()

    // document db (same as previous but return updated entities count)
    @Query("update Model set name='changed'")
    int updateWithCount()

    // document db (same as previous but return updated entities count)
    @Query("update Model set name='changed'")
    Integer updateWithCountObject()

    // no generic - document db will be selected
    @Query("select from Model")
    List selectAllNoType()


    // iterable result
    @Query("select from Model")
    Iterable<Model> selectAllIterable();

    // iterator result
    @Query("select from Model")
    Iterator<Model> selectAllIterator();

    // convert graph iterable to iterator
    @Query("select from Model")
    Iterator<Vertex> selectAllVertex();

    // graph connection return iterable, should pass as is
    @Query("select from Model")
    Iterable<Vertex> selectAllVertexIterable();

    // converted to HashSet
    @Query(value = "select from Model", returnAs = HashSet.class)
    Iterable<Model> selectAllAsSet();

    // converted to HashSet
    @Query(value = "select from Model", returnAs = HashSet.class)
    Iterable<Vertex> selectAllAsSetGraph();

    // use object connection for select
    @Query(value = "select name from Model", connection = DbType.OBJECT)
    List<ODocument> documentOverride();

}
