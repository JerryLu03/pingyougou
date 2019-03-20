package com.baidu.service.seller;

import com.baidu.entity.PageResult;
import com.baidu.pojo.seller.Seller;

public interface SellerService {

    /**
     * 商家入驻
     * @param seller
     */
    void add(Seller seller);

    /**
     * 运营商：查询待审的商家列表
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    PageResult search(Integer page,Integer rows,Seller seller);

    /**
     * 回显商家
     * @param sellerId
     * @return
     */
    Seller findOne(String sellerId);


    /**
     * 审核商家
     * @param sellerId
     * @param status
     */
    void updateStatus(String sellerId,String status);
}
