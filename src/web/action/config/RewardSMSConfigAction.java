package web.action.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.ConfigParserMapping;
import com.lehecai.core.config.impl.lotteryreward.RewardSMSConfig;
import com.lehecai.core.config.impl.lotteryreward.RewardSMSConfigItem;
import com.lehecai.core.config.impl.lotteryreward.RewardSMSConfigParser;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.service.config.ConfigService;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 中奖自动发短信配置
 * @author qatang
 *
 */
public class RewardSMSConfigAction extends BaseAction {
	private static final long serialVersionUID = -2225123800707608839L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ConfigService configService;
	
	private Integer lotteryTypeId;
	private LotteryType lotteryType;
	
	private RewardSMSConfig rewardSMSConfig;
	private RewardSMSConfigItem rewardSMS;
	private JSONObject rewardSMSJSONObject;

	private static String getRewardSMSConfigGroup() {
		return SettingConstant.GROUP_REWARD_SMS_CONFIG;
	}
	
	private static RewardSMSConfigParser getRewardSMSConfigParser() {
		return (RewardSMSConfigParser) ConfigParserMapping.getByGroup(getRewardSMSConfigGroup());
	}
	
	public String handle() {
		logger.info("进入中奖自动发短信配置彩种列表");
		
		RewardSMSConfigParser parser = getRewardSMSConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getRewardSMSConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getRewardSMSConfigGroup());
			return "failure";
		}

		try {
			List<String> itemList = new ArrayList<String>();
			itemList.add(parser.getGlobalItemKey());
			itemList.add(parser.getDefaultItemKey());
			
			rewardSMSConfig = configService.getConfigWithItems(getRewardSMSConfigGroup(), itemList, RewardSMSConfig.class);	//中奖自动发短信全局配置
		} catch (Exception e) {
			logger.error("得到中奖自动发短信配置异常，{}", e);
			super.setErrorMessage("得到中奖自动发短信配置异常，" + e);
			return "failure";
		}
		if (rewardSMSConfig != null) {
			rewardSMS = rewardSMSConfig.getDefaultConfigItem();				//中奖自动发短信默认配置
			if (rewardSMS == null) {
				rewardSMS = new RewardSMSConfigItem();
				rewardSMS.setTemplateExt(rewardSMSConfig.getSmsTemplateExt());
			}
			rewardSMSJSONObject = rewardSMS.toJSONObject();
		} else {
			logger.info("中奖自动发短信配置为空");
		}
		return "list";
	}
	
	/**
	 * 输入彩种中奖自动发短信配置
	 */
	public String input() {
		logger.info("进入输入中奖自动发短信配置");
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
		
		RewardSMSConfigParser parser = getRewardSMSConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getRewardSMSConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getRewardSMSConfigGroup());
			return "failure";
		}
		
		try {
			List<String> itemList = new ArrayList<String>();
			itemList.add(parser.getGlobalItemKey());
			itemList.add(parser.getLotteryTypeItemKey(lotteryType));
			
			rewardSMSConfig = configService.getConfigWithItems(getRewardSMSConfigGroup(), itemList, RewardSMSConfig.class);	//中奖自动发短信全局配置
		} catch (Exception e) {
			logger.error("得到中奖自动发短信配置异常，{}", e);
			super.setErrorMessage("得到中奖自动发短信配置异常，" + e);
			return "failure";
		}
		
		if (rewardSMSConfig != null) {
			Map<LotteryType, RewardSMSConfigItem> rewardSMSMap = rewardSMSConfig.getLotteryTypeConfigItem();
			if (rewardSMSMap != null) {
				rewardSMS = rewardSMSMap.get(LotteryType.getItem(lotteryTypeId));	//中奖自动发短信彩种配置
			} else {
				logger.info("中奖自动发短信彩种配置为空");
			}
			
			if (rewardSMS == null) {
				rewardSMS = new RewardSMSConfigItem();
				rewardSMS.setTemplateExt(rewardSMSConfig.getSmsTemplateExt());
			}
			
			rewardSMSJSONObject = rewardSMS.toJSONObject();

		} else {
			logger.info("中奖自动发短信配置为空");
		}
		return "inputForm";
	}
	
	/**
	 * 更新全局中奖自动发短信配置
	 */
	public String updateGlobal() {
		logger.info("进入更新全局中奖自动发短信配置");
		if (rewardSMSConfig == null) {
			logger.error("中奖自动发短信全局配置为空");
			super.setErrorMessage("请填写中奖自动发短信全局配置");
			return "failure";
		}
		if (rewardSMSConfig.getSmsTemplatePath() == null || rewardSMSConfig.getSmsTemplatePath().equals("")) {
			logger.error("中奖自动发短信全局配置模板路径为空");
			super.setErrorMessage("中奖自动发短信全局配置模板路径为空");
			return "failure";
		}
		if (rewardSMSConfig.getSmsTemplatePrefix() == null || rewardSMSConfig.getSmsTemplatePrefix().equals("")) {
			logger.error("中奖自动发短信全局配置模板前缀为空");
			super.setErrorMessage("中奖自动发短信全局配置模板前缀为空");
			return "failure";
		}
		if (rewardSMSConfig.getSmsTemplateName() == null || rewardSMSConfig.getSmsTemplateName().equals("")) {
			logger.error("中奖自动发短信全局配置模板名称为空");
			super.setErrorMessage("中奖自动发短信全局配置模板名称为空");
			return "failure";
		}
		if (rewardSMSConfig.getSmsTemplateExt() == null || rewardSMSConfig.getSmsTemplateExt().equals("")) {
			logger.error("中奖自动发短信全局配置模板扩展名为空");
			super.setErrorMessage("中奖自动发短信全局配置模板扩展名为空");
			return "failure";
		}
		
		boolean updateResult = false;
		try {
			updateResult = configService.updateConfig(rewardSMSConfig);					//更新配置
		} catch (Exception e) {
			logger.error("更新中奖自动发短信配置失败，{}", e);
			super.setErrorMessage("更新中奖自动发短信配置失败，" + e);
			return "failure";
		}
		
		if (updateResult) {
			logger.info("更新中奖自动发短信配置成功");
			super.setSuccessMessage("更新中奖自动发短信配置成功");
			super.setForwardUrl("/config/rewardSMSConfig.do");
			return "success";
		} else {
			logger.error("更新中奖自动发短信配置失败");
			super.setErrorMessage("更新中奖自动发短信配置失败");
			return "failure";
		}
 	}
	
	/**
	 * 更新中奖自动发短信默认及彩种配置
	 * @return
	 */
	public String update() {
		logger.info("进入更新中奖自动发短信默认及彩种配置");
		if (lotteryTypeId == null || lotteryTypeId == 0L) {
			logger.error("中奖自动发短信配置的彩种编码为空");
			super.setErrorMessage("中奖自动发短信配置的彩种编码不能为空");
			return "failure";
		}
		lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("彩种编码非法, lotteryTypeId={}", lotteryTypeId);
			super.setErrorMessage("彩种编码非法");
			return "failure";
		}
		if (rewardSMS == null) {
			logger.error("中奖自动发短信配置为空");
			super.setErrorMessage("请填写中奖自动发短信配置");
			return "failure";
		}
		if ((lotteryType.getValue() == LotteryType.ALL.getValue()) || (lotteryType.getValue() != LotteryType.ALL.getValue() && !rewardSMS.isUseDefault())) {	//如果不采用默认配置
			if (rewardSMS.getTimelines() == null || rewardSMS.getTimelines().size() == 0) {
				logger.error("中奖自动发短信配置时间为空");
				super.setErrorMessage("请填写中奖自动发短信配置时间");
				return "failure";
			}
			for (String[] timeline : rewardSMS.getTimelines()) {
				Date beginTimelineDate = CoreDateUtils.parseDate(timeline[0], "HH:mm");
				Date endTimelineDate = CoreDateUtils.parseDate(timeline[1], "HH:mm");
				if (beginTimelineDate.getTime() > endTimelineDate.getTime()) {
					logger.error("中奖自动发短信配置时间不正确");
					super.setErrorMessage("请正确填写中奖自动发短信配置时间");
					return "failure";
				}
			}
		}

		RewardSMSConfigParser parser = getRewardSMSConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getRewardSMSConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getRewardSMSConfigGroup());
			return "failure";
		}
		
		if (rewardSMS.getTemplateExt() == null || rewardSMS.getTemplateExt().equals("")) {
			RewardSMSConfig rewardSMSConfig = null;
			try {
				List<String> itemList = new ArrayList<String>();
				itemList.add(parser.getGlobalItemKey());
				
				rewardSMSConfig = configService.getConfigWithItems(getRewardSMSConfigGroup(), itemList, RewardSMSConfig.class);	//中奖自动发短信全局配置
			} catch (Exception e) {
				logger.error("得到中奖自动发短信配置异常，{}", e);
				super.setErrorMessage("得到中奖自动发短信配置异常，" + e);
				return "failure";
			}
			if (rewardSMSConfig != null) {
				if (rewardSMSConfig.getSmsTemplateExt() != null && !rewardSMSConfig.getSmsTemplateExt().equals("")) {
					rewardSMS.setTemplateExt(rewardSMSConfig.getSmsTemplateExt());
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
			updateResult = configService.updateConfigItem(rewardSMS, getRewardSMSConfigGroup(), item);
		} catch (Exception e) {
			logger.error("更新中奖自动发短信配置失败，{}", e);
			super.setErrorMessage("更新中奖自动发短信配置失败，" + e);
			return "failure";
		}
		
		if (updateResult) {
			logger.info("更新中奖自动发短信配置成功");
			super.setSuccessMessage("更新中奖自动发短信配置成功");
			if (lotteryTypeId == LotteryType.ALL.getValue()) {
				super.setForwardUrl("/config/rewardSMSConfig.do");
			} else {
				super.setForwardUrl("/config/rewardSMSConfig.do?action=input&lotteryTypeId=" + lotteryTypeId);
			}
			return "success";
		} else {
			logger.error("更新中奖自动发短信配置失败");
			super.setErrorMessage("更新中奖自动发短信配置失败");
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

	public RewardSMSConfig getRewardSMSConfig() {
		return rewardSMSConfig;
	}

	public void setRewardSMSConfig(RewardSMSConfig rewardSMSConfig) {
		this.rewardSMSConfig = rewardSMSConfig;
	}

	public RewardSMSConfigItem getRewardSMS() {
		return rewardSMS;
	}

	public void setRewardSMS(RewardSMSConfigItem rewardSMS) {
		this.rewardSMS = rewardSMS;
	}

	public JSONObject getRewardSMSJSONObject() {
		return rewardSMSJSONObject;
	}

	public void setRewardSMSJSONObject(JSONObject rewardSMSJSONObject) {
		this.rewardSMSJSONObject = rewardSMSJSONObject;
	}
}
