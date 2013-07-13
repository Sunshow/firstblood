package web.action.openapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.openapi.OpenAPIAppService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.openapi.OpenAPIApp;
import com.lehecai.core.api.openapi.OpenAPIAppStatus;
import com.lehecai.core.api.openapi.OpenAPIAppType;
import com.lehecai.core.api.openapi.OpenAPIAppUpdatePolicyStatus;
import com.lehecai.core.api.openapi.OpenAPIAppVersionLog;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class OpenAPIAppAction extends BaseAction {
	private static final long serialVersionUID = 6603047041310675683L;
	private Logger logger = LoggerFactory.getLogger(OpenAPIAppAction.class);

	private OpenAPIAppService openAPIAppService;
	private MemberService memberService;
	
	private OpenAPIApp openAPIApp;
	
	private List<OpenAPIApp> openAPIAppList;
	private List<OpenAPIAppVersionLog> openAPIAppVersionLogList;
	
	private String oldAppName;
	private String appOwnerName;
	private int statusValue;
	private Integer updatePolicyStatusValue;
	private String orderStr = OpenAPIApp.ORDER_LEVEL;
	private String orderView = ApiConstant.API_REQUEST_ORDER_DESC;
	private Integer newsIsWapValue;
	private Integer openAPIAppTypeValue;
	
	/**
	 * 查询openAPI应用
	 * @return
	 */
	public String handle() {
		logger.info("进入查询OpenAPI应用列表");
		return "list";
	}
	
	/**
	 * 查询openAPI应用
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询OpenAPI应用列表");
		
		if (openAPIApp == null) {
			openAPIApp = new OpenAPIApp();
			openAPIApp.setStatus(OpenAPIAppStatus.ALL);			//设置状态
		} else {
			openAPIApp.setStatus(OpenAPIAppStatus.getItem(statusValue));
		}
		
		if (appOwnerName != null && !appOwnerName.equals("")) {
			long appOwnerId = 0L;
			try {
				appOwnerId = memberService.getIdByUserName(appOwnerName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询会员编码异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员! " + e.getMessage());
				return "failure";
			}
			openAPIApp.setAppOwner(appOwnerId);
			if (appOwnerId == 0L) {
				super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), super.getPageBean()));
				super.setSimplePageString(PageUtil.getSimplePageString(super.getPageBean()));
				return "list";
			}
		}
		
		OpenAPIAppType appType = openAPIAppTypeValue == null || openAPIAppTypeValue == OpenAPIAppType.ALL.getValue() ? null : OpenAPIAppType.getItem(openAPIAppTypeValue);
		
		Map<String, Object> map = null;
		try {
			map = openAPIAppService.findOpenAPIAppList(openAPIApp.getAppName(), openAPIApp.getAppOwner(), openAPIApp.getSource(), openAPIApp.getStatus(), 
					openAPIApp.getLevel(), openAPIApp.getAppKey(), openAPIApp.getAppId(), appType, orderStr, orderView, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询openAPI应用异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			openAPIAppList = (List<OpenAPIApp>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		logger.info("查询OpenAPI应用列表结束");
		
		return "list";
	}
	
	/**
	 * 查询openAPI详情
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String view() {
		logger.info("进入查询OpenAPI应用详情");
		if (openAPIApp == null || openAPIApp.getAppId() == 0L) {
			logger.error("openAPI应用编码为空");
			super.setErrorMessage("openAPI应用编码不能为空");
			return "failure";
		}
		
		Map<String, Object> map = null;
		try {
			map = openAPIAppService.findOpenAPIAppList(null, 0L, 0L, null, 0L, 0L, openAPIApp.getAppId(), null,
					null, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询openAPI应用异常，" , e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			openAPIAppList = (List<OpenAPIApp>)map.get(Global.API_MAP_KEY_LIST);
			if (openAPIAppList != null && openAPIAppList.size() > 0) {
				openAPIApp = openAPIAppList.get(0);
				if (openAPIApp != null && openAPIApp.getAppOwner() != 0L) {
					try {
						appOwnerName = memberService.getUserNameById(openAPIApp.getAppOwner());
					} catch (ApiRemoteCallFailedException e) {
						logger.error("API查询会员编码异常，{}", e.getMessage());
						super.setErrorMessage("API调用异常，请联系技术人员! " + e.getMessage());
						return "failure";
					}
				}
			}
		}
		
		logger.info("查询OpenAPI应用详情结束");
		
		return "view";
	}
	
	/**
	 * 输入
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String input() {
		logger.info("进入输入OpenAPI应用信息");
		if (openAPIApp != null && openAPIApp.getAppId() != 0L) {
			Map<String, Object> map = null;
			try {
				map = openAPIAppService.findOpenAPIAppList(null, 0L, 0L, null, 0L, 0L, openAPIApp.getAppId(), null,
						null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询openAPI应用异常，" , e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
			
			if (map != null) {
				openAPIAppList = (List<OpenAPIApp>)map.get(Global.API_MAP_KEY_LIST);
				if (openAPIAppList != null && openAPIAppList.size() > 0) {
					openAPIApp = openAPIAppList.get(0);
					if (openAPIApp != null && openAPIApp.getAppOwner() != 0L) {
						try {
							appOwnerName = memberService.getUserNameById(openAPIApp.getAppOwner());
						} catch (ApiRemoteCallFailedException e) {
							logger.error("API查询会员编码异常，{}", e.getMessage());
							super.setErrorMessage("API调用异常，请联系技术人员! " + e.getMessage());
							return "failure";
						}
					}
				}
			}
		}
		
		logger.info("输入OpenAPI应用信息结束");
		
		return "inputForm";
	}
	
	@SuppressWarnings("unchecked")
	public String versionLogList() {
		logger.info("查询OpenAPIApp版本列表开始");
		if (openAPIApp == null || openAPIApp.getAppId() == 0) {
			logger.error("查询版本列表,openAPIApp ID为空");
			super.setErrorMessage("查询版本列表,openAPIApp ID为空");
			return "failure";
		}
		Map<String, Object> map = null;
		try {
			map = openAPIAppService.findOpenAPIAppVersionList(openAPIApp.getAppId(), super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询版本列表,API调用失败" + e.getMessage());
			super.setErrorMessage("查询版本列表,API调用失败" + e.getMessage());
			return "failure";
		}
		
		if (map != null) {
			openAPIAppVersionLogList = (List<OpenAPIAppVersionLog>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("查询OpenAPIApp版本列表结束");
		
		return "versionLogList";
	}
	
	/**
	 * 添加修改
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String manage () {
		logger.info("进入添加修改OpenAPI应用信息");
		if (openAPIApp == null) {
			logger.error("openAPI应用为空");
			super.setErrorMessage("openAPI应用不能为空");
			return "failure";
		}
		if (openAPIApp.getAppName() == null || openAPIApp.getAppName().equals("")) {
			logger.error("openAPI应用名称为空");
			super.setErrorMessage("openAPI应用名称不能为空");
			return "failure";
		}
		if (openAPIApp.getAppVersion() == null || openAPIApp.getAppVersion().equals("")) {
			logger.error("openAPI应用版本为空");
			super.setErrorMessage("openAPI应用版本不能为空");
			return "failure";
		}
		if ((openAPIApp.getAppOwner() == 0L) 
				&& (appOwnerName == null || appOwnerName.equals(""))) {
			logger.error("openAPI应用拥有者为空");
			super.setErrorMessage("openAPI应用拥有者不能为空");
			return "failure";
		}
		if (openAPIApp.getSource() == 0L) {
			logger.error("openAPI应用来源为空");
			super.setErrorMessage("openAPI应用来源不能为空");
			return "failure";
		}
		if (statusValue == OpenAPIAppStatus.ALL.getValue()) {
			logger.error("openAPI应用状态为空");
			super.setErrorMessage("openAPI应用状态不能为空");
			return "failure";
		}
		if (openAPIApp.getLevel() == 0L) {
			logger.error("openAPI应用等级为空");
			super.setErrorMessage("openAPI应用等级不能为空");
			return "failure";
		}
		
		OpenAPIAppUpdatePolicyStatus ups = updatePolicyStatusValue == null || updatePolicyStatusValue == 0 ? null : OpenAPIAppUpdatePolicyStatus.getItem(updatePolicyStatusValue);
		
		if (ups != null) {
			openAPIApp.setUpdatePolicy(ups);
		}
		
		Member tempMember = null;
		if (appOwnerName != null && !appOwnerName.equals("")) {		//以拥有者账户名为主
			try {
				tempMember = memberService.get(appOwnerName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询会员信息异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		} else {												//以拥有者编号为辅
			try {
				tempMember = memberService.get(openAPIApp.getAppOwner());
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询会员信息异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		}
		if (tempMember == null) {
			logger.error("会员不存在");
			super.setErrorMessage("会员不存在");
			return "failure";
		}
		
		if (!oldAppName.equals(openAPIApp.getAppName())) {
			Map<String, Object> map = null;
			try {
				map = openAPIAppService.findOpenAPIAppList(openAPIApp.getAppName(), 0L, 0L, null, 0L, 0L, 0L, null, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询openAPI应用异常，" , e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
			
			if (map != null) {
				openAPIAppList = (List<OpenAPIApp>)map.get(Global.API_MAP_KEY_LIST);
				if (openAPIAppList != null && openAPIAppList.size() > 0) {
					logger.error("{}名称的OpenAPI应用名称已经存在", openAPIApp.getAppName());
					super.setErrorMessage(openAPIApp.getAppName() + "名称的OpenAPI应用名称已经存在");
					return "failure";
				}
			}
		}
		YesNoStatus yn = newsIsWapValue == null ? null : YesNoStatus.getItem(newsIsWapValue);
		OpenAPIAppType appType = openAPIAppTypeValue == null || openAPIAppTypeValue == OpenAPIAppType.ALL.getValue() ? null : OpenAPIAppType.getItem(openAPIAppTypeValue);
		
		openAPIApp.setStatus(OpenAPIAppStatus.getItem(statusValue));			//设置状态
		openAPIApp.setAppOwner(tempMember.getUid());							//设置拥有者
		openAPIApp.setNewsIsWap(yn);
		openAPIApp.setOpenAPIAppType(appType);
		
		boolean result = false;
		if (openAPIApp.getAppId() != 0L) {	//编辑
			try {
				result = openAPIAppService.updateOpenAPIApp(openAPIApp);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("openAPI编辑应用异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		} else {						//添加
			try {
				result = openAPIAppService.addOpenAPIApp(openAPIApp);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("openAPI添加应用异常，{}", e.getMessage());
				super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
				return "failure";
			}
		}
		
		logger.info("添加修改OpenAPI应用信息结束");
		
		super.setForwardUrl("/openapi/openAPIApp.do");
		
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
	
	public List<OpenAPIAppStatus> getOpenAPIAppStatusList() {
		return OpenAPIAppStatus.getItems();
	}
	public Map<String, String> getOrderStrMap() {
		Map<String, String> orderStrMap = new HashMap<String, String>();
		orderStrMap.put(OpenAPIApp.ORDER_APP_ID, "应用编号");
		orderStrMap.put(OpenAPIApp.ORDER_LEVEL, "应用级别");
		orderStrMap.put(OpenAPIApp.ORDER_TIME_ADD, "创建时间");
		orderStrMap.put(OpenAPIApp.ORDER_TIME_UPDATE, "修改时间");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		Map<String, String> orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public OpenAPIAppService getOpenAPIAppService() {
		return openAPIAppService;
	}
	public void setOpenAPIAppService(OpenAPIAppService openAPIAppService) {
		this.openAPIAppService = openAPIAppService;
	}
	public MemberService getMemberService() {
		return memberService;
	}
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	public OpenAPIApp getOpenAPIApp() {
		return openAPIApp;
	}
	public void setOpenAPIApp(OpenAPIApp openAPIApp) {
		this.openAPIApp = openAPIApp;
	}
	public List<OpenAPIApp> getOpenAPIAppList() {
		return openAPIAppList;
	}
	public void setOpenAPIAppList(List<OpenAPIApp> openAPIAppList) {
		this.openAPIAppList = openAPIAppList;
	}
	public String getOldAppName() {
		return oldAppName;
	}
	public void setOldAppName(String oldAppName) {
		this.oldAppName = oldAppName;
	}
	public String getAppOwnerName() {
		return appOwnerName;
	}
	public void setAppOwnerName(String appOwnerName) {
		this.appOwnerName = appOwnerName;
	}
	public int getStatusValue() {
		return statusValue;
	}
	public void setStatusValue(int statusValue) {
		this.statusValue = statusValue;
	}
	public String getOrderStr() {
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}

	public Integer getNewsIsWapValue() {
		return newsIsWapValue;
	}

	public void setNewsIsWapValue(Integer newsIsWapValue) {
		this.newsIsWapValue = newsIsWapValue;
	}
	
	public List<YesNoStatus> getYesNoStatuses() {
		return YesNoStatus.getItems();
	}

	public List<OpenAPIAppVersionLog> getOpenAPIAppVersionLogList() {
		return openAPIAppVersionLogList;
	}

	public void setOpenAPIAppVersionLogList(
			List<OpenAPIAppVersionLog> openAPIAppVersionLogList) {
		this.openAPIAppVersionLogList = openAPIAppVersionLogList;
	}
	
	public List<OpenAPIAppUpdatePolicyStatus> getOpenAPIAppUpdatePolicyStatuses() {
		return OpenAPIAppUpdatePolicyStatus.getItems();
	}

	public Integer getUpdatePolicyStatusValue() {
		return updatePolicyStatusValue;
	}

	public void setUpdatePolicyStatusValue(Integer updatePolicyStatusValue) {
		this.updatePolicyStatusValue = updatePolicyStatusValue;
	}
	
	public List<OpenAPIAppType> getOpenAPIAppTypes() {
		return OpenAPIAppType.getItems();
	}
	
	public Integer getOpenAPIAppTypeValue() {
		return openAPIAppTypeValue;
	}

	public void setOpenAPIAppTypeValue(Integer openAPIAppTypeValue) {
		this.openAPIAppTypeValue = openAPIAppTypeValue;
	}

}
