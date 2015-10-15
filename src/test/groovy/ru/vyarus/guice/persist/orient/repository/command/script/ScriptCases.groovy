package ru.vyarus.guice.persist.orient.repository.command.script

import com.google.inject.ProvidedBy
import com.google.inject.internal.DynamicSingletonProvider
import com.google.inject.persist.Transactional

/**
 * @author Vyacheslav Rusakov 
 * @since 25.02.2015
 */
@Transactional
@ProvidedBy(DynamicSingletonProvider)
interface ScriptCases {

    @Script("""let model = select from Model where name='first'
            return \$model.nick""")
    String nick()

    // positional parameters not supported before 2.0.7, but they could be referenced as named (':0')
    @Script("""
        update model set name = ?
        """)
    void positional(String name)

    @Script("""
            begin
            let model = select from Model where name='first'
            commit retry 100
            return \$model.nick""")
    String nickUnderTransaction()


    @Script(language = "javascript", value = """
            for( i = 0; i < 1000; i++ ){
                db.command('insert into Model(name) values ("test'+i+'")');
            }
            """)
    void jsScript()
}
