package ru.megaplan.jira.plugins.permission.manager.ao;

import com.atlassian.activeobjects.tx.Transactional;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionGroupMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 30.05.12
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
@Transactional
public interface MegaPermissionGroupManager {
    IPermissionGroupMock getPermissionGroup(String groupname);
    IPermissionMock getUniquePermission(IPermissionMock permissionMock);
    IPermissionMock getPermission(IPermissionMock permissionMock);
    Collection<IPermissionGroupMock> getAllPermissionGroups();
    void deletePermission(int id);
    void deletePermissionGroup(int id);
    IPermissionMock getNewPermissionMock();
    IPermissionGroupMock getNewPermissionGroupMock();
    void fireGroupUpdate(String groupname);
}
