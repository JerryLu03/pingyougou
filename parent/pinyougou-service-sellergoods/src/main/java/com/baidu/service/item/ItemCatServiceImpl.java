package com.baidu.service.item;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baidu.dao.item.ItemCatDao;
import com.baidu.dao.specification.SpecificationOptionDao;
import com.baidu.dao.template.TypeTemplateDao;
import com.baidu.pojo.item.ItemCat;
import com.baidu.pojo.item.ItemCatQuery;
import com.baidu.pojo.specification.SpecificationOption;
import com.baidu.pojo.specification.SpecificationOptionQuery;
import com.baidu.pojo.specification.SpecificationQuery;
import com.baidu.pojo.template.TypeTemplate;
import com.baidu.service.itemcat.ItemCatService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    //注入dao
    @Resource
    private ItemCatDao itemCatDao;



    /**
     * 商品分类的列表查询
     *
     * @param parentId
     * @return
     */
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        //设置查询条件
        ItemCatQuery query = new ItemCatQuery();
        query.createCriteria().andParentIdEqualTo(parentId);
        //查询
        List<ItemCat> itemCats = itemCatDao.selectByExample(query);
        return itemCats;
    }

    /**
     * 保存分类
     *
     * @param itemCat
     */
    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
    }

    /**
     * 新增商品选择三级分类时，加载商品模板
     *
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    /**
     * 查询所有商品分类
     *
     * @return
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }


}
