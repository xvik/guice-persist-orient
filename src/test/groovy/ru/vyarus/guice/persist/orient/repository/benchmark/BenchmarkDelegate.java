package ru.vyarus.guice.persist.orient.repository.benchmark;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.vyarus.guice.persist.orient.support.model.Model;

import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 28.10.2014
 */
@Singleton
@Transactional
public class BenchmarkDelegate {

    @Inject
    private Provider<ODatabaseObject> provider;

    public List<Model> findAll() {
        return provider.get().query(new OSQLSynchQuery<Object>("select from Model"));
    }
}
