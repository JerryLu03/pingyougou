package com.baidu.controller.temp;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.pojo.template.TypeTemplate;
import com.baidu.service.temp.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    //注入
    @Reference
    private TypeTemplateService typeTemplateService;


    @RequestMapping("/findOne.do")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    @RequestMapping("/findBySpecList.do")
    public List<Map> findBySpecList(Long id){
        return typeTemplateService.findBySpecList(id);
    }
}
