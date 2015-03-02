package ru.vyarus.guice.persist.orient.repository.core.ext.result.ext.off

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional
import ru.vyarus.guice.persist.orient.repository.command.query.Query
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.off.NoConversion
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 02.03.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface NoConversionCases {

    @Query("select from Model")
    Model select()

    // method will fail, because default converter was responsible for list conversion
    @Query("select from Model")
    @NoConversion
    Model selectNoConversion()

}