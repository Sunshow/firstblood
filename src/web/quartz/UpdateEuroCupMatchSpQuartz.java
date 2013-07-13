package web.quartz;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.event.EuroCupService;
import com.lehecai.core.api.event.EuroCupMatch;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class UpdateEuroCupMatchSpQuartz {
	private final Logger logger = LoggerFactory.getLogger(UpdateEuroCupMatchSpQuartz.class);
	
	private EuroCupService euroCupService;
	
	@SuppressWarnings("unchecked")
	public void run(){
		logger.info("定时抓取欧洲杯赛程SP开始");
		Map<String, Object> matchMap = null;
		try {
			matchMap = euroCupService.findMatchList(null);
		} catch (ApiRemoteCallFailedException e) {
			logger.info("定时抓取欧洲杯赛程SP失败，获取赛程信息出错");
		}
		if (matchMap != null) {
			List<EuroCupMatch> euroCupMatchList = (List<EuroCupMatch>)matchMap.get(Global.API_MAP_KEY_LIST);
			for(EuroCupMatch ecm : euroCupMatchList){
				//销售状态开启并且已获取SP值的场次可以定时抓取
				if (ecm.getStatus().intValue() == 1) {
					EuroCupMatch updateEcm = new EuroCupMatch();
					updateEcm.setMatchNum(ecm.getMatchNum());
					updateEcm.setUserId(new Long(9999));
					try {
						euroCupService.updateMatchSp(updateEcm);
					} catch (ApiRemoteCallFailedException e) {
						logger.info("定时抓取欧洲杯赛程SP失败，更新SP出错");
					}
				}
			}
		}
		logger.info("定时抓取欧洲杯赛程SP结束");
	}

	public EuroCupService getEuroCupService() {
		return euroCupService;
	}

	public void setEuroCupService(EuroCupService euroCupService) {
		this.euroCupService = euroCupService;
	}
}
