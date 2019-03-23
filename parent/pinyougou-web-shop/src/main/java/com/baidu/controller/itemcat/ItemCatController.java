package com.baidu.controller.itemcat;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.pojo.item.ItemCat;
import com.baidu.service.itemcat.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    //注入itemcat的service
    @Reference
    private ItemCatService itemCatService;

    //商品分类列表查询
    @RequestMapping("/findByParentId.do")
    public List<ItemCat>findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    /**
     * 新增商品选择三级分类时，加载商品模板
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    /**
     * 商品列表回显分类名称
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}
