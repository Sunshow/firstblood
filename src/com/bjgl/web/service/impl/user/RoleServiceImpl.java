package com.bjgl.web.service.impl.user;

import com.bjgl.web.dao.user.RoleDao;
import com.bjgl.web.dao.user.RolePermissionDao;
import com.bjgl.web.entity.user.Role;
import com.bjgl.web.entity.user.RolePermission;
import com.bjgl.web.service.impl.AbstractBaseServiceImpl;
import com.bjgl.web.service.user.RoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: sunshow
 * Date: 13-7-14
 * Time: 上午9:49
 */
public class RoleServiceImpl extends AbstractBaseServiceImpl<Role> implements RoleService {

    private RolePermissionDao rolePermissionDao;

    protected RoleDao getRoleDao() {
        return (RoleDao)dao;
    }

    @Override
    public void saveOrUpdate(Role role, List<RolePermission> rolePermissionList) {
        // 先保存角色
        if (role.getId() == null) {
            this.getRoleDao().save(role);
        } else {
            this.getRoleDao().merge(role);
        }

        // 先取出已有的角色权限
        List<RolePermission> srcRolePermissionList = rolePermissionDao.findByRoleId(role.getId());

        // 按照permission转置map
        Map<Long, RolePermission> srcRolePermissionMap = new HashMap<Long, RolePermission>();
        if (srcRolePermissionList != null) {
            for (RolePermission rp : srcRolePermissionList) {
                srcRolePermissionMap.put(rp.getPermissionId(), rp);
            }
        }

        // 转置要修改成的数据
        Map<Long, RolePermission> desRolePermissionMap = new HashMap<Long, RolePermission>();
        if (rolePermissionList != null) {
            for (RolePermission rp : rolePermissionList) {
                desRolePermissionMap.put(rp.getPermissionId(), rp);
            }
        }


        List<RolePermission> mergeRolePermissionList = new ArrayList<RolePermission>();
        List<RolePermission> deleteRolePermissionList = new ArrayList<RolePermission>();

        // 查找出已被删除的权限
        if (srcRolePermissionList != null) {
            for (RolePermission srcRolePermission : srcRolePermissionList) {
                if (!desRolePermissionMap.containsKey(srcRolePermission.getPermissionId())) {
                    // 已被删除
                    deleteRolePermissionList.add(srcRolePermission);
                    continue;
                }

                RolePermission desRolePermission = desRolePermissionMap.get(srcRolePermission.getPermissionId());

                // 判断是否需要更新
                if (desRolePermission.getPermissionItemIds() == null) {
                    if (srcRolePermission.getPermissionItemIds() == null) {
                        // 均为空，不需要更新
                        continue;
                    }
                    // 发生了变化需要更新
                    srcRolePermission.setPermissionItemIds(desRolePermission.getPermissionItemIds());
                    mergeRolePermissionList.add(srcRolePermission);
                    continue;
                }

                // 以下都有子权限
                if (desRolePermission.getPermissionItemIds().equals(srcRolePermission.getPermissionItemIds())) {
                    // 没变化
                    continue;
                }

                // 发生了变化需要更新
                srcRolePermission.setPermissionItemIds(desRolePermission.getPermissionItemIds());
                mergeRolePermissionList.add(srcRolePermission);
            }
        }

        // 查找出要新增的权限
        if (rolePermissionList != null) {
            for (RolePermission desRolePermission : rolePermissionList) {
                if (!srcRolePermissionMap.containsKey(desRolePermission.getPermissionId())) {
                    // 不存在的权限，需要添加
                    if (desRolePermission.getRoleId() == null) {
                        desRolePermission.setRoleId(role.getId());
                    }
                    mergeRolePermissionList.add(desRolePermission);
                }
            }
        }

        // 执行删除
        for (RolePermission rolePermission : deleteRolePermissionList) {
            rolePermissionDao.delete(rolePermission.getId());
        }

        // 执行保存和更新
        for (RolePermission rolePermission : mergeRolePermissionList) {
            rolePermissionDao.merge(rolePermission);
        }
    }

    public void setRolePermissionDao(RolePermissionDao rolePermissionDao) {
        this.rolePermissionDao = rolePermissionDao;
    }
}
