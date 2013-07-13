package web.action.include.statics.cms;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.Category;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.service.cms.CategoryService;
import com.lehecai.admin.web.service.cms.NewsService;
import com.lehecai.admin.web.utils.StringUtil;
import com.opensymphony.xwork2.Action;

public class NewsForJsonAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(NewsForJsonAction.class);
	
	private static final long serialVersionUID = 2524999332385073306L;

	private NewsService newsService;
	private CategoryService categoryService;
	
	private String id;//栏目id,格式：1,2,3,4
	private Integer count = 10;//新闻条数
	private Integer title_length = 0;//标题长度
	private Integer containChildren = 1;//是否包括子栏目
	private Integer orderView = 1;		//是否按排序值排序
	
	public String handle(){
		logger.info("开始获取新闻json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONArray jsonArray = new JSONArray();
		
		if(id == null || "".equals(id)){
			rc = 1;
			message = "栏目id不能为空";
			logger.error("栏目id不能为空");
		}else{
			logger.info("查询栏目id={}", id);
			String[] cateIds = StringUtil.split(id, ',');
			List<Long> categories = new ArrayList<Long>();
			for (int i = 0; i < cateIds.length; i ++) {
				Long cateId = 0L;
				try {
					cateId = Long.parseLong(cateIds[i]);
				} catch (NumberFormatException e) {
					logger.error(e.getMessage(), e);
					continue;
				}
				
				Category category = categoryService.get(cateId);
				if(category == null){
					rc = 1;
					message = "id=" + id + "的栏目不存在";
					logger.error("id={}的栏目不存在", id);
				}else{	
					if(containChildren != 0&& category.getCaLevel() == 1){
						logger.info("栏目id={}有子栏目", id);
						categories.add(category.getId());
						
						Category c = new Category();
						c.setParentID(category.getId());
						List<Category> clist = categoryService.list(c);
						for(Category cc : clist){
							categories.add(cc.getId());
						}
					}else{
						logger.info("栏目id={}有无栏目", id);
						categories.add(category.getId());
					}
				}
			}
			
			if (categories.size() != 0) {
				PageBean pageBean = super.getPageBean();
				pageBean.setPageSize(count);
				
				List<News> list = null;
				if (orderView != 0) {
					logger.info("进入按照排序值降序查询新闻列表");
					list = newsService.listByCateIdOrderView(categories, pageBean);
				} else {
					logger.info("进入不按照排序值降序查询新闻列表");
					list = newsService.listByCateId(categories, pageBean);
				}
				for(News n : list) {
					logger.info("查询新闻id={}", n.getId());
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("news_id", n.getId());
					jsonObject.put("cate_id", n.getCateID());
					String viewTitle = n.getTitle() == null ? "" : n.getTitle();
					jsonObject.put("title", viewTitle);
					if(title_length > 0){
						if (title_length < viewTitle.length()) {
							viewTitle = viewTitle.substring(0, title_length) + "..."; 
						}
					}
					jsonObject.put("img_url", n.getImageUrl() == null ? "" : n.getImageUrl());
					jsonObject.put("viewTitle", viewTitle);
					jsonObject.put("author", n.getAuthor() == null ? "" : n.getAuthor());
					jsonObject.put("short_content", n.getShortContent() == null ? "" : n.getShortContent());
					jsonObject.put("keyword", n.getKeyword() == null ? "" : n.getKeyword());
					jsonObject.put("editor", n.getEditor() == null ? "" : n.getEditor());
					jsonObject.put("is_recommend", n.isHeadNews() ? 1 : 0);
					jsonObject.put("from_place", n.getFromPlace() == null ? "" : n.getFromPlace());
					jsonObject.put("publish_date", n.getUpdateTimeStr());
					jsonObject.put("url", n.getUrl() == null ? "" : n.getUrl());
					jsonArray.add(jsonObject);
				}
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", jsonArray);
		
		super.writeRs(response, json);
		
		logger.info("结束获取新闻json数据");
		return Action.NONE;
	}
	
	public NewsService getNewsService() {
		return newsService;
	}
	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
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
	public Integer getTitle_length() {
		return title_length;
	}
	public void setTitle_length(Integer titleLength) {
		title_length = titleLength;
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public Integer getContainChildren() {
		return containChildren;
	}

	public void setContainChildren(Integer containChildren) {
		this.containChildren = containChildren;
	}

	public Integer getOrderView() {
		return orderView;
	}

	public void setOrderView(Integer orderView) {
		this.orderView = orderView;
	}
}
