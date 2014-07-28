package ru.vyarus.guice.persist.orient.support.service

import com.google.inject.Provider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import org.apache.commons.logging.LogFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.vyarus.guice.persist.orient.db.data.DataInitializer
import ru.vyarus.guice.persist.orient.support.model.Model

import javax.inject.Inject

/**
 * Initialize database with test data.
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
@javax.inject.Singleton
class TestDataInitializer implements DataInitializer {
    Logger logger = LoggerFactory.getLogger(TestDataInitializer)

    @Inject
    Provider<OObjectDatabaseTx> provider

    @Override
    @Transactional
    void initializeData() {
        final  OObjectDatabaseTx db = provider.get()
        10.times({
            db.save(new Model(name: "name$it", nick: "nick$it"))
        })
        logger.info("Db initialized with 10 records")
    }
}
