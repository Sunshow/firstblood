package web.action.openapi;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIAppRecommendService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.openapi.OpenAPIAppRecommend;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendDownload;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendFreeType;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendType;
import com.lehecai.core.api.openapi.OpenAPIAppUpdatePolicyStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public class OpenAPIAppRecommendDownloadAction extends BaseAction {
	
	private static final long serialVersionUID = 713059039820225613L;
	private static final String SIGN_ALL = "1";
	private static final String SIGN_SINGLE = "2";
	private Logger logger = LoggerFactory.getLogger(OpenAPIAppRecommendDownloadAction.class);

	private OpenAPIAppRecommendService openAPIAppRecommendService;
	
	private OpenAPIAppRecommend openAPIAppRecommend;
	
	private List<OpenAPIAppRecommendDownload> openAPIAppRecommendDownloadList;
	
	
	private Date beginDate;
	private Date endDate;
	private Long appId;
	private String appName;
	
	/**
	 * 查询openAPI推荐应用
	 * @return
	 */
	public String handle() {
		beginDate = getDefaultQueryBeginDate();
		endDate = getDefaultQueryBeginDate(-1);
		logger.info("进入查询OpenAPI应用推荐下载统计列表");
		return "list";
	}
	
	/**
	 * 查询openAPI推荐应用
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询OpenAPI应用推荐下载统计列表");

		if (beginDate == null) {
			logger.error("API查询openAPI推荐应用下载明细异常，原因：开始时间为空");
			super.setErrorMessage("API查询openAPI推荐应用下载明细异常，原因：开始时间为空");
			return "failure";
		}
		if (endDate == null) {
			logger.error("API查询openAPI推荐应用下载明细异常，原因：结束时间为空");
			super.setErrorMessage("API查询openAPI推荐应用下载明细异常，原因：结束时间为空");
			return "failure";
		}
		Map<String, Object> map = null;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(20);
			map = openAPIAppRecommendService.findOpenAPIAppRecommendDownloadList(SIGN_ALL, null, beginDate, endDate, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询openAPI推荐应用异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			openAPIAppRecommendDownloadList = (List<OpenAPIAppRecommendDownload>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询OpenAPI应用推荐下载统计列表结束");
		
		return "list";
	}
	
	/**
	 * 查询openAPI详情
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String queryDetail() {
		logger.info("进入查询OpenAPI应用推荐下载统计列表");
		if (appId == null) {
			logger.error("API查询openAPI推荐应用下载明细异常，原因：id为空");
			super.setErrorMessage("API查询openAPI推荐应用下载明细异常，原因：id为空");
			return "failure";
		}
		if (beginDate == null) {
			logger.error("API查询openAPI推荐应用下载明细异常，原因：开始时间为空");
			super.setErrorMessage("API查询openAPI推荐应用下载明细异常，原因：开始时间为空");
			return "failure";
		}
		if (endDate == null) {
			logger.error("API查询openAPI推荐应用下载明细异常，原因：结束时间为空");
			super.setErrorMessage("API查询openAPI推荐应用下载明细异常，原因：结束时间为空");
			return "failure";
		}
		Map<String, Object> map = null;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(20);
			map = openAPIAppRecommendService.findOpenAPIAppRecommendDownloadList(SIGN_SINGLE, appId, beginDate, endDate, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询openAPI推荐应用异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			openAPIAppRecommendDownloadList = (List<OpenAPIAppRecommendDownload>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询OpenAPI应用推荐下载统计列表结束");
		
		return "detail";
	}
	
	public List<YesNoStatus> getOpenAPIAppStatusList() {
		return YesNoStatus.getItemsForQuery();
	}
	
	public Integer getNoStatusValue() {
		return OpenAPIAppRecommendFreeType.FREE.getValue();
	}

	public List<OpenAPIAppUpdatePolicyStatus> getOpenAPIAppUpdatePolicyStatuses() {
		return OpenAPIAppUpdatePolicyStatus.getItems();
	}

	public List<OpenAPIAppRecommendType> getOpenAPIAppRecommendTypes() {
		return OpenAPIAppRecommendType.getItems();
	}

	public void setOpenAPIAppRecommendService(OpenAPIAppRecommendService openAPIAppRecommendService) {
		this.openAPIAppRecommendService = openAPIAppRecommendService;
	}

	public OpenAPIAppRecommendService getOpenAPIAppRecommendService() {
		return openAPIAppRecommendService;
	}

	public void setOpenAPIAppRecommend(OpenAPIAppRecommend openAPIAppRecommend) {
		this.openAPIAppRecommend = openAPIAppRecommend;
	}

	public OpenAPIAppRecommend getOpenAPIAppRecommend() {
		return openAPIAppRecommend;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long getAppId() {
		return appId;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setOpenAPIAppRecommendDownloadList(
			List<OpenAPIAppRecommendDownload> openAPIAppRecommendDownloadList) {
		this.openAPIAppRecommendDownloadList = openAPIAppRecommendDownloadList;
	}

	public List<OpenAPIAppRecommendDownload> getOpenAPIAppRecommendDownloadList() {
		return openAPIAppRecommendDownloadList;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return appName;
	}

}
