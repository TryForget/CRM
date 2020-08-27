package com.shsxt.crm.dao;

import com.shsxt.crm.base.BaseMapper;
import com.shsxt.crm.model.TreeModel;
import com.shsxt.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    // 按照指定的数据格式，查询所有的资源列表
    List<TreeModel> queryAllModules();
    //通过资源名和层级查询资源对象
    Module queryModuleNameByGradeAndName(@Param("moduleName") String moduleName ,@Param("grade")Integer grade );
    //通过Url和层级查询资源对象
    Module queryModuleByUrlAndGrade(@Param("url") String url, @Param("grade") Integer grade);
    //通过权限码查询资源对象
    Module queryModuleByOptValue(String optValue);
}