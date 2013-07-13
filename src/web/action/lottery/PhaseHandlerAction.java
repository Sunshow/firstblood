package web.action.lottery;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

public class PhaseHandlerAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	
	private Integer lotteryTypeId;
	
	private PhaseService phaseService;
	
	public String handle() {
		logger.info("进入远程调用彩期守护重载");
		return "phaseHandler";
	}
	
	protected JSONObject invokeRemoteEngineCall(Integer lotteryTypeId, String processCode) {
		JSONObject rs = new JSONObject();
		
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeId);
		if (lotteryType == null) {
			logger.error("彩种编号非法, {}", lotteryTypeId);
			rs.put("code", 1);
			rs.put("data", "彩种编号非法");
			
			return rs;
		}
		
		try {
			ApiResponse apiResponse = phaseService.invokePhaseHandlerRemoteCall(lotteryType, processCode);
			rs.put("code", apiResponse.getCode());
			rs.put("data", apiResponse.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs.put("code", 1);
			rs.put("data", "远程请求engine异常");
		}
		
		return rs;
	}
	
	public String executeReload() {
		logger.info("进入远程调用彩期守护重载");
		
		HttpServletResponse response = ServletActionContext.getResponse();
		
		JSONObject rs = this.invokeRemoteEngineCall(lotteryTypeId, "100001");
		writeRs(response, rs);
		return null;
	}
	
	public String executeQueryStatus() {
		logger.info("进入远程调用彩期守护状态查询");
		
		HttpServletResponse response = ServletActionContext.getResponse();
		
		JSONObject rs = this.invokeRemoteEngineCall(lotteryTypeId, "100002");
		writeRs(response, rs);
		return null;
	}
	
	public String executePause() {
		logger.info("进入远程调用彩期守护暂停");
		
		HttpServletResponse response = ServletActionContext.getResponse();
		
		JSONObject rs = this.invokeRemoteEngineCall(lotteryTypeId, "100003");
		writeRs(response, rs);
		return null;
	}
	
	public String executeResume() {
		logger.info("进入远程调用彩期守护恢复");
		
		HttpServletResponse response = ServletActionContext.getResponse();
		
		JSONObject rs = this.invokeRemoteEngineCall(lotteryTypeId, "100004");
		writeRs(response, rs);
		return null;
	}
	
	public String executeTaskClear() {
		logger.info("进入远程调用彩期守护清空任务");
		
		HttpServletResponse response = ServletActionContext.getResponse();
		
		JSONObject rs = this.invokeRemoteEngineCall(lotteryTypeId, "100005");
		writeRs(response, rs);
		return null;
	}
	
	public List<LotteryType> getLotteryTypes() {
		return OnSaleLotteryList.getForQuery();
	}
	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
}
