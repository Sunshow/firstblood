package web.service.impl.lottery;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.dao.lottery.LotteryTicketConfigDAO;
import com.lehecai.admin.web.service.lottery.LotteryTicketConfigService;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.service.setting.SettingService;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;

public class LotteryTicketConfigServiceImpl implements LotteryTicketConfigService {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private LotteryTicketConfigDAO lotteryTicketConfigDAO;
	
	private SettingService settingService;

	
	@Override
	public LotteryTicketConfig get(LotteryType lotteryType) {
		LotteryTicketConfig config = this.lotteryTicketConfigDAO.get(lotteryType);
		if( config == null){
			config = new LotteryTicketConfig();
			config.setLotteryType(lotteryType);
		}
		return config;
	}

	public LotteryTicketConfigDAO getLotteryTicketConfigDAO() {
		return lotteryTicketConfigDAO;
	}

	public void setLotteryTicketConfigDAO(
			LotteryTicketConfigDAO lotteryTicketConfigDAO) {
		this.lotteryTicketConfigDAO = lotteryTicketConfigDAO;
	}

	@Override
	public void update(LotteryTicketConfig lotteryTicketConfig) {
		this.lotteryTicketConfigDAO.update(lotteryTicketConfig);
	}

	@Override
	public void delete(LotteryTicketConfig lotteryTicketConfig) {
		if( lotteryTicketConfig.getId() != null ){
			this.lotteryTicketConfigDAO.delete(lotteryTicketConfig);
		}
	}

	@Override
	public List<LotteryTicketConfig> get(List<LotteryType> lotteryTypeList) {
		List<LotteryTicketConfig> lotteryTicketConfigList = new ArrayList<LotteryTicketConfig>();
		for(LotteryType lotteryType : lotteryTypeList){
			LotteryTicketConfig config = this.get(lotteryType);
			lotteryTicketConfigList.add(config);
		}
		return lotteryTicketConfigList;
	}

	@Override
	public void delete(LotteryType lotteryType) {
		this.lotteryTicketConfigDAO.delete(lotteryType);
	}

	@Override
	public void updateSetting(LotteryTicketConfig lotteryTicketConfig) throws ApiRemoteCallFailedException {
		String group = SettingConstant.GROUP_LOTTERY_TICKET_CONFIG;
		String item = String.valueOf(lotteryTicketConfig.getLotteryType().getValue());
		settingService.add(group, item);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("hm_terminate_forward", lotteryTicketConfig.getHmTerminateForward());
		jsonObj.put("upload_terminate_forward", lotteryTicketConfig.getUploadTerminateForward());
		jsonObj.put("begin_sale_forward", lotteryTicketConfig.getBeginSaleForward());	//开售提前时间
		jsonObj.put("end_sale_forward", lotteryTicketConfig.getEndSaleForward());		//销售提前截止时间(毫秒)
		jsonObj.put("draw_backward", lotteryTicketConfig.getDrawBackward());			//开奖延后时间
		logger.info("更新SettingService：group=" + group + ",item=" + item + ", value = " + jsonObj.toString());
		settingService.update(group, item, jsonObj.toString());
		logger.info(settingService.get(group, item));
		
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(SettingService settingService) {
		this.settingService = settingService;
	}
}
