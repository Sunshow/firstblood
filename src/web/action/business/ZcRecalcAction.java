package web.action.business;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.business.ZcRecalcService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.message.EventType;
import com.opensymphony.xwork2.Action;
/**
 * 过关统计重算Action
 * @author yanweijie
 *
 */
public class ZcRecalcAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(ZcRecalcAction.class);
	
	private ZcRecalcService zcRecalcService;
	
	private Integer lotteryTypeValue = LotteryType.ALL.getValue();	//彩种
	private String phase;				//彩期
	private String planId;				//方案编号
	private long expire; //停用时间，单位为秒
	private String item; //足彩和单场为彩期，竞彩足球为场次编号

	public String handle() {
		logger.info("进入过关统计重算页面");
		
		return "inputForm";
	}

	/**
	 * 按彩期重算过关统计
	 * @return
	 */
	public String reCalcByPhase() {
		logger.info("进入按彩期重算过关统计");
		
		boolean reCalcResult = false;			//重算结果
		try {
			reCalcResult = zcRecalcService.reCalcByPhase(lotteryTypeValue, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("按彩期重算过关统计，API调用异常，{}", e.getMessage());
		}
		
		JSONObject rs = new JSONObject();
		if (reCalcResult) {
			logger.info("{} 彩种的{} 期重算成功", LotteryType.getItem(lotteryTypeValue).getName(), phase);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + phase + "期重算成功");
		} else {
			logger.error("{} 彩种的{} 期重算失败", LotteryType.getItem(lotteryTypeValue).getName(), phase);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的" + phase + "期重算失败");
		}
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}
		
	/**
	 * 按方案编号重算过关统计
	 * @return
	 */
	public String reCalcByPlanId() {
		logger.info("按方案编号重算过关统计");
		
		boolean reCalcResult = false;
		try {
			reCalcResult = zcRecalcService.reCalcByPlanId(lotteryTypeValue, planId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("按方案编号重算过关统计，API调用异常，{}", e.getMessage());
		}
		
		JSONObject rs = new JSONObject();
		if (reCalcResult) {
			logger.info("{} 彩种的{} 方案重算成功", LotteryType.getItem(lotteryTypeValue).getName(), planId);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + planId + "方案重算成功");
		} else {
			logger.info("{} 彩种的{} 方案重算失败", LotteryType.getItem(lotteryTypeValue).getName(), planId);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + planId + "方案重算失败");
		}

		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}
	/**
	 * 按彩期同步过关统计
	 * @return
	 */
	public String syncByPhase() {
		logger.info("进入按彩期同步过关统计");
		
		boolean syncResult = false;			//同步结果
		try {
			syncResult = zcRecalcService.syncByPhase(lotteryTypeValue, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("按彩期同步过关统计，API调用异常，{}", e.getMessage());
		}
		
		JSONObject rs = new JSONObject();
		if (syncResult) {
			logger.info("{} 彩种的{} 期同步成功", LotteryType.getItem(lotteryTypeValue).getName(), phase);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + phase + "期同步成功");
		} else {
			logger.error("{} 彩种的{} 期同步失败", LotteryType.getItem(lotteryTypeValue).getName(), phase);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的" + phase + "期同步失败");
		}
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}
	/**
	 * 按彩期结束过关统计
	 * @return
	 */
	public String terminateByPhase() {
		logger.info("进入按彩期结束过关统计");
		
		boolean terminateResult = false;			//结果
		try {
			terminateResult = zcRecalcService.terminateByPhase(lotteryTypeValue, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("按彩期结束过关统计，API调用异常，{}", e.getMessage());
		}
		
		JSONObject rs = new JSONObject();
		if (terminateResult) {
			logger.info("{} 彩种的{} 期结束成功", LotteryType.getItem(lotteryTypeValue).getName(), phase);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + phase + "期结束成功");
		} else {
			logger.error("{} 彩种的{} 期结束失败", LotteryType.getItem(lotteryTypeValue).getName(), phase);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的" + phase + "期结束失败");
		}
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}
	
	
	/**
	 * 按方案编号同步过关统计
	 * @return
	 */
	public String syncByPlanId() {
		logger.info("按方案编号同步过关统计");
		
		boolean syncResult = false;
		try {
			syncResult = zcRecalcService.syncByPlanId(lotteryTypeValue, planId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("按方案编号同步过关统计，API调用异常，{}", e.getMessage());
		}
		
		JSONObject rs = new JSONObject();
		if (syncResult) {
			logger.info("{} 彩种的{} 方案同步成功", LotteryType.getItem(lotteryTypeValue).getName(), planId);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + planId + "方案同步成功");
		} else {
			logger.info("{} 彩种的{} 方案同步失败", LotteryType.getItem(lotteryTypeValue).getName(), planId);
			rs.put("msg", LotteryType.getItem(lotteryTypeValue).getName() + "彩种的 " + planId + "方案同步失败");
		}

		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}
	public String getUpdateStatus() {
		logger.info("过关统计获取结果更新状态");
		
		Integer status = null;
		try {
			status = zcRecalcService.getUpdateStatus();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("过关统计获取结果更新状态，API调用异常，{}", e.getMessage());
		}
		
		JSONObject rs = new JSONObject();
		if (status == null) {
			rs.put("code", 0);
			rs.put("msg", "过关统计获取结果更新状态失败");
		} else {
			rs.put("code", 1);
			rs.put("msg", "过关统计获取结果更新状态成功");
			rs.put("data", status);
		}

		super.writeRs(ServletActionContext.getResponse(), rs);
		return Action.NONE;
	}
	public String pauseResultUpdate() {
		logger.info("过关统计暂停结果更新 ");
		JSONObject rs = new JSONObject();
		
		boolean status = false;
		try {
			status = zcRecalcService.pauseResultUpdate(expire);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("过关统计暂停结果更新，API调用异常，{}", e.getMessage());
			rs.put("code", 0);
			rs.put("msg", "过关统计暂停结果更新 失败");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		rs.put("code", 1);
		rs.put("msg", "过关统计暂停结果更新 成功");
		rs.put("data", status);

		super.writeRs(ServletActionContext.getResponse(), rs);
		return Action.NONE;
	}
	public String resumeResultUpdate() {
		logger.info("过关统计恢复结果更新 ");
		JSONObject rs = new JSONObject();
		
		boolean status = false;
		try {
			status = zcRecalcService.resumeResultUpdate();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("过关统计恢复结果更新  ，API调用异常，{}", e.getMessage());
			rs.put("code", 0);
			rs.put("msg", "过关统计恢复结果更新 失败");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		rs.put("code", 1);
		rs.put("msg", "过关统计恢复结果更新  成功");
		rs.put("data", status);
		
		super.writeRs(ServletActionContext.getResponse(), rs);
		return Action.NONE;
	}
	public String removeResult() {
		logger.info("过关统计赛事结果删除 ");
		JSONObject rs = new JSONObject();
		
		if (lotteryTypeValue == null || lotteryTypeValue == LotteryType.ALL.getValue()) {
			rs.put("code", 0);
			rs.put("msg", "过关统计赛事结果删除 lotteryTypeValue不能为空");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		if (item == null || item.equals("")) {
			rs.put("code", 0);
			rs.put("msg", "过关统计赛事结果删除 item不能为空");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		String[] itemTmp = StringUtils.split(item, ",");
		
		List<String> list = new ArrayList<String>();
		for (String s : itemTmp) {
			list.add(s);
		}
		
		boolean status = false;
		try {
			status = zcRecalcService.removeResult(lotteryTypeValue, list);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("过关统计赛事结果删除  ，API调用异常，{}", e.getMessage());
			rs.put("code", 0);
			rs.put("msg", "过关统计赛事结果删除 失败");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		
		rs.put("code", 1);
		rs.put("msg", "过关统计赛事结果删除  成功");
		rs.put("data", status);
		
		super.writeRs(ServletActionContext.getResponse(), rs);
		return Action.NONE;
	}

	/**
	 * 获取彩种列表
	 * @return
	 */
	public List<LotteryType> getLotteryTypeList() {
		List<LotteryType> lotteryTypeList = new ArrayList<LotteryType>();
		lotteryTypeList.add(LotteryType.ALL);				//全部
		lotteryTypeList.add(LotteryType.SFC);				//胜负彩
		lotteryTypeList.add(LotteryType.SFR9);				//任九
		lotteryTypeList.add(LotteryType.JQC);				//进球彩
		lotteryTypeList.add(LotteryType.BQC);				//半全场
		
		lotteryTypeList.add(LotteryType.DC_SFP);			//单场胜负平
		lotteryTypeList.add(LotteryType.DC_SXDS);			//单场上下盘单双
		lotteryTypeList.add(LotteryType.DC_JQS);			//单场总进球数
		lotteryTypeList.add(LotteryType.DC_BF);				//单场比分
		lotteryTypeList.add(LotteryType.DC_BCSFP);			//单场半场胜负平
		
		lotteryTypeList.add(LotteryType.JCZQ_SPF);			//竞彩足球让球胜平负
		lotteryTypeList.add(LotteryType.JCZQ_BF);			//竞彩足球全场比分
		lotteryTypeList.add(LotteryType.JCZQ_JQS);			//竞彩足球进球数
		lotteryTypeList.add(LotteryType.JCZQ_BQC);			//竞彩足球半全场胜平负
		
		return lotteryTypeList;
	}
	
	public List<EventType> getEventTypeList() {
		return EventType.getItems();
	}
	
	public ZcRecalcService getZcRecalcService() {
		return zcRecalcService;
	}

	public void setZcRecalcService(ZcRecalcService zcRecalcService) {
		this.zcRecalcService = zcRecalcService;
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
}
