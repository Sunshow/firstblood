package web.action.include.statics.cms;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.WapNews;
import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.admin.web.service.cms.WapCategoryService;
import com.lehecai.admin.web.service.cms.WapNewsService;
import com.lehecai.admin.web.utils.DateUtil;
import com.opensymphony.xwork2.Action;

public class WapNewsForHomeAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(WapNewsForHomeAction.class);
	
	private static final long serialVersionUID = 2524999332385073306L;

	private WapNewsService wapNewsService;
	private WapCategoryService wapCategoryService;
	
	private Integer count = 5;//新闻条数
	private Integer titleLength = 0;//标题长度
	private Integer contentType;
	private Integer page = 1;
	
	public String handle(){
		logger.info("开始获取新闻json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		JSONObject json = new JSONObject();
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONArray jsonArray = new JSONArray();
		
		PageBean pageBean = super.getPageBean();
		pageBean.setCount(count);
		pageBean.setPageSize(count);
		pageBean.setPage(page);
		
		ContentType ct = contentType == null ? null : ContentType.getItem(contentType); 
		
		List<WapNews> list = wapNewsService.listByHome(true, ct, pageBean);
		if (list != null) {
			if (list.size() < count) {
				PageBean pageBeanNoHead = super.getPageBean();
				pageBeanNoHead.setCount(count - list.size());
				pageBeanNoHead.setPageSize(count - list.size());
				pageBeanNoHead.setPage(page);
				List<WapNews> listNoHome = wapNewsService.listByHome(false, ct, pageBeanNoHead);
				list.addAll(listNoHome);
			}
		} else {
			PageBean pageBeanNoHead = super.getPageBean();
			pageBeanNoHead.setCount(count);
			pageBeanNoHead.setPageSize(count);
			pageBeanNoHead.setPage(page);
			List<WapNews> listNoHome = wapNewsService.listByHome(false, ct, pageBeanNoHead);
			list = listNoHome;
		}
		for(WapNews n : list){
			logger.info("查询新闻id={}", n.getId());
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("news_id", n.getId());
			jsonObject.put("cate_id", n.getCateID());
			jsonObject.put("head_news", n.isHeadNews() ? 1 : 0);
			jsonObject.put("current_new", n.isCurrentNew() ? 1 : 0);
			jsonObject.put("valid", n.isValid()  ? 1 : 0);
			jsonObject.put("home", n.isHomePage() ? 1 : 0);
			String viewTitle = n.getTitle() == null ? "" : n.getTitle();
			jsonObject.put("title", viewTitle);
			if(titleLength > 0){
				if (titleLength < viewTitle.length()) {
					viewTitle = viewTitle.substring(0, titleLength) + "..."; 
				}
			}
			jsonObject.put("viewTitle", viewTitle);
			jsonObject.put("author", n.getAuthor() == null ? "" : n.getAuthor());
			jsonObject.put("short_content", n.getShortContent() == null ? "" : n.getShortContent());
			jsonObject.put("keyword", n.getKeyword() == null ? "" : n.getKeyword());
			jsonObject.put("editor", n.getEditor() == null ? "" : n.getEditor());
			jsonObject.put("from_place", n.getFromPlace() == null ? "" : n.getFromPlace());
			jsonObject.put("create_time", n.getCreateTime() == null ? "" : DateUtil.formatDate(n.getCreateTime(), DateUtil.DATETIME));
			jsonObject.put("update_time", n.getUpdateTime() == null ? "" : DateUtil.formatDate(n.getUpdateTime(), DateUtil.DATETIME));
			jsonObject.put("url", n.getUrl() == null ? "" : n.getUrl());
			jsonObject.put("link", n.getLink() == null ? "" : n.getLink());
			jsonObject.put("content_type", n.getContentType() == null ? "" : n.getContentType().getValue());
			jsonObject.put("click", n.getClick() == null ? "0" : n.getClick());
			jsonArray.add(jsonObject);
			}
		json.put("page", JSONObject.fromObject(pageBean));
		json.put("code", rc);
		json.put("message", message);
		json.put("data", jsonArray);
		super.writeRs(response, json);
		
		logger.info("结束获取新闻json数据");
		return Action.NONE;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public WapNewsService getWapNewsService() {
		return wapNewsService;
	}

	public void setWapNewsService(WapNewsService wapNewsService) {
		this.wapNewsService = wapNewsService;
	}

	public WapCategoryService getWapCategoryService() {
		return wapCategoryService;
	}

	public void setWapCategoryService(WapCategoryService wapCategoryService) {
		this.wapCategoryService = wapCategoryService;
	}

	public Integer getContentType() {
		return contentType;
	}

	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getTitleLength() {
		return titleLength;
	}

	public void setTitleLength(Integer titleLength) {
		this.titleLength = titleLength;
	}
}
