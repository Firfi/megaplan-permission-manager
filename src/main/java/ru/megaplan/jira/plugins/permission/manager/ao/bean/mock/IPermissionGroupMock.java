package ru.megaplan.jira.plugins.permission.manager.ao.bean.mock;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/3/12
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IPermissionGroupMock {
    public String getName();
    public void setName(String s);
    public int getID();
    public void setID(int id);

    List<IPermissionMock> getPermissions();

    void setPermissions(List<IPermissionMock> mocks);
}
