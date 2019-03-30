package com.baidu.service.task;

import com.alibaba.fastjson.JSON;
import com.baidu.dao.item.ItemCatDao;
import com.baidu.dao.specification.SpecificationOptionDao;
import com.baidu.dao.template.TypeTemplateDao;
import com.baidu.pojo.item.ItemCat;
import com.baidu.pojo.specification.SpecificationOption;
import com.baidu.pojo.specification.SpecificationOptionQuery;
import com.baidu.pojo.template.TypeTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RedisTask
 * @Description 定时任务
 * @Author 传智播客
 * @Date 12:41 2019/3/27
 * @Version 2.1
 **/
@Component
public class RedisTask {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;


    /**
     * @author 栗子
     * @Description 将分类的数据写到缓存中
     * @Date 12:59 2019/3/27
     * @param
     * @return void
     **/
    @Scheduled(cron = "30 51 12 27 03 *")
    public void autoItemToRedis(){
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if(itemCatList != null && itemCatList.size() > 0){
            for (ItemCat itemCat : itemCatList) {
                redisTemplate.boundHashOps("itemCatList").put(itemCat.getName(), itemCat.getTypeId());
            }
        }
    }

    /**
     * @author 栗子
     * @Description 将模板的数据写到缓存中
     * @Date 12:57 2019/3/27
     * @param
     * @return void
     **/
    @Scheduled(cron = "30 51 12 27 03 *")
    public void autoTempToRedis(){
        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if(typeTemplateList != null && typeTemplateList.size() > 0){
            for (TypeTemplate template : typeTemplateList) {
                // 例如：[{"id":60,"text":"奔驰"},{"id":62,"text":"奥拓"}]
                String brandIds = template.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                // 品牌结果集写入缓存
                redisTemplate.boundHashOps("brandList").put(template.getId(), brandList);
                // 规格结果集写入缓存（规格与规格选项）
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(), specList);
            }
        }
    }

    public List<Map> findBySpecList(Long id) {
        // 通过模板id获取规格
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        // 栗子：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        List<Map> specList = JSON.parseArray(specIds, Map.class);
        // 通过规格id获取规格选项
        if(specList != null && specList.size() > 0){
            for (Map map : specList) {
                Long specId = Long.parseLong(map.get("id").toString());
                // 通过规格id获取规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                query.createCriteria().andSpecIdEqualTo(specId);
                List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                // 封装到map
                // 栗子：[{"id":27,"text":"网络","options":[{optionName:xxx}]},{}]
                map.put("options", options);
            }
        }
        return specList;
    }
}
