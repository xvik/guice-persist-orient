package ru.vyarus.guice.persist.orient.repository.command.support;

import com.google.common.base.Optional;
import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.google.inject.persist.Transactional;
import ru.vyarus.guice.persist.orient.repository.command.query.Query;
import ru.vyarus.guice.persist.orient.support.model.Model;

/**
 * @author Vyacheslav Rusakov
 * @since 14.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider.class)
public interface OptionalCases {

    // use guava optional
    @Query("select from Model")
    Optional<Model> findGuavaOptional();

    // check empty collection result conversion to single element
    @Query("select from Model where name='not existent'")
    Optional<Model> emptyCollection();
}
