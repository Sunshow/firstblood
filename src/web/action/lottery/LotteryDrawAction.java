package web.action.lottery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.api.setting.SettingConstant;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.fetcher.FetcherType;
import com.lehecai.core.service.setting.SettingService;
/**
 * 开奖结果管理
 * @author leiming
 *
 */
public class LotteryDrawAction extends BaseAction {
	private static final long serialVersionUID = -367266851465374167L;
	private static final Logger logger = LoggerFactory.getLogger(LotteryDrawAction.class);
	
	@SuppressWarnings("unused")
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PhaseService phaseService;
	private SettingService settingService;
	
	private Integer lotteryTypeValue;
	private String phaseNo;
	private Phase phase;
	private Integer phaseStatusValue;
	private String lotteryConfigData;
	private String phaseNoText;
	private boolean fordrawValue;
	
	private Integer fetcherTypeValue;
	
	@SuppressWarnings("unused")
	private List<FetcherType> fetcherTypeList;
	
	@SuppressWarnings("unused")
	private List<LotteryType> lotteryTypeList;
	
	private String lotteryTypeSpell;
	
	private static Set<Integer> phaseStatusSet = new HashSet<Integer>();
	
	static {
		phaseStatusSet.add(PhaseStatus.CLOSE.getValue());
		phaseStatusSet.add(PhaseStatus.RESULT_SET.getValue());
		phaseStatusSet.add(PhaseStatus.DRAW.getValue());
		phaseStatusSet.add(PhaseStatus.REWARD.getValue());
	}
	
	public String handle(){
		return "list";
	}
	
	//打开编辑开奖结果
	public String edit() {
		logger.info("进入获取开奖结果");
		if (phaseNoText!=null&&!"".equals(phaseNoText)) {
			phaseNo = phaseNoText;
		}
		if (lotteryTypeValue == null) {
			logger.error("彩种类型数值为null");
			setErrorMessage("彩种类型数值为null");
			return "failure";
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		try {
			phase = phaseService.getPhaseByPhaseTypeAndPhaseNo(PhaseType.getItem(lotteryType), phaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("开奖结果管理获取彩期api调用异常，lotteryType={}, phase={}", lotteryType, phaseNo);
			logger.error(e.getMessage(), e);
			super.setErrorMessage("开奖结果管理获取彩期api调用异常，lotteryType=" + lotteryType + ", phase=" + phaseNo);
			return "failure";
		}
		if (!phaseStatusSet.contains(phase.getPhaseStatus().getValue())) {
			logger.error("开奖结果管理彩期状态错误，不可修改，彩期状态只能为[关闭，结果已公布，已开奖，已派奖]，lotteryType={}，phase={}，phaseStatus={}", new Object[]{lotteryType, phaseNo, phase.getPhaseStatus()});
			super.setErrorMessage("开奖结果管理彩期状态错误，不可修改，彩期状态只能为[关闭，结果已公布，已开奖，已派奖]，lotteryType=" + lotteryType + "，phase=" + phaseNo + "，phaseStatus=" + phase.getPhaseStatus());
			return "failure";
		}
		Map<Integer,String> lotteryTypeSpellMap = new HashMap<Integer, String>();
		//体彩
		lotteryTypeSpellMap.put(LotteryType.DLT.getValue(), "dlt");
		lotteryTypeSpellMap.put(LotteryType.QXC.getValue(), "qxc");
		lotteryTypeSpellMap.put(LotteryType.PL3.getValue(), "pl3");
		lotteryTypeSpellMap.put(LotteryType.PL5.getValue(), "pl5");
		lotteryTypeSpellMap.put(LotteryType.TC22X5.getValue(), "tc22x5");
		lotteryTypeSpellMap.put(LotteryType.SFC.getValue(), "sfc");
		lotteryTypeSpellMap.put(LotteryType.SFR9.getValue(), "sfr9");
		lotteryTypeSpellMap.put(LotteryType.JQC.getValue(), "jqc");
		lotteryTypeSpellMap.put(LotteryType.BQC.getValue(), "bqc");
		// 北单
		lotteryTypeSpellMap.put(LotteryType.DC_SFP.getValue(), "zqdc/sfp");
		lotteryTypeSpellMap.put(LotteryType.DC_SXDS.getValue(), "qdc/sxds");
		lotteryTypeSpellMap.put(LotteryType.DC_JQS.getValue(), "qdc/jqs");
		lotteryTypeSpellMap.put(LotteryType.DC_BF.getValue(), "qdc/bf");
		lotteryTypeSpellMap.put(LotteryType.DC_BCSFP.getValue(), "qdc/bcsfp");
		// 竞彩篮球
		lotteryTypeSpellMap.put(LotteryType.JCLQ_SF.getValue(), "jclq/sf");
		lotteryTypeSpellMap.put(LotteryType.JCLQ_RFSF.getValue(), "jclq/rfsf");
		lotteryTypeSpellMap.put(LotteryType.JCLQ_SFC.getValue(), "jclq/sfc");
		lotteryTypeSpellMap.put(LotteryType.JCLQ_DXF.getValue(), "jclq/dxf");
		// 竞彩足球
		lotteryTypeSpellMap.put(LotteryType.JCZQ_SPF.getValue(), "jczq/spf");
		lotteryTypeSpellMap.put(LotteryType.JCZQ_BF.getValue(), "jczq/bf");
		lotteryTypeSpellMap.put(LotteryType.JCZQ_JQS.getValue(), "jczq/jqs");
		lotteryTypeSpellMap.put(LotteryType.JCZQ_BQC.getValue(), "jczq/bqc");
		// 福彩
		lotteryTypeSpellMap.put(LotteryType.SSQ.getValue(), "ssq");
		lotteryTypeSpellMap.put(LotteryType.QLC.getValue(), "qlc");
		lotteryTypeSpellMap.put(LotteryType.FC3D.getValue(), "fc3d");
		lotteryTypeSpellMap.put(LotteryType.DF6J1.getValue(), "df6j1");
		lotteryTypeSpellMap.put(LotteryType.HD15X5.getValue(), "hd15x5");
		// 高频彩
		lotteryTypeSpellMap.put(LotteryType.SD11X5.getValue(), "sd11x5");
		lotteryTypeSpellMap.put(LotteryType.JX11X5.getValue(), "jx11x5");
		lotteryTypeSpellMap.put(LotteryType.GD11X5.getValue(), "gd11x5");
		lotteryTypeSpellMap.put(LotteryType.GDKL10.getValue(), "gdkl10");
		lotteryTypeSpellMap.put(LotteryType.CQSSC.getValue(), "cqssc");
		lotteryTypeSpellMap.put(LotteryType.SHSSL.getValue(), "shssl");
		lotteryTypeSpellMap.put(LotteryType.JXSSC.getValue(), "jxssc");
		lotteryTypeSpellMap.put(LotteryType.BJKL8.getValue(), "bjkl8");
		lotteryTypeSpellMap.put(LotteryType.SDQYH.getValue(), "sdqyh");
		
		
		
		try {
			lotteryConfigData = settingService.get(SettingConstant.GROUP_LOTTERY_CONFIG, String.valueOf(lotteryTypeValue));
			fordrawValue = phase.getFordraw().getValue() == YesNoStatus.YES.getValue() ? true : false;
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期配置，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}

		lotteryTypeSpell = lotteryTypeSpellMap.get(lotteryTypeValue);
		logger.info("获取开奖结果结束");
		return "edit";
	}
	
	//更新开奖结果
	public String update() {
		logger.info("进入更新开奖结果");
		if (lotteryTypeValue == null) {
			logger.error("彩种类型数值为null");
			setErrorMessage("彩种类型数值为null");
			return "failure";
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		phase.setPhaseType(PhaseType.getItem(lotteryType));
		
		if (lotteryTypeValue == LotteryType.DF6J1.getValue() && phase.getResult() != null) {
			String shengxiaoStr = null;
			try {
				JSONObject resultObject = JSONObject.fromObject(phase.getResult());
				JSONArray resultArray = resultObject.getJSONArray("result");
				if (resultArray.size() == 2) {
					JSONArray dataArray = resultArray.getJSONObject(1).getJSONArray("data");
					shengxiaoStr = dataArray.getString(0);
					
					int shengxiaoInt = Integer.parseInt(shengxiaoStr);
					
					if (shengxiaoInt < 10) {
						shengxiaoStr = StringUtils.leftPad(shengxiaoStr, 2, '0');
						dataArray.set(0, shengxiaoStr);
						phase.setResult(resultObject.toString());
					}
				}
			} catch (Exception e) {
				logger.info("对东方6加1生肖位" + shengxiaoStr + "补零出错", e);
			}
		}
		
		phase.setFordraw(fordrawValue ? YesNoStatus.YES : YesNoStatus.NO);
		ResultBean resultBean = null;
		try {
			resultBean = phaseService.updatePhase(phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("更新开奖结果，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			super.setForwardUrl("/lottery/lotteryDraw.do");
			return "failure";
		}
		
		if (resultBean.isResult()) {
			super.setForwardUrl("/lottery/lotteryDraw.do");
			// 更改开奖结果状态 已关闭改为结果已公布
			if (phase != null && phaseStatusValue != null && phaseStatusValue == PhaseStatus.CLOSE.getValue()) {
				try {
					resultBean = phaseService.modifyPhaseStatus(phase.getPhaseType(), phaseNo, PhaseStatus.RESULT_SET);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("更新开奖结果成功，但是修改彩期状态失败，{}", e.getMessage());
					super.setErrorMessage("更新开奖结果成功，但是修改彩期状态失败，原因：" + e.getMessage());
					super.setForwardUrl("/lottery/lotteryDraw.do");
					return "failure";
				}
			}
			return "success";
		} else {
			logger.error("更新失败，{}", resultBean.getMessage());
			setErrorMessage("更新失败，" + resultBean.getMessage());
			super.setForwardUrl("/lottery/lotteryDraw.do");
			return "failure";
		}
	}
	
	//抓取开奖结果数据
	public String fetchDrawData() {
		logger.info("进入抓取开奖结果数据");
		JSONObject rs = new JSONObject();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			logger.error("彩种类型数值为null");
			setErrorMessage("彩种类型数值为null");
			return "failure";
		}
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		FetcherType fetcherType = FetcherType.getItem(fetcherTypeValue);
		try {
			phase = phaseService.fetchLotteryDraw(lotteryType, fetcherType, phaseNo);
			if (phase == null) {
				logger.error("抓取失败,抓取彩期对象为null");
				rs.put("state", "failed");
				rs.put("msg", "抓取失败,抓取彩期对象为null");
			} else if (phase.getResult() == null || phase.getResult().trim().length() == 0) {
				logger.error("抓取失败,抓取彩期开奖结果为空,请确认抓取彩期已开奖");
				rs.put("state", "failed");
				rs.put("msg", "抓取失败,抓取彩期开奖结果为空,请确认抓取彩期已开奖");
			} else {
				logger.info("抓取成功");
				rs.put("state", "success");
				rs.put("msg", "抓取成功");
				rs.put("phase", Phase.toEditJSON(phase));
			}
		} catch (Exception e) {
			logger.error("抓取开奖结果异常，{}", e);
			rs.put("state", "success");
			rs.put("msg", e.getMessage());
			rs.put("phase", Phase.toEditJSON(phase));
		}
		writeRs(response, rs);
		logger.info("抓取开奖结果数据结束");
		return null;
		
	}
	
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}
	public SettingService getSettingService() {
		return settingService;
	}
	public void setSettingService(SettingService settingService) {
		this.settingService = settingService;
	}
	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}
	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
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
	public String getLotteryConfigData() {
		return lotteryConfigData;
	}
	public void setLotteryConfigData(String lotteryConfigData) {
		this.lotteryConfigData = lotteryConfigData;
	}
	public List<LotteryType> getLotteryTypeList() {
		// 获取所有彩种列表
		return LotteryType.getItems();
	}
	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}
	public Integer getPhaseStatusValue() {
		return phaseStatusValue;
	}
	public void setPhaseStatusValue(Integer phaseStatusValue) {
		this.phaseStatusValue = phaseStatusValue;
	}
	public Integer getFetcherTypeValue() {
		return fetcherTypeValue;
	}
	public void setFetcherTypeValue(Integer fetcherTypeValue) {
		this.fetcherTypeValue = fetcherTypeValue;
	}
	public List<FetcherType> getFetcherTypeList() {
//		fetcherTypeList = new ArrayList<FetcherType>();
//		fetcherTypeList.add(FetcherType.T_500WAN);
//		fetcherTypeList.add(FetcherType.T_STARLOTT);
//		return fetcherTypeList;
		//2011-3-29 获取所有的抓取器列表
		return FetcherType.getItems();
	}
	public void setFetcherTypeList(List<FetcherType> fetcherTypeList) {
		this.fetcherTypeList = fetcherTypeList;
	}
	public String getPhaseNoText() {
		return phaseNoText;
	}
	public void setPhaseNoText(String phaseNoText) {
		this.phaseNoText = phaseNoText;
	}
	public String getLotteryTypeSpell() {
		return lotteryTypeSpell;
	}

	public boolean isFordrawValue() {
		return fordrawValue;
	}

	public void setFordrawValue(boolean fordrawValue) {
		this.fordrawValue = fordrawValue;
	}
}
