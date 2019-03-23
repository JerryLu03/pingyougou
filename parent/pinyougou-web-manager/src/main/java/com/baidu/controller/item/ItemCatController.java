package com.baidu.controller.item;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.entity.Result;
import com.baidu.pojo.item.ItemCat;
import com.baidu.service.itemcat.ItemCatService;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping("/add.do")
    public Result add( @RequestBody ItemCat itemCat){
        try {
            itemCatService.add(itemCat);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    /**
     * 查询所有商品分类
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}
