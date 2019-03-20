package com.baidu.controller.seller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.entity.PageResult;
import com.baidu.entity.Result;
import com.baidu.pojo.seller.Seller;
import com.baidu.service.seller.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    //注入service
    @Reference
    private SellerService sellerService;


    //查询待审商家列表
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller){
        return sellerService.search(page,rows,seller);
    }


    //回显商家
    @RequestMapping("/findOne.do")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }

    //审核商家
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
           return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
           return new Result(false,"操作失败");
        }
    }

}
