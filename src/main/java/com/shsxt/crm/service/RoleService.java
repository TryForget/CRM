package com.shsxt.crm.service;

import com.shsxt.crm.base.BaseService;
import com.shsxt.crm.dao.ModuleMapper;
import com.shsxt.crm.dao.PermissionMapper;
import com.shsxt.crm.dao.RoleMapper;
import com.shsxt.crm.utils.AssertUtil;
import com.shsxt.crm.vo.Module;
import com.shsxt.crm.vo.Permission;
import com.shsxt.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询所有的角色列表
     * @return
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role) {
        // 判断角色名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空！");
        // 通过角色名查询角色对象
        Role temp = roleMapper.queryRoleByName(role.getRoleName());
        // 判断角色对象是否为空
        AssertUtil.isTrue(temp != null, "角色名称已存在，请重试！");
        // 设置默认值
        role.setUpdateDate(new Date());
        role.setCreateDate(new Date());
        role.setIsValid(1);

        // 添加操作
        AssertUtil.isTrue(roleMapper.insertSelective(role) != 1, "角色数据添加失败！");
    }

    /**
     * 更新角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role) {
        // 判断角色名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空！");
        // 通过角色名查询角色对象
        Role temp = roleMapper.queryRoleByName(role.getRoleName());
        // 判断角色对象是否存在，且id相等
        AssertUtil.isTrue(temp != null && !(role.getId().equals(temp.getId())), "角色名称已存在，请重试！");
        // 设置默认值
        role.setUpdateDate(new Date());

        //更新操作
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) != 1, "角色数据更新失败！");
    }

    /**
     * 删除角色
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId) {
        AssertUtil.isTrue(null == roleId || roleMapper.selectByPrimaryKey(roleId) == null, "待删除记录不存在！");
        AssertUtil.isTrue(roleMapper.deleteRole(roleId) != 1, "角色数据删除失败！");
    }


    /**
     * 角色授权
     *      1. 角色是否存在
     *      2. 判断角色是否拥有权限
     *          如果有，则删除
     *      3. 得到新添加的权限，批量添加到权限表中
     * @param mIds
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer[] mIds, Integer roleId) {
        // 通过角色Id查询角色对象
        Role role = roleMapper.selectByPrimaryKey(roleId);
        // 判断角色是否存在
        AssertUtil.isTrue(role == null, "待授权的角色不存在！");

        // 通过角色ID查询角色拥有的权限
        Integer count = permissionMapper.queryPermissionCountByRoleId(roleId);
        // 如果有，则删除
        if (count > 0) {
            // 删除指定角色的所有权限
            AssertUtil.isTrue(permissionMapper.deleteByRoleId(roleId) != count, "角色授权失败！");
        }


        // 添加角色的权限
        // 判断模块ID是否存在
        if (mIds != null && mIds.length > 0) {
            // 定义权限集合
            List<Permission> permissionList = new ArrayList<>();
            // 遍历模块ID数组
            for (Integer mId: mIds) {
                Permission permission = new Permission();
                // 通过模块ID得到资源对象
                Module module = moduleMapper.selectByPrimaryKey(mId);
                permission.setAclValue(module.getOptValue());
                permission.setUpdateDate(new Date());
                permission.setCreateDate(new Date());
                permission.setRoleId(roleId);
                permission.setModuleId(mId);
                // 设置到集合中
                permissionList.add(permission);
            }

            // 批量添加
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList) != permissionList.size(), "角色授权失败！");
        }

    }
}
