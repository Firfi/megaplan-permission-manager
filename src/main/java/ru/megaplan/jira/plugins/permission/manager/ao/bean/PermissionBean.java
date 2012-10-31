package ru.megaplan.jira.plugins.permission.manager.ao.bean;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 29.05.12
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */
@Preload
public interface PermissionBean extends Entity  {
    boolean getOr();
    void setOr(boolean or);

    String getUserName();
    void setUserName(String userName);

    String getGroupName();
    void setGroupName(String groupName);

    String getProjectRoleName();
    void setProjectRoleName(String projectRoleName);

    String getProjectKey();
    void setProjectKey(String projectKey);

    @NotNull
    public PermissionGroup getPermissionGroup();
    public void setPermissionGroup(PermissionGroup permissionGroup);

}
