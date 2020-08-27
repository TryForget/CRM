package com.shsxt.crm.dao;

import com.shsxt.crm.base.BaseMapper;
import com.shsxt.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    // 通过角色名查询所拥有的的权限
    List<Integer> queryPermissionByRole(Integer roleId);

    // 通过角色ID查询权限数量
    Integer queryPermissionCountByRoleId(Integer roleId);

    // 通过角色ID删除权限数据
    int deleteByRoleId(Integer roleId);

    // 据用户ID查询用户拥有的角色对应的所有权限
    List<String> queryUserHasRolesHasPermissions(Integer userId);
}