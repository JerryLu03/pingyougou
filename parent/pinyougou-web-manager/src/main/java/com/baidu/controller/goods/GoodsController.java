package com.baidu.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.entity.PageResult;
import com.baidu.pojo.good.Goods;
import com.baidu.service.goods.GoodsService;
import org.opensaml.xml.signature.G;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    //注入service
    @Reference
    private GoodsService goodsService;

    /**
     * 运营商查询商品列表
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        return goodsService.search(page,rows,goods);
    }
}
