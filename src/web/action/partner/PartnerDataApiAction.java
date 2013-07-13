package web.action.partner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.partner.PartnerDataApiService;
import com.lehecai.admin.web.service.user.SourceService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.partner.PartnerDataApi;
import com.lehecai.core.api.user.Source;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PhaseType;
import com.opensymphony.xwork2.Action;

/**
 * 
 * @author He Wang
 *
 */
public class PartnerDataApiAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(PartnerDataApiAction.class);
	
	private PartnerDataApiService partnerDataApiService;
	private SourceService sourceService;
	
	private PartnerDataApi partnerDataApi;
	private List<PartnerDataApi> partnerDataApiList;
	private Long agentId;
	
	private Integer categoryValue;
	private Integer showResultValue;
	private Integer statusValue;
	private static final String AND = "AND";
	
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入查询合作商数据项列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> map;
		try {
			if (partnerDataApi == null) {
				partnerDataApi = new PartnerDataApi();
			}
			partnerDataApi.setContentFlag(false);
			map = partnerDataApiService.getPartnerDataApiResult(partnerDataApi, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			partnerDataApiList = (List<PartnerDataApi>)map.get(Global.API_MAP_KEY_LIST);
			if (partnerDataApiList != null) {
				for (PartnerDataApi item : partnerDataApiList) {
					if (item != null && item.getResultFormat() != null) {
						item.setResultFormat(item.getResultFormat().replace(AND, "/"));
					}
				}
			}
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		logger.info("查询合作商数据项列表结束");
		return "list";
	}

	@SuppressWarnings("unchecked")
	public String input() {
		logger.info("进入输入合作商数据项信息");
		if (partnerDataApi != null && partnerDataApi.getDataApiId() != null) {
			Map<String, Object> map;
			try {
				map = partnerDataApiService.getPartnerDataApiResult(partnerDataApi, super.getPageBean());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			if (map != null) {
				partnerDataApiList = (List<PartnerDataApi>)map.get(Global.API_MAP_KEY_LIST);
				partnerDataApi = partnerDataApiList != null ? partnerDataApiList.get(0) : null;
				if (partnerDataApi != null && partnerDataApi.getResultFormat() != null) {
					String[] resultArray = partnerDataApi.getResultFormat().split(AND);
					if (resultArray.length == 2) {
						partnerDataApi.setResultFormatStart(resultArray[0]);
						partnerDataApi.setResultFormatEnd(resultArray[1]);
					}
				}
			}
		} else {
			partnerDataApi = new PartnerDataApi();
			partnerDataApi.setCategory(YesNoStatus.NO);
			partnerDataApi.setPhase(1);
			partnerDataApi.setFrequency(900);
		}
		return "inputForm";
	}

	public String manage() {
		logger.info("进入更新合作商数据项信息");
		boolean b = false;
		if (partnerDataApi == null) {
			super.setErrorMessage("数据项为空");
			return "failure";
		}
		try {
			partnerDataApi.setCategory(YesNoStatus.getItem(categoryValue));
			partnerDataApi.setShowResult(YesNoStatus.getItem(showResultValue));
			if (partnerDataApi.getResultFormatStart() == null && partnerDataApi.getResultFormatStart() == null) {
				super.setErrorMessage("结果格式空");
				return "failure";
			}
			//拼接结果格式
			partnerDataApi.setResultFormat(partnerDataApi.getResultFormatStart()  + AND  + partnerDataApi.getResultFormatEnd() );
			if (partnerDataApi.getDataApiId() != null) {
				b = partnerDataApiService.updatePartnerDataApi(partnerDataApi);
			} else {
				if (checkSourceById(partnerDataApi.getAgentId())) {
					b = partnerDataApiService.createPartnerDataApi(partnerDataApi);	
				} else {
					super.setErrorMessage("渠道来源id不存在");
					return "failure";
				}
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (b) {
			super.setSuccessMessage("合作商数据项信息更新成功");
			logger.info("合作商数据项信息更新成功");
		} else {
			super.setErrorMessage("合作商数据项信息更新失败");
			logger.error("合作商数据项信息更新失败");
		}
		
		partnerDataApi.clear();
		logger.info("更新合作商数据项信息结束");
		super.setForwardUrl("/agent/partnerDataApi.do");
		return "success";
	}
	
	public String checkSource() {
		boolean flag = checkSourceById(agentId);
		JSONObject rs = new JSONObject();
		rs.put("flag", flag);
		writeRs(ServletActionContext.getResponse(), rs);
		return Action.NONE;
	}
	
	/**
	 * 判断根据id是否能够获取到来源
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean checkSourceById(Long agentId) {
		logger.info("进入检查来源ID是否存在");
		boolean flag = false;
		Map<String, Object> map;
		try {
			map = sourceService.getResult(agentId, null, null, null, null);
			if(map != null){
				List<Source> sources = (List<Source>)map.get(Global.API_MAP_KEY_LIST);
				Source source = sources != null ? sources.get(0) : null;
				if (source != null) {
					flag = true;
				}
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
		}
		logger.info("检查来源ID是否存在结束");
		return flag;
	}

	@SuppressWarnings("unchecked")
	public String inputTemplate() {
		logger.info("进入输入合作商数据项模板信息");
		if (partnerDataApi != null && partnerDataApi.getDataApiId() != null) {
			Map<String, Object> map;
			try {
				partnerDataApi.setContentFlag(true);
				map = partnerDataApiService.getPartnerDataApiResult(partnerDataApi, super.getPageBean());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
			if(map != null){
				partnerDataApiList = (List<PartnerDataApi>)map.get(Global.API_MAP_KEY_LIST);
				partnerDataApi = partnerDataApiList != null ? partnerDataApiList.get(0) : null;
			}
		}
		return "inputTemplate";
	}
	
	public String manageTemplate() {
		logger.info("进入更新合作商数据项模板信息");
		boolean b = false;
		try {
			if (partnerDataApi != null && partnerDataApi.getDataApiId() != null) {
				b = partnerDataApiService.updatePartnerDataApiTemplate(partnerDataApi);
			} else {
				logger.error("数据项ID为空");
				super.setErrorMessage("数据项ID为空");
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (b) {
			super.setSuccessMessage("合作商数据项模板信息更新成功");
			logger.info("合作商数据项信息更新成功");
		} else {
			super.setErrorMessage("合作商数据项模板信息更新失败");
			logger.error("合作商数据项模板信息更新失败");
		}
		
		partnerDataApi.clear();
		logger.info("更新合作商数据项模板信息结束");
		super.setForwardUrl("/agent/partnerDataApi.do");
		return "success";
	}
	
	/**
	 * 保存数据项状态启用/停用
	 * @return
	 */
	public String manageDataApiStatus() {
		logger.info("进入更新合作商数据项状态信息");
		boolean b = false;
		try {
			if (partnerDataApi != null && partnerDataApi.getDataApiId() != null) {
				if (statusValue != null) {
					if (statusValue == YesNoStatus.YES.getValue()) {
						partnerDataApi.setStatus(YesNoStatus.NO);
					} else {
						partnerDataApi.setStatus(YesNoStatus.YES);
					}
					b = partnerDataApiService.updatePartnerDataApiStatus(partnerDataApi);
				} else {
					logger.error("数据项状态为空");
					super.setErrorMessage("数据项状态为空");
					return "failure";
				}
			} else {
				logger.error("数据项ID为空");
				super.setErrorMessage("数据项ID为空");
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}

		if (b) {
			super.setSuccessMessage("合作商数据项状态更新成功");
			logger.info("合作商数据项状态更新成功");
		} else {
			super.setErrorMessage("合作商数据项模板状态更新失败");
			logger.error("合作商数据项模板状态更新失败");
		}
		partnerDataApi.clear();
		logger.info("更新合作商数据项状态信息结束");
		return handle();
	}
	
	/**
	 * 手动更新
	 * @return
	 */
	public String updateManual() {
		logger.info("进入合作商数据项手动更新");
		boolean b = false;
		try {
			if (partnerDataApi != null && partnerDataApi.getDataApiId() != null) {
				b = partnerDataApiService.updateManual(partnerDataApi);
			} else {
				logger.error("数据项ID为空");
				super.setErrorMessage("数据项ID为空");
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}

		if (b) {
			super.setSuccessMessage("合作商数据项手动更新成功");
			logger.info("合作商数据项手动更新成功");
		} else {
			super.setErrorMessage("合作商数据项手动更新失败");
			logger.error("合作商数据项手动更新失败");
		}
		partnerDataApi.clear();
		logger.info("合作商数据项手动更新结束");
		return handle();
	}
	

	/**
	 * 预览
	 * @return
	 */
	public String preview() {
		logger.info("进入更新合作商数据项模板预览");
		boolean b = false;
		JSONObject rs = new JSONObject();
		String message = "";
		
		try {
			UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
			User user = userSessionBean.getUser();
			if (partnerDataApi != null && partnerDataApi.getDataApiId() != null) {
				partnerDataApi = partnerDataApiService.preview(partnerDataApi, user.getId());
				if (partnerDataApi.getPreviewUrl() != null && !partnerDataApi.getPreviewUrl().equals("")) {
					b = true;
					rs.put("previewUrl", partnerDataApi.getPreviewUrl());
				} else {
					message = "API调用异常，未能获取到预览路径";
				}
			} else {
				logger.error("数据项ID为空");
				message = "数据项ID为空";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			message = "api调用异常，原因:" + e.getMessage();
		}
		partnerDataApi.clear();
		rs.put("flag", b);
		rs.put("message", message);
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("更新合作商数据项模板预览结束");
		return Action.NONE;
	}
	
	public PartnerDataApi getPartnerDataApi() {
		return partnerDataApi;
	}

	public void setPartnerDataApi(PartnerDataApi partnerDataApi) {
		this.partnerDataApi = partnerDataApi;
	}
	
	public List<YesNoStatus> getShowResultList() {
		return YesNoStatus.getItems();
	}
	
	public List<YesNoStatus> getCategoryList() {
		return YesNoStatus.getItems();
	}
	
	public Map<String, String> getCharsetTypeMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("UTF8", "UTF8");
		map.put("GBK", "GBK");
		return map;
	}
	
	public List<PhaseType> getLotteryTypeList() {
		return PhaseType.getItems();
	}

	public void setSourceService(SourceService sourceService) {
		this.sourceService = sourceService;
	}

	public SourceService getSourceService() {
		return sourceService;
	}

	public void setShowResultValue(Integer showResultValue) {
		this.showResultValue = showResultValue;
	}

	public Integer getShowResultValue() {
		return showResultValue;
	}

	public void setPartnerDataApiList(List<PartnerDataApi> partnerDataApiList) {
		this.partnerDataApiList = partnerDataApiList;
	}

	public List<PartnerDataApi> getPartnerDataApiList() {
		return partnerDataApiList;
	}

	public void setStatusValue(Integer statusValue) {
		this.statusValue = statusValue;
	}

	public Integer getStatusValue() {
		return statusValue;
	}

	public void setPartnerDataApiService(PartnerDataApiService partnerDataApiService) {
		this.partnerDataApiService = partnerDataApiService;
	}

	public PartnerDataApiService getPartnerDataApiService() {
		return partnerDataApiService;
	}

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public void setCategoryValue(Integer categoryValue) {
		this.categoryValue = categoryValue;
	}

	public Integer getCategoryValue() {
		return categoryValue;
	}

}
