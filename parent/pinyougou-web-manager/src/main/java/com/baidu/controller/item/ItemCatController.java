package com.baidu.controller.item;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.pojo.item.ItemCat;
import com.baidu.service.itemcat.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    //注入service
    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findByParentId.do")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }
}