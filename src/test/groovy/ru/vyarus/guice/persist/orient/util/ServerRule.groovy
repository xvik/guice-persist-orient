package ru.vyarus.guice.persist.orient.util

import com.orientechnologies.common.log.OLogManager
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.server.OServerMain
import org.junit.rules.ExternalResource
import org.junit.rules.TemporaryFolder
import ru.vyarus.guice.persist.orient.db.OrientDBFactory
import ru.vyarus.guice.persist.orient.support.Config

/**
 * Supposed to be used as @ClassRule. To switch to remote db call initRemoteDb() in setup() (to re-create db before each test)
 * To switch to memory db between tests use reset().
 *
 * @author Vyacheslav Rusakov 
 * @since 02.05.2015
 */
class ServerRule extends ExternalResource {

    TemporaryFolder folder = new TemporaryFolder()

    static String remoteUrl = "remote:localhost/test"
    static String memoryUrl = "memory:test"

    @Override
    protected void before() throws Throwable {
        startServer()
    }

    public void startServer() {
        folder.create()
        System.setProperty("ORIENTDB_HOME", folder.root.getAbsolutePath());
        System.setProperty("orientdb.www.path", "")
        OGlobalConfiguration.SERVER_SECURITY_FILE.setValue("src/test/resources/ru/vyarus/guice/persist/orient/security.json")
        OGlobalConfiguration.SERVER_BACKWARD_COMPATIBILITY.setValue(false)

        // TODO for now reverted 3.2 behaviours, but no-users mode should be supported directly
        OGlobalConfiguration.SCRIPT_POLYGLOT_USE_GRAAL.setValue(false)
        OGlobalConfiguration.CREATE_DEFAULT_USERS.setValue(true)

        OServerMain
        // have to use shutdownEngineOnExit=false and manually shutdown engine to prevent log manager shutdown (which is impossible to recover)
                .create(false)
                .startup(getClass().getResourceAsStream("/ru/vyarus/guice/persist/orient/server-config.xml") as InputStream)
                .activate();
        println 'remote server started'
    }

    @Override
    protected void after() {
        stopServer()
    }

    public void stopServer() {
        OServerMain.server().shutdown()
        // no way to reset shutdown state properly
        OLogManager.instance.shutdownFlag.set(false)
        folder.delete()
        // re-init engines, without it following in memory tests will fail
        reset()
        println 'remote server shut down'
    }

    public static void setRemoteConf(String id) {
        Config.DB = remoteUrl + id
        Config.USER = "root"
        Config.PASS = "root"

        OrientDBFactory.enableAutoCreationRemoteDatabase('root', 'root', ODatabaseType.MEMORY)
    }

    public static void reset() {
        Config.DB = memoryUrl
        Config.USER = "admin"
        Config.PASS = "admin"
        OrientDBFactory.disableAutoCreationRemoteDatabase()
    }
}
