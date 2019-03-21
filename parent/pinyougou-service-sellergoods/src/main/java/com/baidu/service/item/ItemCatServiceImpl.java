package com.baidu.service.item;

import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.dao.item.ItemCatDao;
import com.baidu.pojo.item.ItemCat;
import com.baidu.pojo.item.ItemCatQuery;
import com.baidu.service.itemcat.ItemCatService;

import javax.annotation.Resource;
import java.util.List;
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
}
