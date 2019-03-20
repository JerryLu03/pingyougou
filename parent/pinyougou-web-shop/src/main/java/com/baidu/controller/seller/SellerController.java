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

    @RequestMapping("/add.do")
    public Result add(@RequestBody Seller seller){
        try {
            sellerService.add(seller);
            return new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }


}
