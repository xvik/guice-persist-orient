package ru.vyarus.guice.persist.orient.support

import org.slf4j.bridge.SLF4JBridgeHandler
import ru.vyarus.guice.persist.orient.db.util.DBUriUtils

import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger

/**
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
class Config {

    static {
        // redirect orient logs into slf4j
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
        Logger.getLogger("global").setLevel(Level.WARNING);
    }

    public static String MODEL_PKG = "ru.vyarus.guice.persist.orient.support.model"
    public static String DB = "memory:test"
    public static String USER = "admin"
    public static String PASS = "admin"

    public static String getDbUrl() {
        DBUriUtils.parseUri(DB)[0]
    }

    public static String getDbName() {
        DBUriUtils.parseUri(DB)[1]
    }
}