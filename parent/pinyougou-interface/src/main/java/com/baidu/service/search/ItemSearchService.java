package com.baidu.service.search;

import java.util.Map;

public interface ItemSearchService {
    /**
     * 前台系统检索
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,String> searchMap);

    /**
     * 商品上架保存到索引库中
     * @param id
     */
    void addItemToSolr(Long id);
}
