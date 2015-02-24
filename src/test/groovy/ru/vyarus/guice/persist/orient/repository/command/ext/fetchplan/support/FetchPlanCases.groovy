package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.FetchPlan
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.ext.CheckCommand
import ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model.Basket
import ru.vyarus.guice.persist.orient.repository.command.query.Query

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
@CheckCommand
interface FetchPlanCases {

    @Query("select from Basket")
    Basket selectBasket(@FetchPlan("*:0") String plan);

    @Query("select from Basket")
    Basket selectBasketNoDefault(@FetchPlan String plan);

    // error: duplicate param
    @Query("select from Basket")
    Basket duplicate(@FetchPlan String plan, @FetchPlan String plan2);

    // error: bad parameter type
    @Query("select from Basket")
    Basket badType(@FetchPlan Integer plan);
}