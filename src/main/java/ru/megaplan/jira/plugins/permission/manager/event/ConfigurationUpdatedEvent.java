package ru.megaplan.jira.plugins.permission.manager.event;

import javax.annotation.Nullable;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/17/12
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ConfigurationUpdatedEvent {
    @Nullable
    String getPermissionGroup();
}
