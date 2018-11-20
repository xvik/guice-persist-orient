package ru.vyarus.guice.persist.orient.repository.benchmark

import com.google.inject.Provider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import ru.vyarus.guice.persist.orient.support.model.Model

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 28.10.2014
 */
@javax.inject.Singleton
@Transactional
class BenchmarkDelegate {
    @Inject
    private Provider<ODatabaseObject> provider;

    public List<Model> findAll() {
        return provider.get().query(new OSQLSynchQuery<Object>("select from Model"));
    }
}
