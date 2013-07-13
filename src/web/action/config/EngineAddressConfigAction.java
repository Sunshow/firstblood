package web.action.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.ConfigParserMapping;
import com.lehecai.core.config.impl.engine.EngineAddressConfigItem;
import com.lehecai.core.config.impl.engine.EngineAddressConfigParser;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.service.config.ConfigService;

/**
 * engine调用地址配置
 * @author yanweijie
 *
 */
public class EngineAddressConfigAction extends BaseAction {
	private static final long serialVersionUID = -2225123800707608839L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ConfigService configService;
	
	private Integer lotteryTypeId;
	private LotteryType lotteryType;
	
	private EngineAddressConfigItem engineAddress;

	private static String getEngineAddressConfigGroup() {
		return SettingConstant.GROUP_ENGINE_ADDRESS_CONFIG;
	}
	
	private static EngineAddressConfigParser getEngineAddressConfigParser() {
		return (EngineAddressConfigParser) ConfigParserMapping.getByGroup(getEngineAddressConfigGroup());
	}
	
	public String handle() {
		logger.info("进入engine调用地址配置彩种列表");
		
		EngineAddressConfigParser parser = getEngineAddressConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getEngineAddressConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getEngineAddressConfigGroup());
			return "failure";
		}
		try {
			engineAddress = configService.getConfigItem(getEngineAddressConfigGroup(), parser.getDefaultItemKey(), EngineAddressConfigItem.class);
		} catch (Exception e) {
			logger.error("engine调用地址配置异常", e);
			super.setErrorMessage("engine调用地址配置异常，" + e.getMessage());
			return "failure";
		}
		if (engineAddress == null) {
			logger.info("engine调用地址配置为空");
		}
		return "list";
	}
	
	/**
	 * 输入engine调用地址彩种配置
	 */
	public String input() {
		logger.info("进入输入engine调用地址彩种配置");
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
		
		EngineAddressConfigParser parser = getEngineAddressConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, ", getEngineAddressConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getEngineAddressConfigGroup());
			return "failure";
		}
		
		try {
			engineAddress = configService.getConfigItem(getEngineAddressConfigGroup(), parser.getLotteryTypeItemKey(lotteryType), EngineAddressConfigItem.class);
		} catch (Exception e) {
			logger.error("engine调用地址配置异常", e);
			super.setErrorMessage("engine调用地址配置异常，" + e.getMessage());
			return "failure";
		}
		if (engineAddress == null) {
			logger.info("engien调用地址配置为空, lottery={}", lotteryType);
			
			engineAddress = new EngineAddressConfigItem();
			engineAddress.setUseDefault(true);
		}
		return "inputForm";
	}
	
	/**
	 * 更新engine调用地址默认及彩种配置
	 * @return
	 */
	public String update() {
		logger.info("进入更新engine调用地址默认及彩种配置");
		if (lotteryTypeId == null || lotteryTypeId == 0) {
			logger.error("彩种编码为空");
			super.setErrorMessage("彩种编码不能为空");
			return "failure";
		}
		
		lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("彩种编码非法, lotteryTypeId={}", lotteryTypeId);
			super.setErrorMessage("彩种编码非法");
			return "failure";
		}
		
		if ((lotteryType.getValue() == LotteryType.ALL.getValue()) 
				|| (lotteryType.getValue() != LotteryType.ALL.getValue() && !engineAddress.isUseDefault())) {	//如果不采用默认配置
			if (engineAddress == null || (engineAddress.getAddress() == null 
					|| engineAddress.getAddress().trim().equals(""))) {
				logger.error("engine调用地址配置为空");
				super.setErrorMessage("请填写engine调用地址");
				return "failure";
			}
		}
		
		EngineAddressConfigParser parser = getEngineAddressConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, {}", getEngineAddressConfigGroup());
			super.setErrorMessage("未配置对应的解析器, " + getEngineAddressConfigGroup());
			return "failure";
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
			updateResult = configService.updateConfigItem(engineAddress, getEngineAddressConfigGroup(), item);
		} catch (Exception e) {
			logger.error("更新engine调用地址配置失败", e);
			super.setErrorMessage("更新engine调用地址配置失败，" + e.getMessage());
			return "failure";
		}
		
		if (updateResult) {
			logger.info("更新engine调用地址配置成功");
			super.setSuccessMessage("更新engine调用地址配置成功");
			if (lotteryType.getValue() == LotteryType.ALL.getValue()) {
				super.setForwardUrl("/config/engineAddressConfig.do");
			} else {
				super.setForwardUrl("/config/engineAddressConfig.do?action=input&lotteryTypeId=" + lotteryType.getValue());
			}
			return "success";
		} else {
			logger.error("更新engine调用地址配置失败");
			super.setErrorMessage("更新engine调用地址配置失败");
			return "failure";
		}
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

	public EngineAddressConfigItem getEngineAddress() {
		return engineAddress;
	}

	public void setEngineAddress(EngineAddressConfigItem engineAddress) {
		this.engineAddress = engineAddress;
	}
}
