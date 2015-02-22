package ru.vyarus.guice.persist.orient.repository.core.ext.support

import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.CmdAmend
import ru.vyarus.guice.persist.orient.repository.core.ext.support.exts.DelegateAmend

/**
 * Checking amend extension correct recognition and appliance.
 *
 * @author Vyacheslav Rusakov 
 * @since 22.02.2015
 */
// extensions will be applied to both methods in mixin, but filtered as duplicate and not compatible
@CmdAmend
@DelegateAmend
interface Root extends Mixin {

}