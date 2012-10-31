package ru.megaplan.jira.plugins.permission.manager.ao.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.event.PluginEventManager;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import ru.megaplan.jira.plugins.permission.manager.ao.MegaPermissionGroupManager;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionBean;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.PermissionGroup;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionGroupMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.IPermissionMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.PermissionGroupMock;
import ru.megaplan.jira.plugins.permission.manager.ao.bean.mock.PermissionMock;
import ru.megaplan.jira.plugins.permission.manager.event.DefaultConfigurationUpdatedEvent;
import ru.megaplan.jira.plugins.permission.manager.listener.ConfigurationRefreshListener;

import java.util.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: i.loskutov
 * Date: 30.05.12
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class MegaPermissionGroupManagerImpl implements MegaPermissionGroupManager, DisposableBean {

    private static final String PROJECT_ROLE_NAME = "PROJECT_ROLE_NAME";
    private static final String GROUP_NAME = "GROUP_NAME";
    private static final String USER_NAME = "USER_NAME";
    private static final String PROJECT_KEY = "PROJECT_KEY";
    private static final String PERMISSION_GROUP_ID = "PERMISSION_GROUP_ID";
    private static final String NAME = "NAME";


    private final static Logger log = Logger.getLogger(MegaPermissionGroupManagerImpl.class);

    private final ActiveObjects ao;
    private final PluginEventManager pluginEventManager;

    public MegaPermissionGroupManagerImpl(ActiveObjects ao, PluginEventManager pluginEventManager) {
        this.ao = ao;
        this.pluginEventManager = pluginEventManager;
        pluginEventManager.register(new ConfigurationRefreshListener());
    }

    @Override
    public Collection<IPermissionGroupMock> getAllPermissionGroups() {
        PermissionGroup[] pgs = ao.find(PermissionGroup.class);
        List<IPermissionGroupMock> pgms = new ArrayList<IPermissionGroupMock>();
        for (PermissionGroup pg : pgs) {
            pgms.add(new PermissionGroupMock(pg));
        }
        return pgms;
    }

    @Override
    public void deletePermission(int id) {
        PermissionBean pb = ao.get(PermissionBean.class, id);
        if (pb != null)
            ao.delete(pb);
    }

    @Override
    public void deletePermissionGroup(int id) {
        PermissionGroup pg = ao.get(PermissionGroup.class, id) ;
        if (pg != null)
            ao.delete(pg);
    }

    @Override
    public IPermissionMock getNewPermissionMock() {
        return new PermissionMock();
    }

    @Override
    public IPermissionGroupMock getNewPermissionGroupMock() {
        return new PermissionGroupMock();
    }

    @Override
    public IPermissionGroupMock getPermissionGroup(String groupname) {
        IPermissionGroupMock permissionGroupMock = new PermissionGroupMock();
        permissionGroupMock.setName(groupname);
        return new PermissionGroupMock(getPermissionGroupBean(permissionGroupMock));
    }


    @Override
    public void fireGroupUpdate(String groupname) {
        pluginEventManager.broadcast(new DefaultConfigurationUpdatedEvent(groupname));
    }

    @Override
    public void destroy() throws Exception {
    }

    private PermissionGroup getPermissionGroupBean(IPermissionGroupMock permissionGroupMock) {
        PermissionGroup result;
        if (permissionGroupMock.getID() != 0) {
            result = ao.get(PermissionGroup.class, permissionGroupMock.getID());
            if (result != null) return result;
        }
        checkNotNull(permissionGroupMock.getName());
        PermissionGroup[] pgs = getPgs(permissionGroupMock.getName());
        if (pgs.length == 0) {
            PermissionGroup pg = initPermissionGroup(permissionGroupMock.getName());
            checkNotNull(pg);
            return pg;
        } else
            return pgs[0];
    }
    //dsa


    @Override
    public IPermissionMock getUniquePermission(IPermissionMock permissionMock) {
        checkNotNull(permissionMock);
        IPermissionGroupMock permissionGroupMock = permissionMock.getPermissionGroupMock();
        PermissionGroup pg = getPermissionGroupBean(permissionGroupMock);
        Query q = getPermissionQuery(permissionMock, pg);
        if (q.getWhereClause()== null || q.getWhereClause().isEmpty())
            throw new RuntimeException("error in where clause construction");
        PermissionBean[] pb = ao.find(PermissionBean.class, q);
        if (pb.length > 0) {
            if (pb.length == 1) return new PermissionMock(pg, pb[0]);
            else throw new RuntimeException("permission bean isn't unique!");
        } else {
            return new PermissionMock(pg, createPermissionBean(pg, permissionMock));
        }
    }

    @Override
    public IPermissionMock getPermission(IPermissionMock permissionMock) {
        checkNotNull(permissionMock);
        PermissionBean pb = null;
        PermissionGroup pg = null;
        if (permissionMock.getID() != 0) {
            pb = ao.get(PermissionBean.class,permissionMock.getID());
            if (pb == null) {
                log.error("trying fetch permission group with id : " + permissionMock.getID() + " but it isn't exist");
                return null;
            }
            IPermissionGroupMock permissionGroupMock = getNewPermissionGroupMock();
            permissionGroupMock.setName(pb.getPermissionGroup().getName());
            pg = getPermissionGroupBean(permissionGroupMock);
        }
        else {
            checkNotNull(permissionMock.getPermissionGroupMock());
            if (!permissionMock.isValid()) {
                throw new IllegalArgumentException("permission mock isn't valid");
            }
            pg = getPermissionGroupBean(permissionMock.getPermissionGroupMock());
            PermissionBean[] pbs = findPermissionBeans(pg, permissionMock);
            if (pbs.length == 0) return null;
            if (pbs.length > 1) log.error("permission beans isn't unique : " + Arrays.asList(pbs));
            pb = pbs[0];
        }
        if (pb == null) return null;
        return new PermissionMock(pg, pb);
    }



    /*
    getPermissionQuery method.
    It has side effect : permission group creation is it isn't exist AND
    if its name is set in PermissionMock parameter
     */
    private Query getPermissionQuery(IPermissionMock pm, PermissionGroup pg) {
        Query q = Query.select();
        StringBuilder sb = new StringBuilder();
        MutableBoolean and = new MutableBoolean(false);
        List<Object> notNullList = new ArrayList<Object>();
        notNullList.add(addQueryParam(sb, and, PROJECT_ROLE_NAME, pm.getProjectRoleName()));
        notNullList.add(addQueryParam(sb, and, GROUP_NAME, pm.getGroupName()));
        notNullList.add(addQueryParam(sb, and, USER_NAME, pm.getUserName()));
        notNullList.add(addQueryParam(sb, and, PROJECT_KEY, pm.getProjectKey()));
        notNullList.add(addQueryParam(sb, and, PERMISSION_GROUP_ID, pg.getID()));

        while (notNullList.contains(null)) notNullList.remove(null);
        q.where(sb.toString(), notNullList.toArray(new Object[notNullList.size()]));
        return q;
    }

    private Object addQueryParam(StringBuilder sb, MutableBoolean and, String field, Object value) {
        if (value == null) return null;
        if (and.isTrue()) sb.append(" AND ");
        and.setValue(true);
        sb.append(field).append(" = ? ");
        return value;
    }

    private PermissionBean createPermissionBean(PermissionGroup mpsPg, IPermissionMock permissionMock) {
        List<DBParam> paramList = new ArrayList<DBParam>();
        paramList.add(new DBParam(PROJECT_ROLE_NAME, permissionMock.getProjectRoleName()));
        paramList.add(new DBParam(GROUP_NAME, permissionMock.getGroupName()));
        paramList.add(new DBParam(USER_NAME, permissionMock.getUserName()));
        paramList.add(new DBParam(PROJECT_KEY, permissionMock.getProjectKey()));
        paramList.add(new DBParam(PERMISSION_GROUP_ID, mpsPg.getID()));
        Iterator<DBParam> it = paramList.iterator();
        while(it.hasNext()) {
            DBParam next = it.next();
            if (next.getValue() == null) {
                log.debug(next.getField() + " is null, deleting");
                it.remove();
            }
        }
        PermissionBean pb = ao.create(PermissionBean.class, paramList.toArray(new DBParam[paramList.size()]));
        log.debug("created PB : " + pb.getProjectRoleName() + pb.getGroupName() + pb.getUserName());
        return pb;
    }


    private PermissionBean[] findPermissionBeans(PermissionGroup mpsPg, IPermissionMock permissionMock) {
        Query q = getPermissionQuery(permissionMock, mpsPg);
        return ao.find(PermissionBean.class,q);
    }

    private PermissionGroup[] getPgs(String groupname) {
        return ao.find(PermissionGroup.class, Query.select().where(NAME + " = ?",groupname));
    }

    private PermissionGroup initPermissionGroup(String groupname) {
        DBParam name = new DBParam(NAME,groupname );
        PermissionGroup pg = ao.create(PermissionGroup.class, name);
        pg.save();
        return pg;
    }



}
