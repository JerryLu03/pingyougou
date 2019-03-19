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

    /**
     * 回显模板
     * @param id
     * @return
     */
    TypeTemplate findOne(Long id);


    /**
     * 修改模板
     * @param typeTemplate
     */
    void update(TypeTemplate typeTemplate);

    /**
     * 删除模板
     * @param ids
     */
    void delete(Long[] ids);
}
