package web.action.business;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.BIService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;
import com.lehecai.core.util.CoreDateUtils;

/**
 * 江西时时彩彩期同步
 * @author yanweijie
 *
 */
public class JxsscPhaseSynchronizeAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private HttpServletRequest request;
	private PhaseService phaseService;
	private List<Phase> phases;
	
	private String phaseStr;				//被修改彩期号
	private String startSaleTimeStr;		//被修改开始销售时间
	private String endSaleTimeStr;			//被修改结束销售时间
	private String endTicketTimeStr;		//被修改停止出票时间
	private String drawTimeStr;				//被修改开奖时间
	
	//查询参数区
	private Integer lotteryTypeValue = LotteryType.JXSSC.getValue();//江西时时彩
	
	/**
	 * 查询江西时时彩在售的彩期
	 * @return
	 */
	public String handle() {
		logger.info("进入查询江西时时彩在售的彩期列表");
		PhaseType phaseType = null;
		if (lotteryTypeValue == null || lotteryTypeValue == 0) {
			logger.error("查询彩期的彩种类型为空");
			super.setErrorMessage("查询彩期的彩种类型为空");
			return "failure";
		}
		phaseType = PhaseType.getItem(LotteryType.getItem(lotteryTypeValue));	//彩种类型
		try {
			phases = phaseService.findVenderOnSalePhases(phaseType, BIService.JXFC_Phase_Synchronous);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询江西时时彩在售的彩期列表，api调用异常，{}",e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		if (phases == null || phases.size() == 0) {
			logger.info("江西时时彩在售的彩期列表为空");
		}
		
		return "list";
	}
	
	/**
	 * 批量修改
	 * @return
	 */
	public String batchUpdate() {
		logger.info("进入批量修改江西时时彩彩期");
		if (lotteryTypeValue == null || lotteryTypeValue == 0) {
			logger.error("彩种类型为空");
			super.setErrorMessage("彩种类型不能为空");
			return "failure";
		}
		if ((phaseStr == null || phaseStr.equals("")) || (startSaleTimeStr == null || startSaleTimeStr.equals("")) 
				|| (endSaleTimeStr == null || endSaleTimeStr.equals("")) || (drawTimeStr == null || drawTimeStr.equals(""))) {
			logger.error("修改数据为空");
			super.setErrorMessage("修改数据不能为空");
			return "failure";
		}
		PhaseType phaseType = PhaseType.getItem(LotteryType.getItem(lotteryTypeValue));
		JSONArray array = new JSONArray();
		ResultBean resultBean = null;
		
		String[] phaseList = phaseStr.split(",");					//拆分被修改彩期号
		String[] startSaleTimeList = startSaleTimeStr.split(",");	//拆分被修改开始销售时间
		String[] endSaleTimeList = endSaleTimeStr.split(",");		//拆分被修改结束销售时间
		String[] endTicketTimeList = endTicketTimeStr.split(",");	//拆分被修改出票时间
		String[] drawTimeList = drawTimeStr.split(",");				//拆分被修改开奖时间
		for (int i = 0;i < phaseList.length ;i ++) {
			Phase phase = new Phase();
			phase.setPhaseType(phaseType);
			phase.setPhase(phaseList[i]);
			phase.setStartSaleTime(CoreDateUtils.parseDate(startSaleTimeList[i],CoreDateUtils.DATETIME));
			phase.setEndSaleTime(CoreDateUtils.parseDate(endSaleTimeList[i],CoreDateUtils.DATETIME));
			phase.setEndTicketTime(CoreDateUtils.parseDate(endTicketTimeList[i],CoreDateUtils.DATETIME));
			phase.setDrawTime(CoreDateUtils.parseDate(drawTimeList[i],CoreDateUtils.DATETIME));
			
			try {
				resultBean = phaseService.updatePhase(phase);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("更新彩期，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
			
			JSONObject rs = new JSONObject();
			if (resultBean.getCode() != ApiConstant.RC_SUCCESS) {
				rs.put("code", ApiConstant.RC_FAILURE);
			} else {
				rs.put("code", ApiConstant.RC_SUCCESS);
				rs.put(Phase.JSON_KEY_PHASE, phase.getPhase());																			//彩期
				rs.put(Phase.JSON_KEY_STARTSALETIME, CoreDateUtils.formatDate(phase.getStartSaleTime(),CoreDateUtils.DATETIME));		//开始销售时间
				rs.put(Phase.JSON_KEY_ENDSALETIME, CoreDateUtils.formatDate(phase.getEndSaleTime(), CoreDateUtils.DATETIME));			//结束销售时间
				rs.put(Phase.JSON_KEY_ENDTICKETTIME, CoreDateUtils.formatDate(phase.getEndTicketTime(), CoreDateUtils.DATETIME));		//出票时间
				rs.put(Phase.JSON_KEY_DRAWTIME, CoreDateUtils.formatDate(phase.getDrawTime(), CoreDateUtils.DATETIME));					//开奖时间
				
				array.add(rs);
			}
		}
		
		super.writeRs(ServletActionContext.getResponse(), array);
		
		return null;
	}
	
	public List<LotteryType> getLotteryTypeList() {
		return OnSaleLotteryList.get();
	}
	public List<PhaseStatus> getPhaseStatusList() {
		return PhaseStatus.getItemsForQuery();
	}
	public List<YesNoStatus> getYesNoStatusList() {
		return YesNoStatus.getItemsForQuery();
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public List<Phase> getPhases() {
		return phases;
	}
	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}
	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}
	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public String getPhaseStr() {
		return phaseStr;
	}

	public void setPhaseStr(String phaseStr) {
		this.phaseStr = phaseStr;
	}

	public String getStartSaleTimeStr() {
		return startSaleTimeStr;
	}

	public void setStartSaleTimeStr(String startSaleTimeStr) {
		this.startSaleTimeStr = startSaleTimeStr;
	}

	public String getEndSaleTimeStr() {
		return endSaleTimeStr;
	}

	public void setEndSaleTimeStr(String endSaleTimeStr) {
		this.endSaleTimeStr = endSaleTimeStr;
	}

	public String getEndTicketTimeStr() {
		return endTicketTimeStr;
	}

	public void setEndTicketTimeStr(String endTicketTimeStr) {
		this.endTicketTimeStr = endTicketTimeStr;
	}

	public String getDrawTimeStr() {
		return drawTimeStr;
	}

	public void setDrawTimeStr(String drawTimeStr) {
		this.drawTimeStr = drawTimeStr;
	}
	
}
