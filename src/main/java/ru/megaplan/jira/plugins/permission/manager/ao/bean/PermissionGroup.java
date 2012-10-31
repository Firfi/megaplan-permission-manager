package ru.megaplan.jira.plugins.permission.manager.ao.bean;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Unique;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 29.05.12
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public interface PermissionGroup extends Entity {
    @OneToMany
    PermissionBean[] getPermissions();
    @NotNull
    @Unique
    String getName();
    void setName(String s);
}
