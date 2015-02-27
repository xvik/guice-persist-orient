package ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar
import ru.vyarus.guice.persist.orient.repository.command.function.Function
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 *
 * @author Vyacheslav Rusakov
 * @since 21.09.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider.class)
interface ElVarsCases {

    // query with single placeholder, guarded with defaults
    @Query('select from Model where ${field} = ?')
    Model findByField(@ElVar(value = "field", allowedValues = ["name", "nick"]) String field, String value);

    // query with two placeholders, guarded with defaults
    @Query('select from Model where ${field1} = ? and ${field2} = ?')
    Model findByTwoFields(@ElVar(value = "field1", allowedValues = ["name", "nick"]) String field1,
                          @ElVar(value = "field2", allowedValues = ["name", "nick"]) String field2,
                          String value1, String value2);

    // use enum as var (no need for explicit values definition)
    @Query('select from Model where ${field} = ?')
    Model findByEnumField(@ElVar("field") VarDefinitionEnum field, String value);

    // dynamic function name
    @Function('func${name}')
    List<Model> functionWithPlaceholder(@ElVar("name") String name);

    // dynamic function name with enum
    @Function('func${name}')
    List<Model> functionWithEnum(@ElVar("name") VarDefinitionEnum name);

    // explicitly declare string as safe to avoid warning
    @Function('func${name}')
    List<Model> safeString(@ElVar(value = "name", safe = true) String name);

    // integer var
    @Function('func${name}')
    List<Model> intVar(@ElVar("name") int name);

    // integer var
    @Function('func${name}')
    List<Model> integerVar(@ElVar("name") Integer name);

    // object var
    @Function('func${name}')
    List<Model> objVar(@ElVar("name") ObjVar name);

    @Query('select from ${type}')
    List<Model> classVar(@ElVar("type") Class<?> type);
}
