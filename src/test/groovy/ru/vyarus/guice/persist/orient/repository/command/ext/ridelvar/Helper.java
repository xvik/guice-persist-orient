package ru.vyarus.guice.persist.orient.repository.command.ext.ridelvar;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;
import ru.vyarus.guice.persist.orient.support.model.VertexModel;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud;

import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 03.06.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class Helper implements ObjectCrud<VertexModel> {

    @Query("insert into VertexModel (name) values (?)")
    public abstract VertexModel createNode(String name);

    @Query("create edge EdgeModel from ${from} to ${to}")
    public abstract void connect(@RidElVar("from") VertexModel form, @RidElVar("to") VertexModel to);

    @Query("select from VertexModel where name = ?")
    public abstract VertexModel findByName(String name);

    @Query("select from VertexModel where @rid = ?")
    public abstract Vertex getVertex(String rid);

    @Query("select from VertexModel where @rid = ?")
    public abstract ODocument getDocument(String rid);

    @Query("select from VertexModel")
    public abstract List<VertexModel> all();

    public void createPair() {
        VertexModel from = createNode("from");
        VertexModel to = createNode("to");
        connect(from, to);
    }

}
