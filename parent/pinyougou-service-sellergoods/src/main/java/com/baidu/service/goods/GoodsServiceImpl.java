package com.baidu.service.goods;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baidu.dao.good.BrandDao;
import com.baidu.dao.good.GoodsDao;
import com.baidu.dao.good.GoodsDescDao;
import com.baidu.dao.item.ItemCatDao;
import com.baidu.dao.item.ItemDao;
import com.baidu.dao.seller.SellerDao;
import com.baidu.entity.PageResult;
import com.baidu.pojo.good.Goods;
import com.baidu.pojo.good.GoodsDesc;
import com.baidu.pojo.good.GoodsQuery;
import com.baidu.pojo.item.Item;
import com.baidu.pojo.item.ItemQuery;
import com.baidu.vo.GoodsVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {
    //注入dao

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private BrandDao brandDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private SolrTemplate solrTemplate;

    /**
     * 商品录入
     *
     * @param goodsVo
     */
    @Transactional
    @Override
    public void add(GoodsVo goodsVo) {
        //保存商品基本信息
        Goods goods = goodsVo.getGoods();
        goods.setAuditStatus("0");//商品的审核状态：待审核
        //返回自增主键的id
        goodsDao.insertSelective(goods);
        //保存商品描述信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);

        // 保存商品库存信息
        // 分析：判断是否启用规格   启用规格：spu 1 -- sku n  不启用规格：spu 1 -- sku 1
        if ("1".equals(goods.getIsEnableSpec())){
            //启用规格spu1--sku n
            List<Item> itemList = goodsVo.getItemList();
            if (itemList !=null && itemList.size()>0){
                for (Item item:itemList){
                    //title = spu名称+spu副标题+规格名称
                    String title = goods.getGoodsName()+""+goods.getCaption();
                    // 数据：{"机身内存":"16G","网络":"联通3G"}
                    String spec = item.getSpec();
                    Map<String,String> map = JSON.parseObject(spec,Map.class);
                    Set<Map.Entry<String,String>> entries = map.entrySet();
                    for (Map.Entry<String,String> entry : entries){
                        title += ""+entry.getValue();
                    }
                    item.setTitle(title);
                    // 设置库存属性
                    setAttributeForItem(goods, goodsDesc, item);

                    itemDao.insertSelective(item);

                }
            }
        }else {
            //未启用规格
            Item item = new Item();
            item.setTitle(goods.getGoodsName()+""+goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setSpec("{}");
            setAttributeForItem(goods,goodsDesc,item);
            itemDao.insertSelective(item);
        }
    }

    /**
     * 商家系统商品列表查询（当前商家）
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        if (goods.getGoodsName() !=null && !"".equals(goods.getGoodsName().trim())){
            criteria.andGoodsNameEqualTo(goods.getGoodsName().trim());
        }
        // 查询当前商家下的商品列表
        if (goods.getSellerId()!=null){
            criteria.andSellerIdEqualTo(goods.getSellerId().trim());
        }

        //设置排序
        query.setOrderByClause("id desc");

        // 根据条件查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(query);

        //封装到结果集
        return new PageResult(p.getTotal(),p.getResult());
    }

    /**
     * 回显商品
     *
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
        //商品信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);

        //商品描述信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);

        //库存信息
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(id);
        List<Item> itemList = itemDao.selectByExample(query);
        goodsVo.setItemList(itemList);

        return goodsVo;
    }

    /**
     * 更新商品
     *
     * @param goodsVo
     */
    @Override
    public void update(GoodsVo goodsVo) {
        //更新商品信息
        Goods goods = goodsVo.getGoods();
        goods.setAuditStatus("0");//商品如果审核不通过，打回来重新修改，再次审核
        goodsDao.updateByPrimaryKeySelective(goods);
        //更新商品描述信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
        //更新商品库存信息
        //先删除，
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(query);
        //再插入
        if ("1".equals(goods.getIsEnableSpec())){
            //启用规格spu1 -- sku n
            List<Item> itemList = goodsVo.getItemList();
            if (itemList !=null && itemList.size()>0){
                for (Item item : itemList){
                    //title = spu名称+spu副标题+规格名称
                    String title = goods.getGoodsName()+" "+goods.getCaption();
                    //数据：{"机身内存":"16G","网络":"联通3G"}
                    String spec = item.getSpec();
                    Map<String,String> map = JSON.parseObject(spec,Map.class);
                    Set<Map.Entry<String,String>> entries = map.entrySet();
                    for (Map.Entry<String,String> entry:entries){
                        title += " "+entry.getValue();
                    }
                    item.setTitle(title);
                    //设置库存信息
                    setAttributeForItem(goods,goodsDesc,item);
                }
            }
        }else{
            // 未启用规格
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setSpec("{}");
            setAttributeForItem(goods, goodsDesc, item);
            itemDao.insertSelective(item);
        }



    }

    /**
     * 运营商查询商品列表
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult searchForManager(Integer page, Integer rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件，待审核并且是未删除
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        if (goods.getGoodsName() !=null && !"".equals(goods.getGoodsName().trim())){
            criteria.andGoodsNameEqualTo(goods.getGoodsName().trim());
        }
        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus().trim())){
            criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());//待审核
        }
        criteria.andIsDeleteIsNull();//未删除
        //设置排序
        query.setOrderByClause("id desc");
        //根据条件查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(query);

        //封装到结果集并返回
        return new PageResult(p.getTotal(),p.getResult());
    }

    /**
     * 审核商品
     *
     * @param ids
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids != null && ids.length>0){
            //1.更新商品状态
            Goods goods = new Goods();
            goods.setAuditStatus(status);
            for (Long id : ids){
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                if ("1".equals(status)){
                    //商品上架，将数据存到索引库中
                    saveItemToSolr(id);
                    //为了测试检索，将库存所有数据存到索引库中
//                    dataImportToSolr();

                    //TODO 生成商品详情静态页
                }
            }
        }
    }

    //将商品对应的库存保存到索引库中
    private void saveItemToSolr(Long id) {
        ItemQuery itemQuery = new ItemQuery();
        //条件：根据商品id查询对应的库存，并且库存大于0的
        itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1")
                .andIsDefaultEqualTo("1").andNumGreaterThan(0);
        List<Item> items = itemDao.selectByExample(itemQuery);
        if (items != null && items.size()>0){
            for (Item item:items){
                String spec = item.getSpec();
                Map<String,String> map = JSON.parseObject(spec,Map.class);
                item.setSpecMap(map);
            }
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }

    }


    //将库存所有数据存到索引库中
    private void dataImportToSolr() {
        //首先将库存的数据查询出来
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andStatusEqualTo("1");
        List<Item> items = itemDao.selectByExample(itemQuery);
        if (items != null && items.size()>0){
            for (Item item : items){
                String spec = item.getSpec();
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }

            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }

    }

    /**
     * 商品删除
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length>0){
            Goods goods = new Goods();
            goods.setIsDelete("1");//设置成1，表示逻辑删除
            for (Long id : ids){
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                // TODO 2、商品下架
                // TODO 3、删除商品详情的静态页【可选】
            }

        }
    }

    //设置库存属性
    private void setAttributeForItem(Goods goods,GoodsDesc goodsDesc,Item item){
        String itemImage = goodsDesc.getItemImages();
        List<Map> images = JSON.parseArray(itemImage,Map.class);
        if (images != null && images.size()>0){
            String image = images.get(0).get("url").toString();
            item.setImage(image);
        }
        item.setCategoryid(goods.getCategory3Id());
        item.setStatus("1");//商品状态
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getId());//spu的id
        item.setSellerId(goods.getSellerId());//商家id
        //三级分类名称
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        //品牌名称
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());
        //商家的店铺名称
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getName());

    }

}
