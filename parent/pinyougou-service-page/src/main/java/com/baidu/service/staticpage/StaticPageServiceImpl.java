package com.baidu.service.staticpage;

import com.baidu.dao.good.GoodsDao;
import com.baidu.dao.good.GoodsDescDao;
import com.baidu.dao.item.ItemCatDao;
import com.baidu.dao.item.ItemDao;
import com.baidu.pojo.good.Goods;
import com.baidu.pojo.good.GoodsDesc;
import com.baidu.pojo.item.Item;
import com.baidu.pojo.item.ItemCat;
import com.baidu.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {
    //创建configuration并且指定模板位置
    private Configuration configuration;

    // 注入FreeMarkerConfigurer，好处：获取configuration 指定模板位置

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    //注入四张表的dao
    //注入商品表
    @Resource
    private GoodsDao goodsDao;

    //注入商品描述信息表
    @Resource
    private GoodsDescDao goodsDescDao;

    //注入商品分类信息表
    @Resource
    private ItemCatDao itemCatDao;

    //注入商品库存信息表
    @Resource
    private ItemDao itemDao;

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 获取静态页的方法
     *
     * @param id
     */
    @Override
    public void getHtml(Long id) {

        try {
            //1.创建configuration并且指定模板位置
            //2.获取该位置下的模板
            Template template = configuration.getTemplate("item.ftl");
            //3.准备数据
            Map<String,Object> dataModel = getDateModel(id);
            // 创建file：指定静态页生成的位置
            // 生成的静态页位置：可以直接访问。存在项目发布的真实的路径下
            // 获取项目发布的真实路径：
            // request.getRealPath(xxx)：此路不通
            // 其他的路：ServletContext.getRealPath(xxx)
            String pathname = "/" + id + ".html";
            String path = servletContext.getRealPath(pathname);

            File file = new File(path);

            //4.模板+数据=输出
            template.process(dataModel, new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Map<String, Object> getDateModel(Long id) {
        Map<String,Object> map = new HashMap<>();
        //商品基本信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        map.put("goods",goods);
        //商品描述信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        map.put("goodsDesc",goodsDesc);
        //商品分类信息
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());

        map.put("itemCat1",itemCat1);
        map.put("itemCat2",itemCat2);
        map.put("itemCat3",itemCat3);
        //商品库存信息
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        map.put("itemList",itemList);

        return map;
    }


}
