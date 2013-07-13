package web.service.cms;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.WapNews;
import com.lehecai.admin.web.enums.ContentType;

public interface WapNewsService {
	WapNews manage(WapNews wapNews);
	List<WapNews> list(WapNews wapNews, PageBean pageBean);
	List<WapNews> listByCateId(List<Long> cateIds, Integer headNews, ContentType contentType, Integer valid, PageBean pageBean);
	WapNews getPrev(Long cateId, Date updateTime, Integer orderView);
	WapNews getNext(Long cateId, Date updateTime, Integer orderView);
	PageBean countByCateId(List<Long> cateIds, PageBean pageBean);
	PageBean getPageBean(WapNews wapNews, PageBean pageBean);
	PageBean getPageBeanByHome(ContentType contentType, PageBean pageBean);
	WapNews get(Long ID);
	void del(WapNews wapNews);
	public List<WapNews> listByHome(boolean home, ContentType contentType, PageBean pageBean);
	
	/**
	 * 按条件查询
	 * @param wapNews
	 * @param cateIds
	 * @param beginTime
	 * @param endTime
	 * @param orderStr
	 * @param orderView
	 * @param pageBean
	 * @return
	 */
	List<WapNews> listByCondition(WapNews wapNews, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean);
	
	/**
	 * 按条件查询分页处理
	 * @param wapNews
	 * @param cateIds
	 * @param beginTime
	 * @param endTime
	 * @param orderStr
	 * @param orderView
	 * @param pageBean
	 * @return
	 */
	PageBean getPageBeanByCondition(WapNews wapNews, List<Long> cateIds, Date beginTime, Date endTime, String orderStr, String orderView, PageBean pageBean);

}