package com.baidu.service.temp;

import com.alibaba.dubbo.config.annotation.Service;

import com.baidu.dao.template.TypeTemplateDao;
import com.baidu.entity.PageResult;

import com.baidu.pojo.template.TypeTemplate;
import com.baidu.pojo.template.TypeTemplateQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;


@Service
public class TypeTemplateServiceImpl implements TypeTemplateService{
     //注入dao
    @Resource
    private TypeTemplateDao typeTemplateDao;


    /**
     * 查询
     *
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //1.设置分页
        PageHelper.startPage(page,rows);
        //2.设置查询条件
        TypeTemplateQuery query = new TypeTemplateQuery();
        if (typeTemplate.getName() !=null && !"".equals(typeTemplate.getName().trim())){
            query.createCriteria().andNameLike("%"+typeTemplate.getName().trim()+"%");
        }
        //3.查询
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);
        //4.封装结果集
        return new PageResult(p.getTotal(),p.getResult());
    }

    /**
     * 保存
     *
     * @param typeTemplate
     * @return
     */
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 回显
     *
     * @param id
     * @return
     */
    @Override
    public TypeTemplate findOne(Long id) {
       return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 修改模板
     *
     * @param typeTemplate
     */
    @Transactional
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }


    @Override
    public void delete(Long[] ids) {
        if (ids !=null && ids.length>0){
            for (Long id : ids){
                typeTemplateDao.deleteByPrimaryKey(id);
            }

            typeTemplateDao.deleteByPrimaryKeys(ids);
        }
    }


}
