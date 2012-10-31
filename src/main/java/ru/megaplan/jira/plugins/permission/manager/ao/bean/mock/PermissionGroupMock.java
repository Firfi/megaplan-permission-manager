package ru.megaplan.jira.plugins.permission.manager.ao.bean.mock;

import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionBean;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: Firfi
* Date: 6/3/12
* Time: 7:40 PM
* To change this template use File | Settings | File Templates.
*/
public class PermissionGroupMock implements IPermissionGroupMock {

    private int ID;

    private List<IPermissionMock> permissionMocks;
    private String name;

    public PermissionGroupMock() {

    }

    public PermissionGroupMock(PermissionGroup pg) {
        ID = pg.getID();
        name = pg.getName();
        PermissionBean[] perms = pg.getPermissions();
        List<PermissionBean> lpb = Arrays.asList(perms);
        permissionMocks = new ArrayList<IPermissionMock>();
        for (PermissionBean pb : lpb) {
            permissionMocks.add(new PermissionMock(this, pb));
        }
    }

    @Override
    public List<IPermissionMock> getPermissions() {
        return permissionMocks;
    }

    @Override
    public void setPermissions(List<IPermissionMock> mocks) {
        this.permissionMocks = mocks;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void setID(int id) {
        ID = id;
    }

}
