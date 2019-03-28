package com.baidu.service.search;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baidu.entity.Result;
import com.baidu.pojo.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import javax.annotation.Resource;
import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    //注入solr
    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

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
        //处理关键字中包含的空格问题
        String keywords = searchMap.get("keywords");
        if (keywords !=null && !keywords.equals("")){
            //去除中间所有空格
            keywords = keywords.replace(" ", "");
            searchMap.put("keywords",keywords);
        }

        // 1、根据关键字检索并且分页
//        Map<String,Object> map = searchForPage(searchMap);
//        resultMap.putAll(map);
        //2.根据关键字检索，并且高亮关键字
        Map<String,Object> map = searchForHighLightPage(searchMap);
        resultMap.putAll(map);
        // 2、加载分类结果集
        List<String> categoryList = searchForGroupPage(searchMap);
        if (categoryList != null && categoryList.size()>0){
            resultMap.put("categoryList",categoryList);

            //3、默认加载第一个个分类下的品牌、规格结果集
            Map<String, Object> brandAndSpecMap = searchBrandAndSpecListByCategory(categoryList.get(0));
            resultMap.putAll(brandAndSpecMap);
            
        }


        return resultMap;
    }

    // 默认加载第一个个分类下的品牌、规格结果集
    private Map<String,Object> searchBrandAndSpecListByCategory(String categoryName) {
        Map<String,Object> map = new HashMap<>();

        //通过分类名称获取模板id
        Object typeId = redisTemplate.boundHashOps("itemCatList").get(categoryName);
        //通过模板id获取品牌结果集
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        map.put("brandList",brandList);
        //通过模板id获取规格规格集
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("specList",specList);


        return map;
    }

    private List<String> searchForGroupPage(Map<String, String> searchMap) {
        // select * table where condition1 group by xxx
        // 1、设置关键字条件
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)){
            criteria.is(keywords);
        }
        SimpleQuery query = new SimpleQuery(criteria);

        // 2、设置分组条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        // 3、根据条件查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);

        // 4、将结果封装到list中
        List<String> list = new ArrayList<>();
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries){
            String groupValue = groupEntry.getGroupValue(); //分组名称
            list.add(groupValue);
        }
        return list;

    }

    //根据关键字检索，并且高亮关键字
    private Map<String,Object> searchForHighLightPage(Map<String, String> searchMap) {
        // 1、设置检索关键字
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if(keywords != null && !"".equals(keywords)){
            // lucene:wildCardQuery
            criteria.is(keywords);  // is不是等于某个值。is方法：根据词条进行模糊检索  like%%
        }

        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);

        // 2、设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer start = (pageNo - 1) * pageSize;
        query.setOffset(start);     // 其始行
        query.setRows(pageSize);    // 每页显示的条数

        // 3、设置高亮条件
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");    // 标题中包含的关键字需要高亮显示
        highlightOptions.setSimplePrefix("<font color='red'>");
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);

        //设置过滤条件
        //根据商品分类过滤
        String category = searchMap.get("category");
        if (category != null && !category.equals("")){
            Criteria criteria1 = new Criteria("item_category");
            criteria1.is(category);

            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);

            query.addFilterQuery(filterQuery);

        }

        //根据商品品牌过滤
        String brand = searchMap.get("brand");
        if (brand != null && !brand.equals("")){
            Criteria criteria1 = new Criteria("item_brand");
            criteria1.is(brand);

            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);

            query.addFilterQuery(filterQuery);
        }

        //根据商品规格过滤
        String spec = searchMap.get("spec");
        if (spec != null && !spec.equals("")){
            //把集合转成对象，然后取value值
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries){
                //拼接"item_spec"+value
                Criteria criteria1 = new Criteria("item_spec_"+entry.getKey());
                criteria1.is(entry.getValue());

                SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
                 query.addFilterQuery(filterQuery);
            }
        }
        //根据商品价格过滤
        String price = searchMap.get("price");
        if (price != null && !price.equals("")){
            //价格区间段
            String[] prices = price.split("-");
            Criteria criteria1 = new Criteria("item_price");

            if (price.contains("*")){
                criteria1.greaterThanEqual(prices[0]);
            }else {
                criteria1.between(prices[0],prices[1],true,true);
            }
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(criteria1);
            query.addFilterQuery(filterQuery);
        }

        //添加排序条件
        //根据新品，价格排序
        String sort = searchMap.get("sort");
        if (sort != null && !sort.equals("")){
            if ("ASC".equals(sort)){
                Sort s = new Sort(Sort.Direction.ASC,"item_"+searchMap.get("sortField"));
                query.addSort(s);
            }else {
                Sort s = new Sort(Sort.Direction.DESC,"item_"+searchMap.get("sortField"));
                query.addSort(s);
            }
        }


        // 4、根据条件检索
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);
        // 处理高亮的结果集
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();

        if (highlighted != null && highlighted.size()>0){
            for (HighlightEntry<Item> highlightEntry : highlighted){
                Item item = highlightEntry.getEntity();//普通结果

                List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
                if (highlights !=null && highlights.size()>0){
                    String title = highlights.get(0).getSnipplets().get(0);
                    item.setTitle(title);
                }
            }
        }
       // 5、封装结果集到map中
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", highlightPage.getTotalPages());  // 总页数
        map.put("total", highlightPage.getTotalElements());    // 总条数
        map.put("rows", highlightPage.getContent());           // 结果集
        return map;
    }




    // 根据关键字检索并且分页
    private Map<String,Object> searchForPage(Map<String, String> searchMap) {
        //1.设置检索条件
        Criteria criteria = new Criteria("item_keywords");  //字段域

        String keywords = searchMap.get("keywords");    //前台搜索关键词

        if (keywords != null && !"".equals(keywords)){
            criteria.is(keywords);    //is就像like模糊查询
        }

        SimpleQuery query = new SimpleQuery(criteria);
        //2.设置分页
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer start = (pageNo-1)*pageSize;    //起始行

        query.setOffset(start);     //起始行
        query.setRows(pageSize);    //每页显示条数

        //3.设置查询条件
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
        //4.封装结果集到map
        Map<String,Object> map = new HashMap<>();
        map.put("totalPages",scoredPage.getTotalPages());   //总页数
        map.put("total",scoredPage.getTotalElements());     //总条数
        map.put("rows",scoredPage.getContent());            //结果集

        return map;
    }
}

