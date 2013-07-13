package web.action.lottery;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.LotteryTicketConfigService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;
/**
 * 
 * @author 唐容
 *
 */
public class LotteryTicketConfigAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private List<LotteryType> lotteryTypeList;
	
	private List<LotteryTicketConfig> lotteryTicketConfigList;
	
	private LotteryTicketConfig lotteryTicketConfig;
	
	private Integer LotteryTypeId;	//输入LotteryType的id
	
	private LotteryTicketConfigService lotteryTicketConfigService;
	
	public String handle() {
		logger.info("进入查询彩种出票信息");
		this.setLotteryTypeList(OnSaleLotteryList.get());
		this.lotteryTicketConfigList = this.lotteryTicketConfigService.get(lotteryTypeList);
		return "lotteryTicketConfigList";
	}

	public String list() {
		logger.info("进入查询彩种出票配置");
		LotteryType lotteryType = LotteryType.getItem(this.LotteryTypeId);
		this.lotteryTicketConfig = this.lotteryTicketConfigService.get(lotteryType);
		return "list";
	}
	
	public String modify() {
		logger.info("进入更新彩种出票配置");
		LotteryType lotteryType = LotteryType.getItem(this.LotteryTypeId);
		this.lotteryTicketConfig.setLotteryType(lotteryType);
		this.lotteryTicketConfigService.update(lotteryTicketConfig);
		
		//将属性同步更新到API Service
		try {
			this.lotteryTicketConfigService.updateSetting(lotteryTicketConfig);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		super.setForwardUrl("/lottery/lotteryTicketConfig.do");
		logger.info("更新彩种出票配置结束");
		return "success";
	}
	
	public String delete() {
		logger.info("进入删除彩种出票配置");
		LotteryType lotteryType = LotteryType.getItem(this.LotteryTypeId);
		this.lotteryTicketConfigService.delete(lotteryType);
		
		//更新ApiSetting
		this.lotteryTicketConfig = new LotteryTicketConfig();
		this.lotteryTicketConfig.setLotteryType(lotteryType);
		this.lotteryTicketConfig.setBeginSaleForward(0L);
		this.lotteryTicketConfig.setEndSaleForward(0L);
		this.lotteryTicketConfig.setHmTerminateForward(0L);
		this.lotteryTicketConfig.setUploadTerminateForward(0L);
		try {
			this.lotteryTicketConfigService.updateSetting(this.lotteryTicketConfig);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		super.setForwardUrl("/lottery/lotteryTicketConfig.do");
		logger.info("删除彩种出票配置结束");
		return "success";
	}
	
	public List<LotteryType> getLotteryTypeList() {
		return lotteryTypeList;
	}

	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}

	public Integer getLotteryTypeId() {
		return LotteryTypeId;
	}

	public void setLotteryTypeId(Integer lotteryTypeId) {
		LotteryTypeId = lotteryTypeId;
	}

	public LotteryTicketConfigService getLotteryTicketConfigService() {
		return lotteryTicketConfigService;
	}

	public void setLotteryTicketConfigService(
			LotteryTicketConfigService lotteryTicketConfigService) {
		this.lotteryTicketConfigService = lotteryTicketConfigService;
	}

	public void setLotteryTicketConfigList(List<LotteryTicketConfig> lotteryTicketConfigList) {
		this.lotteryTicketConfigList = lotteryTicketConfigList;
	}

	public List<LotteryTicketConfig> getLotteryTicketConfigList() {
		return lotteryTicketConfigList;
	}

	public void setLotteryTicketConfig(LotteryTicketConfig lotteryTicketConfig) {
		this.lotteryTicketConfig = lotteryTicketConfig;
	}

	public LotteryTicketConfig getLotteryTicketConfig() {
		return lotteryTicketConfig;
	}
	
}
