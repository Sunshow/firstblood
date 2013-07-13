package web.action.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.lottery.PhaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.admin.web.utils.StringUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.lottery.Phase;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseStatus;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.util.lottery.FetcherLotteryDrawConverter;

/**
 * 彩期删除
 * @author chirowong
 *
 */
public class PhaseDeleteAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	
	public static final String QUERY_BEGINSALETIME = "beginSaleTime";
	public static final String QUERY_ENDSALETIME = "endSaleTime";
	
	private HttpServletRequest request;
	private PhaseService phaseService;
	private List<Phase> phases;
	private String phasesStr;
	//查询参数区
	private Integer lotteryTypeValue;
	private String matchPhaseNo;	//有限匹配查询
	private String forsale;			//是否开启销售
	private String isCurrent;		//是否为当前彩期
	private String phaseStatus;		//彩期状态
	private String phase = "-1";	//期数
	private Date startCreateTime;	//创建开始时间
	private Date endCreateTime;		//创建结束时间
	private Date beginSaleTime;		//彩期开售时间
	private Date endSaleTime;		///彩期结售时间
	
	private YesNoStatus yesStatus =  YesNoStatus.YES;//yes状态,用于页面判断
	
	/**
	 * @return
	 */
	public String handle() {
		return "list";
	}
	
	/**
	 * 查询彩期
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询彩期列表");
		Map<String,Object> condition = getCondition();	//获取查询条件
		Map<String, Object> map = null;
		
		try {
			map = phaseService.getPhases(condition, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询期列表，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		if (map == null) {
			logger.info("彩期列表为空");
			super.setErrorMessage("彩期列表为空!");
			return "failure";
		}
		phases = (List<Phase>)map.get(Global.API_MAP_KEY_LIST);
		//处理开奖结果显示
		if (phases != null) {
			for(Phase ph:phases) {
				ph.setResult(FetcherLotteryDrawConverter.convertResultJsonString2ShowString(ph.getResult()));
			}
		}
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		// 替换定位当前期的action参数  将action=currentPhasePosition 改为action=search,保证分页有效
		request = ServletActionContext.getRequest();
		String pageStr = PageUtil.getPageString(request, pageBean);
		pageStr = pageStr.replaceAll("currentPhasePosition", "query");
		String simplePageStr = PageUtil.getSimplePageString(pageBean);
		simplePageStr = simplePageStr.replaceAll("currentPhasePosition", "query");
		// end 替换定位当前期的action参数
		
		super.setPageString(pageStr);
		super.setSimplePageString(simplePageStr);
		
		return "list";
	}
	
	/*
	 * 获得查询条件
	 */
	private Map<String,Object> getCondition() {
		Map<String,Object> conditionMap = new HashMap<String,Object>();
		
		if (lotteryTypeValue != null) {
			PhaseType phaseType = PhaseType.getItem(LotteryType.getItem(lotteryTypeValue));
			conditionMap.put(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));//彩期类型
		}
		
		if (phase != null && !phase.equals("-1")) {
			conditionMap.put(Phase.QUERY_PHASE, phase);//彩期号
		}
		Long tmpLong = null;
		if (matchPhaseNo != null && !matchPhaseNo.equals("")) {
			if (StringUtil.isNumeric(matchPhaseNo)) {
				if (matchPhaseNo.trim().length() < 18) {
					try {
						tmpLong = Long.parseLong(matchPhaseNo);
					} catch (Exception e) {
						tmpLong = null;
					}
					if (tmpLong != null && tmpLong > 0L) {
						conditionMap.put(PhaseAction.QUERY_MATCH_PHASE_NO, matchPhaseNo);//彩期匹配
					}
				}
			}
		}
		Integer tmpInt = null;
		if (phaseStatus != null && !phaseStatus.equals("")) {
			try {
				tmpInt = Integer.parseInt(phaseStatus);
			} catch (Exception e) {
				tmpInt = null;
			}
			if (tmpInt != null && tmpInt > 0) {
				List<String> list = new ArrayList<String>();
				list.add(phaseStatus);
				conditionMap.put(Phase.QUERY_STATUS, list);	//彩期状态
			}
		}
		if (forsale != null && !forsale.equals("")) {
			try {
				tmpInt = Integer.parseInt(forsale);
			} catch (Exception e) {
				tmpInt = null;
			}
			if (tmpInt != null && tmpInt >= 0) {
				conditionMap.put(Phase.QUERY_FORSALE, forsale);//是否开启销售
			}
			
		}
		if (isCurrent != null && !isCurrent.equals("")) {
			try {
				tmpInt = Integer.parseInt(isCurrent);
			} catch (Exception e) {
				tmpInt = null;
			}
			if (tmpInt != null && tmpInt >= 0) {
				conditionMap.put(Phase.QUERY_IS_CURRENT, isCurrent);//是否为当前期
			}
		}
		if (startCreateTime != null) {
			conditionMap.put(PhaseAction.QUERY_CREATETIME_START, startCreateTime);//创建开始时间
		}
		if (endCreateTime != null) {
			conditionMap.put(PhaseAction.QUERY_CREATETIME_END, endCreateTime);//创建结束时间
		}
		if (beginSaleTime != null) {
			conditionMap.put(PhaseDeleteAction.QUERY_BEGINSALETIME, beginSaleTime); //创建开售时间
		}
		if (endSaleTime != null) {
			conditionMap.put(PhaseDeleteAction.QUERY_ENDSALETIME, endSaleTime);    //创建结售时间
		}
		return conditionMap;
	}
	
	/**
	 * 定位到当前期
	 * @return
	 */
	public String currentPhasePosition() {
		logger.info("进入定位到当前期");
		request = ServletActionContext.getRequest();
		Map<String,Object> condition = getCondition();
		PageBean currentPageBean = null;
		
		try {
			currentPageBean = phaseService.getPageOfCurrentPhase(condition, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取当前期所在页，api调用异常，{}",e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		if (currentPageBean != null) {
			super.setPageBean(currentPageBean);
		}
		return query();
	}
	
	/**
	 * 批量删除
	 * @return
	 */
	public String batchDelete() {
		logger.info("进入批量删除彩期");
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject object = new JSONObject();
		object.put("code", -1);
		object.put("msg", "删除失败！");
		if(lotteryTypeValue == null || lotteryTypeValue.intValue() == -1){
			logger.error("删除的彩种为空");
			object.put("code", -1);
			object.put("msg", "删除的彩种为空！");
			writeRs(response, object);
			return null;
		}
		
		if (phasesStr == null || phasesStr.equals("")) {
			logger.error("删除的彩期为空");
			object.put("code", -1);
			object.put("msg", "删除的彩期为空！");
			writeRs(response, object);
			return null;
		}
		
		List<String> deletePhases =  Arrays.asList(phasesStr.split(","));
		try {
			ResultBean resultBean = phaseService.deletePhases(LotteryType.getItem(lotteryTypeValue),deletePhases);
			object.put("code", resultBean.getCode());
			object.put("msg", resultBean.getMessage());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("删除彩期，api调用异常，{}", e.getMessage());
			object.put("code", -1);
			object.put("msg", "api调用异常，请联系技术人员!原因：" + e.getMessage());
			writeRs(response, object);
		}
		writeRs(response, object);
		return null;
	}
	
	public List<LotteryType> getLotteryTypeList() {
		List<LotteryType> lotteryTypes = new ArrayList<LotteryType>();
		lotteryTypes.add(LotteryType.ALL);
		lotteryTypes.add(LotteryType.JXSSC);
		lotteryTypes.add(LotteryType.DC_SFP);
		lotteryTypes.add(LotteryType.DC_SFGG);
		lotteryTypes.add(LotteryType.JSK3);
		lotteryTypes.add(LotteryType.JLK3);
		lotteryTypes.add(LotteryType.GXK3);
		return lotteryTypes;
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
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getMatchPhaseNo() {
		return matchPhaseNo;
	}
	public void setMatchPhaseNo(String matchPhaseNo) {
		this.matchPhaseNo = matchPhaseNo;
	}
	public String getPhaseStatus() {
		return phaseStatus;
	}
	public void setPhaseStatus(String phaseStatus) {
		this.phaseStatus = phaseStatus;
	}
	public String getForsale() {
		return forsale;
	}
	public void setForsale(String forsale) {
		this.forsale = forsale;
	}
	public String getIsCurrent() {
		return isCurrent;
	}
	public void setIsCurrent(String isCurrent) {
		this.isCurrent = isCurrent;
	}
	public Date getStartCreateTime() {
		return startCreateTime;
	}
	public void setStartCreateTime(Date startCreateTime) {
		this.startCreateTime = startCreateTime;
	}
	public Date getEndCreateTime() {
		return endCreateTime;
	}
	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime;
	}
	public String getPhasesStr() {
		return phasesStr;
	}
	public void setPhasesStr(String phasesStr) {
		this.phasesStr = phasesStr;
	}
	public Date getBeginSaleTime() {
		return beginSaleTime;
	}
	public void setBeginSaleTime(Date beginSaleTime) {
		this.beginSaleTime = beginSaleTime;
	}
	public void setEndSaleTime(Date endSaleTime) {
		this.endSaleTime = endSaleTime;
	}
	public Date getEndSaleTime() {
		return endSaleTime;
	}

	public void setYesStatus(YesNoStatus yesStatus) {
		this.yesStatus = yesStatus;
	}

	public YesNoStatus getYesStatus() {
		return yesStatus;
	}
}
