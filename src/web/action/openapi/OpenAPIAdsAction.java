package web.action.openapi;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.openapi.OpenAPIAdsService;
import com.lehecai.admin.web.service.openapi.OpenAPIAppService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.openapi.*;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.opensymphony.xwork2.Action;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OpenAPIAdsAction extends BaseAction {

    private static final long serialVersionUID = 6603047041310675683L;
    private Logger logger = LoggerFactory.getLogger(OpenAPIAdsAction.class);

    private Integer statusValue;
    private OpenAPIAds openAPIAds;
    private OpenAPIAdsService openAPIAdsService;
    private OpenAPIAppService openAPIAppService;
    private List<OpenAPIAds> openAPIAdsList;

    private Date addTimeFrom;
    private Date addTimeEnd;

    private Integer openAPIAppTypeValue;
    private Integer openAPIAdsTypeValue;

    private List<Long> sources;
    private List<String> versions;

    private Integer appTypeValue;
    
    private String appVersionInput;
	private String sourceInput;
    /**
     * 查询openAPI广告
     * @return
     */
    public String handle() {
        return "list";
    }

    /**
     * 查询openAPI广告
     * @return
     */
    @SuppressWarnings("unchecked")
    public String query() {
        logger.info("进入查询OpenAPI广告列表");

        if (openAPIAds == null) {
            openAPIAds = new OpenAPIAds();
        } else {
            openAPIAds.setStatus(YesNoStatus.getItem(statusValue));
        }
        if(openAPIAppTypeValue != null) {
            openAPIAds.setAppType(OpenAPIAppType.getItem(openAPIAppTypeValue));
        }
        if(openAPIAdsTypeValue != null) {
            openAPIAds.setType(OpenAPIAdsType.getItem(openAPIAdsTypeValue));
        }
        Map<String, Object> map = null;

        try {
            map = openAPIAdsService.findOpenAPIAdsList(openAPIAds.getAdsId(), openAPIAds.getAdsTitle(), openAPIAds.getStatus(), openAPIAds.getAppType(), openAPIAds.getType(),addTimeFrom, addTimeEnd, super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("API查询openAPI应用异常，" , e.getMessage());
            super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
            return "failure";
        }
        if (map != null) {
            openAPIAdsList = (List<OpenAPIAds>)map.get(Global.API_MAP_KEY_LIST);
            PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
            super.setPageString(PageUtil.getPageString(ServletActionContext.getRequest(), pageBean));
            super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
        }
        logger.info("查询OpenAPI广告列表结束");
        return "list";
    }

    /**
     * 查询openAPI广告详情
     * @return
     */
    @SuppressWarnings("unchecked")
    public String view() {
        logger.info("进入查询OpenAPI广告详情");

        if (openAPIAds == null || openAPIAds.getAdsId() == 0L) {
            logger.error("openAPI广告编码为空");
            super.setErrorMessage("openAPI广告编码不能为空");
            return "failure";
        }
        Map<String, Object> map = null;

        try {
            map = openAPIAdsService.findOpenAPIAdsList(openAPIAds.getAdsId(), null, null, null, null, null, null, null);
        } catch (ApiRemoteCallFailedException e) {
            logger.error("API查询openAPI广告异常，" , e.getMessage());
            super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
            return "failure";
        }
        if (map != null) {
            openAPIAdsList = (List<OpenAPIAds>)map.get(Global.API_MAP_KEY_LIST);
            
            if (openAPIAdsList != null && openAPIAdsList.size() > 0) {
                openAPIAds = openAPIAdsList.get(0);
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
        logger.info("进入输入OpenAPI广告信息");
        logger.info("进入查询OpenAPI应用列表");
        
        OpenAPIApp openAPIApp = new OpenAPIApp();
        openAPIApp.setStatus(OpenAPIAppStatus.ALL);
        List<OpenAPIApp> openAPIAppList = null;

        if (appTypeValue == null) {
            if (openAPIAds != null && openAPIAds.getAdsId() != 0L) {
                Map<String, Object> map = null;

                try {
                    map = openAPIAdsService.findOpenAPIAdsList(openAPIAds.getAdsId(), null, null, null, null, null, null, null);
                } catch (ApiRemoteCallFailedException e) {
                    logger.error("API查询openAPI广告异常，" , e.getMessage());
                    super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
                    return "failure";
                }
                if (map != null) {
                    openAPIAdsList = (List<OpenAPIAds>)map.get(Global.API_MAP_KEY_LIST);
                    
                    if (openAPIAdsList != null && openAPIAdsList.size() > 0) {
                        openAPIAds = openAPIAdsList.get(0);
                        
                        try {
                            openAPIAppList = openAPIAppService.findAllOpenAPIAppList(null, openAPIApp.getAppOwner(), openAPIApp.getSource(), openAPIApp.getStatus(), openAPIApp.getLevel(), openAPIApp.getAppKey(), openAPIApp.getAppId(), openAPIAds.getAppType(), null, null);
                        } catch (ApiRemoteCallFailedException e) {
                            logger.error("API查询openAPI应用异常，" , e.getMessage());
                            super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
                            return "failure";
                        }
                        versions = new ArrayList<String>();
                        sources = new ArrayList<Long>();
                        
                        if (openAPIAppList != null && openAPIAppList.size() > 0) {
                        	

                            for(OpenAPIApp tmpOpenAPIApp : openAPIAppList) {
                            	if (tmpOpenAPIApp.getStatus().getValue() == OpenAPIAppStatus.ENABLED.getValue()) {
                            		versions.add(tmpOpenAPIApp.getAppVersion());
                                    sources.add(tmpOpenAPIApp.getSource());
                            	}       	
                            }
                            versions = new ArrayList(new LinkedHashSet(versions));
                            sources = new ArrayList(new LinkedHashSet(sources));
                        }
                        String appVersionRead = openAPIAds.getAppVersion();
                        
                        if (!StringUtils.isEmpty(appVersionRead)) {
                        	StringBuilder appVersion = new StringBuilder("");
                            String[] appVersions = appVersionRead.split(",");
                            
                            for (String appVersionStr : appVersions) {
                        		if (!versions.contains(appVersionStr.trim())) {
                            		appVersion.append(appVersionStr + ";");
                            	}                    	
                            }
                            appVersionInput = appVersion.toString();
                        }
                        String sourceRead = openAPIAds.getSource();
                        
                        if (!StringUtils.isEmpty(sourceRead)) {
                        	StringBuilder source = new StringBuilder("");
                            String[] excludeSources = sourceRead.split(",");
                            
                            for (String sourceStr : excludeSources) {
                            	try { 		
                            		if (!sources.contains(Long.parseLong(sourceStr.trim()))) {
                                		source.append(sourceStr + ";");
                                	}
                            	} catch (NumberFormatException e) {
                            		logger.error("排除的渠道号" + sourceStr.trim() + "格式错误！");
                            		super.setErrorMessage("排除的渠道号" + sourceStr.trim() + "格式错误！");
                            		return "failure";
                            	}
                            }
                            sourceInput = source.toString();
                        }     
                    }
                }
            } else {
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
            }
            logger.info("输入OpenAPI广告信息结束");
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
            logger.info("输入OpenAPI广告信息结束");
            return Action.NONE;
        }
    }

    /**
     * 添加修改
     * @return
     */
    public String manage () {
        logger.info("进入添加修改OpenAPI广告信息");

        if (openAPIAds == null) {
            logger.error("openAPI应用为空");
            super.setErrorMessage("openAPI广告不能为空");
            return "failure";
        }
        if (openAPIAds.getAdsTitle() == null || openAPIAds.getAdsTitle().equals("")) {
            logger.error("openAPI广告名称为空");
            super.setErrorMessage("openAPI广告名称不能为空");
            return "failure";
        }
        if (openAPIAds.getAdsDesc() == null || openAPIAds.getAdsDesc().equals("")) {
            logger.error("openAPI广告描述为空");
            super.setErrorMessage("openAPI广告描述不能为空");
            return "failure";
        }
        if (openAPIAds.getAdsContent() == null || openAPIAds.getAdsContent().equals("")) {
            logger.error("openAPI广告内容为空");
            super.setErrorMessage("openAPI广告内容不能为空");
            return "failure";
        }
        if (statusValue == null) {
            openAPIAds.setStatus(YesNoStatus.NO);
        } else {
            openAPIAds.setStatus(YesNoStatus.getItem(statusValue));
        }
        OpenAPIAppType oaat = openAPIAppTypeValue == null ? null : OpenAPIAppType.getItem(openAPIAppTypeValue);
        openAPIAds.setAppType(oaat);

        OpenAPIAdsType type = openAPIAdsTypeValue == null ? null : OpenAPIAdsType.getItem(openAPIAdsTypeValue);
        openAPIAds.setType(type);
        
        //拼接版本号和选择的渠道号     
        String appVersion = openAPIAds.getAppVersion();
        
        if (!StringUtils.isEmpty(appVersion)) {
        	StringBuilder appVersionSb = new StringBuilder(appVersion.replace(" ", ""));		
        	List<String> appVersionList = new ArrayList<String>();
    		String[] appVersions = appVersion.split(",");
    		
        	if (!StringUtils.isEmpty(appVersionInput)) {
        		for (String appVersionStr : appVersions) {
        			appVersionList.add(appVersionStr);
        		}
            	String[] appVersionsInput = appVersionInput.split(";");
            	
            	for (String appVersionInputStr : appVersionsInput) {
            		if (!appVersionList.contains(appVersionInputStr)) {
            			appVersionSb.append("," + appVersionInputStr.replace(" ", ""));
            		}
            	}
        	}
        	openAPIAds.setAppVersion(appVersionSb.toString().trim());
        } else if (!StringUtils.isEmpty(appVersionInput)) {
        	StringBuilder appVersionSb = new StringBuilder();
        	String[] appVersionsInput = appVersionInput.split(";");
        	
        	for (String appVersionInputStr : appVersionsInput) {
        		appVersionSb.append("," + appVersionInputStr);
    		}
        	openAPIAds.setAppVersion(appVersionSb.toString().trim().substring(1));
        }        
        String source = openAPIAds.getSource();
        
        if (!StringUtils.isEmpty(source)) {
        	StringBuilder sourceSb = new StringBuilder(source.replace(" ", ""));		
        	List<String> sourceList = new ArrayList<String>();
    		String[] sources = source.split(",");
    		
        	if (!StringUtils.isEmpty(sourceInput)) {
        		for (String sourceStr : sources) {
        			sourceList.add(sourceStr);
        		}
            	String[] sourcesInput = sourceInput.split(";");
            	
            	for (String sourceInputStr : sourcesInput) {
            		if (!sourceList.contains(sourceInputStr)) {
            			sourceSb.append("," + sourceInputStr.replace(" ", ""));
            		}
            	}
        	}
        	openAPIAds.setSource(sourceSb.toString().trim());
        } else if (!StringUtils.isEmpty(sourceInput)) {
        	StringBuilder sourceSb = new StringBuilder();
        	String[] sourcesInput = sourceInput.split(";");
        	
        	for (String sourceInputStr : sourcesInput) {
        		sourceSb.append("," + sourceInputStr);
    		}
        	openAPIAds.setSource(sourceSb.toString().trim().substring(1));
        }
        boolean result = false;

        if (openAPIAds.getAdsId() != null && openAPIAds.getAdsId() != 0L) {//编辑
            try {
                result = openAPIAdsService.updateOpenAPIAds(openAPIAds);
            } catch (ApiRemoteCallFailedException e) {
                logger.error("openAPI编辑应用异常，{}", e.getMessage());
                super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
                return "failure";
            }
        } else {//添加
            try {
                result = openAPIAdsService.addOpenAPIAds(openAPIAds);
            } catch (ApiRemoteCallFailedException e) {
                logger.error("openAPI添加应用异常，{}", e.getMessage());
                super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
                return "failure";
            }
        }
        logger.info("添加修改OpenAPI应用信息结束");
        super.setForwardUrl("/openapi/openAPIAds.do");

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

    public List<OpenAPIAppType> getOpenAPIAppTypes() {
        return OpenAPIAppType.getItems();
    }

    public List<YesNoStatus> getStatuses() {
        return YesNoStatus.getItems();
    }

    public Integer getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(Integer statusValue) {
        this.statusValue = statusValue;
    }

    public OpenAPIAds getOpenAPIAds() {
        return openAPIAds;
    }

    public void setOpenAPIAds(OpenAPIAds openAPIAds) {
        this.openAPIAds = openAPIAds;
    }

    public OpenAPIAdsService getOpenAPIAdsService() {
        return openAPIAdsService;
    }

    public void setOpenAPIAdsService(OpenAPIAdsService openAPIAdsService) {
        this.openAPIAdsService = openAPIAdsService;
    }

    public List<OpenAPIAds> getOpenAPIAdsList() {
        return openAPIAdsList;
    }

    public void setOpenAPIAdsList(List<OpenAPIAds> openAPIAdsList) {
        this.openAPIAdsList = openAPIAdsList;
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

    public List<OpenAPIAdsType> getOpenAPIAdsTypes(){
        return OpenAPIAdsType.getItems();
    }

    public Integer getOpenAPIAdsTypeValue() {
        return openAPIAdsTypeValue;
    }

    public void setOpenAPIAdsTypeValue(Integer openAPIAdsTypeValue) {
        this.openAPIAdsTypeValue = openAPIAdsTypeValue;
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

    public OpenAPIAppService getOpenAPIAppService() {
        return openAPIAppService;
    }

    public void setOpenAPIAppService(OpenAPIAppService openAPIAppService) {
        this.openAPIAppService = openAPIAppService;
    }

    public int getAppTypeValue() {
        return appTypeValue;
    }

    public void setAppTypeValue(int appTypeValue) {
        this.appTypeValue = appTypeValue;
    }
    
    public String getAppVersionInput() {
		return appVersionInput;
	}

	public void setAppVersionInput(String appVersionInput) {
		this.appVersionInput = appVersionInput;
	}
    
    public String getSourceInput() {
		return sourceInput;
	}

	public void setSourceInput(String sourceInput) {
		this.sourceInput = sourceInput;
	}
}