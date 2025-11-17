package ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.support;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.connection.Connection;
import ru.vyarus.guice.persist.orient.support.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 23.02.2015
 */
public class ConnectionDelegate {

    public List<Model> rawConnection(@Connection Object db) {
        Model model = new Model();
        model.setName("rawConnection");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    public List<Model> subtypeMatch(@Connection ODatabase db) {
        Model model = new Model();
        model.setName("subtypeMatch");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    public List<Model> exactConnection(@Connection ODatabaseObject db) {
        Model model = new Model();
        model.setName("exactConnection");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    // error: incompatible type
    public List<Model> incompatible(@Connection OrientGraph db) {
        return null;
    }

    // error: duplicate definition
    public List<Model> duplicate(@Connection ODatabaseObject db, @Connection ODatabaseObject db2) {
        return null;
    }

}
