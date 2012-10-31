package ru.megaplan.jira.plugins.permission.manager.ao.bean.mock;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/3/12
 * Time: 12:32 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IPermissionMock {

    public int getID();
    public void setID(int id);

    public String getProjectRoleName() ;

    public void setProjectRoleName(String projectRoleName) ;

    public String getUserName();

    public void setUserName(String userName);

    public String getGroupName() ;
    public void setGroupName(String groupName);

    public String getProjectKey();
    public void setProjectKey(String projectKey);

    public IPermissionGroupMock getPermissionGroupMock() ;

    public void setPermissionGroupMock(IPermissionGroupMock permissionGroupMock) ;

    public boolean isValid() ;
}
