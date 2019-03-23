package com.baidu.service.goods;

import com.baidu.entity.PageResult;
import com.baidu.pojo.good.Goods;
import com.baidu.vo.GoodsVo;

public interface GoodsService {
    /**
     * 商品录入
     * @param goodsVo
     */
    void add(GoodsVo goodsVo);

    /**
     * 查询商品列表信息
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult search(Integer page, Integer rows, Goods goods);

    /**
     * 回显商品
     * @param id
     * @return
     */
    GoodsVo findOne(Long id);

    /**
     * 更新商品
     * @param goodsVo
     */
    void update(GoodsVo goodsVo);

    /**
     * 运营商查询商品列表
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult searchForManager(Integer page,Integer rows,Goods goods);
}
