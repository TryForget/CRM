package com.shsxt.crm.service;

import com.github.pagehelper.PageHelper;
import com.shsxt.crm.base.BaseQuery;
import com.shsxt.crm.base.BaseService;
import com.shsxt.crm.dao.ModuleMapper;
import com.shsxt.crm.dao.PermissionMapper;
import com.shsxt.crm.model.TreeModel;
import com.shsxt.crm.utils.AssertUtil;
import com.shsxt.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module, Integer> {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 按照指定的数据格式，查询所有的资源列表
     * @return
     */
    public List<TreeModel> queryAllModules(Integer roleId){

        // 所有资源列表
        List<TreeModel> treeModelList = moduleMapper.queryAllModules();

        // 查询指定角色拥有的资源列表
        List<Integer> roleHasPermissionIds =  permissionMapper.queryPermissionByRole(roleId);

        // 判断角色ID是否为空
        if (roleId != null) {
            // 判断当前资源是否被选中
            for (TreeModel tree:treeModelList) {
                // 判断当前角色是否包含该资源
                if (roleHasPermissionIds.contains(tree.getId())) {
                    tree.setChecked(true);
                }
            }
        }
        return treeModelList;
    }


    /**
     * 查询资源列表
     * @return
     */
    public Map<String, Object> selectByParams() {
        Map<String,Object> result = new HashMap<String,Object>();
        List<Module> moduleList = moduleMapper.selectByParams(new BaseQuery());
        result.put("count",moduleList.size());
        result.put("data",moduleList);
        result.put("code",0);
        result.put("msg","");
        return result;
    }

    /**
     * 1.非空校验
     *   层级grade 非空且取值为 220/1/2
     *   moduleName 模块名  非空 在同一层级下名称唯一
     *   url菜单地址
     *      如果是二级菜单(grade=1)非空且唯一
     *    prentId父级菜单
     *      如果是一级菜单(grade=-1)
     *      如果是二级菜单或三级菜单 值非空 且父菜单存在
     *    权限码
     *      非空且唯一
     * 2.设置默认值
     *  isValid updateDate createDate
     * 3.添加操作
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module) {

        /* 非空判断  */
        //  grade层级     非空，取值为 0|1|2
        AssertUtil.isTrue(module.getGrade() == null, "层级不能为空！");
        AssertUtil.isTrue(!(module.getGrade() == 0 || module.getGrade() == 1 || module.getGrade() == 2), "层级不符合！");

        //  moduleName模块名   非空，在同一层级下名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "资源名称不能为空！");
        // 通过层级和名称查询资源对象
        Module temp = moduleMapper.queryModuleNameByGradeAndName(module.getModuleName(), module.getGrade());
        // 如果不为空，表示已存在
        AssertUtil.isTrue(temp != null, "资源名称已存在，请重新输入！");


        // url菜单地址  如果是二级菜单（grade=1），非空且唯一
        if (module.getGrade() == 1) { // 二级菜单
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "资源URL不能为空！");
            // 判断是否重复
            temp = moduleMapper.queryModuleByUrlAndGrade(module.getUrl(), module.getGrade());
            AssertUtil.isTrue(temp != null, "资源URL已存在，请重试！");
        }

        //  parentId父级菜单  如果是一级菜单，值为-1  如果是二级或三级菜单，值非空，且父菜单存在
        if (module.getGrade() == 0) {
            module.setParentId(-1);
        } else {
            AssertUtil.isTrue(module.getParentId() == null, "父级菜单不能为空！");
            // 通过parentId查询资源对象
            temp = moduleMapper.selectByPrimaryKey(module.getParentId());
            // 判断 父级菜单是否存在
            AssertUtil.isTrue(temp == null , "父级菜单必须存在！");
        }

        // 权限码  非空且唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空！");
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        AssertUtil.isTrue(temp != null, "权限码已存在，请重试！");


        /* 设置默认值 */
        module.setIsValid((byte) 1);
        module.setUpdateDate(new Date());
        module.setCreateDate(new Date());


        /* 添加操作 */
        AssertUtil.isTrue(moduleMapper.insertSelective(module) != 1, "资源数据添加失败！");

    }

}
