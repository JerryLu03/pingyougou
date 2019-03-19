package com.baidu.service.brand;

import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.dao.good.BrandDao;
import com.baidu.entity.PageResult;
import com.baidu.pojo.good.Brand;
import com.baidu.pojo.good.BrandQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    //注入dao
    @Resource
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        List<Brand> brands = brandDao.selectByExample(null);
        return brands;
    }



    //分页查询
    @Override
    public PageResult findPage(Integer pageNo, Integer pageSize) {
        // 1、设置分页条件
        PageHelper.startPage(pageNo, pageSize);
        // 2、进行查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
        // 3、将结果封装到PageResult中
        // 思考：为什么不直接返回page  原因：page封装的数据太多了，网络传输的过程效率低
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 条件查询
     * @param pageNo
     * @param pageSize
     * @param brand
     * @return
     */
    @Override
    public PageResult search(Integer pageNo, Integer pageSize, @RequestBody Brand brand) {
        // 1、设置分页条件
        PageHelper.startPage(pageNo, pageSize);
        // 2、设置查询条件：封装查询条件对象XxxQuery
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria(); // 封装具体的查询条件对象
        // select * from tb_brand where name like "%xxxx%"
        // 拼接sql语句
        if(brand.getName() != null && !"".equals(brand.getName().trim())){
            criteria.andNameLike("%" + brand.getName().trim() + "%" );
        }
        if(brand.getFirstChar() != null && !"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }
        // 根据id排序
        brandQuery.setOrderByClause("id desc");
        // 3、进行查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        // 4、将结果封装到PageResult中
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 新增品牌
     * @param brand
     */
    @Transient
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    /**
     * 回显
     * @param id
     * @return
     */
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * 修改
     *
     * @param brand
     */
    @Transient
    @Override
    public void update(Brand brand) {
         brandDao.updateByPrimaryKeySelective(brand);
    }

    /**
     * 删除
     *
     * @param ids
     */
    @Transient
    @Override
    public void deleteByPrimaryKeys(Long[] ids) {
        if (ids != null && ids.length>0){
           brandDao.deleteByPrimaryKeys(ids);
        }
    }

    /**
     * 新增模板，初始化下拉框品牌的数据
     *
     * @return
     */
    @Override
    public List<Map<String, String>> selectOptionList() {
        return brandDao.selectOptionList();
    }


}
