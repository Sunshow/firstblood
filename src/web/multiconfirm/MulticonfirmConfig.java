package web.multiconfirm;

import java.util.Date;

import com.lehecai.core.YesNoStatus;


public class MulticonfirmConfig {
	private Long id;
	private String configKey;
	private String configName;
	private Integer timeout;
	private String specialSigns;
	private Integer confirmCount;
	private Date createTime;
	private MulticonfirmConfigType multiconfirmConfigType;
	private YesNoStatus isEmail;
	private String emailAddress;
	private YesNoStatus isSms;
	private String smsAddress;
	
	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getConfirmCount() {
		return confirmCount;
	}

	public void setConfirmCount(Integer confirmCount) {
		this.confirmCount = confirmCount;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getSpecialSigns() {
		return specialSigns;
	}

	public void setSpecialSigns(String specialSigns) {
		this.specialSigns = specialSigns;
	}

	public Date getCreateTime() {
		if (createTime == null) {
			createTime = new Date();
		}
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public MulticonfirmConfigType getMulticonfirmConfigType() {
		return multiconfirmConfigType;
	}

	public void setMulticonfirmConfigType(
			MulticonfirmConfigType multiconfirmConfigType) {
		this.multiconfirmConfigType = multiconfirmConfigType;
	}

	public YesNoStatus getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(YesNoStatus isEmail) {
		this.isEmail = isEmail;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public YesNoStatus getIsSms() {
		return isSms;
	}

	public void setIsSms(YesNoStatus isSms) {
		this.isSms = isSms;
	}

	public String getSmsAddress() {
		return smsAddress;
	}

	public void setSmsAddress(String smsAddress) {
		this.smsAddress = smsAddress;
	}

}
