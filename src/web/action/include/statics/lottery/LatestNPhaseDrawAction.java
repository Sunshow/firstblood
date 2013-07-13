package web.action.include.statics.lottery;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.Fb46Match;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.lottery.SfpArrange;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;

public class LatestNPhaseDrawAction extends BaseAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4678855952068112136L;

	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private PhaseService phaseService;
	
	private int lotteryTypeId;
	private int num;
	
	@SuppressWarnings("unchecked")
	public void handle() {
		logger.info("开始获取最新状态为开奖的彩期");
		PhaseType type = PhaseType.getItem(LotteryType.getItem(this.lotteryTypeId));
		List<Phase> phases = null;
		if(num < 1){
			num = 1;
		}
		if(num > 10){//最多去十期
			num = 10;
		}
		try {
			phases = this.phaseService.getLatestDrawedPhase(type,num);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("远程调用出现异常",e.getMessage());
		}
		if (phases == null || phases.size() == 0) {
			return;
		}
		String jsonStr = null;
		JSONObject obj = null;
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		for (int i=phases.size()-1 ; i>=0 ; i--) {
			Phase phase = phases.get(i);
			if (phase.getResult() != null && !"".equals(phase.getResult()) && !phase.getResult().equals("null") ) {
				jsonStr = phase.getResult();
				obj = JSONObject.fromObject(jsonStr);
				jsonStr = obj.getString("result");
			} else {
				jsonStr = new JSONObject().toString();
			}
			JSONObject drawObj = new JSONObject();
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
			
			if(type.getValue() == PhaseType.getItem(LotteryType.SFC).getValue() ||
					type.getValue() == PhaseType.getItem(LotteryType.SFR9).getValue()
				){//获取主客队信息
				Map<String, Object> mapMatches = null;
				try{
					Map<String,Object> conditionMatch = new HashMap<String, Object>();
					conditionMatch.put(SfpArrange.QUERY_PHASE, phase.getPhase());
					
					//按照彩期降序排列
					conditionMatch.put("orderStr", SfpArrange.ORDER_MATCH_NUM);
					conditionMatch.put("orderView", ApiConstant.API_REQUEST_ORDER_ASC);
					
					PageBean pageBeanMatch = new PageBean();
					pageBeanMatch.setPageSize(14);
					
					mapMatches = phaseService.getSfpMatches(conditionMatch, pageBeanMatch);
					JSONArray jsonArrayMatch = new JSONArray();
					
					if(mapMatches == null){
						logger.error("查询phase={}时返回的map结果为空", phase.getPhase());
					}else{
						List<SfpArrange> matches = (List<SfpArrange>)mapMatches.get(Global.API_MAP_KEY_LIST);
						//处理开奖结果显示
						if(matches == null || matches.size() <= 0){
							logger.error("查询phase={}时返回的map结果为空", phase.getPhase());
						}else{
							for(SfpArrange sfpArrange : matches){
								JSONObject jsonObjectMatch = new JSONObject();
								jsonObjectMatch.put("home_team", sfpArrange.getHomeTeam());
								jsonObjectMatch.put("away_team", sfpArrange.getGuestTeam());
								jsonArrayMatch.add(jsonObjectMatch);
							}
						}
					}
					drawObj.put("match", jsonArrayMatch);
					
				}catch(ApiRemoteCallFailedException e){
					logger.error(e.getMessage(), e);
				}
			}
			if(type.getValue() == PhaseType.getItem(LotteryType.JQC).getValue() ||
					type.getValue() == PhaseType.getItem(LotteryType.BQC).getValue()
				){//获取主客队信息
				Map<String, Object> mapMatches = null;
				try{
					Map<String,Object> conditionMatch = new HashMap<String, Object>();
					conditionMatch.put(Fb46Match.QUERY_TYPE, lotteryTypeId + "");
					conditionMatch.put(Fb46Match.QUERY_PHASE, phase.getPhase());
					//按照彩期降序排列
					conditionMatch.put("orderStr", Fb46Match.ORDER_MATCH_NUM);
					conditionMatch.put("orderView", ApiConstant.API_REQUEST_ORDER_ASC);
					
					PageBean pageBeanMatch = new PageBean();
					pageBeanMatch.setPageSize(14);
					
					mapMatches = phaseService.getFb46Matches(conditionMatch, pageBeanMatch);
					JSONArray jsonArrayMatch = new JSONArray();
					
					if(mapMatches == null){
						logger.error("查询phase={}时返回的map结果为空", phase.getPhase());
					}else{
						List<Fb46Match> matches = (List<Fb46Match>)mapMatches.get(Global.API_MAP_KEY_LIST);
						//处理开奖结果显示
						if(matches == null || matches.size() <= 0){
							logger.error("查询phase={}时返回的map结果为空", phase.getPhase());
						}else{
							for(Fb46Match fb46Match : matches){
								JSONObject jsonObjectMatch = new JSONObject();
								jsonObjectMatch.put("home_team", fb46Match.getHomeTeam());
								jsonObjectMatch.put("away_team", fb46Match.getGuestTeam());
								jsonArrayMatch.add(jsonObjectMatch);
							}
						}
					}
					drawObj.put("match", jsonArrayMatch);
					
				}catch(ApiRemoteCallFailedException e){
					logger.error(e.getMessage(), e);
				}
			}
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
			array.add(drawObj);
		}
		result.put("result", array);
		HttpServletResponse response = ServletActionContext.getResponse();
		writeRs(response,result.toString());
		logger.info("结束获取最新状态为开奖的彩期");
		return ;
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

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
}
