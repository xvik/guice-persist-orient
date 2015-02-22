package ru.vyarus.guice.persist.orient.repository.benchmark

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicClassProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.delegate.Delegate
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * Compares performance of raw query execution, repository call and delegated call.
 * Ofc it's not accurate, but to get raw estimation.
 *
 * @author Vyacheslav Rusakov 
 * @since 28.10.2014
 */
@Transactional
@ProvidedBy(DynamicClassProvider)
interface RepositoryBenchmark {

    @Query("select from Model")
    List<Model> findAll()

    @Delegate(BenchmarkDelegate)
    List<Model> findAllDelegate()
}
