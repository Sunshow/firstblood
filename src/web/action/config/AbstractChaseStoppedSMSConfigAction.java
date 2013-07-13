package web.action.config;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import net.sf.json.JSONObject;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.config.ConfigParserMapping;
import com.lehecai.core.config.impl.lotterychase.AbstractChaseStoppedSMSConfigParser;
import com.lehecai.core.config.impl.lotterychase.ChaseStoppedSMSConfig;
import com.lehecai.core.config.impl.lotterychase.ChaseStoppedSMSConfigItem;
import com.lehecai.core.lottery.ChaseStatus;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.service.config.ConfigService;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 追号停止自动发短信配置
 * @author qatang
 *
 */
public abstract class AbstractChaseStoppedSMSConfigAction<T extends ChaseStoppedSMSConfig> extends BaseAction {

	private static final long serialVersionUID = 1L;

	private ConfigService configService;
	
	private Integer lotteryTypeId;
	private LotteryType lotteryType;
	
	private T chaseStoppedSMSConfig;
	private ChaseStoppedSMSConfigItem chaseStoppedSMS;
	private JSONObject chaseStoppedSMSJSONObject;
	
	@SuppressWarnings("unchecked")
	protected Class<T> getArchiveActualType() {
		ParameterizedType paramType = (ParameterizedType)this.getClass().getGenericSuperclass();
		
		Class<T> clazz = (Class<T>)paramType.getActualTypeArguments()[0];
		
		return clazz;
	}

	abstract public ChaseStatus getChaseStoppedStatus();
	
	abstract protected String getChaseStoppedSMSConfigGroup();

	@SuppressWarnings("unchecked")
	protected AbstractChaseStoppedSMSConfigParser<T> getChaseStoppedSMSConfigParser() {
		return (AbstractChaseStoppedSMSConfigParser<T>) ConfigParserMapping.getByGroup(this.getChaseStoppedSMSConfigGroup());
	}
	
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入" + this.getChaseStoppedStatus().getName() + "自动发短信配置彩种列表");

		AbstractChaseStoppedSMSConfigParser<T> parser = getChaseStoppedSMSConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getChaseStoppedSMSConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getChaseStoppedSMSConfigGroup());
			return "failure";
		}

		try {
			List<String> itemList = new ArrayList<String>();
			itemList.add(parser.getGlobalItemKey());
			itemList.add(parser.getDefaultItemKey());
			
			chaseStoppedSMSConfig = (T)configService.getConfigWithItems(getChaseStoppedSMSConfigGroup(), itemList);	//中奖自动发短信全局配置
		} catch (Exception e) {
			logger.error("得到" + this.getChaseStoppedStatus().getName() + "自动发短信配置异常，{}", e);
			super.setErrorMessage("得到" + this.getChaseStoppedStatus().getName() + "自动发短信配置异常，" + e);
			return "failure";
		}
		if (chaseStoppedSMSConfig != null) {
			chaseStoppedSMS = chaseStoppedSMSConfig.getDefaultConfigItem();				//中奖自动发短信默认配置
			if (chaseStoppedSMS == null) {
				chaseStoppedSMS = new ChaseStoppedSMSConfigItem();
				chaseStoppedSMS.setTemplateExt(chaseStoppedSMSConfig.getSmsTemplateExt());
			}
			chaseStoppedSMSJSONObject = chaseStoppedSMS.toJSONObject();
		} else {
			logger.info("" + this.getChaseStoppedStatus().getName() + "自动发短信配置为空");
		}
		return "list";
	}
	
	/**
	 * 输入彩种" + this.getChaseStoppedStatus().getName() + "自动发短信配置
	 */
	@SuppressWarnings("unchecked")
	public String input() {
		logger.info("进入输入" + this.getChaseStoppedStatus().getName() + "自动发短信配置");
		if (lotteryTypeId == null) {
			logger.error("彩种编码为空");
			super.setErrorMessage("彩种编码为空");
			return "failure";
		}
		lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("彩种编码非法, lotteryTypeId={}", lotteryTypeId);
			super.setErrorMessage("彩种编码非法");
			return "failure";
		}
		
		AbstractChaseStoppedSMSConfigParser<T> parser = getChaseStoppedSMSConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getChaseStoppedSMSConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getChaseStoppedSMSConfigGroup());
			return "failure";
		}
		
		try {
			List<String> itemList = new ArrayList<String>();
			itemList.add(parser.getGlobalItemKey());
			itemList.add(parser.getLotteryTypeItemKey(lotteryType));
			
			chaseStoppedSMSConfig = (T)configService.getConfigWithItems(getChaseStoppedSMSConfigGroup(), itemList);
		} catch (Exception e) {
			logger.error("得到" + this.getChaseStoppedStatus().getName() + "自动发短信配置异常，{}", e);
			super.setErrorMessage("得到" + this.getChaseStoppedStatus().getName() + "自动发短信配置异常，" + e);
			return "failure";
		}
		if (chaseStoppedSMSConfig != null) {
			Map<LotteryType,ChaseStoppedSMSConfigItem> chaseStoppedSMSMap = chaseStoppedSMSConfig.getLotteryTypeConfigItem();
			if (chaseStoppedSMSMap != null) {
				chaseStoppedSMS = chaseStoppedSMSMap.get(LotteryType.getItem(lotteryTypeId));	//中奖自动发短信彩种配置
			} else {
				logger.info("中奖自动发短信彩种配置为空");
			}
			
			if (chaseStoppedSMS == null) {
				chaseStoppedSMS = new ChaseStoppedSMSConfigItem();
				chaseStoppedSMS.setTemplateExt(chaseStoppedSMSConfig.getSmsTemplateExt());
			}
			
			chaseStoppedSMSJSONObject = chaseStoppedSMS.toJSONObject();

		} else {
			logger.info("" + this.getChaseStoppedStatus().getName() + "自动发短信配置为空");
		}
		return "inputForm";
	}

	protected String getRequestURI() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String contextPath = request.getContextPath();
		return StringUtils.substringAfter(request.getRequestURI(), contextPath);
	}
	
	/**
	 * 更新全局" + this.getChaseStoppedStatus().getName() + "自动发短信配置
	 */
	public String updateGlobal() {
		logger.info("进入更新全局" + this.getChaseStoppedStatus().getName() + "自动发短信配置");
		if (chaseStoppedSMSConfig == null) {
			logger.error("中奖自动发短信全局配置为空");
			super.setErrorMessage("请填写中奖自动发短信全局配置");
			return "failure";
		}
		if (chaseStoppedSMSConfig.getSmsTemplatePath() == null || chaseStoppedSMSConfig.getSmsTemplatePath().equals("")) {
			logger.error("中奖自动发短信全局配置模板路径为空");
			super.setErrorMessage("中奖自动发短信全局配置模板路径为空");
			return "failure";
		}
		if (chaseStoppedSMSConfig.getSmsTemplatePrefix() == null || chaseStoppedSMSConfig.getSmsTemplatePrefix().equals("")) {
			logger.error("中奖自动发短信全局配置模板前缀为空");
			super.setErrorMessage("中奖自动发短信全局配置模板前缀为空");
			return "failure";
		}
		if (chaseStoppedSMSConfig.getSmsTemplateName() == null || chaseStoppedSMSConfig.getSmsTemplateName().equals("")) {
			logger.error("中奖自动发短信全局配置模板名称为空");
			super.setErrorMessage("中奖自动发短信全局配置模板名称为空");
			return "failure";
		}
		if (chaseStoppedSMSConfig.getSmsTemplateExt() == null || chaseStoppedSMSConfig.getSmsTemplateExt().equals("")) {
			logger.error("中奖自动发短信全局配置模板扩展名为空");
			super.setErrorMessage("中奖自动发短信全局配置模板扩展名为空");
			return "failure";
		}
		
		boolean updateResult = false;
		try {
			updateResult = configService.updateConfig(chaseStoppedSMSConfig);					//更新配置
		} catch (Exception e) {
			logger.error("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败，{}", e);
			super.setErrorMessage("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败，" + e);
			return "failure";
		}
		
		if (updateResult) {
			logger.info("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置成功");
			super.setSuccessMessage("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置成功");
			super.setForwardUrl(this.getRequestURI());
			return "success";
		} else {
			logger.error("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败");
			super.setErrorMessage("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败");
			return "failure";
		}
 	}
	
	/**
	 * 更新中奖自动发短信默认及彩种配置
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String update() {
		logger.info("进入更新中奖自动发短信默认及彩种配置");
		if (lotteryTypeId == null || lotteryTypeId == 0L) {
			logger.error("" + this.getChaseStoppedStatus().getName() + "自动发短信配置的彩种编码为空");
			super.setErrorMessage("" + this.getChaseStoppedStatus().getName() + "自动发短信配置的彩种编码不能为空");
			return "failure";
		}
		
		lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("彩种编码非法, lotteryTypeId={}", lotteryTypeId);
			super.setErrorMessage("彩种编码非法");
			return "failure";
		}
		
		AbstractChaseStoppedSMSConfigParser<T> parser = getChaseStoppedSMSConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getChaseStoppedSMSConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getChaseStoppedSMSConfigGroup());
			return "failure";
		}
		
		if (chaseStoppedSMS == null) {
			logger.error("" + this.getChaseStoppedStatus().getName() + "自动发短信配置为空");
			super.setErrorMessage("请填写" + this.getChaseStoppedStatus().getName() + "自动发短信配置");
			return "failure";
		}

		if ((lotteryType.getValue() == LotteryType.ALL.getValue()) || (lotteryType.getValue() != LotteryType.ALL.getValue() && !chaseStoppedSMS.isUseDefault())) {	//如果不采用默认配置
			if (chaseStoppedSMS.getTimelines() == null || chaseStoppedSMS.getTimelines().size() == 0) {
				logger.error("" + this.getChaseStoppedStatus().getName() + "自动发短信配置时间为空");
				super.setErrorMessage("请填写" + this.getChaseStoppedStatus().getName() + "自动发短信配置时间");
				return "failure";
			}
			for (String[] timeline : chaseStoppedSMS.getTimelines()) {
				Date beginTimelineDate = CoreDateUtils.parseDate(timeline[0], "HH:mm");
				Date endTimelineDate = CoreDateUtils.parseDate(timeline[1], "HH:mm");
				if (beginTimelineDate.getTime() > endTimelineDate.getTime()) {
					logger.error("" + this.getChaseStoppedStatus().getName() + "自动发短信配置时间不正确");
					super.setErrorMessage("请正确填写" + this.getChaseStoppedStatus().getName() + "自动发短信配置时间");
					return "failure";
				}
			}
		}
		
		if (chaseStoppedSMS.getTemplateExt() == null || chaseStoppedSMS.getTemplateExt().equals("")) {
			ChaseStoppedSMSConfig chaseStoppedSMSConfig = null;
			try {
				List<String> itemList = new ArrayList<String>();
				itemList.add(parser.getGlobalItemKey());
				
				chaseStoppedSMSConfig = (T)configService.getConfigWithItems(getChaseStoppedSMSConfigGroup(), itemList);
			} catch (Exception e) {
				logger.error("得到" + this.getChaseStoppedStatus().getName() + "自动发短信配置异常，{}", e);
				super.setErrorMessage("得到" + this.getChaseStoppedStatus().getName() + "自动发短信配置异常，" + e);
				return "failure";
			}
			if (chaseStoppedSMSConfig != null) {
				if (chaseStoppedSMSConfig.getSmsTemplateExt() != null && !chaseStoppedSMSConfig.getSmsTemplateExt().equals("")) {
					chaseStoppedSMS.setTemplateExt(chaseStoppedSMSConfig.getSmsTemplateExt());
				}
			}
		}
		
		boolean updateResult = false;
		try {
			String item = null;
			if (lotteryType.getValue() == LotteryType.ALL.getValue()) {
				item = parser.getDefaultItemKey();
			} else {
				item = parser.getLotteryTypeItemKey(lotteryType);
			}

			// 更新配置
			updateResult = configService.updateConfigItem(chaseStoppedSMS, getChaseStoppedSMSConfigGroup(), item);
		} catch (Exception e) {
			logger.error("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败，{}", e);
			super.setErrorMessage("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败，" + e);
			return "failure";
		}
		
		if (updateResult) {
			logger.info("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置成功");
			super.setSuccessMessage("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置成功");
			if (lotteryTypeId == LotteryType.ALL.getValue()) {
				super.setForwardUrl(this.getRequestURI());
			} else {
				super.setForwardUrl(this.getRequestURI() + "?action=input&lotteryTypeId=" + lotteryTypeId);
			}
			return "success";
		} else {
			logger.error("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败");
			super.setErrorMessage("更新" + this.getChaseStoppedStatus().getName() + "自动发短信配置失败");
			return "failure";
		}
	}
	
	public String del() {
		return "forward";
	}

	public List<YesNoStatus> getYesNoStatusItems() {
		return YesNoStatus.getItems();
	}
	
	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.get();
	}

	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}

	public T getChaseStoppedSMSConfig() {
		if (chaseStoppedSMSConfig == null) {
			// java的泛型是傻逼，不自己初始化就会给你弄个别的类型的实例出来
			try {
				chaseStoppedSMSConfig = this.getArchiveActualType().newInstance();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return chaseStoppedSMSConfig;
	}

	public void setChaseStoppedSMSConfig(T chaseStoppedSMSConfig) {
		this.chaseStoppedSMSConfig = chaseStoppedSMSConfig;
	}

	public ChaseStoppedSMSConfigItem getChaseStoppedSMS() {
		return chaseStoppedSMS;
	}

	public void setChaseStoppedSMS(ChaseStoppedSMSConfigItem chaseStoppedSMS) {
		this.chaseStoppedSMS = chaseStoppedSMS;
	}

	public JSONObject getChaseStoppedSMSJSONObject() {
		return chaseStoppedSMSJSONObject;
	}

	public void setChaseStoppedSMSJSONObject(JSONObject chaseStoppedSMSJSONObject) {
		this.chaseStoppedSMSJSONObject = chaseStoppedSMSJSONObject;
	}
}
