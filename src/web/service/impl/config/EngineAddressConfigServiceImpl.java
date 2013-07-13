/**
 * 
 */
package web.service.impl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.config.EngineAddressConfigService;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.config.ConfigParserMapping;
import com.lehecai.core.config.impl.engine.EngineAddressConfig;
import com.lehecai.core.config.impl.engine.EngineAddressConfigItem;
import com.lehecai.core.config.impl.engine.EngineAddressConfigParser;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.service.config.ConfigService;

/**
 * @author Sunshow
 *
 */
public class EngineAddressConfigServiceImpl implements
		EngineAddressConfigService {
	
	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private ConfigService configService;

	protected String getEngineAddressConfigGroup() {
		return SettingConstant.GROUP_ENGINE_ADDRESS_CONFIG;
	}
	
	protected EngineAddressConfigParser getEngineAddressConfigParser() {
		return (EngineAddressConfigParser) ConfigParserMapping.getByGroup(getEngineAddressConfigGroup());
	}
	
	protected EngineAddressConfig getConfig() throws Exception {
		return configService.getConfig(this.getEngineAddressConfigGroup(), EngineAddressConfig.class);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.config.EngineAddressConfigService#getDefaultAddress()
	 */
	@Override
	public String getDefaultAddress() throws Exception {
		EngineAddressConfigParser parser = getEngineAddressConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, ", getEngineAddressConfigGroup());
			throw new RuntimeException("未配置对应的解析器, " + getEngineAddressConfigGroup());
		}
		
		try {
			EngineAddressConfigItem engineAddress = configService.getConfigItem(getEngineAddressConfigGroup(), parser.getDefaultItemKey(), EngineAddressConfigItem.class);
			return engineAddress.getAddress();
		} catch (Exception e) {
			logger.error("engine调用地址配置异常", e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.config.EngineAddressConfigService#getLotteryAddress(com.lehecai.core.lottery.LotteryType)
	 */
	@Override
	public String getLotteryAddress(LotteryType lotteryType) throws Exception {
		EngineAddressConfigParser parser = getEngineAddressConfigParser();
		if (parser == null) {
			logger.error("未配置对应的解析器, ", getEngineAddressConfigGroup());
			throw new RuntimeException("未配置对应的解析器, " + getEngineAddressConfigGroup());
		}
		
		try {
			EngineAddressConfigItem engineAddress = configService.getConfigItem(getEngineAddressConfigGroup(), parser.getLotteryTypeItemKey(lotteryType), EngineAddressConfigItem.class);
			if (engineAddress == null) {
				return this.getDefaultAddress();
			}
			if (engineAddress.isUseDefault()) {
				return this.getDefaultAddress();
			}
			return engineAddress.getAddress();
		} catch (Exception e) {
			logger.error("engine调用地址配置异常", e);
			throw e;
		}
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

}
