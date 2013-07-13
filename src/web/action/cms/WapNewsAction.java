package web.action.cms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.cms.Resource;
import com.lehecai.admin.web.domain.cms.WapCategory;
import com.lehecai.admin.web.domain.cms.WapNews;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.enums.ContentType;
import com.lehecai.admin.web.service.cms.ResourceService;
import com.lehecai.admin.web.service.cms.WapCategoryService;
import com.lehecai.admin.web.service.cms.WapNewsService;
import com.lehecai.admin.web.service.user.PermissionService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.admin.web.utils.UploadUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreStringUtils;
import com.opensymphony.xwork2.Action;

public class WapNewsAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(WapNewsAction.class);
	private static final long serialVersionUID = 2436161530465382824L;
	
	private static final String KEY_ORDER_BY_ID = "id";
	private static final String KEY_ORDER_BY_CREATETIME = "createTime";
	private static final String KEY_ORDER_BY_UPDATETIME = "updateTime";
	private static final String URL_CONDITION = "conditionQuery";
	
	private  String URL;
	
	private WapNewsService wapNewsService;
	private WapCategoryService wapCategoryService;
	private ResourceService resourceService;
	private PermissionService permissionService;

	private WapNews wapNews;
	private WapCategory wapCategory;
	private WapCategory parentWapCategory;
	
	private List<WapNews> wapNewsList;
	private List<WapCategory> wapCategories;
	
	private String createTime;
	private Integer contentTypeId;
	private Integer disableEditorValue;
	
	private String defaultTemplatePath;
	private String defaultTargetPath;
	private Integer defaultStaticPageType;
	
	private String orderStr;
	private String idStr;
	
	private Integer order;
	
	private Long wapNewsId;
	
	private String variable;
	private String domain; 
	
	private String queryFlag;
	private String userName;
	private Date beginTime;
	private Date endTime;
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	private String orderView;
	
	private String fromUrl;


	public String handle(){
		List<WapCategory> list = wapCategoryService.list(wapCategory);
		wapCategories = new ArrayList<WapCategory>();
		Map<Long, WapCategory> parentMap = new HashMap<Long, WapCategory>();
		if(list != null && list.size() != 0){
			for(WapCategory sc : list){
				if(sc.getCaLevel() == 1){
					parentMap.put(sc.getId(), sc);			
					wapCategories.add(sc);				
				} else {
					WapCategory parent = parentMap.get(sc.getParentID());
					if (parent != null) {
						List<WapCategory> children = parent.getChildren();
						children.add(sc);
					}
				}
			}
		}
		return "listCategories";
	}
	
	public String list(){
		HttpServletRequest request = ServletActionContext.getRequest();
		wapNewsList = wapNewsService.list(wapNews, super.getPageBean());
		PageBean pageBean = wapNewsService.getPageBean(wapNews, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		if(wapNews != null && wapNews.getCateID() != null){		
			wapCategory = wapCategoryService.get(wapNews.getCateID());
			if(wapCategory != null && wapCategory.getCaLevel() == 2){
				parentWapCategory = wapCategoryService.get(wapCategory.getParentID());
			}
		}
		
		String key;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Integer year = calendar.get(Calendar.YEAR);
		calendar.get(Calendar.MONTH);
		String month = "";
		if (calendar.get(Calendar.MONTH) <= 8) {
			month = "0" + (calendar.get(Calendar.MONTH) + 1);
		} else {
			month = (calendar.get(Calendar.MONTH) + 1) + "";
		}
		Integer day = calendar.get(Calendar.DAY_OF_MONTH);
		String dayValue = "";
		if (day!= null && day < 10) {
			dayValue = "0" + day;
		} else {
			dayValue = String.valueOf(day);
		}
		Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
		Integer minute = calendar.get(Calendar.MINUTE);
		minute = minute - minute%10;
		key = "" + year + month + day + hour + minute;
		
		
		for (int i = 0; i < wapNewsList.size(); i++) {
			WapNews tempWapNews = wapNewsList.get(i);
			key = "" + year + month + dayValue + hour + minute + tempWapNews.getId();
			key = CoreStringUtils.md5(key, CharsetConstant.CHARSET_UTF8);
			tempWapNews.setPreviewURL(URL + "aid=" + wapNewsList.get(i).getId()
					+ "&s=" + key);
		}
		return "list";
	}

	public String manage(){
		if(wapNews != null){
			if(wapNews.getTitle() == null || "".equals(wapNews.getTitle())){
				super.setErrorMessage("新闻名称不能为空！");
				return "failure";
			}

			YesNoStatus disableEditor = YesNoStatus.getItem(disableEditorValue);
			logger.info("diableEditorValue = {}, YesNoStatus = {}", disableEditorValue, disableEditor != null ? disableEditor.getValue() : "null");
			wapNews.setDisableEditor(disableEditor);
			HttpServletRequest request = ServletActionContext.getRequest();
			UserSessionBean userSessionBean = (UserSessionBean)request.getSession().getAttribute(Global.USER_SESSION);
			if (wapNews.getId() != null) {
				WapNews tempNews = wapNewsService.get(wapNews.getId());
				wapNews.setCreateTime(tempNews.getCreateTime());
				wapNews.setUserId(tempNews.getUserId());
				wapNews.setLastUpdateUserId(userSessionBean.getUser().getId());
			} else{
				wapNews.setUserId(userSessionBean.getUser().getId());
				wapNews.setLastUpdateUserId(userSessionBean.getUser().getId());
			}
			wapNews.setContentType(ContentType.getItem(contentTypeId));
			if (wapNews.getContentType().getValue() == ContentType.NEWSTYPE.getValue()) {
				wapNews.setContent(UploadUtil.replaceContentToDB(wapNews.getContent()));
			}
			wapNewsService.manage(wapNews);
		} else {
			super.setErrorMessage("添加新闻错误，提交表单为空！");
			return "failure";
		}
		if (fromUrl != null && fromUrl.equals(URL_CONDITION)) {
			super.setForwardUrl("/cms/wapNews.do?action=conditionQuery");
		} else {
			super.setForwardUrl("/cms/wapNews.do?action=list&wapNews.cateID="+wapNews.getCateID());			
		}
		return "success";
	}
	
	public String homeList() {
		HttpServletRequest request = ServletActionContext.getRequest();
		wapNewsList = wapNewsService.listByHome(true ,null, super.getPageBean());
		PageBean pageBean = wapNewsService.getPageBeanByHome(null, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		return "homeList";
	}
	
	public String updateAllOrder() {
		String[] idArray = idStr.split(",");
		String[] orderArray = orderStr.split(",");
		int rc = 0;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (idArray.length == 0 || orderArray.length == 0) {
			logger.info("新闻数据为空");
			rc = 1;
			msg = "新闻数据不能为空";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		if (idArray.length != orderArray.length) {
			logger.info("id个数与排序值个数不一致");
			rc = 1;
			msg = "id个数与排序值个数不一致";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		try{
			for (int i = 0; i < idArray.length; i++) {
				WapNews wapNewsTmp = wapNewsService.get(Long.parseLong(idArray[i])); 
				wapNewsTmp.setOrderView(Integer.parseInt(orderArray[i]));
				wapNewsService.manage(wapNewsTmp);
			}
		} catch (Exception e) {
			logger.info("排序值设置出错");
			rc = 1;
			msg = "排序值设置出错";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		rc = 0;
		msg = "设置排序值成功";
		json.put("code", rc);
		json.put("msg", msg);
		json.put("orderValue", order);
		writeRs(response, json);
		return Action.NONE;
	}
	
	public String setOrder() {
		int rc = 0;
		String msg = "";
		JSONObject json = new JSONObject();
		HttpServletResponse response = ServletActionContext.getResponse();
		if (wapNews == null || wapNews.getId() == null || wapNews.getId() == 0) {
			logger.info("新闻实体或新闻id为空");
			rc = 1;
			msg = "新闻实体或新闻id不能为空";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		if (order == null) {
			logger.info("排序值id为空");
			rc = 1;
			msg = "排序值id不能为空";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		WapNews wapNewsTmp = wapNewsService.get(wapNews.getId());
		wapNewsTmp.setOrderView(order);
		wapNewsService.manage(wapNewsTmp);
		logger.info("设置排序值成功");
		rc = 0;
		msg = "设置排序值成功";
		json.put("code", rc);
		json.put("msg", msg);
		json.put("orderValue", order);
		writeRs(response, json);
		return Action.NONE;
	}
	
	public String audit() {
		WapNews wapNewsTmp = null;
		if(wapNews != null && wapNews.getId() != null){
			wapNewsTmp = wapNewsService.get(wapNews.getId());
			wapNewsTmp.setValid(wapNews.isValid());
			if (!wapNewsTmp.isValid()) {
				wapNewsTmp.setHeadNews(false);
			}
			wapNewsService.manage(wapNewsTmp);
		}else{
			super.setErrorMessage("添加新闻错误，提交表单为空！");
			return "failure";
		}
		if (fromUrl != null && fromUrl.equals(URL_CONDITION)) {
			super.setForwardUrl("/cms/wapNews.do?action=conditionQuery");
		} else {
			super.setForwardUrl("/cms/wapNews.do?action=list&wapNews.cateID="+wapNewsTmp.getCateID());	
		}
		return "success";
	}
	
	public String replaceImgPath() throws Exception {
		logger.info("进入更新全部新闻路径");
		PageBean page = new PageBean();
		page.setPageFlag(false);
		final Map<String, Resource> resourceMap = UploadUtil.resourceMapping(resourceService.list(null, page));
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				PageBean pageBean = new PageBean();
				pageBean.setPageSize(200);
				int page = 1;
				while (true) {
					pageBean.setPage(page);
					WapNews param = new WapNews();
					List<WapNews> list = wapNewsService.list(param, pageBean);
					if (list == null || list.isEmpty()) {
						break;
					}
					for(WapNews n : list){
						if(n.getContentType().getValue() == ContentType.NEWSTYPE.getValue()){
							String newContent = UploadUtil.replaceResourceOldData(resourceMap, n.getContent());
							if (newContent != null && n.getContent() != null && !newContent.equals(n.getContent())) {
								n.setContent(newContent);
								wapNewsService.manage(n);
							}
							
						}
					}
					
					if (list.size() < pageBean.getPageSize()) {
						break;
					}
					// 准备读取下一页
					page ++;
				}
				
			}
			
		});
		thread.start();
		super.setForwardUrl("/cms/wapNews.do");
		logger.info("更新全部新闻结束");
		return "success";
	}
	
	//按条件查询
	public String conditionQuery(){
		logger.info("进入按条件查询新闻");
		List<WapCategory> list = wapCategoryService.list(wapCategory);
		fromUrl = URL_CONDITION;
		wapCategories = new ArrayList<WapCategory>();
		if(list != null && list.size() != 0){
			for(WapCategory sc : list){
				if(sc.getCaLevel() == 1){
					wapCategories.add(sc);				
				}
			}
		}
		if(queryFlag != null && queryFlag.equals("ifConditionQuery")){
			HttpServletRequest request = ServletActionContext.getRequest();
			List<Long> cateIds = null;
			Long cateID = wapNews.getCateID();
			if(cateID != null && cateID != 0){
				cateIds = new ArrayList<Long>();
				WapCategory queryCate = wapCategoryService.get(cateID);
				cateIds.add(queryCate.getId());
				WapCategory parentCategory = new WapCategory();
				parentCategory.setParentID(queryCate.getId());
				List<WapCategory> queryCateChildren = wapCategoryService.list(parentCategory);
				for(WapCategory ca : queryCateChildren){
					cateIds.add(ca.getId());
				}
			}
			
			if(userName != null && !"".equals(userName.trim())){
				User user = permissionService.getByUserName(userName);
				if(user != null){
					wapNews.setUserId(user.getId());
				}else{
					wapNews.setUserId(new Long(0));
				}
			}
			
			if(orderStr == null || orderStr.equals("")){
				orderStr = KEY_ORDER_BY_ID;
			}
			if(orderView == null || orderView.equals("")){
				orderView = ApiConstant.API_REQUEST_ORDER_DESC;
			}
			wapNewsList = wapNewsService.listByCondition(wapNews, cateIds,beginTime,endTime,orderStr,orderView,super.getPageBean());
			if (wapNewsList != null) {
				Map<Long, User> userMap = permissionService.userMapping();
				for (WapNews wn : wapNewsList) {
					String key = this.getPreviewKey(wn.getId());
					wn.setPreviewURL(URL + "aid=" + wn.getId() + "&s=" + key);
					Long userId = wn.getUserId();
					Long lastUpdateUserId = wn.getLastUpdateUserId();
					if (userId != null && userMap.get(userId) != null) {
						wn.setUserName(userMap.get(userId).getName());
					}
					if (lastUpdateUserId != null && userMap.get(lastUpdateUserId) != null){
						wn.setLastUpdateUserName(userMap.get(lastUpdateUserId).getName());
					}
				}
			}
			PageBean pageBean = wapNewsService.getPageBeanByCondition(wapNews, cateIds,beginTime,endTime,orderStr,orderView,super.getPageBean());
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		logger.info("按条件查询新闻结束");
		return "conditionQuery";
	}
	
	public String input(){
		if(wapNews != null){
			if(wapNews.getId() != null){
				wapNews = wapNewsService.get(wapNews.getId());
				contentTypeId = wapNews.getContentType().getValue();
				wapNews.setContentStatic(UploadUtil.replaceContentFromDB(wapNews.getContent()));
			}else{
				wapNews.setValid(true);
				wapNews.setOrderView(0);
			}
			if(wapNews.getCateID() != null){		
				wapCategory = wapCategoryService.get(wapNews.getCateID());
				if(wapCategory != null && wapCategory.getCaLevel() == 2){
					parentWapCategory = wapCategoryService.get(wapCategory.getParentID());
				}
			}
		}

		if (wapNews != null) {
			if (wapNews.getDisableEditor() == null) {
				disableEditorValue = YesNoStatus.NO.getValue();
			}
			else {
				disableEditorValue = wapNews.getDisableEditor().getValue();
			}
		}
		
		return "inputForm";
	}
	public String view(){
		if(wapNews != null && wapNews.getId() != null){
			wapNews = wapNewsService.get(wapNews.getId());
			if(wapNews != null && wapNews.getCateID() != null){		
				wapCategory = wapCategoryService.get(wapNews.getCateID());
				if(wapCategory != null && wapCategory.getCaLevel() == 2){
					parentWapCategory = wapCategoryService.get(wapCategory.getParentID());
				}
			}
			String key = this.getPreviewKey(wapNews.getId());
			wapNews.setPreviewURL(URL + "aid=" + wapNews.getId() + "&s=" + key);
		}else{
			return "failure";
		}
		
		return "view";
	}

	public String del(){
		if(wapNews != null && wapNews.getId() != null){
			wapNews = wapNewsService.get(wapNews.getId());
			wapNewsService.del(wapNews);
		}else{
			return "failure";
		}
		super.setForwardUrl("/cms/wapNews.do?action=list&wapNews.cateID="+wapNews.getCateID());
		return "forward";
	}
	
	public String cancelHead(){
		int rc = 0;
		String message = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (wapNewsId == null && wapNewsId == 0) {
			rc = 1;
			message = "新闻ID不能为空或为0!";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		WapNews wn = wapNewsService.get(wapNewsId);
		if (wn == null || wn.getId() == null || wn.getId() == 0) {
			rc = 1;
			message = "新闻获取错误";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		if(!wn.isHeadNews()){
			rc = 1;
			message = "该新闻已经是非头条，请勿重复设置！";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		wn.setHeadNews(false);
		wapNewsService.manage(wn);
		rc = 0;
		message = "新闻" + wn.getId() + "取消头条成功";
		json.put("code", rc);
		json.put("msg", message);
		writeRs(response, json);
		return Action.NONE;
	}
	
	public String setHead(){
		int rc = 0;
		String message = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (wapNewsId == null && wapNewsId == 0) {
			rc = 1;
			message = "新闻ID不能为空或为0!";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		WapNews wn = wapNewsService.get(wapNewsId);
		if (wn == null || wn.getId() == null || wn.getId() == 0) {
			rc = 1;
			message = "新闻获取错误";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		if(!wn.isValid()) {
			rc = 1;
			message = "新闻未进行审核，不可设置头条";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		if(wn.isHeadNews()){
			rc = 1;
			message = "该新闻已经是头条，请勿重复设置！";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		wn.setHeadNews(true);
		wapNewsService.manage(wn);
		rc = 0;
		message = "新闻" + wn.getId() + "设置头条成功";
		json.put("code", rc);
		json.put("msg", message);
		writeRs(response, json);
		return Action.NONE;
	}

	public String cancelHome(){
		int rc = 0;
		String message = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (wapNewsId == null && wapNewsId == 0) {
			rc = 1;
			message = "新闻ID不能为空或为0!";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		WapNews wn = wapNewsService.get(wapNewsId);
		if (wn == null || wn.getId() == null || wn.getId() == 0) {
			rc = 1;
			message = "新闻获取错误";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		if(!wn.isHomePage()){
			rc = 1;
			message = "该新闻已经是非首页新闻，请勿重复设置！";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		wn.setHomePage(false);
		wapNewsService.manage(wn);
		rc = 0;
		message = "新闻" + wn.getId() + "取消首页成功";
		json.put("code", rc);
		json.put("msg", message);
		writeRs(response, json);
		return Action.NONE;
	}
	
	public String setHome(){
		int rc = 0;
		String message = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (wapNewsId == null && wapNewsId == 0) {
			rc = 1;
			message = "新闻ID不能为空或为0!";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		WapNews wn = wapNewsService.get(wapNewsId);
		if (wn == null || wn.getId() == null || wn.getId() == 0) {
			rc = 1;
			message = "新闻获取错误";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		if(!wn.isValid()) {
			rc = 1;
			message = "新闻未进行审核，不可设置为首页新闻";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		if(wn.isHomePage()){
			rc = 1;
			message = "该新闻已经是首页新闻，请勿重复设置！";
			json.put("code", rc);
			json.put("msg", message);
			writeRs(response, json);
			return Action.NONE;
		}
		wn.setHomePage(true);
		wapNewsService.manage(wn);
		rc = 0;
		message = "新闻" + wn.getId() + "设置为首页新闻成功";
		json.put("code", rc);
		json.put("msg", message);
		writeRs(response, json);
		return Action.NONE;
	}
	
	private String getPreviewKey(Long id){
		String key;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		Integer year = calendar.get(Calendar.YEAR);
		calendar.get(Calendar.MONTH);
		String month = "";
		if (calendar.get(Calendar.MONTH) <= 8) {
			month = "0" + (calendar.get(Calendar.MONTH) + 1);
		} else {
			month = (calendar.get(Calendar.MONTH) + 1) + "";
		}
		Integer day = calendar.get(Calendar.DAY_OF_MONTH);
		Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
		Integer minute = calendar.get(Calendar.MINUTE);
		minute = minute - minute%10;
		key = "" + year + month + day + hour + minute + id;
		key = CoreStringUtils.md5(key, CharsetConstant.CHARSET_UTF8);
		return key;
		
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getContentTypeId() {
		return contentTypeId;
	}

	public void setContentTypeId(Integer contentTypeId) {
		this.contentTypeId = contentTypeId;
	}
	public List<ContentType> getContentTypes(){
		return ContentType.list;
	}
	public ContentType getLinkContentType(){
		return ContentType.LINKTYPE;
	}

	public String getDefaultTemplatePath() {
		return defaultTemplatePath;
	}

	public void setDefaultTemplatePath(String defaultTemplatePath) {
		this.defaultTemplatePath = defaultTemplatePath;
	}

	public String getDefaultTargetPath() {
		return defaultTargetPath;
	}

	public void setDefaultTargetPath(String defaultTargetPath) {
		this.defaultTargetPath = defaultTargetPath;
	}

	public Integer getDefaultStaticPageType() {
		return defaultStaticPageType;
	}

	public void setDefaultStaticPageType(Integer defaultStaticPageType) {
		this.defaultStaticPageType = defaultStaticPageType;
	}
	
	public List<YesNoStatus> getYesNoStatusList() {
		return YesNoStatus.getItems();
	}

	public Integer getDisableEditorValue() {
		return disableEditorValue;
	}

	public void setDisableEditorValue(Integer disableEditorValue) {
		this.disableEditorValue = disableEditorValue;
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
	
	public ResourceService getResourceService() {
		return resourceService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setWapCategoryService(WapCategoryService wapCategoryService) {
		this.wapCategoryService = wapCategoryService;
	}

	public WapNews getWapNews() {
		return wapNews;
	}

	public void setWapNews(WapNews wapNews) {
		this.wapNews = wapNews;
	}

	public WapCategory getWapCategory() {
		return wapCategory;
	}

	public void setWapCategory(WapCategory wapCategory) {
		this.wapCategory = wapCategory;
	}

	public WapCategory getParentWapCategory() {
		return parentWapCategory;
	}

	public void setParentWapCategory(WapCategory parentWapCategory) {
		this.parentWapCategory = parentWapCategory;
	}

	public List<WapNews> getWapNewsList() {
		return wapNewsList;
	}

	public void setWapNewsList(List<WapNews> wapNewsList) {
		this.wapNewsList = wapNewsList;
	}

	public List<WapCategory> getWapCategories() {
		return wapCategories;
	}

	public void setWapCategories(List<WapCategory> wapCategories) {
		this.wapCategories = wapCategories;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public Long getWapNewsId() {
		return wapNewsId;
	}

	public void setWapNewsId(Long wapNewsId) {
		this.wapNewsId = wapNewsId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getOrderStr() {
		return orderStr;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getVariable() {
		variable = UploadUtil.IMG_STATIC;
		return variable;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		domain = UploadUtil.IMG_URL;
		return domain;
	}
	
	public String getQueryFlag() {
		return queryFlag;
	}

	public void setQueryFlag(String queryFlag) {
		this.queryFlag = queryFlag;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}

	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(KEY_ORDER_BY_ID, "文章编号");
		orderStrMap.put(KEY_ORDER_BY_CREATETIME, "发布时间");
		orderStrMap.put(KEY_ORDER_BY_UPDATETIME, "修改时间");
		return orderStrMap;
	}

	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}

	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		return orderViewMap;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getOrderView() {
		return orderView;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}

	public String getFromUrl() {
		return fromUrl;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}
}
