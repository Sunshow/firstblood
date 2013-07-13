package web.service.impl.cms;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.WapNewsDao;
import com.lehecai.admin.web.domain.cms.WapNews;
import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.admin.web.service.cms.WapNewsService;

public class WapNewsServiceImpl implements WapNewsService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.NewsService#add()
	 */
	private WapNewsDao wapNewsDao;
	//private WapCategoryService wapCategoryService;
	
	public WapNews manage(WapNews wapNews){
		return wapNewsDao.merge(wapNews);
	}

	public WapNewsDao getWapNewsDao() {
		return wapNewsDao;
	}

	public void setWapNewsDao(WapNewsDao wapNewsDao) {
		this.wapNewsDao = wapNewsDao;
	}

	@Override
	public WapNews getNext(Long cateId, Date updateTime, Integer orderView) {
		return wapNewsDao.getNext(cateId, updateTime, orderView);
	}

	@Override
	public WapNews getPrev(Long cateId, Date updateTime, Integer orderView) {
		return wapNewsDao.getPrev(cateId, updateTime, orderView);
	}
	
	@Override
	public List<WapNews> list(WapNews wapNews, PageBean pageBean) {
		return wapNewsDao.list(wapNews, pageBean);
	}
	@Override
	public List<WapNews> listByCateId(List<Long> cateIds, Integer headNews, ContentType contentType, Integer valid, PageBean pageBean) {
		return wapNewsDao.listByCateId(cateIds, headNews, contentType, valid, pageBean);
	}
	@Override
	public List<WapNews> listByHome(boolean home, ContentType contentType, PageBean pageBean) {
		// TODO Auto-generated method stub
		return wapNewsDao.listByHome(home, contentType, pageBean);
	}

	@Override
	public WapNews get(Long ID) {
		return wapNewsDao.get(ID);
	}

	@Override
	public void del(WapNews wapNews) {
		wapNewsDao.del(wapNews);
	}

	@Override
	public PageBean getPageBean(WapNews wapNews, PageBean pageBean) {
		return wapNewsDao.getPageBean(wapNews, pageBean);
	}

	@Override
	public PageBean getPageBeanByHome(ContentType contentType, PageBean pageBean) {
		// TODO Auto-generated method stub
		return wapNewsDao.getPageBeanByHome(contentType, pageBean);
	}

	@Override
	public PageBean countByCateId(List<Long> cateIds, PageBean pageBean) {
		return wapNewsDao.countByCateId(cateIds, pageBean);
	}

	@Override
	public List<WapNews> listByCondition(WapNews wapNews, List<Long> cateIds,
			Date beginTime, Date endTime, String orderStr, String orderView,
			PageBean pageBean) {
		return wapNewsDao.listByCondition(wapNews,cateIds,beginTime,endTime,orderStr,orderView,pageBean);
	}

	@Override
	public PageBean getPageBeanByCondition(WapNews wapNews, List<Long> cateIds,
			Date beginTime, Date endTime, String orderStr, String orderView,
			PageBean pageBean) {
		return wapNewsDao.getPageBeanByCondition(wapNews,cateIds,beginTime,endTime,orderStr,orderView,pageBean);
	}
}
