package web.action.lottery;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.opensymphony.xwork2.Action;


/**福彩3D试机号录入
 * 2011-4-1
 */
public class Fc3dsjhAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(LotteryHmSetTopAction.class);
	
	private static final Integer lotteryTypeValue = LotteryType.FC3D.getValue();
	
	private PhaseService phaseService;
	
	private String phaseNo;
	private Phase phase;
	private String phaseNoText;
	private List<String> fc3dsjh;
	private String machine;
	private String ball;
	
	private Integer fetcherTypeValue;
	
	public String handle() {
		logger.info("进入福彩3D试机号录入");
		return "list";
	}
	
	public String defaultView() {
		logger.info("进入获取彩期数据");
		if (phaseNoText != null && !"".equals(phaseNoText)) {
			phaseNo = phaseNoText;
		}
		
		JSONObject rs = new JSONObject();
		HttpServletResponse response = ServletActionContext.getResponse();
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		
		try {
			phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(PhaseType.getItem(lotteryType), phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", "获取Phase失败");
			return null;
		}
		rs.put("state", "success");
		rs.put("msg", "获取成功");
		
		rs.put("phase", Phase.toEditJSON(phase));
		writeRs(response, rs);
		logger.info("获取彩期数据结束");
		return Action.NONE;
	}
	
	//更新试机号
	public String update() {
		logger.info("进入更新福彩3D试机号");
		if (phaseNoText != null && !"".equals(phaseNoText)) {
			phaseNo = phaseNoText;
		}
		
		JSONObject rs = new JSONObject();
		HttpServletResponse response = ServletActionContext.getResponse();
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		
		try {
			phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(PhaseType.getItem(lotteryType), phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", "更新失败");
			return null;
		}
		if (fc3dsjh != null && !"".equals(fc3dsjh)) {
				
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("phase", phaseNo);
			jsonObject.put("machine", machine);
			jsonObject.put("ball", ball);
			jsonObject.put("sjh", fc3dsjh);
			
			JSONObject jsonObjectTmp = new JSONObject();
			jsonObjectTmp.put("fc3d_sjh", jsonObject);
				
			phase.setFc3dSjh(jsonObjectTmp.toString());
				
			try {
				phaseService.updatePhase(phase);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				rs.put("state", "failed");
				rs.put("msg", "更新失败");
				return null;
			}
			rs.put("state", "success");
			rs.put("msg", "更新成功");
			logger.info("更新成功");
		}else{
			logger.error("更新失败,请正确填写试机号");
			setErrorMessage("更新失败,请正确填写试机号");
			rs.put("state", "failed");
			rs.put("msg", "更新失败,请正确填写试机号");
			return null;
		}
		writeRs(response, rs);
		logger.info("更新福彩3D试机号结束");
		return null;
	}
	
	//抓取福彩3D试机号
	public String fetch() {
		logger.info("进入抓取福彩3D试机号");
		if(phaseNoText != null && !"".equals(phaseNoText)){
			phaseNo = phaseNoText;
		}
		JSONObject rs = new JSONObject();
		HttpServletResponse response = ServletActionContext.getResponse();
		
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		FetcherType fetcherType = FetcherType.getItem(fetcherTypeValue);
		try {
			phase = phaseService.fetchFC3DSJH(lotteryType, fetcherType, phaseNo);
			if(phase == null){
				rs.put("state", "failed");
				rs.put("msg", "抓取失败,抓取彩期对象为null");
				logger.error("抓取失败,抓取彩期对象为null");
			}else if(phase.getFc3dSjh() == null || phase.getFc3dSjh().trim().length() == 0){
				rs.put("state", "failed");
				rs.put("msg", "抓取失败,抓取试机号为空");
				logger.error("抓取失败,抓取试机号为空");
			}else{
				rs.put("state", "success");
				rs.put("msg", "抓取成功");
				rs.put("phase", Phase.toEditJSON(phase));
				machine = ((String [])phase.getExt().split(","))[0];
				ball = ((String [])phase.getExt().split(","))[1];
				rs.put("machine", machine);
				rs.put("ball", ball);
				logger.info("抓取成功");
			}
		} catch (Exception e) {
			logger.error("抓取福彩3D试机号异常，{}",e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
		}
		writeRs(response, rs);
		logger.info("抓取福彩3D试机号结束");
		return null;	
	}
	
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}
	public String getPhaseNo() {
		return phaseNo;
	}
	public void setPhaseNo(String phaseNo) {
		this.phaseNo = phaseNo;
	}
	public Phase getPhase() {
		return phase;
	}
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	public Integer getFetcherTypeValue() {
		return fetcherTypeValue;
	}
	public void setFetcherTypeValue(Integer fetcherTypeValue) {
		this.fetcherTypeValue = fetcherTypeValue;
	}
	public List<FetcherType> getFetcherTypeList() {
		return FetcherType.getItems();
	}
	public String getPhaseNoText() {
		return phaseNoText;
	}
	public void setPhaseNoText(String phaseNoText) {
		this.phaseNoText = phaseNoText;
	}
	public String getMachine() {
		return machine;
	}
	public void setMachine(String machine) {
		this.machine = machine;
	}
	public String getBall() {
		return ball;
	}
	public void setBall(String ball) {
		this.ball = ball;
	}
	public void setFc3dsjh(ArrayList<String> fc3dsjh) {
		this.fc3dsjh = fc3dsjh;
	}
	public LotteryType getFc3dLotteryType() {
		return LotteryType.FC3D;
	}
}
