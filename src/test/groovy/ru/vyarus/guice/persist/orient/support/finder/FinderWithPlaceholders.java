package ru.vyarus.guice.persist.orient.support.finder;

import com.google.inject.persist.Transactional;
import com.google.inject.persist.finder.Finder;
import ru.vyarus.guice.persist.orient.finder.placeholder.Placeholder;
import ru.vyarus.guice.persist.orient.finder.placeholder.PlaceholderValues;
import ru.vyarus.guice.persist.orient.finder.placeholder.Placeholders;
import ru.vyarus.guice.persist.orient.support.model.Model;

import java.util.List;

/**
 * Using java file, to remain annotations syntax (a bit different in groovy) for example purposes.
 *
 * @author Vyacheslav Rusakov
 * @since 21.09.2014
 */
@Transactional
public interface FinderWithPlaceholders {

    // query with single placeholder, guarded with defaults
    @Finder(query = "select from Model where ${field} = ?")
    @PlaceholderValues(name = "field", values = {"name", "nick"})
    Model findByField(@Placeholder("field") String field, String value);


    // query with two placeholders, guarded with defaults
    @Finder( query = "select from Model where ${field1} = ? and ${field2} = ?")
    @Placeholders({
          @PlaceholderValues(name="field1", values = {"name", "nick"}),
          @PlaceholderValues(name="field2", values = {"name", "nick"})
    })
    Model findByTwoFields(@Placeholder("field1") String field1, @Placeholder("field2") String field2,
                          String value1, String value2);

    // use enum as placeholder (no need for explicit values definition)
    @Finder(query = "select from Model where ${field} = ?")
    Model findByEnumField(@Placeholder("field") PlaceholdersEnum field, String value);

    // dynamic function name
    @Finder(namedQuery = "func${name}")
    List<Model> functionWithPlaceholder(@Placeholder("name") String name);

    // dynamic function name with enum
    @Finder(namedQuery = "func${name}")
    List<Model> functionWithPlaceholderEnum(@Placeholder("name") PlaceholdersEnum name);

    // -------------------------------------------- errors

    // error - no need for definition if enum used
    @Finder(namedQuery = "func${name}")
    @PlaceholderValues(name="name", values = {"name", "nick"})
    List<Model> functionWithPlaceholderEnumErr(@Placeholder("name") PlaceholdersEnum name);

    // error - defaults for not defined placeholder
    @Finder(query = "select from Model where ${field} = ?")
    @PlaceholderValues(name = "field1", values = {"name", "nick"})
    Model findByFieldErrDefaults(@Placeholder("field") String field, String value);

    // error - unsupported parameter type
    @Finder(query = "select from Model where ${field} = ?")
    @PlaceholderValues(name = "field", values = {"name", "nick"})
    Model findByFieldErrType(@Placeholder("field") Integer field, String value);

    // error - not all placeholders defined
    @Finder(query = "select from Model where ${field1} = ? and ${field2} = ?")
    Model findByFieldsErrParam(@Placeholder("field1") String field, String value, String value2);

    // error - no placeholders defined
    @Finder(query = "select from Model where name = ?")
    Model findByFieldErrNoPlaceholders(@Placeholder("field") String field, String value);

    // error - too many placeholders defined
    @Finder(query = "select from Model where ${field} = ?")
    @PlaceholderValues(name = "field", values = {"name", "nick"})
    Model findByFieldErrTooMany(@Placeholder("field") String field, @Placeholder("field2") String field2, String value);

    // error - duplicate placeholder usage in query
    @Finder(query = "select from Model where ${field} = ? and ${field} = ?")
    @PlaceholderValues(name = "field", values = {"name", "nick"})
    Model findByFieldErrDuplicateDefinition(@Placeholder("field") String field, String value);

    // error - duplicate param placeholder definition
    @Finder(query = "select from Model where ${field} = ? and ${field} = ?")
    @PlaceholderValues(name = "field", values = {"name", "nick"})
    Model findByFieldErrDuplicateParamDefinition(@Placeholder("field") String field, @Placeholder("field") String field2, String value);

    // error - duplicate placeholder defaults definition
    @Finder(query = "select from Model where ${field} = ?")
    @Placeholders({
            @PlaceholderValues(name = "field", values = {"name", "nick"}),
            @PlaceholderValues(name = "field", values = {"name", "nick"})
    })
    Model findByFieldErrDuplicateDefaultsDefinition(@Placeholder("field") String field, String value);
}
