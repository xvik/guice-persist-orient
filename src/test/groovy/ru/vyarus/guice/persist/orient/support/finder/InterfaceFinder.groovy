package ru.vyarus.guice.persist.orient.support.finder

import com.google.inject.persist.Transactional
import com.google.inject.persist.finder.Finder
import com.google.inject.persist.finder.FirstResult
import com.google.inject.persist.finder.MaxResults
import com.orientechnologies.orient.core.record.impl.ODocument
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.finder.Use
import ru.vyarus.guice.persist.orient.support.model.Model

import javax.inject.Named

/**
 * Contain all possible usage cases.
 *
 * @author Vyacheslav Rusakov 
 * @since 31.07.2014
 */
@Transactional
public interface InterfaceFinder {

    // -------------------------------------------- db type recognition

    // object db
    @Finder(query = "select from Model")
    List<Model> selectAll()

    // object db
    @Finder(query = "select from Model")
    Model[] selectAllAsArray()

    // single element select
    @Finder(query = "select from Model")
    Model selectUnique()

    // document db
    @Finder(query = "select from Model")
    List<ODocument> selectAllAsDocument()

    // graph db
    @Finder(query = "select from Model")
    List<Vertex> selectAllAsVertex()

    // document db by default
    @Finder(query = "update Model set name='changed'")
    void update()

    // explicitly define to use object connection instead of default document one
    @Finder(query = "update Model set name='changed'")
    @Use(DbType.OBJECT)
    void updateUsingObjectConnection()

    // document db (same as previous but return updated entities count)
    @Finder(query = "update Model set name='changed'")
    int updateWithCount()

    // document db (same as previous but return updated entities count)
    @Finder(query = "update Model set name='changed'")
    Integer updateWithCountObject()

    // no generic - document db will be selected
    @Finder(query = "select from Model")
    List selectAllNoType()

    // -------------------------------------------- function recognition

    // recognize as function call
    @Finder(namedQuery = "function1")
    List<Model> function();

    // fail because of confusing definition
    @Finder(namedQuery = "function1", query = "select from Model")
    List<Model> functionWrongDefinition();

    // -------------------------------------------- parameters recognition

    // positional parameters
    @Finder(query = "select from Model where name=? and nick=?")
    List<Model> parametersPositional(String name, String nick)

    // named parameters (using both possible annotations)
    @Finder(query = "select from Model where name=:name and nick=:nick")
    List<Model> parametersNamed(@Named("name") String name, @com.google.inject.name.Named("nick") String nick)

    // recognized as positional params because of first parameter
    @Finder(query = "select from Model where name=? and nick=?")
    List<Model> parametersPositionalWithWarning(String name, @Named("nick") String nick)

    // error because all parameters expect to be named, because of first one
    @Finder(query = "select from Model where name=:name and nick=:nick")
    List<Model> parametersNames(@Named("name") String name, String nick)

    // error - duplicate parameter name
    @Finder(query = "select from Model where name=:name and nick=:nick")
    List<Model> parametersNamesDuplicateName(@Named("name") String name, @Named("name") String nick)

    // with page definition
    @Finder(query = "select from Model where name=? and nick=?")
    List<Model> parametersPaged(String name, String nick, @FirstResult int start, @MaxResults int max);

    // with page definition (as objects)
    @Finder(query = "select from Model where name=? and nick=?")
    List<Model> parametersPagedObject(String name, String nick, @FirstResult Long start, @MaxResults Long max);

    // error - not allowed duplicate definition
    @Finder(query = "select from Model where name=? and nick=?")
    List<Model> parametersPagedDouble(String name, String nick, @FirstResult int start, @FirstResult int max);

    // error - page definition must be numbers
    @Finder(query = "select from Model where name=? and nick=?")
    List<Model> parametersPagedWrongType(String name, String nick, @FirstResult String start, @MaxResults int max);

    // error - page definition must be numbers
    @Finder(query = "select from Model where name=? and nick=?")
    List<Model> parametersPagedWrongType2(String name, String nick, @FirstResult int start, @MaxResults String max);

    // using parameters in update query
    @Finder(query = "update Model set name=? where name=?")
    int updateWithParam(String toName, String whereName);
}