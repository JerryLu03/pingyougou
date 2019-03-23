package com.baidu.service.itemcat;

import com.baidu.pojo.item.ItemCat;

import java.util.List;
import java.util.Map;

public interface ItemCatService {

    /**
     * 商品分类的列表查询
     * @param parentId
     * @return
     */
    List<ItemCat> findByParentId(Long parentId);

    /**
     * 保存分类
     * @param itemCat
     */
    void add(ItemCat itemCat);

    /**
     * 新增商品选择三级分类时，加载商品模板
     * @param id
     * @return
     */
    ItemCat findOne(Long id);

    /**
     * 查询所有商品分类
     * @return
     */
    List<ItemCat> findAll();


}
