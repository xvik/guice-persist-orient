package ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.support;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic;
import ru.vyarus.guice.persist.orient.support.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Using abstract class to tie delegate to interface (even if no methods are directly implemented)
 *
 * @author Vyacheslav Rusakov
 * @since 23.02.2015
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class GenericMixinDelegate implements GenericMixin {

    public List getAll(@Generic("T") Class<Model> type) {
        Model model = new Model();
        model.setName("getAll");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    // generic lookup on specific class
    public List getAll2(@Generic(value = "P", genericHolder = OtherMixin.class) Class<Model> type) {
        Model model = new Model();
        model.setName("getAll2");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    // duplicate generic is allowed - too obvious error
    public List duplicateGeneric(@Generic("T") Class<Model> type, @Generic("T") Class<Model> type2) {
        Model model = new Model();
        model.setName("duplicateGeneric");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    // error: lookup generic on type, not present in repository hierarchy
    public List lookupError(@Generic(value = "P", genericHolder = Callable.class) Class<Model> type) {
        Model model = new Model();
        model.setName("tst");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    // error: bad generic name
    public List genericError(@Generic("E") Class<Model> type) {
        return null;
    }

    // error: incompatible generic type
    public List genericTypeError(@Generic("T") Object type) {
        return null;
    }

}
