package web.action.business;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.Action;
import net.sf.json.JSONObject;
import org.apache.struts2.ServletActionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.business.GeneralLotteryRecalcService;

import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.message.EventType;


/**
 * 普通彩种过关统计重算Action
 * @author jinsheng
 */
public class GeneralLotteryRecalcAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(GeneralLotteryRecalcAction.class);
	
	private Integer lotteryTypeValue = LotteryType.ALL.getValue();	//彩种		
	private Integer eventTypeValue;		//事件 
	private String phaseNo;				//彩期
	private String phaseNoText;			
	private GeneralLotteryRecalcService generalLotteryRecalcService;
	
	public String handle() {
		logger.info("进入普通彩种过关统计重算页面");
		
		return "inputForm";
	}
	
	/**
	 * 普通彩种按彩期重算过关统计
	 * @return
	 */
	public String recalcByGeneralLotteryPhase() {
		logger.info("进入普通彩种按彩期重算过关统计");
		
		if (phaseNo == null) {
			logger.error("彩期数值为null");
			setErrorMessage("彩期数值为null");
			return "failure";
		}
		if (lotteryTypeValue == null) {
			logger.error("彩种类型数值为null");
			setErrorMessage("彩种类型数值为null");
			return "failure";
		}
		if (eventTypeValue == null) {
			logger.error("事件类型数值为null");
			setErrorMessage("事件类型数值为null");
			return "failure";
		}
		if (phaseNoText!=null&&!"".equals(phaseNoText)) {
			phaseNo = phaseNoText;
		}
		boolean recalcResult = false;
		try {
			recalcResult = generalLotteryRecalcService.recalcByGeneralLotteryPhase(lotteryTypeValue, phaseNo, eventTypeValue);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("普通彩种按彩期重算过关统计，API调用异常，{}", e.getMessage());
		}
		
		JSONObject rs = new JSONObject();
		if (recalcResult) {
			logger.info("{} 彩种的{} 期重算成功", LotteryType.getItem(lotteryTypeValue).getName(), phaseNo);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + phaseNo + "期重算成功");
		} else {
			logger.error("{} 彩种的{} 期重算失败", LotteryType.getItem(lotteryTypeValue).getName(), phaseNo);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的" + phaseNo + "期重算失败");
		}
		
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}
	
	/**
	 * 获取普通彩种列表
	 * @return
	 */
	public List<LotteryType> getGeneralLotteryTypeList() {
		return OnSaleLotteryList.getGeneral();
	}
	
	public List<EventType> getEventTypeList() {
		List<EventType> eventTypeList = new ArrayList<EventType>();
		eventTypeList.add(EventType.LOTTERY_PHASE_DRAW_FINISH);
		eventTypeList.add(EventType.LOTTERY_PHASE_PRIZE_COMPUTE_FINISH);
		
		return eventTypeList;
	}
	
	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public GeneralLotteryRecalcService getGeneralLotteryRecalcService() {
		return generalLotteryRecalcService;
	}

	public void setGeneralLotteryRecalcService(
			GeneralLotteryRecalcService generalLotteryRecalcService) {
		this.generalLotteryRecalcService = generalLotteryRecalcService;
	}

	public Integer getEventTypeValue() {
		return eventTypeValue;
	}

	public void setEventTypeValue(Integer eventTypeValue) {
		this.eventTypeValue = eventTypeValue;
	}
	
	public String getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}
	
	public String getPhaseNoText() {
		return phaseNoText;
	}

	public void setPhaseNoText(String phaseNoText) {
		this.phaseNoText = phaseNoText;
	}
}
