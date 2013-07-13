package web.action.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConfigType;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConstant;
import com.lehecai.admin.web.multiconfirm.MulticonfirmSign;
import com.lehecai.admin.web.service.multiconfirm.MulticonfirmService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;

public class MulticonfirmConfigAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5872878035173685419L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private MulticonfirmService multiconfirmService;
	
	private Long id;
	private String configKey;
	private String configName;
	private Date createTimeFrom;
	private Date createTimeTo;
	private List<MulticonfirmConfig> multiconfirmConfigList;
	private MulticonfirmConfig multiconfirmConfig;
	
	private String actionName;
	private String methodName;
	private String specialValue;
	private Integer multiconfirmConfigTypeValue;
	private Integer emailNoticeValue;
	private Integer smsNoticeValue;
	
	public String handle() {
		return "list";
	}
	
	public String query() {
		logger.info("进入多次确认配置查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		MulticonfirmConfigType mct = multiconfirmConfigTypeValue == null ? MulticonfirmConfigType.ALL : MulticonfirmConfigType.getItem(multiconfirmConfigTypeValue);
		multiconfirmConfigList = multiconfirmService.getConfigList(id, configKey, configName, mct, createTimeFrom, createTimeTo, super.getPageBean());
		PageBean pageBean = multiconfirmService.getConfigPageBean(id, configKey, configName, mct, createTimeFrom, createTimeTo, super.getPageBean());
		super.setPageString(PageUtil.getPageString(request, pageBean));
		logger.info("结束多次确认配置查询");
		return "list";
	}
	
	public String view() {
		logger.info("进入查询多次确认配置信息");
		if (multiconfirmConfig != null && multiconfirmConfig.getId() != null && multiconfirmConfig.getId() != 0) {
			multiconfirmConfig = multiconfirmService.getConfig(multiconfirmConfig.getId());
		}
		logger.info("查询多次确认配置信息结束");
		return "view";
	}
	
	public String input() {
		if (multiconfirmConfig != null && multiconfirmConfig.getId() != null && multiconfirmConfig.getId() != 0) {
			multiconfirmConfig = multiconfirmService.getConfig(multiconfirmConfig.getId());
			Map<String, String> map = MulticonfirmSign.paraseConfigKey(multiconfirmConfig.getConfigKey());
			if (map != null) {
				actionName = map.get(MulticonfirmConstant.MULITCONFIRM_ACTIONNAME);
				methodName = map.get(MulticonfirmConstant.MULITCONFIRM_METHODNAME);
			}
		} else {
			multiconfirmConfig = new MulticonfirmConfig();
			multiconfirmConfig.setIsEmail(YesNoStatus.NO);
			multiconfirmConfig.setIsSms(YesNoStatus.NO);
		}
		return "inputForm";
	}
	
	public String inputSpecial() {
		if (multiconfirmConfig != null && multiconfirmConfig.getId() != null && multiconfirmConfig.getId() != 0) {
			MulticonfirmConfig multiconfirmConfigTemp = multiconfirmService.getConfig(multiconfirmConfig.getId());
			Map<String, String> map = MulticonfirmSign.paraseConfigKey(multiconfirmConfigTemp.getConfigKey());
			if (map != null) {
				actionName = map.get(MulticonfirmConstant.MULITCONFIRM_ACTIONNAME);
				methodName = map.get(MulticonfirmConstant.MULITCONFIRM_METHODNAME);
				if (map.containsKey(MulticonfirmConstant.MULTICONFIRM_SPECIAL_VALUE)) {
					specialValue = map.get(MulticonfirmConstant.MULTICONFIRM_SPECIAL_VALUE);
				}
			}
			multiconfirmConfig.setConfirmCount(multiconfirmConfigTemp.getConfirmCount());
			multiconfirmConfig.setSpecialSigns(multiconfirmConfigTemp.getSpecialSigns());
			multiconfirmConfig.setIsEmail(YesNoStatus.NO);
			multiconfirmConfig.setIsSms(YesNoStatus.NO);
			multiconfirmConfig.setTimeout(multiconfirmConfigTemp.getTimeout());
			multiconfirmConfig.setIsEmail(multiconfirmConfigTemp.getIsEmail());
			multiconfirmConfig.setIsSms(multiconfirmConfigTemp.getIsSms());
			multiconfirmConfig.setEmailAddress(multiconfirmConfigTemp.getEmailAddress());
			multiconfirmConfig.setSmsAddress(multiconfirmConfigTemp.getSmsAddress());
			if (multiconfirmConfigTemp.getMulticonfirmConfigType().getValue() == MulticonfirmConfigType.SPECIAL.getValue()) {
				multiconfirmConfig.setConfigName(multiconfirmConfigTemp.getConfigName());
				return "inputSpecial";
			}
			multiconfirmConfig.setConfigName(multiconfirmConfigTemp.getConfigName() + "(特殊)");
			multiconfirmConfig.setId(null);
		}
		return "inputSpecial";
	}
	
	public String manageSpecial() {
		logger.info("进入管理多次确认特殊配置信息");
		if (multiconfirmConfig == null) {
			logger.error("配置信息为空");
			super.setErrorMessage("多次确认配置信息为空");
			return "failure";
		}
		if (actionName == null || methodName == null || actionName.equals("") || methodName.equals("")) {
			logger.error("配置Key信息为空");
			super.setErrorMessage("多次确认配置Key为空");
			return "failure";
		}
		
		String key = MulticonfirmSign.getSpecialConfigString(actionName, methodName, specialValue);
		
		if (key == null ||key.equals("")) {
			logger.error("配置Key信息为空");
			super.setErrorMessage("多次确认配置Key为空");
			return "failure";
		}
		
		MulticonfirmConfig temp = multiconfirmService.getConfig(key);
		if (temp != null && temp.getId() != null && temp.getId() != 0) {
			if (multiconfirmConfig.getId() == null || multiconfirmConfig.getId().intValue() != temp.getId().intValue()) {
				logger.error("配置KEY已存在不可添加，如要进行修改请到修改页面");
				super.setErrorMessage("配置KEY已存在不可添加，如要进行修改请到修改页面");
				return "failure";
			} 
		}
		
		multiconfirmConfig.setConfigKey(key);
		multiconfirmConfig.setMulticonfirmConfigType(MulticonfirmConfigType.SPECIAL);
		multiconfirmConfig.setIsEmail(YesNoStatus.getItem(emailNoticeValue));
		multiconfirmConfig.setIsSms(YesNoStatus.getItem(smsNoticeValue));
		if (multiconfirmConfig.getConfigName() == null || multiconfirmConfig.getConfigName().equals("")) {
			logger.error("配置名称为空");
			super.setErrorMessage("多次确认配置名称为空");
			return "failure";
		}
		if (multiconfirmConfig.getConfirmCount() == null || multiconfirmConfig.getConfirmCount() == 0) {
			logger.error("配置信息:确认次数为空");
			super.setErrorMessage("多次确认配置信息：确认次数为空");
			return "failure";
		} else {
			if (multiconfirmConfig.getConfirmCount() < 0) {
				logger.error("配置信息:确认次数为负");
				super.setErrorMessage("多次确认配置信息：确认次数不能为负");
				return "failure";
			}
		}
		multiconfirmService.manageConfig(multiconfirmConfig);
		logger.info("管理多次确认配置信息结束");
		return "success";
	}

	public String manage() {
		logger.info("进入管理多次确认通用配置信息");
		if (multiconfirmConfig == null) {
			logger.error("配置信息为空");
			super.setErrorMessage("多次确认配置信息为空");
			return "failure";
		}
		if (actionName == null || methodName == null || actionName.equals("") || methodName.equals("")) {
			logger.error("配置Key信息为空");
			super.setErrorMessage("多次确认配置Key为空");
			return "failure";
		}
		
		String key = MulticonfirmSign.getDefaultConfigString(actionName, methodName);
		
		if (key == null ||key.equals("")) {
			logger.error("配置Key信息为空");
			super.setErrorMessage("多次确认配置Key为空");
			return "failure";
		}
		
		MulticonfirmConfig temp = multiconfirmService.getConfig(key);
		if (temp != null && temp.getId() != null && temp.getId() != 0) {
			if (multiconfirmConfig.getId() == null || multiconfirmConfig.getId().intValue() != temp.getId().intValue()) {
				logger.error("配置KEY已存在不可添加，如要进行修改请到修改页面");
				super.setErrorMessage("配置KEY已存在不可添加，如要进行修改请到修改页面");
				return "failure";
			} 
		}
		
		multiconfirmConfig.setConfigKey(key);
		multiconfirmConfig.setIsEmail(YesNoStatus.getItem(emailNoticeValue));
		multiconfirmConfig.setIsSms(YesNoStatus.getItem(smsNoticeValue));
		multiconfirmConfig.setMulticonfirmConfigType(MulticonfirmConfigType.DEFAULT);
		if (multiconfirmConfig.getConfigName() == null || multiconfirmConfig.getConfigName().equals("")) {
			logger.error("配置名称为空");
			super.setErrorMessage("多次确认配置名称为空");
			return "failure";
		}
		if (multiconfirmConfig.getConfirmCount() == null || multiconfirmConfig.getConfirmCount() == 0) {
			logger.error("配置信息:确认次数为空");
			super.setErrorMessage("多次确认配置信息：确认次数为空");
			return "failure";
		} else {
			if (multiconfirmConfig.getConfirmCount() < 0) {
				logger.error("配置信息:确认次数为负");
				super.setErrorMessage("多次确认配置信息：确认次数不能为负");
				return "failure";
			}
		}
		multiconfirmService.manageConfig(multiconfirmConfig);
		logger.info("管理多次确认通用配置信息结束");
		return "success";
	}
	
	public String del() {
		logger.info("进入删除多次确认配置信息");
		if (multiconfirmConfig != null && multiconfirmConfig.getId() != null && multiconfirmConfig.getId() != 0) {
			multiconfirmConfig = multiconfirmService.getConfig(multiconfirmConfig.getId());
		} else {
			logger.error("配置id为空");
			super.setErrorMessage("多次确认配置id为空");
			return "failure";
		}
		multiconfirmService.delConfig(multiconfirmConfig);
		logger.info("结束删除多次确认配置信息");
		return "success";
	}
	
	public List<YesNoStatus> getYesNoStatuses() {
		return YesNoStatus.getItems();
	}

	public MulticonfirmService getMulticonfirmService() {
		return multiconfirmService;
	}

	public void setMulticonfirmService(MulticonfirmService multiconfirmService) {
		this.multiconfirmService = multiconfirmService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public List<MulticonfirmConfig> getMulticonfirmConfigList() {
		return multiconfirmConfigList;
	}

	public void setMulticonfirmConfigList(
			List<MulticonfirmConfig> multiconfirmConfigList) {
		this.multiconfirmConfigList = multiconfirmConfigList;
	}

	public MulticonfirmConfig getMulticonfirmConfig() {
		return multiconfirmConfig;
	}

	public void setMulticonfirmConfig(MulticonfirmConfig multiconfirmConfig) {
		this.multiconfirmConfig = multiconfirmConfig;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Date getCreateTimeFrom() {
		return createTimeFrom;
	}

	public void setCreateTimeFrom(Date createTimeFrom) {
		this.createTimeFrom = createTimeFrom;
	}

	public Date getCreateTimeTo() {
		return createTimeTo;
	}

	public void setCreateTimeTo(Date createTimeTo) {
		this.createTimeTo = createTimeTo;
	}

	public String getSpecialValue() {
		return specialValue;
	}

	public void setSpecialValue(String specialValue) {
		this.specialValue = specialValue;
	}
	
	public MulticonfirmConfigType getMulticonfirmConfigTypeDefault() {
		return MulticonfirmConfigType.DEFAULT;
	}
	
	public MulticonfirmConfigType getMulticonfirmConfigTypeSpecial() {
		return MulticonfirmConfigType.SPECIAL;
	}

	public Integer getMulticonfirmConfigTypeValue() {
		return multiconfirmConfigTypeValue;
	}

	public void setMulticonfirmConfigTypeValue(Integer multiconfirmConfigTypeValue) {
		this.multiconfirmConfigTypeValue = multiconfirmConfigTypeValue;
	}
	
	public List<MulticonfirmConfigType> getMulticonfirmConfigTypes() {
		return MulticonfirmConfigType.list;
	}

	public Integer getEmailNoticeValue() {
		return emailNoticeValue;
	}

	public void setEmailNoticeValue(Integer emailNoticeValue) {
		this.emailNoticeValue = emailNoticeValue;
	}

	public Integer getSmsNoticeValue() {
		return smsNoticeValue;
	}

	public void setSmsNoticeValue(Integer smsNoticeValue) {
		this.smsNoticeValue = smsNoticeValue;
	}
	
}
