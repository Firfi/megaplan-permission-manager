package ru.megaplan.jira.plugins.permission.manager.event;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/17/12
 * Time: 2:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultConfigurationUpdatedEvent implements ConfigurationUpdatedEvent {

    private String permissionGroup;

    public DefaultConfigurationUpdatedEvent() {
    }

    public DefaultConfigurationUpdatedEvent(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }
}
