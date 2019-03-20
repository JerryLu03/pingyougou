package com.baidu.service.seller;

import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.dao.seller.SellerDao;
import com.baidu.entity.PageResult;
import com.baidu.pojo.seller.Seller;
import com.baidu.pojo.seller.SellerQuery;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {

    //注入dao
    @Resource
    private SellerDao sellerDao;

    /**
     * 商家入驻
     *
     * @param seller
     */
    @Override
    public void add(Seller seller) {
        //设置商家状态
        seller.setStatus("0");//待审核
        seller.setCreateTime(new Date());//注册日期
        //明文密码--加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String password = bCryptPasswordEncoder.encode(seller.getPassword());
        seller.setPassword(password);
        //保存
        sellerDao.insertSelective(seller);

    }

    /**
     * 运营商：查询待审的商家列表
     *
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //1.设置分页条件
        PageHelper.startPage(page,rows);
        //2.设置查询条件
        SellerQuery query = new SellerQuery();
        if (seller.getStatus()!=null && !"".equals(seller.getStatus().trim())){
            query.createCriteria().andStatusEqualTo(seller.getStatus().trim());
        }
        //3.根据条件查询
        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(query);
        //4.封装结果集
        return new PageResult(p.getTotal(),p.getResult());

    }

    /**
     * 回显商家
     *
     * @param sellerId
     * @return
     */
    @Override
    public Seller findOne(String sellerId) {
       return sellerDao.selectByPrimaryKey(sellerId);
    }

    /**
     * 审核商家
     *
     * @param sellerId
     * @param status
     */
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setStatus(status);
        seller.setSellerId(sellerId);
        //更新审核信息
       sellerDao.updateByPrimaryKeySelective(seller);
    }
}
