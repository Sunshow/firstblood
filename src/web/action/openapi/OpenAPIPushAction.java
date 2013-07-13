package web.action.openapi;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIAppService;
import com.lehecai.admin.web.service.openapi.OpenAPIPushService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.openapi.*;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.opensymphony.xwork2.Action;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OpenAPIPushAction extends BaseAction {

	private static final long serialVersionUID = -378824301869436167L;
	private Logger logger = LoggerFactory.getLogger(OpenAPIPushAction.class);

    private int statusValue = YesNoStatus.ALL.getValue();

    private OpenAPIPush openAPIPush;
    private OpenAPIPushService openAPIPushService;
    private OpenAPIAppService openAPIAppService;

    private List<OpenAPIPush> openAPIPushList;
    private List<Long> sources;
	private List<String> versions;

    private Date addTimeFrom;
    private Date addTimeEnd;

    private Integer openAPIAppTypeValue;

    private String orderStr;
    private String orderView;

    private Map<String, String> orderStrMap;
    private Map<String, String> orderViewMap;
    
    private Integer appTypeValue;
    
	/**
     * 查询openAPIPush信息
     * @return
     */
    public String handle() {
        return "list";
    }

    /**
     * 查询openAPIPush信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public String query() {
        logger.info("进入查询OpenAPIPush信息列表");

        if (openAPIPush == null) {
            openAPIPush = new OpenAPIPush();
        } else {
            openAPIPush.setStatus(YesNoStatus.getItem(statusValue));
        }
        if(openAPIAppTypeValue != null) {
            openAPIPush.setAppType(OpenAPIAppType.getItem(openAPIAppTypeValue));
        }
        Map<String, Object> map = null;
        
        try {
            map = openAPIPushService.findOpenAPIPushList(openAPIPush.getId(), openAPIPush.getTitle(), openAPIPush.getStatus(), openAPIPush.getAppType(), addTimeFrom, addTimeEnd, orderStr, orderView, super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("API查询openAPI应用异常，" , e.getMessage());
            super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
            return "failure";
        }
        if (map != null) {
            openAPIPushList = (List<OpenAPIPush>)map.get(Global.API_MAP_KEY_LIST);
            PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
            super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
            super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
        }
        logger.info("查询OpenAPIPush信息列表结束");
        return "list";
    }

    /**
     * 查询openAPIPush信息详情
     * @return
     */
    @SuppressWarnings("unchecked")
    public String view() {
        logger.info("进入查询OpenAPIPush信息详情");
        
        if (openAPIPush == null || StringUtils.isEmpty(openAPIPush.getId())) {
            logger.error("openAPIPush信息编码为空");
            super.setErrorMessage("openAPIPush信息编码不能为空");
            return "failure";
        }
        Map<String, Object> map = null;

        try {
            map = openAPIPushService.findOpenAPIPushList(openAPIPush.getId(), null, null, null, null, null, null, null, null);
        } catch (ApiRemoteCallFailedException e) {
            logger.error("API查询openAPIPush信息异常，" , e.getMessage());
            super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
            return "failure";
        }
        if (map != null) {
            openAPIPushList = (List<OpenAPIPush>)map.get(Global.API_MAP_KEY_LIST);
            if (openAPIPushList != null && openAPIPushList.size() > 0) {
                openAPIPush = openAPIPushList.get(0);
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
        logger.info("进入输入OpenAPIPush信息");
        logger.info("进入查询OpenAPI应用列表");
        
        OpenAPIApp openAPIApp = new OpenAPIApp();
        openAPIApp.setStatus(OpenAPIAppStatus.ALL);
        List<OpenAPIApp> openAPIAppList = null;
        
        if (appTypeValue == null) {
	        try {
	        	openAPIAppList = openAPIAppService.findAllOpenAPIAppList(openAPIApp.getAppName(), openAPIApp.getAppOwner(), openAPIApp.getSource(), openAPIApp.getStatus(), openAPIApp.getLevel(), openAPIApp.getAppKey(), openAPIApp.getAppId(), null, null, null);
	        } catch (ApiRemoteCallFailedException e) {
	            logger.error("API查询openAPI应用异常，" , e.getMessage());
	            super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
	            return "failure";
	        }
	        if (openAPIAppList != null && openAPIAppList.size() > 0) {
            	versions = new ArrayList<String>();
                sources = new ArrayList<Long>();
                
                for(OpenAPIApp tmpOpenAPIApp : openAPIAppList) {
                	if (tmpOpenAPIApp.getStatus().getValue() == OpenAPIAppStatus.ENABLED.getValue()) {
                		versions.add(tmpOpenAPIApp.getAppVersion());
                        sources.add(tmpOpenAPIApp.getSource());
                	}
                }
                versions = new ArrayList(new LinkedHashSet(versions));
                sources = new ArrayList(new LinkedHashSet(sources));
            }	        
	        if (openAPIPush != null && !StringUtils.isEmpty(openAPIPush.getId())) {
	            Map<String, Object> map = null;
	
	            try {
	                map = openAPIPushService.findOpenAPIPushList(openAPIPush.getId(), null, null, null, null, null, null, null, null);
	            } catch (ApiRemoteCallFailedException e) {
	                logger.error("API查询openAPIPush异常，" , e.getMessage());
	                super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
	                return "failure";
	            }
	            if (map != null) {
	                openAPIPushList = (List<OpenAPIPush>)map.get(Global.API_MAP_KEY_LIST);
	                if (openAPIPushList != null && openAPIPushList.size() > 0) {
	                    openAPIPush = openAPIPushList.get(0);
	                }
	            }
	        }
	        logger.info("输入OpenAPIPush信息结束");
	        return "inputForm";
        } else {
        	JSONObject rs = new JSONObject();
            OpenAPIAppType appType = OpenAPIAppType.getItem(appTypeValue);

            try {
                openAPIAppList = openAPIAppService.findAllOpenAPIAppList(openAPIApp.getAppName(), openAPIApp.getAppOwner(), openAPIApp.getSource(), openAPIApp.getStatus(), openAPIApp.getLevel(), openAPIApp.getAppKey(), openAPIApp.getAppId(), appType, null, null);
            } catch (ApiRemoteCallFailedException e) {
                logger.error("API查询openAPI应用异常，", e.getMessage());
                rs.put(false, "API查询openAPI应用异常，" + e.getMessage());
            }
            if (openAPIAppList != null && openAPIAppList.size() > 0) {
            	versions = new ArrayList<String>();
                sources = new ArrayList<Long>();

                for(OpenAPIApp tmpOpenAPIApp : openAPIAppList) {
                	if (tmpOpenAPIApp.getStatus().getValue() == OpenAPIAppStatus.ENABLED.getValue()) {
                		versions.add(tmpOpenAPIApp.getAppVersion());
                        sources.add(tmpOpenAPIApp.getSource());
                	}                	
                }
                versions = new ArrayList(new LinkedHashSet(versions));
                sources = new ArrayList(new LinkedHashSet(sources));
                rs.put("versions", versions);
                rs.put("sources", sources);
            }
            writeRs(ServletActionContext.getResponse(), rs);
            logger.info("输入OpenAPIPush信息结束");
            return Action.NONE;
        }
    }

    /**
     * 添加修改
     * @return
     */
    public String manage () {
        logger.info("进入添加修改OpenAPIPush信息");
        
        if (openAPIPush == null) {
            logger.error("openAPI应用为空");
            super.setErrorMessage("openAPIPush信息不能为空");
            return "failure";
        }
        if (StringUtils.isEmpty(openAPIPush.getTitle())) {
            logger.error("openAPIPush标题为空");
            super.setErrorMessage("openAPIPush标题不能为空");
            return "failure";
        }
        if (StringUtils.isEmpty(openAPIPush.getContent())) {
            logger.error("openAPIPush内容为空");
            super.setErrorMessage("openAPIPush内容不能为空");
            return "failure";
        }
        if (statusValue == 0) {
            openAPIPush.setStatus(YesNoStatus.NO);
        } else {
            openAPIPush.setStatus(YesNoStatus.getItem(statusValue));
        }
        OpenAPIAppType oaat = openAPIAppTypeValue == null ? null : OpenAPIAppType.getItem(openAPIAppTypeValue);
        openAPIPush.setAppType(oaat);
        boolean result = false;

        if (!StringUtils.isEmpty(openAPIPush.getId())) {//编辑
            try {
                result = openAPIPushService.updateOpenAPIPush(openAPIPush);
            } catch (ApiRemoteCallFailedException e) {
                logger.error("openAPI编辑应用异常，{}", e.getMessage());
                super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
                return "failure";
            }
        } else {//添加
            try {
                result = openAPIPushService.addOpenAPIPush(openAPIPush);
            } catch (ApiRemoteCallFailedException e) {
                logger.error("openAPI添加应用异常，{}", e.getMessage());
                super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
                return "failure";
            }
        }
        logger.info("添加修改OpenAPIPush信息结束");
        super.setForwardUrl("/openapi/openAPIPush.do");

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

    public List<YesNoStatus> getStatusList() {
        return YesNoStatus.getItems();
    }

    public List<OpenAPIAppType> getOpenAPIPushTypeList() {
        List<OpenAPIAppType> openAPIPushTypeList = new ArrayList<OpenAPIAppType>();
        openAPIPushTypeList.add(OpenAPIAppType.ALL);
        openAPIPushTypeList.add(OpenAPIAppType.OPENAPI_APP_TYPE_STD_IOS);				//官方iOS标准版
        openAPIPushTypeList.add(OpenAPIAppType.OPENAPI_APP_TYPE_STD_ANDROID);			//官方Android标准版
        openAPIPushTypeList.add(OpenAPIAppType.OPENAPI_SPORTS_APP_TYPE_STD_IOS);		//球迷版官方iOS标准版
        openAPIPushTypeList.add(OpenAPIAppType.OPENAPI_SPORTS_APP_TYPE_STD_ANDROID);	//球迷版官方Android标准版
        return openAPIPushTypeList;
    }

    public Integer getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(Integer statusValue) {
        this.statusValue = statusValue;
    }

    public OpenAPIPush getOpenAPIPush() {
        return openAPIPush;
    }

    public void setOpenAPIPush(OpenAPIPush openAPIPush) {
        this.openAPIPush = openAPIPush;
    }

    public OpenAPIPushService getOpenAPIPushService() {
        return openAPIPushService;
    }

    public void setOpenAPIPushService(OpenAPIPushService openAPIPushService) {
        this.openAPIPushService = openAPIPushService;
    }

    public OpenAPIAppService getOpenAPIAppService() {
        return openAPIAppService;
    }

    public void setOpenAPIAppService(OpenAPIAppService openAPIAppService) {
        this.openAPIAppService = openAPIAppService;
    }

    public List<OpenAPIPush> getOpenAPIPushList() {
        return openAPIPushList;
    }

    public void setOpenAPIPushList(List<OpenAPIPush> openAPIPushList) {
        this.openAPIPushList = openAPIPushList;
    }

    public List<Long> getSources() {
        return sources;
    }

    public void setSources(List<Long> sources) {
        this.sources = sources;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public Date getAddTimeFrom() {
        return addTimeFrom;
    }

    public void setAddTimeFrom(Date addTimeFrom) {
        this.addTimeFrom = addTimeFrom;
    }

    public Date getAddTimeEnd() {
        return addTimeEnd;
    }

    public void setAddTimeEnd(Date addTimeEnd) {
        this.addTimeEnd = addTimeEnd;
    }

    public Integer getOpenAPIAppTypeValue() {
        return openAPIAppTypeValue;
    }

    public void setOpenAPIAppTypeValue(Integer openAPIAppTypeValue) {
        this.openAPIAppTypeValue = openAPIAppTypeValue;
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

    public Map<String, String> getOrderStrMap() {
        orderStrMap = new HashMap<String, String>();
        orderStrMap.put(OpenAPIPush.ORDER_PUSH_ID, "编号");
        orderStrMap.put(OpenAPIPush.QUERY_PUSH_TIME_ADD, "添加时间");
        return orderStrMap;
    }

    public void setOrderStrMap(Map<String, String> orderStrMap) {
        this.orderStrMap = orderStrMap;
    }

    public Map<String, String> getOrderViewMap() {
        orderViewMap = new HashMap<String, String>();
        orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
        orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
        return orderViewMap;
    }

    public void setOrderViewMap(Map<String, String> orderViewMap) {
        this.orderViewMap = orderViewMap;
    }
    
    public Integer getAppTypeValue() {
		return appTypeValue;
	}

	public void setAppTypeValue(Integer appTypeValue) {
		this.appTypeValue = appTypeValue;
	}
}