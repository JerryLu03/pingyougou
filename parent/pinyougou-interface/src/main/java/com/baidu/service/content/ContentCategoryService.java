package com.baidu.service.content;

import com.baidu.entity.PageResult;
import com.baidu.pojo.ad.ContentCategory;

import java.util.List;


public interface ContentCategoryService {

public List<ContentCategory> findAll();
	
	public PageResult findPage(ContentCategory contentCategory, Integer pageNum, Integer pageSize);
	
	public void add(ContentCategory contentCategory);
	
	public void edit(ContentCategory contentCategory);
	
	public ContentCategory findOne(Long id);
	
	public void delAll(Long[] ids);


}
