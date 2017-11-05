package ru.vyarus.guice.persist.orient.repository.core.result.projection

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * Examples of sql projections conversions.
 *
 * @author Vyacheslav Rusakov 
 * @since 01.11.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
public interface RepositoryResultProjections {

    // orient returns ODocument with singe field, but it will be recognized and "unwrapped"
    @Query("select count(@rid) from Model")
    int getCount()

    // orient will return list of ODocument and result will be "unwrapped" to raw values
    @Query("select name from Model")
    List<String> getNames()

    // orient will return list of ODocument and result will be "unwrapped" to raw values
    @Query("select name from Model")
    String[] getNamesArray()

    // tricky case: query return list, but as soon as one result expected only first element taken
    // and projection will work on it
    @Query("select name from Model")
    String getOneName()

    // in graph connections Vertex returned instead of document

    @Query(value = "select name from VertexModel", connection = DbType.GRAPH)
    String[] getGraphNamesArray()

    @Query(value = "select name from VertexModel", connection = DbType.GRAPH)
    List<String> getGraphNames()

    @Query(value = "select count(@rid) from VertexModel", connection = DbType.GRAPH)
    int getGraphCount()

    @Query("select from SingleValueVertex")
    Vertex getSingleValueVertex()
}