package web.action.include.statics.cms;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.WapNews;
import com.lehecai.admin.web.service.cms.WapNewsService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.UploadUtil;
import com.opensymphony.xwork2.Action;

public class WapNewsForJsonAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(WapNewsForJsonAction.class);
	
	private static final long serialVersionUID = 2524999332385073306L;

	private WapNewsService wapNewsService;
	private Long id;//新闻id,格式：1,2,3,4
	private Integer titleLength = 0;//标题长度
	
	public String handle(){
		logger.info("开始获取新闻json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		
		if(id == null || "".equals(id)){
			rc = 1;
			message = "栏目id不能为空";
			logger.error("栏目id不能为空");
		}else{
			WapNews n = wapNewsService.get(id);
			if (n == null) {
				rc = 1;
				message = "id=" + id + "的新闻不存在";
				logger.error("id={}的新闻不存在", id);
				json.put("code", rc);
				json.put("message", message);
				super.writeRs(response, json);
				logger.info("结束获取新闻json数据");
				return Action.NONE;
			}
			logger.info("查询新闻id={}", n.getId());
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("news_id", n.getId());
			jsonObject.put("cate_id", n.getCateID());
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
			jsonObject.put("headNews", n.isHeadNews() ? 1 : 0);
			jsonObject.put("from_place", n.getFromPlace() == null ? "" : n.getFromPlace());
			jsonObject.put("publish_date", n.getCreateTime() == null ? "" : DateUtil.formatDate(n.getCreateTime(), DateUtil.DATETIME));
			jsonObject.put("url", n.getUrl() == null ? "" : n.getUrl());
			jsonObject.put("content", n.getContent() == null ? "" : UploadUtil.replaceContentFromDB(n.getContent()));
			jsonObject.put("valid", n.isValid() ? 1 : 0);
			
			WapNews wapNewsPrev = wapNewsService.getPrev(n.getCateID(), n.getUpdateTime(), n.getOrderView());
			WapNews wapNewsNext = wapNewsService.getNext(n.getCateID(), n.getUpdateTime(), n.getOrderView());
			
			if (wapNewsPrev == null || wapNewsPrev.getId() == null) {
				JSONObject jPrev = new JSONObject();
				jPrev.put("prevId", "");
				jPrev.put("prevTitle", "");
				jPrev.put("content_type", "");
				jPrev.put("link", "");
				jsonObject.put("prev", jPrev);
			} else {
				JSONObject jPrev = new JSONObject();
				jPrev.put("prevId", wapNewsPrev.getId());
				jPrev.put("prevTitle", wapNewsPrev.getTitle() == null ? "" : wapNewsPrev.getTitle());
				jPrev.put("content_type", wapNewsPrev.getContentType() == null ? "" : wapNewsPrev.getContentType().getValue());
				jPrev.put("link", wapNewsPrev.getLink());
				jsonObject.put("prev", jPrev);
			}
			if (wapNewsNext == null || wapNewsNext.getId() == null) {
				JSONObject jNext = new JSONObject();
				jNext.put("nextId", "");
				jNext.put("nextTitle", "");
				jNext.put("content_type", "");
				jNext.put("link", "");
				jsonObject.put("next", jNext);
			} else {
				JSONObject jNext = new JSONObject();
				jNext.put("nextId", wapNewsNext.getId());
				jNext.put("nextTitle", wapNewsNext.getTitle() == null ? "" : wapNewsNext.getTitle());
				jNext.put("content_type", wapNewsNext.getContentType() == null ? "" : wapNewsNext.getContentType().getValue());
				jNext.put("link", wapNewsNext.getLink());
				jsonObject.put("next", jNext);
			}			
			json.put("data", jsonObject);
		}
		
		json.put("code", rc);
		json.put("message", message);
		super.writeRs(response, json);
		
		logger.info("结束获取新闻json数据");
		return Action.NONE;
	}

	public WapNewsService getWapNewsService() {
		return wapNewsService;
	}

	public void setWapNewsService(WapNewsService wapNewsService) {
		this.wapNewsService = wapNewsService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTitleLength() {
		return titleLength;
	}

	public void setTitleLength(Integer titleLength) {
		this.titleLength = titleLength;
	}

}
