package ru.vyarus.guice.persist.orient.study.index.ci

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * @author Vyacheslav Rusakov 
 * @since 12.06.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface IndexCiRepository {

    @Query("create index Test.foo on Test (foo collate ci) notunique")
    void createCiIndex()

    @Query("create index Test.foo on Test (foo) notunique")
    void createNonCiIndex()

    @Query("select from Test where foo = ?")
    List<Test> select(String text)

    @Query("select from index:Test.foo where key = ?")
    List<Test> selectByIndex(String text)
}