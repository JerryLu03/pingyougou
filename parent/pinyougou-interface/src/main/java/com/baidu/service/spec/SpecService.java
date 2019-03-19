package com.baidu.service.spec;

import com.baidu.entity.PageResult;
import com.baidu.pojo.specification.Specification;
import com.baidu.vo.SpecVo;

import java.util.List;
import java.util.Map;

/**
 * 规格列表查询
 */
public interface SpecService {
    /**
     * 条件查询
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    PageResult search(Integer page, Integer rows, Specification specification);


    /**
     * 新增
     * @param specVo
     */
    void add(SpecVo specVo);


    /**
     * 回显
     * @param id
     * @return
     */
    SpecVo findOne(Long id);

    /**
     * 修改
     * @param specVo
     */
    void update(SpecVo specVo);

    /**
     * 删除
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 新增模板，初始化下拉框规格的数据
     * @return
     */
    List<Map<String,String>> selectOptionList();

}
