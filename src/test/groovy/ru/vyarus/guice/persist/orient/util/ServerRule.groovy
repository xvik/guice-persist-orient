package ru.vyarus.guice.persist.orient.util

import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.core.Orient
import com.orientechnologies.orient.server.OServer
import com.orientechnologies.orient.server.OServerMain
import org.junit.rules.ExternalResource
import org.junit.rules.TemporaryFolder
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
    OServer server

    static String remoteUrl = "remote:localhost/test"
    static String memoryUrl = "memory:test"

    @Override
    protected void before() throws Throwable {
        startServer()
    }

    public void startServer() {
        folder.create()
        System.setProperty("ORIENTDB_HOME", folder.root.getAbsolutePath());
        System.setProperty("orientdb.www.path", "");
        server = OServerMain
                .create()
                .startup(getClass().getResourceAsStream("/ru/vyarus/guice/persist/orient/server-config.xml") as InputStream)
                .activate();
    }

    @Override
    protected void after() {
        stopServer()
    }

    public void stopServer() {
        OServerMain.server().shutdown()
        folder.delete()
        // re-init engines, without it following in memory tests will fail
        Orient.instance().startup()
        reset()
    }

    public void initRemoteDb() {
        OServerAdmin admin = new OServerAdmin(remoteUrl).connect('root', 'root')
        if (admin.existsDatabase()) {
            admin.dropDatabase("memory")
        }
        admin.createDatabase('graph', 'memory').close()
        setRemoteConf()
    }

    public static void setRemoteConf() {
        Config.DB = remoteUrl
    }

    public static void reset() {
        Config.DB = memoryUrl
    }
}
