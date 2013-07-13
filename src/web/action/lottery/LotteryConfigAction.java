package web.action.lottery;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.EnabledStatus;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.ConfigParserMapping;
import com.lehecai.core.config.impl.lottery.LotteryPrizeItem;
import com.lehecai.core.config.impl.lottery.LotteryResultItem;
import com.lehecai.core.config.impl.lottery.LotterySettingConfigItem;
import com.lehecai.core.config.impl.lottery.LotterySettingConfigParser;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.ResultRegionType;
import com.lehecai.core.service.config.ConfigService;


/**
 * 奖项
 * @author qatang
 *
 */
public class LotteryConfigAction extends BaseAction {
	private static final long serialVersionUID = 8098684421155735770L;
	private static final Logger logger = LoggerFactory.getLogger(LotteryConfigAction.class);
	
	private ConfigService configService;
	
	private Integer lotteryTypeId;
	private LotteryType lotteryType;
	
	private LotterySettingConfigItem lotterySettingItem;

	private static String getLotterySettingConfigGroup() {
		return SettingConstant.GROUP_LOTTERY_CONFIG;
	}
	
	private static LotterySettingConfigParser getLotterySettingConfigParser() {
		return (LotterySettingConfigParser) ConfigParserMapping.getByGroup(getLotterySettingConfigGroup());
	}
	
	public String handle() {
		logger.info("进入彩种列表查询");
		return "list";
	}
	
	//显示奖项配置信息
	public String config() {
		logger.info("进入获取彩票类型配置");
		if (lotteryTypeId == null) {
			logger.error("配置的彩票类型不存在");
			super.setErrorMessage("要配置的彩票类型不存在");
			return "failure";
		}
		lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("配置的彩票类型不存在,lotteryTypeId={}", lotteryTypeId);
			super.setErrorMessage("要配置的彩票类型不存在,lotteryTypeId=" + lotteryTypeId);
			return "failure";
		}

		LotterySettingConfigParser parser = getLotterySettingConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, ", getLotterySettingConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getLotterySettingConfigGroup());
			return "failure";
		}

		try {
			lotterySettingItem = configService.getConfigItem(getLotterySettingConfigGroup(), parser.getLotteryTypeItemKey(lotteryType), LotterySettingConfigItem.class);
		} catch (Exception e) {
			logger.error("获取奖级配置异常");
			logger.error(e.getMessage(), e);
			super.setErrorMessage("获取奖级配置异常，" + e.getMessage());
			return "failure";
		}
		return "config";
	}
	
	//更新奖项配置
	public String updateConfig() {
		logger.info("进入配置彩票类型");
		if (lotteryTypeId == null) {
			logger.error("要配置的彩票类型不存在");
			setErrorMessage("要配置的彩票类型不存在");
			return "failure";
		}
		lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("配置的彩票类型不存在,lotteryTypeId={}", lotteryTypeId);
			super.setErrorMessage("要配置的彩票类型不存在,lotteryTypeId=" + lotteryTypeId);
			return "failure";
		}
		if (lotterySettingItem == null) {
			logger.error("获取[{}]彩票配置表单数据失败！", lotteryType.getName());
			super.setErrorMessage("获取[" + lotteryType.getName() + "]彩票配置表单数据失败！");
			return "failure";
		}
		LotterySettingConfigParser parser = getLotterySettingConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, ", getLotterySettingConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getLotterySettingConfigGroup());
			return "failure";
		}
		
		logger.info("更新彩票配置：对结果配置去空");
		List<LotteryResultItem> tmpLotteryResultItemList = lotterySettingItem.getLotteryResultItemList();
		List<LotteryResultItem> lotteryResultItemList = new ArrayList<LotteryResultItem>();
		if (tmpLotteryResultItemList != null && tmpLotteryResultItemList.size() != 0) {
			for (LotteryResultItem lotteryResultItem : tmpLotteryResultItemList) {
				if (lotteryResultItem != null) {
					if (lotteryResultItem.getResultRegionTypeValue() == ResultRegionType.DIGITAL_SECTION.getValue()) {
						lotteryResultItem.setDesignatedSection(null);
					} else {
						lotteryResultItem.setMinDigit(null);
						lotteryResultItem.setMaxDigit(null);
						lotteryResultItem.setDigitCapacity(null);
					}
					lotteryResultItemList.add(lotteryResultItem);
				}
			}
		}
		lotterySettingItem.setLotteryResultItemList(lotteryResultItemList);
		
		logger.info("更新彩票配置：对奖项配置去空");
		List<LotteryPrizeItem> tmpLotteryPrizeItemList = lotterySettingItem.getLotteryPrizeItemList();
		List<LotteryPrizeItem> lotteryPrizeItemList = new ArrayList<LotteryPrizeItem>();
		if (tmpLotteryPrizeItemList != null && tmpLotteryPrizeItemList.size() != 0) {
			for (LotteryPrizeItem lotteryPrizeItem : tmpLotteryPrizeItemList) {
				if (lotteryPrizeItem != null) {
					lotteryPrizeItemList.add(lotteryPrizeItem);
				}
			}
		}
		lotterySettingItem.setLotteryPrizeItemList(lotteryPrizeItemList);
		
		boolean updateResult = false;
		try {
			updateResult = configService.updateConfigItem(lotterySettingItem, getLotterySettingConfigGroup(), parser.getLotteryTypeItemKey(lotteryType));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		if (updateResult) {
			logger.info("更新[{}]彩票配置成功", lotteryType.getName());
			super.setSuccessMessage("更新[" + lotteryType.getName() + "]彩票配置成功");
			super.setForwardUrl("/lottery/lotteryConfig.do");
			return "success";
		} else {
			logger.error("更新[{}]彩票配置失败", lotteryType.getName());
			super.setErrorMessage("更新[" + lotteryType.getName() + "]彩票配置失败");
			return "failure";
		}
	}
	
	public List<LotteryType> getLotteryTypeList() {
		return LotteryType.getItems();
	}
	
	public List<EnabledStatus> getEnabledStatusList() {
		return EnabledStatus.getItems();
	}
	
	public List<YesNoStatus> getYesNoStatusList() {
		return YesNoStatus.getItems();
	}
	
	public List<ResultRegionType> getResultRegionTypeList() {
		return ResultRegionType.getItems();
	}
	
	public ResultRegionType getDigitResultRegionType() {
		return ResultRegionType.DIGITAL_SECTION;
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

	public LotterySettingConfigItem getLotterySettingItem() {
		return lotterySettingItem;
	}

	public void setLotterySettingItem(LotterySettingConfigItem lotterySettingItem) {
		this.lotterySettingItem = lotterySettingItem;
	}

	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	
}
