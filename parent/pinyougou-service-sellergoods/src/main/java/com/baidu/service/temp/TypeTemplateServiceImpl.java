package com.baidu.service.temp;

import com.alibaba.dubbo.config.annotation.Service;

import com.alibaba.fastjson.JSON;
import com.baidu.dao.specification.SpecificationOptionDao;
import com.baidu.dao.template.TypeTemplateDao;
import com.baidu.entity.PageResult;

import com.baidu.pojo.specification.SpecificationOption;
import com.baidu.pojo.specification.SpecificationOptionQuery;
import com.baidu.pojo.template.TypeTemplate;
import com.baidu.pojo.template.TypeTemplateQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class TypeTemplateServiceImpl implements TypeTemplateService{
     //注入dao
    @Resource
    private TypeTemplateDao typeTemplateDao;


    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;


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

        //将模板的数据写入缓存中
        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if (typeTemplateList != null && typeTemplateList.size()>0){
            for (TypeTemplate template : typeTemplateList){
                //从模板中拿到品牌结果集
                String brandIds = template.getBrandIds();
                //把拿到的String数据类型转到list集合中
                List<Map> brandList = JSON.parseArray(brandIds,Map.class);
                //品牌结果集写入缓存
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                //规格结果集写入缓存（规格和规格选项）
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);

            }
        }

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

    /**
     * 新增分类时，加载模板列表
     *
     * @return
     */
    @Override
    public List<TypeTemplate> findAll() {
        return typeTemplateDao.selectByExample(null);
    }

    /**
     * 新增商品选择三级分类时，加载商品规格选项
     *
     * @param id
     * @return
     */
    @Override
    public List<Map> findBySpecList(Long id) {
        //通过模板id获取规格
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> specList = JSON.parseArray(specIds,Map.class);
        //通过规格id获取规格选项
        if (specList != null && specList.size()>0){
            for (Map map : specList){
                Long specId = Long.parseLong(map.get("id").toString());
                //通过规格id获取规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                query.createCriteria().andSpecIdEqualTo(specId);
                List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                //封装到map
                map.put("options",options);
            }
        }
        return specList;
    }


}
