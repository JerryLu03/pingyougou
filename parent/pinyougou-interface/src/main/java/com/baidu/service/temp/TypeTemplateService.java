package com.baidu.service.temp;

import com.baidu.entity.PageResult;
import com.baidu.pojo.template.TypeTemplate;

public interface TypeTemplateService {

    /**
     * 查询
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    PageResult search(Integer page,Integer rows,TypeTemplate typeTemplate);

    /**
     * 保存
     * @param typeTemplate
     * @return
     */
    void add(TypeTemplate typeTemplate);
}
