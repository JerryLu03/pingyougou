package com.baidu.controller.spec;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.entity.PageResult;
import com.baidu.entity.Result;
import com.baidu.pojo.specification.Specification;
import com.baidu.service.spec.SpecService;
import com.baidu.vo.SpecVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecController {

    //注入service
    @Reference
    private SpecService specService;

    /**
     * 条件查询
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification){
       return specService.search(page,rows,specification);
    }

    /**
     * 添加
     * @param specVo
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody SpecVo specVo){
        try {
             specService.add(specVo);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public SpecVo findOne(Long id){
        return specService.findOne(id);
    }


    /**
     * 修改
     * @param specVo
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody SpecVo specVo){
        try {
            specService.update(specVo);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            specService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    /**
     * 新增模板，初始化下拉框规格的数据
     *
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map<String,String>>selectOptionList(){
        return specService.selectOptionList();
    }


}
