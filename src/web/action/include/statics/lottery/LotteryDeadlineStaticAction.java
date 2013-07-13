package web.action.include.statics.lottery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.LotteryTicketConfigService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.engine.entity.lottery.LotteryTicketConfig;
import com.opensymphony.xwork2.Action;

public class LotteryDeadlineStaticAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private String staticDir; 
	
	private PhaseService phaseService;
	
	private LotteryTicketConfigService lotteryTicketConfigService;
	
	public String handle() {
		logger.info("开始获取新闻json数据");
		JSONObject jsonObj = new JSONObject();
		
		Date today = new Date();		//今天	
		jsonObj.put("today", this.getPhaseList(today));
		
		Date tomorrow = DateUtil.add(today, 1);
		jsonObj.put("tomorrow", this.getPhaseList(tomorrow));
		
		logger.info(jsonObj.toString());
		
		super.writeRs(ServletActionContext.getResponse(), jsonObj);
		
		logger.info("结束获取新闻json数据");
		return Action.NONE;
	}

	private JSONArray getPhaseList(Date deadline) {
		logger.info("开始获取截止日期为指定日期的彩期列表json数据");
		List<Phase> phaseList = null;
		JSONArray jsonArr = new JSONArray();
		
		try {
			phaseList = this.phaseService.findPhaseListByDeadline(deadline, OnSaleLotteryList.getGeneral());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("远程调用失败",e.getMessage());
			return jsonArr;
		}
		
		if( phaseList == null || phaseList.size() == 0){
			return jsonArr;
		}
		
		List<LotteryTicketConfig> configs = lotteryTicketConfigService.get(OnSaleLotteryList.getGeneral());
		for (LotteryTicketConfig config : configs) {
			JSONObject jsonObj = null;
			for( Phase phase : phaseList ){
				if (phase.getPhaseType().getValue() == PhaseType.getItem(config.getLotteryType()).getValue()) {
					jsonObj = new JSONObject();
					jsonObj.put("lottery_type", config.getLotteryType().getValue());
					jsonObj.put("lottery_name", config.getLotteryType().getName());
					jsonObj.put("phase", phase.getPhase());
					if (phase.getEndSaleTime() == null) {
						jsonObj.put("deadline", "");
					} else {
						Calendar cd = Calendar.getInstance();
						cd.setTime(phase.getEndSaleTime());
						cd.add(Calendar.MILLISECOND, (config.getEndSaleForward() == null ? 0 : -config.getEndSaleForward().intValue()));
						jsonObj.put("deadline", DateUtil.formatDate(cd.getTime(), "HH:mm"));
					}
					jsonArr.add(jsonObj);
					break;
				}
			}
		}
		logger.info("结束获取截止日期为指定日期的彩期列表json数据");
		return jsonArr;
	}
	
	
	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public LotteryTicketConfigService getLotteryTicketConfigService() {
		return lotteryTicketConfigService;
	}

	public void setLotteryTicketConfigService(
			LotteryTicketConfigService lotteryTicketConfigService) {
		this.lotteryTicketConfigService = lotteryTicketConfigService;
	}
}
