package web.action.statics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.service.lottery.LotteryCommonService;
/**
 * 彩票配置缓存管理
 * @author leiming
 *
 */
public class LotteryConfigCacheAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private LotteryCommonService lotteryCommonService;
	
	public String handle(){
		logger.info("进入查询彩票配置缓存列表");
		return "list";
	}
	
	public String initCache() {
		logger.info("进入更新彩票配置缓存");
		
		lotteryCommonService.removeAllLotteryConfigCache();
		
		logger.info("更新彩票配置缓存成功");
		super.setSuccessMessage("更新彩票配置缓存成功");
		super.setForwardUrl("/statics/lotteryConfigCache.do");
		return "success";
	}

	public LotteryCommonService getLotteryCommonService() {
		return lotteryCommonService;
	}

	public void setLotteryCommonService(LotteryCommonService lotteryCommonService) {
		this.lotteryCommonService = lotteryCommonService;
	}

	
}
