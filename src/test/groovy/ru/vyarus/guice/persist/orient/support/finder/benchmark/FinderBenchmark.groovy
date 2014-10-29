package ru.vyarus.guice.persist.orient.support.finder.benchmark

import com.google.inject.persist.Transactional
import com.google.inject.persist.finder.Finder
import ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * Compares performance of raw query execution, finder call and delegated call.
 * Ofc it's not accurate, but to get raw estimation.
 *
 * @author Vyacheslav Rusakov 
 * @since 28.10.2014
 */
@Transactional
interface FinderBenchmark {

    @Finder(query = "select from Model")
    List<Model> findAll()

    @FinderDelegate(BenchmarkDelegate)
    List<Model> findAllDelegate()
}
