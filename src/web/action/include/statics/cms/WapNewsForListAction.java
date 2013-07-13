package web.action.include.statics.cms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.WapCategory;
import com.lehecai.admin.web.domain.cms.WapNews;
import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.admin.web.service.cms.WapCategoryService;
import com.lehecai.admin.web.service.cms.WapNewsService;
import com.lehecai.admin.web.utils.DateUtil;
import com.opensymphony.xwork2.Action;

public class WapNewsForListAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(WapNewsForListAction.class);
	
	private static final long serialVersionUID = 2524999332385073306L;

	private WapNewsService wapNewsService;
	private WapCategoryService wapCategoryService;
	
	private String id;//栏目id
	private Integer count = 30;//新闻条数
	private Integer titleLength = 0;//标题长度
	private boolean containChildren = true;//是否包括子栏目
	private Integer headNews = -1;
	private Integer contentType;
	private Integer valid = 1;
	private Integer page = 1;
	private Integer pageSize = 50;
	
	public String handle(){
		logger.info("开始获取新闻json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		JSONObject json = new JSONObject();
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONArray jsonArray = new JSONArray();
		
		if(StringUtils.isEmpty(id)){
			rc = 1;
			message = "string型栏目id不能为空";
			logger.error("string型栏目id不能为空");
		}else{
			Long nid = null;
			try {
				nid = Long.valueOf(id);
			} catch (NumberFormatException e) {
				logger.error("栏目id={}，string转换为Long错误", id);
				logger.error(e.getMessage(), e);
				json.put("code", rc);
				json.put("message", message);
				json.put("data", jsonArray);
				
				super.writeRs(response, json);
				
				return Action.NONE;
			}
			logger.info("查询栏目id={}", id);
			
			List<Long> wapCategories = new ArrayList<Long>();
			WapCategory wapCategory = null;
			try {
				wapCategory = wapCategoryService.get(nid);
			} catch (Exception e) {
				rc = 1;
				message = "未查询到栏目";
				logger.error("未查询到栏目");
				json.put("code", rc);
				json.put("message", message);
				super.writeRs(response, json);
				logger.info("结束获取新闻json数据");
				return Action.NONE;
			}
			if(wapCategory == null){
				rc = 1;
				message = "id=" + id + "的栏目不存在";
				logger.error("id={}的栏目不存在", id);
				json.put("code", rc);
				json.put("message", message);
				super.writeRs(response, json);
				logger.info("结束获取新闻json数据");
				return Action.NONE;
			}else{	
				if(containChildren && wapCategory.getCaLevel() == 1){
					logger.info("栏目id={}有子栏目", id);
					wapCategories.add(wapCategory.getId());
					
					WapCategory c = new WapCategory();
					c.setParentID(wapCategory.getId());
					List<WapCategory> clist = wapCategoryService.list(c);
					for(WapCategory cc : clist){
						wapCategories.add(cc.getId());
					}
				}else{
					logger.info("栏目id={}有无栏目", id);
					wapCategories.add(wapCategory.getId());
				}
			}
			
			PageBean pageBean = super.getPageBean();
			pageBean.setCount(count);
			pageBean.setPageSize(pageSize);
			pageBean.setPage(page);
			
			ContentType ct = contentType == null ? null : ContentType.getItem(contentType); 
			
			List<WapNews> list = wapNewsService.listByCateId(wapCategories, headNews, ct, valid, pageBean);
			for(WapNews n : list){
				logger.info("查询新闻id={}", n.getId());
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("news_id", n.getId());
				jsonObject.put("cate_id", n.getCateID());
				jsonObject.put("head_news", n.isHeadNews() ? 1 : 0);
				jsonObject.put("current_new", n.isCurrentNew() ? 1 : 0);
				jsonObject.put("valid", n.isValid()  ? 1 : 0);
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
			pageBean = wapNewsService.countByCateId(wapCategories, pageBean);
			json.put("page", JSONObject.fromObject(pageBean));
			json.put("cate_name", wapCategory.getName());
		}
		
		json.put("code", rc);
		json.put("message", message);
		json.put("data", jsonArray);
		super.writeRs(response, json);
		
		logger.info("结束获取新闻json数据");
		return Action.NONE;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public boolean isContainChildren() {
		return containChildren;
	}

	public void setContainChildren(boolean containChildren) {
		this.containChildren = containChildren;
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

	public Integer getHeadNews() {
		return headNews;
	}

	public void setHeadNews(Integer headNews) {
		this.headNews = headNews;
	}

	public Integer getContentType() {
		return contentType;
	}

	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTitleLength() {
		return titleLength;
	}

	public void setTitleLength(Integer titleLength) {
		this.titleLength = titleLength;
	}
}
