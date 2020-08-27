package com.shsxt.crm.service;

import com.shsxt.crm.base.BaseService;
import com.shsxt.crm.dao.PermissionMapper;
import com.shsxt.crm.vo.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService extends BaseService<Permission, Integer> {

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 根据用户ID查询用户拥有的角色对应的所有权限
     * @param userId
     * @return
     */
    public List<String> queryUserHasRolesHasPermissions(Integer userId) {

        return permissionMapper.queryUserHasRolesHasPermissions(userId);

    }
}
