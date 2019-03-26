package com.baidu.service.search;

import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.pojo.item.Item;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    //注入solr
    @Resource
    private SolrTemplate solrTemplate;

    /**
     * 前台系统检索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //创建一个大map，封装所有的结果集
        Map<String,Object> resultMap = new HashMap<>();

        // 1、根据关键字检索并且分页
        Map<String,Object> map = searchForPage(searchMap);
        resultMap.putAll(map);
        // TODO 2、加载分类结果集
        // TODO 3、加载品牌、规格结果集

        return resultMap;
    }

    // 根据关键字检索并且分页
    private Map<String,Object> searchForPage(Map<String, String> searchMap) {
        //1.设置检索条件
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords!=null && !"".equals(keywords)){
            criteria.is(keywords);//is不是等于某个值，is方法：根据词条进行模糊检索类似like%%
        }
        SimpleQuery query = new SimpleQuery(criteria);

        //2.设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer start = (pageNo - 1)*pageSize;

        query.setOffset(start);//起始行
        query.setRows(pageSize);//每页显示的条数

        // 3、根据条件查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
        // 4、封装结果集到map中
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", scoredPage.getTotalPages());  // 总页数
        map.put("total", scoredPage.getTotalElements());    // 总条数
        map.put("rows", scoredPage.getContent());           // 结果集
        return map;
    }
}

