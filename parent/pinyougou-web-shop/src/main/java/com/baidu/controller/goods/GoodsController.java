package com.baidu.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.entity.PageResult;
import com.baidu.entity.Result;
import com.baidu.pojo.good.Goods;
import com.baidu.service.goods.GoodsService;
import com.baidu.vo.GoodsVo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    //注入service
    @Reference
    private GoodsService goodsService;

    @RequestMapping("/add.do")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {
            //设置商家id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(sellerId);//设置商家id
            goodsService.add(goodsVo);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        //设置商家id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page,rows,goods);
    }

    /**
     * 回显商品
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public GoodsVo findOne(Long id){
        return goodsService.findOne(id);
    }


    /**
     * 更新商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("/update.do")
    public Result update( @RequestBody GoodsVo goodsVo){
        try {
            goodsService.update(goodsVo);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }

    }
}
