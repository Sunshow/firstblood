package web.multiconfirm.param;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lehecai.admin.web.multiconfirm.MulticonfirmConfig;
import com.lehecai.admin.web.multiconfirm.MulticonfirmConstant;
import com.lehecai.core.api.lottery.LotteryConfig;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.validator.LotteryDrawResultValidator;
import com.lehecai.core.service.lottery.LotteryCommonService;
import com.lehecai.core.util.lottery.FetcherLotteryDrawConverter;

public class DrawResultParams extends AbstractMulticonfirmParam {

	private LotteryCommonService lotteryCommonService;
	
	public LotteryCommonService getLotteryCommonService() {
		return lotteryCommonService;
	}

	public void setLotteryCommonService(LotteryCommonService lotteryCommonService) {
		this.lotteryCommonService = lotteryCommonService;
	}

	@Override
	public String getResult(Map<?, ?> map) {
		String result = "";
		if (map != null && map.size() > 0 && map.containsKey("phase.result")) {
			result = ((String[])map.get("phase.result"))[0];
			String lotteryTypeValue = ((String[]) map.get("lotteryTypeValue"))[0];
			try {
				Integer ltv = Integer.parseInt(lotteryTypeValue);
				JSONObject json = JSONObject.fromObject(result);
				JSONArray ja = json.getJSONArray(LotteryConfig.JSON_KEYNAME_RESULT);
				LotteryDrawResultValidator.validate(LotteryType.getItem(ltv), ja, lotteryCommonService.getLotteryConfigFromCache(LotteryType.getItem(ltv)));
			} catch (Exception e) {
				return null;
			}
			if (result != null && !result.equals("")) {
				result = FetcherLotteryDrawConverter.convertResultJsonString2String(result);
			}
		}
		return result;
	}

	@Override
	protected String getTaskKey(MulticonfirmConfig multiconfirmConfig, Map<?, ?> paramMap) {
		String key = "";
		key = key + multiconfirmConfig.getId();
		if (paramMap != null && paramMap.containsKey("lotteryTypeValue") && paramMap.containsKey("phaseNo")) {
			String[] lotteryTypeValueArray = (String[]) paramMap.get("lotteryTypeValue");
			key = key + MulticonfirmConstant.SPLIT_FLAG + lotteryTypeValueArray[0];
			String[] phaseNoArray = (String[]) paramMap.get("phaseNo");
			key = key + MulticonfirmConstant.SPLIT_FLAG + phaseNoArray[0];
		} else {
			return null;
		}
		return key;
		
	}

}
