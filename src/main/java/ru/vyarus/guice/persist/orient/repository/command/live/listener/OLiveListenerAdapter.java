package ru.vyarus.guice.persist.orient.repository.command.live.listener;

import com.orientechnologies.orient.core.sql.query.OLiveResultListener;
import com.orientechnologies.orient.core.sql.query.OLocalLiveResultListener;

/**
 * Class is required only to open {@link OLocalLiveResultListener} constructor and be able to use it in the extension.
 *
 * @author Vyacheslav Rusakov
 * @since 28.09.2017
 */
public class OLiveListenerAdapter extends OLocalLiveResultListener {

    public OLiveListenerAdapter(final OLiveResultListener underlying) {
        super(underlying);
    }
}
