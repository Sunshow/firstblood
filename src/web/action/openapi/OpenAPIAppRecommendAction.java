package web.action.openapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIAppRecommendService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.openapi.OpenAPIAppRecommend;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendFreeType;
import com.lehecai.core.api.openapi.OpenAPIAppRecommendType;
import com.lehecai.core.api.openapi.OpenAPIAppUpdatePolicyStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 2013-05-09
 * @author He Wang
 *
 */
public class OpenAPIAppRecommendAction extends BaseAction {
	private static final long serialVersionUID = 4403047039820675618L;
	private Logger logger = LoggerFactory.getLogger(OpenAPIAppRecommendAction.class);

	private OpenAPIAppRecommendService openAPIAppRecommendService;
	
	private OpenAPIAppRecommend openAPIAppRecommend;
	
	private List<OpenAPIAppRecommend> openAPIAppRecommendList;
	
	private List<YesNoStatus> appIsOpenList;
	private List<OpenAPIAppRecommendFreeType> appIsFreeList;
	private List<YesNoStatus> appStatusList;
	private List<OpenAPIAppRecommendType> appRecommendTypeList;
	
	private Integer appRecommendTypeValue;
	private Integer appStatusValue;
	private Integer appIsOpenValue;
	private Integer appIsFreeValue;
	private Long appId;
	private Integer auditFlag;
	
	private String orderStr = OpenAPIAppRecommend.ORDER_APP_ID;
	private String orderView = ApiConstant.API_REQUEST_ORDER_DESC;
	
	/**
	 * 查询openAPI推荐应用
	 * @return
	 */
	public String handle() {
		logger.info("进入查询OpenAPI应用推荐列表");
		appIsOpenList = YesNoStatus.getItemsForQuery();
		appStatusList = YesNoStatus.getItemsForQuery();
		appRecommendTypeList = OpenAPIAppRecommendType.getItemsForQuery();
		return "list";
	}
	
	/**
	 * 查询openAPI推荐应用
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询OpenAPI应用推荐列表");
		appIsOpenList = YesNoStatus.getItemsForQuery();
		appStatusList = YesNoStatus.getItemsForQuery();
		appRecommendTypeList = OpenAPIAppRecommendType.getItemsForQuery();
		if (openAPIAppRecommend == null) {
			openAPIAppRecommend = new OpenAPIAppRecommend();
		}
		openAPIAppRecommend.setAppType(OpenAPIAppRecommendType.getItem(appRecommendTypeValue));
		openAPIAppRecommend.setAppStatus(YesNoStatus.getItem(appStatusValue));
		openAPIAppRecommend.setAppIsOpen(YesNoStatus.getItem(appIsOpenValue));
		Map<String, Object> map = null;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(20);
			map = openAPIAppRecommendService.findOpenAPIAppRecommendList(openAPIAppRecommend.getAppStatus(), openAPIAppRecommend.getAppIsOpen(), appId, openAPIAppRecommend.getAppType(),orderStr, orderView, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询openAPI推荐应用异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			openAPIAppRecommendList = (List<OpenAPIAppRecommend>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询OpenAPI应用推荐列表结束");
		
		return "list";
	}
	
	/**
	 * 查询openAPI详情
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String view() {
		logger.info("进入查询OpenAPI应用推荐详情");
		if (openAPIAppRecommend == null || openAPIAppRecommend.getAppId() == 0L) {
			logger.error("openAPI推荐应用编码为空");
			super.setErrorMessage("openAPI推荐应用编码不能为空");
			return "failure";
		}
		Map<String, Object> map = null;
		try {
			map = openAPIAppRecommendService.findOpenAPIAppRecommendList(openAPIAppRecommend.getAppStatus(), openAPIAppRecommend.getAppIsOpen(), openAPIAppRecommend.getAppId(), openAPIAppRecommend.getAppType(), null, null, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询openAPI推荐应用异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			openAPIAppRecommendList = (List<OpenAPIAppRecommend>)map.get(Global.API_MAP_KEY_LIST);
			if (openAPIAppRecommendList != null && openAPIAppRecommendList.size() > 0) {
				openAPIAppRecommend = openAPIAppRecommendList.get(0);
			}
		}
		logger.info("查询OpenAPI应用推荐详情结束");
		return "view";
	}
	
	/**
	 * 输入
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String input() {
		logger.info("进入输入OpenAPI应用推荐信息");
		
		appIsOpenList = YesNoStatus.getItems();
		appStatusValue = YesNoStatus.NO.getValue();
		if (openAPIAppRecommend != null && openAPIAppRecommend.getAppId() != 0L) {
			Map<String, Object> map = null;
			try {
				map = openAPIAppRecommendService.findOpenAPIAppRecommendList(null, null, openAPIAppRecommend.getAppId(), null, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询openAPI推荐应用异常，" , e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
			if (map != null) {
				openAPIAppRecommendList = (List<OpenAPIAppRecommend>)map.get(Global.API_MAP_KEY_LIST);
				if (openAPIAppRecommendList != null && openAPIAppRecommendList.size() > 0) {
					openAPIAppRecommend = openAPIAppRecommendList.get(0);
					appStatusValue = openAPIAppRecommend.getAppStatus().getValue();
					appIsFreeValue = openAPIAppRecommend.getAppIsFree().getValue();
					appRecommendTypeValue = openAPIAppRecommend.getAppType().getValue();
				}
			}
		} else {
			
			appIsOpenValue = YesNoStatus.NO.getValue();
			appIsFreeValue = OpenAPIAppRecommendFreeType.FREE.getValue();
		}
		if (appRecommendTypeValue == null || appRecommendTypeValue <=0 || OpenAPIAppRecommendType.getItem(appRecommendTypeValue) == null) {
			logger.error("添加推荐app类型异常，推荐类型为空");
			super.setErrorMessage("添加推荐app类型异常，推荐类型为空");
			return "failure";
		}
		setAppIsFreeList(OpenAPIAppRecommendFreeType.getItems());
		appRecommendTypeList = new ArrayList<OpenAPIAppRecommendType>();
		appRecommendTypeList.add(OpenAPIAppRecommendType.getItem(appRecommendTypeValue));
		
		if (appRecommendTypeValue == OpenAPIAppRecommendType.OPENAPI_APP_TYPE_RECOMMEND_ANDROID.getValue()) {
			appIsFreeList = new ArrayList<OpenAPIAppRecommendFreeType>();
			appIsFreeList.add(OpenAPIAppRecommendFreeType.FREE);
		} else {
			appIsFreeList = OpenAPIAppRecommendFreeType.getItems();
		}
		logger.info("输入OpenAPI应用推荐信息结束");
		return "inputForm";
	}
	
	/**
	 * 添加修改
	 * @return
	 */
	public String manage () {
		logger.info("进入添加修改OpenAPI应用推荐信息");
		if (openAPIAppRecommend == null) {
			logger.error("openAPI推荐应用为空");
			super.setErrorMessage("openAPI推荐应用不能为空");
			return "failure";
		}
		if (openAPIAppRecommend.getAppName() == null || openAPIAppRecommend.getAppName().equals("")) {
			logger.error("openAPI推荐应用名称为空");
			super.setErrorMessage("openAPI推荐应用名称不能为空");
			return "failure";
		}
		if (openAPIAppRecommend.getAppVersion() == null || openAPIAppRecommend.getAppVersion().equals("")) {
			logger.error("openAPI推荐应用版本为空");
			super.setErrorMessage("openAPI推荐应用版本不能为空");
			return "failure";
		}
		if (StringUtils.isEmpty(openAPIAppRecommend.getAppDownUrl())) {
			logger.error("openAPI推荐应用下载地址为空");
			super.setErrorMessage("openAPI推荐应用下载地址不能为空");
			return "failure";
		}
		if (StringUtils.isEmpty(openAPIAppRecommend.getAppIcon())) {
			logger.error("openAPI推荐应用图标为空");
			super.setErrorMessage("openAPI推荐应用图标不能为空");
			return "failure";
		}
		if (appIsFreeValue == null || appIsFreeValue < 0) {
			logger.error("openAPI推荐收费情况为空");
			super.setErrorMessage("openAPI推荐收费情况不能为空");
			return "failure";
		}

		openAPIAppRecommend.setAppType(OpenAPIAppRecommendType.getItem(appRecommendTypeValue));
		openAPIAppRecommend.setAppStatus(YesNoStatus.getItem(appStatusValue));
		openAPIAppRecommend.setAppIsFree(OpenAPIAppRecommendFreeType.getItem(appIsFreeValue));
		openAPIAppRecommend.setAppIsOpen(YesNoStatus.getItem(appIsOpenValue));
		
		boolean result = false;
		if (openAPIAppRecommend.getAppId() != 0L) {	//编辑
			try {
				result = openAPIAppRecommendService.updateOpenAPIAppRecommend(openAPIAppRecommend);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("openAPI编辑应用异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		} else {						//添加
			try {
				result = openAPIAppRecommendService.addOpenAPIAppRecommend(openAPIAppRecommend);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("openAPI添加应用异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		}
		logger.info("添加修改OpenAPI应用推荐信息结束");
		super.setForwardUrl("/openapi/openAPIAppRecommend.do");
		
		if (result) {
			logger.info("操作成功");
			super.setErrorMessage("操作成功");
			return "success";
		} else {
			logger.error("操作失败");
			super.setErrorMessage("操作失败");
			return "failure";
		}
	}
	
	/**
	 * 审核
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String auditInput() {
		logger.info("进入查询OpenAPI应用推荐详情");
		if (openAPIAppRecommend == null || openAPIAppRecommend.getAppId() == 0L) {
			logger.error("openAPI推荐应用编码为空");
			super.setErrorMessage("openAPI推荐应用编码不能为空");
			return "failure";
		}
		Map<String, Object> map = null;
		try {
			map = openAPIAppRecommendService.findOpenAPIAppRecommendList(null, null, openAPIAppRecommend.getAppId(), null, null, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询openAPI推荐应用异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			openAPIAppRecommendList = (List<OpenAPIAppRecommend>)map.get(Global.API_MAP_KEY_LIST);
			if (openAPIAppRecommendList != null && openAPIAppRecommendList.size() > 0) {
				openAPIAppRecommend = openAPIAppRecommendList.get(0);
				appStatusValue = openAPIAppRecommend.getAppStatus().getValue();
			}
		}
		auditFlag = 1;
		logger.info("查询OpenAPI应用推荐详情结束");
		return "view";
	}
	
	/**
	 * 添加修改
	 * @return
	 */
	public String audit () {
		logger.info("进入添加修改OpenAPI应用推荐信息");
		if (openAPIAppRecommend == null) {
			logger.error("openAPI推荐应用为空");
			super.setErrorMessage("openAPI推荐应用不能为空");
			return "failure";
		}

		if (appStatusValue == null || appStatusValue < 0) {
			logger.error("openAPI推荐审核状态为空");
			super.setErrorMessage("openAPI推荐审核状态为空");
			return "failure";
		}

		openAPIAppRecommend.setAppStatus(YesNoStatus.getItem(appStatusValue));
		
		boolean result = false;
		if (openAPIAppRecommend.getAppId() != 0L) {
			try {
				result = openAPIAppRecommendService.auditOpenAPIAppRecommend(openAPIAppRecommend);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("openAPI编辑应用异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		}
		logger.info("添加修改OpenAPI应用推荐信息结束");
		super.setForwardUrl("/openapi/openAPIAppRecommend.do");
		
		if (result) {
			logger.info("操作成功");
			super.setErrorMessage("操作成功");
			return "success";
		} else {
			logger.error("操作失败");
			super.setErrorMessage("操作失败");
			return "failure";
		}
	}
	
	public Map<String, String> getOrderStrMap() {
		Map<String, String> orderStrMap = new LinkedHashMap<String, String>();
		orderStrMap.put(OpenAPIAppRecommend.ORDER_APP_ID, "应用编号");
		orderStrMap.put(OpenAPIAppRecommend.ORDER_TIME_ADD, "新增时间");
		orderStrMap.put(OpenAPIAppRecommend.ORDER_TIME_UPDATE, "更新时间");
		orderStrMap.put(OpenAPIAppRecommend.SET_APP_SORT, "排序");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		Map<String, String> orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	
	public List<YesNoStatus> getOpenAPIAppStatusList() {
		return YesNoStatus.getItemsForQuery();
	}
	
	public Integer getNoStatusValue() {
		return YesNoStatus.NO.getValue();
	}

	public List<OpenAPIAppUpdatePolicyStatus> getOpenAPIAppUpdatePolicyStatuses() {
		return OpenAPIAppUpdatePolicyStatus.getItems();
	}

	public List<OpenAPIAppRecommendType> getOpenAPIAppRecommendTypes() {
		return OpenAPIAppRecommendType.getItems();
	}

	public void setOpenAPIAppRecommendList(List<OpenAPIAppRecommend> openAPIAppRecommendList) {
		this.openAPIAppRecommendList = openAPIAppRecommendList;
	}

	public List<OpenAPIAppRecommend> getOpenAPIAppRecommendList() {
		return openAPIAppRecommendList;
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

	public void setAppStatusList(List<YesNoStatus> appStatusList) {
		this.appStatusList = appStatusList;
	}

	public List<YesNoStatus> getAppStatusList() {
		return appStatusList;
	}

	public void setAppRecommendTypeList(List<OpenAPIAppRecommendType> appRecommendTypeList) {
		this.appRecommendTypeList = appRecommendTypeList;
	}

	public List<OpenAPIAppRecommendType> getAppRecommendTypeList() {
		return appRecommendTypeList;
	}
	
	public OpenAPIAppRecommendType getAppRecommendTypeIos() {
		return OpenAPIAppRecommendType.OPENAPI_APP_TYPE_RECOMMEND_IOS;
	}
	
	public OpenAPIAppRecommendType getAppRecommendTypeAndroid() {
		return OpenAPIAppRecommendType.OPENAPI_APP_TYPE_RECOMMEND_ANDROID;
	}

	public void setAppIsOpenList(List<YesNoStatus> appIsOpenList) {
		this.appIsOpenList = appIsOpenList;
	}

	public List<YesNoStatus> getAppIsOpenList() {
		return appIsOpenList;
	}

	public void setAppRecommendTypeValue(Integer appRecommendTypeValue) {
		this.appRecommendTypeValue = appRecommendTypeValue;
	}

	public Integer getAppRecommendTypeValue() {
		return appRecommendTypeValue;
	}

	public void setAppStatusValue(Integer appStatusValue) {
		this.appStatusValue = appStatusValue;
	}

	public Integer getAppStatusValue() {
		return appStatusValue;
	}

	public void setAppIsOpenValue(Integer appIsOpenValue) {
		this.appIsOpenValue = appIsOpenValue;
	}

	public Integer getAppIsOpenValue() {
		return appIsOpenValue;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long getAppId() {
		return appId;
	}


	public void setAppIsFreeValue(Integer appIsFreeValue) {
		this.appIsFreeValue = appIsFreeValue;
	}

	public Integer getAppIsFreeValue() {
		return appIsFreeValue;
	}

	public void setAppIsFreeList(List<OpenAPIAppRecommendFreeType> appIsFreeList) {
		this.appIsFreeList = appIsFreeList;
	}

	public List<OpenAPIAppRecommendFreeType> getAppIsFreeList() {
		return appIsFreeList;
	}

	public void setAuditFlag(Integer auditFlag) {
		this.auditFlag = auditFlag;
	}

	public Integer getAuditFlag() {
		return auditFlag;
	}

	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	public String getOrderStr() {
		return orderStr;
	}

	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}

	public String getOrderView() {
		return orderView;
	}

}
