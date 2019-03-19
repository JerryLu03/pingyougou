package com.baidu.service.brand;

import com.baidu.entity.PageResult;
import com.baidu.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    //查询所有品牌
    List<Brand> findAll();

    //分页查询
    PageResult findPage(Integer pageNo,Integer pageSize);

    //条件查询
    PageResult search(Integer pageNo, Integer pageSize, Brand brand);

    //新增品牌
    void add(Brand brand);

    //回显
    Brand findOne(Long id);

    /**
     * 修改
     * @param brand
     */
    void update(Brand brand);

    /**
     * 删除
     * @param ids
     */
    void deleteByPrimaryKeys(Long[] ids);

    /**
     * 新增模板，初始化下拉框品牌的数据
     * @return
     */
    List<Map<String,String>> selectOptionList();


}
