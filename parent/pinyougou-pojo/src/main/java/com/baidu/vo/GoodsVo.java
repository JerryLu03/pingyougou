package com.baidu.vo;

import com.baidu.pojo.good.Goods;
import com.baidu.pojo.good.GoodsDesc;
import com.baidu.pojo.item.Item;

import java.io.Serializable;
import java.util.List;

/**
 * 封装：商品保存需要的数据
 */
public class GoodsVo implements Serializable {
    private Goods goods;
    private GoodsDesc goodsDesc;
    private List<Item> itemList;

    public GoodsVo() {
    }

    public GoodsVo(Goods goods, GoodsDesc goodsDesc, List<Item> itemList) {
        this.goods = goods;
        this.goodsDesc = goodsDesc;
        this.itemList = itemList;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
