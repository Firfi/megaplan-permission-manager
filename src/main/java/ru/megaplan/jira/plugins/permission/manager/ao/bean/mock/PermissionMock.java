package ru.megaplan.jira.plugins.permission.manager.ao.bean.mock;

import org.apache.log4j.Logger;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionBean;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionGroup;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
* Created with IntelliJ IDEA.
* User: Firfi
* Date: 6/3/12
* Time: 7:39 PM
* To change this template use File | Settings | File Templates.
*/
public class PermissionMock implements IPermissionMock {




    private static Logger log = Logger.getLogger(PermissionMock.class);

    private int ID;

    private String userName;

    private String groupName;

    private String projectRoleName;

    private IPermissionGroupMock permissionGroupMock;

    private String projectKey;

    public PermissionMock() {

    }

    private PermissionMock(PermissionBean permissionBean) {
        ID = permissionBean.getID();
        userName = permissionBean.getUserName();
        groupName = permissionBean.getGroupName();
        projectRoleName = permissionBean.getProjectRoleName();
        projectKey = permissionBean.getProjectKey();
    }

    public PermissionMock(PermissionGroup pg, PermissionBean permissionBean) {
        this(permissionBean);
        permissionGroupMock = new PermissionGroupMock(pg);
    }

    public PermissionMock(PermissionGroupMock pgm, PermissionBean permissionBean) {
        this(permissionBean);
        permissionGroupMock = pgm;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void setID(int id) {
         ID = id;
    }

    public String getProjectRoleName() {
        return projectRoleName;
    }

    public void setProjectRoleName(String projectRoleName) {
        this.projectRoleName = projectRoleName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public IPermissionGroupMock getPermissionGroupMock() {
        return permissionGroupMock;
    }

    public void setPermissionGroupMock(IPermissionGroupMock permissionGroupMock) {
        this.permissionGroupMock = permissionGroupMock;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public boolean isValid() {
        if (permissionGroupMock == null) return false;
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName()) && !"permissionGroupMock".equals(pd.getName()))
                    try {
                        if (pd.getReadMethod().invoke(this) != null) return true;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return false;
    }

}
