package com.baidu.service.goods;

import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.dao.good.GoodsDao;
import com.baidu.dao.good.GoodsDescDao;
import com.baidu.pojo.good.Goods;
import com.baidu.pojo.good.GoodsDesc;
import com.baidu.vo.GoodsVo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class GoodsServiceImpl implements GoodsService {
    //注入dao

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

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


    }
}
