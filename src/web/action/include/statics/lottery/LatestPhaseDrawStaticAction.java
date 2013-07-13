package web.action.include.statics.lottery;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.FileUtil;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;

public class LatestPhaseDrawStaticAction extends BaseAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	protected static Object __lock__ = new Object();
	
	private PhaseService phaseService;
	
	private int lotteryTypeId;
	private String staticDir; 
	
	public void handle() {
		logger.info("开始获取最新状态为开奖的彩期");
		PhaseType type = PhaseType.getItem(LotteryType.getItem(this.lotteryTypeId));
		List<Phase> phases = null;
		try {
			phases = this.phaseService.getLatestDrawedPhase(type,1);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("远程调用出现异常",e.getMessage());
		}
		String jsonStr = null;
		JSONObject obj = null;
		JSONObject drawObj = new JSONObject();
		if (phases == null || phases.size() == 0) {
			return;
		}
		Phase phase = phases.get(0);
		if (phase.getResult() != null && !"".equals(phase.getResult()) && !phase.getResult().equals("null") ) {
			jsonStr = phase.getResult();
			obj = JSONObject.fromObject(jsonStr);
			jsonStr = obj.getString("result");
		} else {
			jsonStr = new JSONObject().toString();
		}
		drawObj.put("result", jsonStr);
		drawObj.put("phase", phase.getPhase());
		drawObj.put("lottery_type", this.lotteryTypeId);
		String drawDate = "";
		Date drawTime = phase.getDrawTime();//开奖日期
		if (drawTime != null) {
			try {
				drawDate = DateUtil.formatDate(drawTime, "yyyy-MM-dd HH:mm:ss");
			} catch (Exception e) {
				logger.info("格式化开奖日期异常");
			}
		}
		drawObj.put("draw_time",drawDate);
		drawObj.put("pool_amount", phase.getPoolAmount());
		drawObj.put("sale_amount", phase.getSaleAmount());
		String resultDetailStr = null;
		if (phase.getResultDetail() != null && !"".equals(phase.getResultDetail()) && !phase.getResultDetail().equals("null") ) {
			resultDetailStr = JSONObject.fromObject(phase.getResultDetail()).getString("resultDetail");
		} else {
			resultDetailStr = new JSONObject().toString();
		}
		drawObj.put("result_detail", resultDetailStr);
		
		if (lotteryTypeId == LotteryType.FC3D.getValue()) {
			String sjh = phase.getFc3dSjh();
			Phase nextPhase = null;
			try {
				nextPhase = phaseService.getNextPhase(type, phase.getPhase());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
			}
			if (nextPhase != null && nextPhase.getFc3dSjh() != null && !"".equals(nextPhase.getFc3dSjh())) {
				sjh = nextPhase.getFc3dSjh();
			}
			drawObj.put("fc3d_sjh", sjh);
		}
		
		String webRoot = "";
		try {
			webRoot = WebUtils.getRealPath(ServletActionContext.getServletContext(), "");
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		
		FileUtil.mkdir(webRoot + this.staticDir);
		String filePath = webRoot + this.staticDir + type.getValue()+".json";
		filePath = filePath.replace('/', File.separatorChar);
		logger.info(filePath);
		synchronized (__lock__) {
			FileUtil.write(filePath, drawObj.toString());
		}
		logger.info("结束获取最新状态为开奖的彩期");
		return ;
	}
	
	public static Object getLock() {
		return __lock__;
	}

	public String getStaticDir() {
		return staticDir;
	}
	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}

	public int getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(int lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public PhaseService getPhaseService() {
		return phaseService;
	}
}
