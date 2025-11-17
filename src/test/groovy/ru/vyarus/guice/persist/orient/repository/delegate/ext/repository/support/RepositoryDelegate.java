package ru.vyarus.guice.persist.orient.repository.delegate.ext.repository.support;

import ru.vyarus.guice.persist.orient.repository.delegate.ext.instance.Repository;
import ru.vyarus.guice.persist.orient.support.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Vyacheslav Rusakov
 * @since 23.02.2015
 */
public class RepositoryDelegate {
    public List<Model> repo(@Repository RepositoryRoot repo) {
        Model model = new Model();
        model.setName("repo");

        return new ArrayList<Model>(Arrays.asList(model));
    }

    public List<Model> repoCustom(@Repository CustomMixin repo) {
        Model model = new Model();
        model.setName("repoCustom");
        return new ArrayList<Model>(Arrays.asList(model));
    }

    // error: incompatible type
    public List<Model> badType(@Repository RepositoryDelegate repo) {
        return null;
    }

    // error: duplicate definition
    public List<Model> duplicate(@Repository RepositoryRoot repo, @Repository RepositoryRoot repo2) {
        return null;
    }

}
