package ru.vyarus.guice.persist.orient.support.finder

import com.google.inject.persist.Transactional
import com.google.inject.persist.finder.Finder
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.finder.Use

/**
 * Examples of sql projections conversions.
 *
 * @author Vyacheslav Rusakov 
 * @since 01.11.2014
 */
@Transactional
public interface FinderResultProjections {

    // orient returns ODocument with singe field, but it will be recognized and "unwrapped"
    @Finder(query = "select count(@rid) from Model")
    int getCount()

    // automatic projection will not work here, but due to type erasure this will work even in runtime,
    // except that actual result would be List<ODocument>
    @Finder(query = "select name from Model")
    List<String> getNames()

    // orient will return list of ODocument and result will be "unwrapped" to raw values
    @Finder(query = "select name from Model")
    String[] getNamesArray()

    // tricky case: query return list, but as soon as one result expected only first element taken
    // and projection will work on it
    @Finder(query = "select name from Model")
    String getOneName()

    // in graph connections Vertex returned instead of document

    @Finder(query = "select name from VertexModel")
    @Use(DbType.GRAPH)
    String[] getGraphNamesArray()

    @Finder(query = "select name from VertexModel")
    @Use(DbType.GRAPH)
    List<String> getGraphNames()

    @Finder(query = "select count(@rid) from VertexModel")
    @Use(DbType.GRAPH)
    int getGraphCount()
}