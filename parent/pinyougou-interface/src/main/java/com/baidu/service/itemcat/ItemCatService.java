package com.baidu.service.itemcat;

import com.baidu.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    /**
     * 商品分类的列表查询
     * @param parentId
     * @return
     */
    List<ItemCat> findByParentId(Long parentId);
}
