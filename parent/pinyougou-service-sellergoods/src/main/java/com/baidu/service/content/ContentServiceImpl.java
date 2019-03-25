package com.baidu.service.content;

import java.util.List;

import com.baidu.dao.ad.ContentDao;
import com.baidu.entity.PageResult;
import com.baidu.pojo.ad.Content;
import com.baidu.pojo.ad.ContentQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;


@Service
public class ContentServiceImpl implements ContentService {

	@Resource
	private ContentDao contentDao;

	//注入redis链接
	@Resource
    private RedisTemplate<String,Object> redisTemplate;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Content content) {
		//缓存同步，清空缓存
		clearCache(content.getCategoryId());
		//新增广告
		contentDao.insertSelective(content);
	}
	//清除缓存
	private void clearCache(Long categoryId) {
		redisTemplate.boundHashOps("content").delete(categoryId);
	}

	//需要判断广告的分类是否发生改变
	//如果分类发生改变，删除之前数据，删除现在更新的分类数据
	@Override
	public void edit(Content content) {
		Long newCategoryId = content.getCategoryId();
		Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();

		if (newCategoryId != oldCategoryId){
			//分类发生改变
			clearCache(newCategoryId);
			clearCache(oldCategoryId);
		}else {
			clearCache(newCategoryId);
		}

		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				//缓存同步，清空缓存
				clearCache(contentDao.selectByPrimaryKey(id).getCategoryId());
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	/**
	 * 首页大广告的轮播图(使用redis缓存)
	 *
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<Content> findByCategoryId(Long categoryId) {
        //采用redis缓存
        //优化：hash hset key (map)filed value 交互一次
        //首先判断缓存中是否有数据
        List<Content> list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
        //排队，解决高并发问题
        synchronized (this){
			if (list == null){
				//缓存中没有，就从数据库中查询
				// 设置条件：根据广告分类查询，并且是可用的
				ContentQuery query = new ContentQuery();
				query.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
				query.setOrderByClause("sort_order desc");
				list= contentDao.selectByExample(query);
				//然后再把数据存到缓存里
				redisTemplate.boundHashOps("content").put(categoryId,list);
			}

		}

		return list;

	}


}
