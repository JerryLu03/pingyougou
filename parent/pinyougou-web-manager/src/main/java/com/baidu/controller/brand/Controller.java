package com.baidu.controller.brand;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baidu.entity.PageResult;
import com.baidu.entity.Result;
import com.baidu.pojo.good.Brand;
import com.baidu.service.brand.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class Controller {

    //注入service
    @Reference
    private BrandService brandService;
    //查询所有品牌
    @RequestMapping("/findAll.do")
    public List<Brand> findAll(){
        List<Brand> brands = brandService.findAll();
        return brands;
    }



    @RequestMapping("/findPage.do")
    public PageResult findPage(Integer pageNo, Integer pageSize){
        return brandService.findPage(pageNo, pageSize);
    }

    /**
     * 条件查询
     * @param pageNo
     * @param pageSize
     * @param brand
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer pageNo, Integer pageSize,@RequestBody Brand brand){
        return brandService.search(pageNo, pageSize, brand);
    }

    //保存
    @RequestMapping("/add.do")
    public Result add(@RequestBody Brand brand){

        try {
            brandService.add(brand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }

    }

    /**
     * 回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 修改
     * @param brand
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByPrimaryKeys(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/selectOptionList.do")
    public List<Map<String,String>> selectOptionList(){
        return brandService.selectOptionList();
    }

}
