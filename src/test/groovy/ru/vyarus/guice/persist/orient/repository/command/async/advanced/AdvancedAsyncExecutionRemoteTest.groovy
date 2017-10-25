package ru.vyarus.guice.persist.orient.repository.command.async.advanced

import ru.vyarus.guice.persist.orient.util.remoteext.UseRemote

/**
 * @author Vyacheslav Rusakov
 * @since 18.10.2017
 */
@UseRemote
class AdvancedAsyncExecutionRemoteTest extends AdvancedAsyncExecutionTest {

    void setupSpec() {
        remote = true
    }
}
