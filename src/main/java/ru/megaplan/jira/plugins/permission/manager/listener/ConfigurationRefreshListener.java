package ru.megaplan.jira.plugins.permission.manager.listener;

import com.atlassian.plugin.event.PluginEventListener;
import org.apache.log4j.Logger;
import ru.megaplan.jira.plugins.permission.manager.event.DefaultConfigurationUpdatedEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/17/12
 * Time: 1:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationRefreshListener {

    private static final Logger log = Logger.getLogger(ConfigurationRefreshListener.class);

    @PluginEventListener
    public void onPluginRefreshedEvent(DefaultConfigurationUpdatedEvent event) {
        log.debug("refresh event found");
        if (event == null) return;
        log.debug("perm group updated : " + event.getPermissionGroup());
    }
}
