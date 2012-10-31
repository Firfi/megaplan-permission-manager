package ru.megaplan.jira.plugins.permission.manager.action.admin;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.megaplan.jira.plugins.permission.manager.ao.MegaPermissionGroupManager;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionGroupMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Firfi
 * Date: 6/2/12
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureMegaPermissionsAction extends JiraWebActionSupport {

    private final MegaPermissionGroupManager megaPermissionGroupManager;
    private final UserManager userManager;
    private final GroupManager groupManager;
    private final ProjectRoleManager projectRoleManager;
    private final ProjectManager projectManager;


    public ConfigureMegaPermissionsAction(MegaPermissionGroupManager megaPermissionGroupManager,
                                          UserManager userManager,
                                          GroupManager groupManager,
                                          ProjectRoleManager projectRoleManager,
                                          ProjectManager projectManager) {
        this.megaPermissionGroupManager = megaPermissionGroupManager;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.projectRoleManager = projectRoleManager;
        this.projectManager = projectManager;

    }

    private IPermissionGroupMock[] permissionGroups;

    private String userName;
    private String projectRoleName;
    private String groupName;
    private String projectKey;

    private int permissionId;


    private String permissionGroupName;
    private boolean add;


    boolean hasPermission() {
        return isHasPermission(Permissions.ADMINISTER);
    }

    @Override
    public void doValidation() {
        if (userName != null && !userName.isEmpty()) {
            User u = userManager.getUser(userName);
            if (u == null) {
                this.addError("userName","User does not exist");
            }
        }
        if (groupName != null && !groupName.isEmpty()) {
            Group g = groupManager.getGroup(groupName);
            if (g == null) {
                this.addError("groupName","Group does not exist");
            }
        }
        if (projectRoleName != null && !projectRoleName.isEmpty()) {
            ProjectRole pr = projectRoleManager.getProjectRole(projectRoleName);
            log.warn("projectRoleName " + projectRoleName);
            if (pr == null) {
                this.addError("projectRoleName", "ProjectRole does not exist");
            }
        }
        if (projectKey != null && !projectKey.isEmpty()) {
            Project p = projectManager.getProjectObjByKeyIgnoreCase(projectKey);
            if (p == null) this.addError("projectKey", "Project does not exist");
        }
        if (permissionGroupName == null || permissionGroupName.isEmpty())
            this.addErrorMessage("where is permission group?");
        log.warn("validation");
    }

    @Override
    public String doDefault() {
        if (!hasPermission()) return PERMISSION_VIOLATION_RESULT;
        Collection<IPermissionGroupMock> pgs = megaPermissionGroupManager.getAllPermissionGroups();
        for (IPermissionGroupMock pe : pgs) {
            log.debug(pe.getID() + pe.getName());
        }
        permissionGroups = pgs.toArray(new IPermissionGroupMock[pgs.size()]);
        return SUCCESS;
    }



    public String doAddPermission() {
        if (!hasPermission()) return PERMISSION_VIOLATION_RESULT;

        doValidation();
        if (!add || hasAnyErrors()) {
            return INPUT;
        }

        IPermissionGroupMock pg = megaPermissionGroupManager.getPermissionGroup(permissionGroupName);
        IPermissionMock pm = megaPermissionGroupManager.getNewPermissionMock();
        if (userName != null && !userName.isEmpty()) pm.setUserName(userName);
        if (groupName != null && !groupName.isEmpty()) pm.setGroupName(groupName);
        if (projectRoleName != null && !projectRoleName.isEmpty()) pm.setProjectRoleName(projectRoleName);
        if (projectKey != null && !projectKey.isEmpty()) pm.setProjectKey(projectKey);
        pm.setPermissionGroupMock(pg);
        if (pm.isValid()) {
            IPermissionMock pb = megaPermissionGroupManager.getUniquePermission(pm);
            log.debug("PB ID : " + pb.getID() + pb.getGroupName() + pb.getUserName() + pb.getGroupName());
        }
        notifyUpdate(permissionGroupName);
        return getRedirect("MegaPermissionsConfig!default.jspa");
    }

    public String doDeletePermission() {
        if (!hasPermission()) return PERMISSION_VIOLATION_RESULT;
        IPermissionMock permissionMock = megaPermissionGroupManager.getNewPermissionMock();
        permissionMock.setID(permissionId);
        IPermissionMock permission = megaPermissionGroupManager.getPermission(permissionMock);
        megaPermissionGroupManager.deletePermission(permissionId);
        log.warn("deleting permission with group : " + permission.getGroupName());
        notifyUpdate(permission.getPermissionGroupMock().getName());
        return getRedirect("MegaPermissionsConfig!default.jspa");
    }

    private void notifyUpdate(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            log.warn("groupname is null/empty");
            return;
        }
        megaPermissionGroupManager.fireGroupUpdate(groupName);
    }


    public IPermissionGroupMock[] getPermissionGroups() {
        return permissionGroups;
    }


    public String[] getPermissionHeaders() {
        return new String[] {"User name","Group","Project role","Project","Or"};
    }

    public String[] getPermissionBeanValues(IPermissionMock pb) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        List<String> result = new ArrayList<String>();
        result.add(pb.getUserName());
        result.add(pb.getGroupName());
        result.add(pb.getProjectRoleName());
        result.add(pb.getProjectKey());
        result.add(Boolean.toString(false));
        return result.toArray(new String[result.size()]);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectRoleName() {
        return projectRoleName;
    }

    public void setProjectRoleName(String projectRoleName) {
        this.projectRoleName = projectRoleName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPermissionGroupName() {
        return permissionGroupName;
    }

    public void setPermissionGroupName(String permissionGroupName) {
        this.permissionGroupName = permissionGroupName;
    }


    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
