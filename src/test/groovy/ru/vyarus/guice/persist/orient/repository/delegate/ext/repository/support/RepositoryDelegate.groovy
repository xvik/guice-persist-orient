package ru.vyarus.guice.persist.orient.repository.delegate.ext.repository.support

import ru.vyarus.guice.persist.orient.repository.delegate.ext.instance.Repository
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
class RepositoryDelegate {

    List<Model> repo(@Repository RepositoryRoot repo){
        [new Model(name: 'repo')]
    }

    List<Model> repoCustom(@Repository CustomMixin repo){
        [new Model(name: 'repoCustom')]
    }

    // error: incompatible type
    List<Model> badType(@Repository RepositoryDelegate repo){
    }

    // error: duplicate definition
    List<Model> duplicate(@Repository RepositoryRoot repo, @Repository RepositoryRoot repo2){
    }
}
