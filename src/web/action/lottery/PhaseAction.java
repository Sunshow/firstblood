package web.action.lottery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
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
import com.lehecai.core.lottery.TerminalStatus;
import com.lehecai.core.util.lottery.FetcherLotteryDrawConverter;

public class PhaseAction extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(PhaseAction.class);
	private static final long serialVersionUID = 2436161530465382824L;
	
	private static final String QUERY_ALL_PHASE = "-1";// 查询所有彩期
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PhaseService phaseService;
	
	@SuppressWarnings("unused")
	private Integer disabledPhaseStatus;
	
	@SuppressWarnings("unused")
	private Integer unOpenPhaseStatus;
	@SuppressWarnings("unused")
	private Integer closePhaseStatus;
	@SuppressWarnings("unused")
	private Integer openPhaseStatus;
	
	private Integer lastCount;//最新彩期列表数量
	private String assignPhaseNo;//指定彩期号
	private Date batchStartTime;//批量创建的起始时间
	private String batchAmountParam;//批次数量参数
	private List<Phase> phases;
	private Phase phaseObj;
	private int count = 10;
	//查询参数区
	private Integer lotteryTypeValue;//彩票类型值 公用于批量创建和指定创建彩期
	private String phase;
	private String matchPhaseNo;
	private String phaseStatus;
	private String forsaleStatus;
	private String terminalStatus;
	private String forsale;
	private String is_current;
	private Date startCreateTime;
	private Date endCreateTime;
	
	private Date startSaleTime;			//开始销售时间
	private Date endSaleTime;			//结束销售时间
	private Date endTicketTime;			//停止出票时间
	private Date drawTime;				//开奖时间	
	
	public static final String QUERY_CREATETIME_START = "create_at_start";//
	public static final String QUERY_CREATETIME_END = "create_at_end";//
	public static final String QUERY_MATCH_PHASE_NO = "matchPhaseNo";//自定义参数,php查询接口未提供,service查询方法需提供模糊查询优先于指定彩期
	//end 查询参数区

	@SuppressWarnings("unused")
	private List<LotteryType> lotteryTypeList;
	@SuppressWarnings("unused")
	private List<YesNoStatus> yesNoStatusList;
	@SuppressWarnings("unused")
	private List<PhaseStatus> phaseStatusList;
	@SuppressWarnings("unused")
	private List<TerminalStatus> terminalStatusList;
	private YesNoStatus yesStatus =  YesNoStatus.YES;//yes状态,用于页面判断
	private YesNoStatus noStatus =  YesNoStatus.NO;//no状态,用于页面判断
	
	public String handle() {
		return "list";
	}
	
	//查询
	@SuppressWarnings("unchecked")
	public String search() {
		logger.info("进入彩期列表查询");
		request = ServletActionContext.getRequest();
		HashMap<String,Object> condition = getCondition();
		Map<String, Object> map = null;
		
		try {
			map = phaseService.getPhases(condition, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询彩期列表，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map == null) {
			logger.error("list查询结果为空");
			super.setErrorMessage("list查询结果为空");
			return "failure";
		}
		phases = (List<Phase>)map.get(Global.API_MAP_KEY_LIST);
		//处理开奖结果显示
		if (phases!=null) {
			for(Phase ph:phases) {
				ph.setResult(FetcherLotteryDrawConverter.convertResultJsonString2ShowString(ph.getResult()));
			}
		}
		PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
		//ResultBean resultBean = (ResultBean)map.get(Global.API_MAP_KEY_RESULTBEAN);
		// 替换定位当前期的action参数  将action=currentPhasePosition 改为action=search,保证分页有效
		String pageStr = PageUtil.getPageString(request, pageBean);
		pageStr = pageStr.replaceAll("currentPhasePosition", "search");
		String simplePageStr = PageUtil.getSimplePageString(pageBean);
		simplePageStr = simplePageStr.replaceAll("currentPhasePosition", "search");
		// end 替换定位当前期的action参数 
		super.setPageString(pageStr);
		super.setSimplePageString(simplePageStr);
		logger.info("查询彩期列表结束");
		return "list";
	}
	
	//彩期修改
	public String input() {
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType phaseType = PhaseType.getItem(lotteryType);
		try {
			phaseObj = phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		return "inputForm";
	}
	public String manage() {
		LotteryType lotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType phaseType = PhaseType.getItem(lotteryType);
		try {
			phaseObj = phaseService.getPhaseByPhaseTypeAndPhaseNo(phaseType, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (phaseObj != null) {
			if (startSaleTime != null) {
				phaseObj.setStartSaleTime(startSaleTime);
			}
			if (endSaleTime != null) {
				phaseObj.setEndSaleTime(endSaleTime);
			}
			if (endTicketTime != null) {
				phaseObj.setEndTicketTime(endTicketTime);
			}
			if (drawTime != null) {
				phaseObj.setDrawTime(drawTime);
			}
		}
		try {
			phaseService.updatePhase(phaseObj);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("修改彩期，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		return "success";
	}
	
	//创建指定彩期
	public String createAssignPhase() {
		logger.info("进入创建指定彩期");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType assignLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType assignPhaseType = PhaseType.getItem(assignLotteryType);
		Phase checkSamePhase = null;
		try {
			checkSamePhase = phaseService.getPhaseByPhaseTypeAndPhaseNo(assignPhaseType, assignPhaseNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取彩期，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		String messageHeader = "创建["+assignLotteryType.getName()+"]指定彩期<第"+assignPhaseNo+"期>,结果:";
		
		if (checkSamePhase==null) {
			ResultBean resultBean = null;
			try {
				resultBean = phaseService.createAssignPhase(assignPhaseType, assignPhaseNo,startSaleTime,endSaleTime,endTicketTime,drawTime);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("创建指定彩期，api调用异常，{}", e.getMessage());
				rs.put("state", "failed");
				rs.put("msg", messageHeader+e.getMessage());
				writeRs(response, rs);
				return null;
			}
			rs.put("state", "success");
			rs.put("msg", messageHeader+resultBean.getMessage());
		} else {
			rs.put("state", "failed");
			rs.put("msg", messageHeader+"指定创建的彩期已经存在,系统禁止创建重复的彩期,创建失败");
		}
		writeRs(response, rs);
		logger.info("创建指定彩期结束");
		return null;
	}
	
	//获取指定彩期的最新彩期列表
	public String getLastPhaseList() {
		logger.info("进入获取最新彩期列表");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			lotteryTypeValue = LotteryType.ALL.getValue();
		}
		LotteryType lastLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType lastPhaseType = PhaseType.getItem(lastLotteryType);
		List<Phase> phases = null;
		
		try {
			phases = phaseService.getPhaseListByPhaseType(lastPhaseType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取最新彩期列表，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("phases", "[]");
			writeRs(response, rs);
			return null;
		}
		rs.put("state", "success");
		rs.put("phases", Phase.toJSONArray(phases));
		writeRs(response, rs);
		logger.info("获取最新彩期列表结束");
		return null;
	}
	
	/**
	 * 获取指定彩期的前后N期彩期列表
	 * @return
	 */
	public String getAppointPhaseList() {
		logger.info("进入获取前后N期彩期列表");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			lotteryTypeValue = LotteryType.ALL.getValue();
		}
		LotteryType lastLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType lastPhaseType = PhaseType.getItem(lastLotteryType);
		List<Phase> phases = null;
		boolean allPhaseFlag = false;
		try {//如果没有指定彩期，将当前期设为指定期
			if (QUERY_ALL_PHASE.equals(assignPhaseNo)) {
				allPhaseFlag = true;
			}
			if (assignPhaseNo == null || assignPhaseNo.isEmpty() || assignPhaseNo.equals(QUERY_ALL_PHASE)) {
				//查询当前期
				Phase currentPhase = phaseService.getCurrentPhase(lastPhaseType);
				if (currentPhase != null) {
					assignPhaseNo = currentPhase.getPhase();
				} else {
					// 获取离当前时间最近一期
					currentPhase = phaseService.getNearestPhase(lastPhaseType, new Date());
					if (currentPhase != null) {
						assignPhaseNo = currentPhase.getPhase();
					}
				}
				
				if (assignPhaseNo == null) {
					assignPhaseNo = "";
				}

			}
			
			phases = phaseService.getAppointPhaseList(lastPhaseType, assignPhaseNo, count);
			if (phases != null) {
				//指定彩期去重
				removeRepeat(phases);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取前后N期彩期列表，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("phases", "[]");
			rs.put("assignPhaseNo", "");
			writeRs(response, rs);
			return null;
		}
		rs.put("state", "success");
		rs.put("phases", Phase.toJSONArray(phases));
		rs.put("assignPhaseNo", allPhaseFlag==true?Integer.parseInt(QUERY_ALL_PHASE):assignPhaseNo);
		writeRs(response, rs);
		logger.info("获取前后N期彩期列表结束");
		return null;
	}
	
	/**
	 * 获取指定彩期的前N期彩期列表
	 * @return
	 */
	public String getAppointPhaseListBefore() {
		logger.info("进入获取前N期彩期列表");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			lotteryTypeValue = LotteryType.ALL.getValue();
		}
		LotteryType lastLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType lastPhaseType = PhaseType.getItem(lastLotteryType);
		List<Phase> phases = null;
		boolean allPhaseFlag = false;
		try {//如果没有指定彩期，将当前期设为指定期
			if (QUERY_ALL_PHASE.equals(assignPhaseNo)) {
				allPhaseFlag = true;
			}
			if (assignPhaseNo == null || assignPhaseNo.isEmpty() || assignPhaseNo.equals(QUERY_ALL_PHASE)) {
				//查询当前期
				Phase currentPhase = phaseService.getCurrentPhase(lastPhaseType);
				if (currentPhase != null) {
					assignPhaseNo = currentPhase.getPhase();
				} else {
					// 获取离当前时间最近一期
					currentPhase = phaseService.getNearestPhase(lastPhaseType, new Date());
					if (currentPhase != null) {
						assignPhaseNo = currentPhase.getPhase();
					}
				}
				
				if (assignPhaseNo == null) {
					assignPhaseNo = "";
				}

			}
			
			phases = phaseService.getAppointPhaseListBefore(lastPhaseType, assignPhaseNo, count);
			if (phases != null) {
				//指定彩期去重
				removeRepeat(phases);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取前N期彩期列表，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("phases", "[]");
			rs.put("assignPhaseNo", "");
			writeRs(response, rs);
			return null;
		}
		rs.put("state", "success");
		rs.put("phases", Phase.toJSONArray(phases));
		rs.put("assignPhaseNo", allPhaseFlag==true?Integer.parseInt(QUERY_ALL_PHASE):assignPhaseNo);
		writeRs(response, rs);
		logger.info("获取前后N期彩期列表结束");
		return null;
	}
	
	public String getCurrentPhase() {
		PhaseType phaseType = PhaseType.getItem(lotteryTypeValue);
		Phase p = null;
		try {
			p = phaseService.getCurrentPhase(phaseType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获得{}当前期，api调用异常，{}",phaseType.getName(),e.getMessage());
		}
		if (p != null) {
			assignPhaseNo = p.getPhase();
		} else {
			assignPhaseNo = "";
		}
		JSONObject rs = new JSONObject();
		rs.put("phase", assignPhaseNo);
		writeRs(ServletActionContext.getResponse(), rs);
		return null;
	}
	
	/**
	 * 指定彩期列表去重
	 * @param phases
	 */
	private void removeRepeat(List<Phase> phases) {
		//去重
		boolean flag = false;
		for (int i = 0; i < phases.size(); i++) {
			Phase p = phases.get(i);
			if (assignPhaseNo.equals(p.getPhase())) {
				if (flag) {
					phases.remove(i);
				}
				flag = true;
			}
		}
		//排序
		for (int i = 0; i < phases.size(); i++) {
			for (int j = i; j < phases.size(); j++) {
				if (Long.parseLong(phases.get(i).getPhase()) < Long.parseLong(phases.get(j).getPhase())) {
					Phase temppPhase = phases.get(i);
					phases.set(i, phases.get(j));
					phases.set(j, temppPhase);
				}
			}
		}
	}
	
	//批量生成彩期
	public String batchCreatePhase() {
		logger.info("进入批量生成彩期");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		String messageHeader = null;
		Integer batchAmount = null;//批次总数量
		//校验数量参数
		if (batchAmountParam!=null&&batchAmountParam.trim().length()>0) {
			try {
				batchAmount = Integer.parseInt(batchAmountParam);
			} catch(NumberFormatException e) {
				messageHeader = "批量生成彩期数量参数为:"+batchAmountParam+",格式不合法,必须为大于零的整数";
				logger.error(messageHeader);
				rs.put("state", "failed");
				rs.put("msg", messageHeader);
				writeRs(response, rs);
				return null;
			}
			if (batchAmount==null||batchAmount<=0) {
				messageHeader = "批量生成彩期数量参数为:"+batchAmountParam+",格式不合法,必须为大于零的整数";
				logger.error(messageHeader);
				rs.put("state", "failed");
				rs.put("msg", messageHeader);
				writeRs(response, rs);
				return null;
			}
		}//end校验
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType batchLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType batchPhaseType = PhaseType.getItem(batchLotteryType);
		ResultBean resultBean = null;
		try {
			resultBean = phaseService.batchCreatePhase(batchPhaseType, batchAmount, batchStartTime);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("批量生成彩期，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			writeRs(response, rs);
			return null;
		}
		
		messageHeader = "批量生成["+batchLotteryType.getName()+"]彩期,共"+batchAmount+"期,结果:";
		rs.put("state", "success");
		rs.put("msg", messageHeader+resultBean.getMessage());
		
		writeRs(response, rs);
		logger.info("批量生成彩期结束");
		return null;
	}
	
	//当前期所在位置
	public String currentPhasePosition() {
		logger.info("进入定位到当前期");
		request = ServletActionContext.getRequest();
		HashMap<String,Object> condition = getCondition();
		PageBean currentPageBean = null;
		
		try {
			currentPageBean = phaseService.getPageOfCurrentPhase(condition, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("获取当前期所在页，api调用异常，{}", e.getMessage());
			setErrorMessage(e.getMessage());
			return "failure";
		}
		if (currentPageBean != null) {
			super.setPageBean(currentPageBean);
		}
		logger.info("定位到当前期结束");
		return search();
		/*
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		int pageNum = 1;
		if (lotteryTypeValue == null) {
			super.setForwardUrl("/lottery/phase.do");
			setErrorMessage("定位当前期彩票类型不存在");
			return "failure";
		}
		LotteryType currentLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType currentPhaseType = PhaseType.getItem(currentLotteryType);
		PageBean page = null;
		try {
			page = phaseService.getPageOfCurrentPhase(currentPhaseType, super.getPageBean().getPageSize());
		} catch (ApiRemoteCallFailedException e) {
			logger.error("定位当前期彩票类型 发生错误,"+e.getMessage(),e);
		}
		super.setPageBean(page);
		super.setPageString(PageUtil.getPageString(request, super.getPageBean()));
		StringBuffer url = new StringBuffer();
		url.append("/lottery/phase.do?").append("action=search");
		url.append("&").append("lotteryTypeValue=").append(lotteryTypeValue);
		url.append("&").append("pageBean.page=").append(pageNum);
		logger.info("url:"+url);
		super.setForwardUrl(url.toString());
		return "list";
		*/
	}
	
	//修改彩期状态
	public String modifyPhaseStatus() {
		logger.info("进入修改彩期状态");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType modifyLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType modifyPhaseType = PhaseType.getItem(modifyLotteryType);
		PhaseStatus modifyPhaseStatus = PhaseStatus.getItem(Integer.parseInt(phaseStatus));
		ResultBean resultBean = null;
		
		try {
			resultBean = phaseService.modifyPhaseStatus(modifyPhaseType, phase, modifyPhaseStatus);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("修改彩期状态，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("modifyPhaseStatusName", "");
			rs.put("modifyPhase", "");
			writeRs(response, rs);
			return null;
		}
		
		String messageHeader = "修改["+modifyLotteryType.getName()+"]指定彩期<第"+phase+"期>的彩期状态为"+modifyPhaseStatus.getName()+",结果:";
		if (resultBean.isResult()) {
			rs.put("state", "success");
		} else {
			rs.put("state", "failed");
		}
		rs.put("msg", messageHeader+resultBean.getMessage());
		rs.put("modifyPhaseStatusName", modifyPhaseStatus.getName());//修改后的彩期状态名称
		rs.put("modifyPhase", phase);//修改的彩期号
		writeRs(response, rs);
		logger.info("修改彩期状态结束");
		return null;
	}
	
	//修改彩期状态
	public String modifyForsaleStatus() {
		logger.info("进入修改销售状态");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType modifyLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType modifyPhaseType = PhaseType.getItem(modifyLotteryType);
		YesNoStatus modifyForsaleStatus = YesNoStatus.getItem(Integer.parseInt(forsaleStatus));
		ResultBean resultBean = null;
		
		try {
			resultBean = phaseService.modifyForsaleStatus(modifyPhaseType, phase, modifyForsaleStatus);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("修改销售状态，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("modifyForsaleStatusName", "");
			rs.put("modifyPhase", "");
			writeRs(response, rs);
			return null;
		}
		
		String messageHeader = "修改["+modifyLotteryType.getName()+"]指定彩期<第"+phase+"期>的销售状态为"+modifyForsaleStatus.getName()+",结果:";
		if (resultBean.isResult()) {
			rs.put("state", "success");
		} else {
			rs.put("state", "failed");
		}
		rs.put("msg", messageHeader+resultBean.getMessage());
		rs.put("modifyForsaleStatusName", modifyForsaleStatus.getName());//修改后的销售状态名称
		rs.put("modifyPhase", phase);//修改的彩期号
		writeRs(response, rs);
		logger.info("修改销售状态结束");
		return null;
	}
	
	//设置当前期
	public String modifyIsCurrentStatus() {
		logger.info("进入设置当前期");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType modifyLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType modifyPhaseType = PhaseType.getItem(modifyLotteryType);
		ResultBean resultBean = null;
		
		Phase phaseTmp = null;
		try {
			phaseTmp = phaseService.getCurrentPhase(modifyPhaseType);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("设置当前期，api调用异常，{}", e.getMessage());
			logger.error("获取原当前期出错");
		}
		String currentHistory = "";
		if (phaseTmp != null) {
			currentHistory = phaseTmp.getPhase();
		} else {
			logger.error("设置当前期，原当前期号获取异常");
			currentHistory = "原当前期期号获取异常";
		}
		
		try {
			resultBean = phaseService.setPhaseCurrent(modifyPhaseType, phase);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("设置当前期，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("modifyPhase", "");
			writeRs(response, rs);
			return null;
		}
		
		String messageHeader = "设置["+modifyLotteryType.getName()+"]指定彩期<第"+phase+"期>为当前期,结果:";
		if (resultBean.isResult()) {
			rs.put("state", "success");
		} else {
			rs.put("state", "failed");
		}
		rs.put("msg", messageHeader+resultBean.getMessage());
		rs.put("modifyPhase", phase);//修改的彩期号
		rs.put("currentHistory", currentHistory);//修改前的当前期号
		writeRs(response, rs);
		logger.info("修改销售状态结束");
		return null;
	}
	
	// 修改彩期终端状态
	public String modifyPhaseTerminalStatus() {
		logger.info("进入修改彩期终端状态");
		JSONObject rs = new JSONObject();
		request = ServletActionContext.getRequest();
		response = ServletActionContext.getResponse();
		if (lotteryTypeValue == null) {
			rs.put("state","failed");
			rs.put("msg", "彩种类型数值不存在,null");
			writeRs(response,rs);
			return null;
		}
		LotteryType modifyLotteryType = LotteryType.getItem(lotteryTypeValue);
		PhaseType modifyPhaseType = PhaseType.getItem(modifyLotteryType);
		TerminalStatus modifyTerminalStatus = TerminalStatus.getItem(Integer.parseInt(terminalStatus));
		ResultBean resultBean = null;
		
		try {
			resultBean = phaseService.modifyPhaseTerminalStatus(modifyPhaseType, phase, modifyTerminalStatus);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("修改彩期终端状态，api调用异常，{}", e.getMessage());
			rs.put("state", "failed");
			rs.put("msg", e.getMessage());
			rs.put("modifyTerminalStatusName", "");
			rs.put("modifyPhase", "");
			writeRs(response, rs);
			return null;
		}
		
		String messageHeader = "修改["+modifyLotteryType.getName()+"]指定彩期<第"+phase+"期>的彩期终端状态为"+modifyTerminalStatus.getName()+",结果:";
		if (resultBean.isResult()) {
			rs.put("state", "success");
		} else {
			rs.put("state", "failed");
		}
		rs.put("msg", messageHeader+resultBean.getMessage());
		rs.put("modifyTerminalStatusName", modifyTerminalStatus.getName());//修改后的彩期终端状态名称
		rs.put("modifyPhase", phase);//修改的彩期号
		writeRs(response, rs);
		logger.info("修改彩期终端状态结束");
		return null;
	}
	
	//获得查询条件
	public HashMap<String,Object> getCondition() {
		HashMap<String,Object> conditionMap = new HashMap<String,Object>();
		Integer tmpInt = null;
		Long tmpLong = null;
		//彩期类型
		if (lotteryTypeValue!=null) {
			PhaseType phaseType = PhaseType.getItem(LotteryType.getItem(lotteryTypeValue));
			conditionMap.put(Phase.QUERY_PHASETYPE, String.valueOf(phaseType.getValue()));
		}
		//彩期号
		if (phase!=null&&phase.trim().length()>0) {
			if (StringUtil.isNumeric(phase)) {
				if (phase.trim().length()<18) {
					try {
						tmpLong = Long.parseLong(phase);
					} catch (Exception e) {
						tmpLong = null;
					}
					if (tmpLong!=null&&tmpLong>0) {
						conditionMap.put(Phase.QUERY_PHASE, phase);
					}
				}
			}
		}
		//彩期匹配
		if (matchPhaseNo != null && matchPhaseNo.trim().length()>0) {
			if (StringUtil.isNumeric(matchPhaseNo)) {
				if (matchPhaseNo.trim().length()<18) {
					try {
						tmpLong = Long.parseLong(matchPhaseNo);
					} catch (Exception e) {
						tmpLong = null;
					}
					if (tmpLong!=null&&tmpLong>0) {
						conditionMap.put(PhaseAction.QUERY_MATCH_PHASE_NO, matchPhaseNo);
					}
				}
			}
		}
		//彩期状态
		if (phaseStatus!=null&&phaseStatus.trim().length()>0) {
			try {
				tmpInt = Integer.parseInt(phaseStatus);
			} catch (Exception e) {
				tmpInt = null;
			}
			if (tmpInt!=null&&tmpInt>0) {
				List<String> list = new ArrayList<String>();
				list.add(phaseStatus);
				conditionMap.put(Phase.QUERY_STATUS, list);
			}
		}
		//是否销售
		if (forsale!=null&&forsale.trim().length()>0) {
			try {
				tmpInt = Integer.parseInt(forsale);
			} catch (Exception e) {
				tmpInt = null;
			}
			if (tmpInt!=null&&tmpInt>0) {
				conditionMap.put(Phase.QUERY_FORSALE, forsale);
			}
			
		}
		//是否当前期
		if (is_current!=null&&is_current.trim().length()>0) {
			try {
				tmpInt = Integer.parseInt(is_current);
			} catch (Exception e) {
				tmpInt = null;
			}
			if (tmpInt!=null&&tmpInt>0) {
				conditionMap.put(Phase.QUERY_IS_CURRENT, is_current);
			}
		}
		//创建时间开始
		if (startCreateTime!=null) {
			conditionMap.put(PhaseAction.QUERY_CREATETIME_START, startCreateTime);
		}
		//创建时间结尾
		if (endCreateTime!=null) {
			conditionMap.put(PhaseAction.QUERY_CREATETIME_END, endCreateTime);
		}
		return conditionMap;
	}
	
	
	public Integer getLastCount() {
		return lastCount;
	}
	public void setLastCount(Integer lastCount) {
		this.lastCount = lastCount;
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
	public String getBatchAmountParam() {
		return batchAmountParam;
	}
	public void setBatchAmountParam(String batchAmountParam) {
		this.batchAmountParam = batchAmountParam;
	}
	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}
	public String getAssignPhaseNo() {
		return assignPhaseNo;
	}
	public void setAssignPhaseNo(String assignPhaseNo) {
		this.assignPhaseNo = assignPhaseNo;
	}
	public PhaseService getPhaseService() {
		return phaseService;
	}
	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public List<PhaseType> getLotteryTypeList() {
		//2011-4-1获取所有彩种列表
		return PhaseType.getItems();
	}
	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}
	public List<YesNoStatus> getYesNoStatusList() {
		return YesNoStatus.getItemsForQuery();
	}
	public void setYesNoStatusList(List<YesNoStatus> yesNoStatusList) {
		this.yesNoStatusList = yesNoStatusList;
	}
	public List<PhaseStatus> getPhaseStatusList() {
		return PhaseStatus.getItemsForQuery();
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
	public String getIs_current() {
		return is_current;
	}
	public void setIs_current(String isCurrent) {
		is_current = isCurrent;
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
	public void setPhaseStatusList(List<PhaseStatus> phaseStatusList) {
		this.phaseStatusList = phaseStatusList;
	}
	public Integer getDisabledPhaseStatus() {
		return PhaseStatus.DISABLED.getValue();
	}
	public void setDisabledPhaseStatus(Integer disabledPhaseStatus) {
		this.disabledPhaseStatus = disabledPhaseStatus;
	}
	public Integer getUnOpenPhaseStatus() {
		return PhaseStatus.UNOPEN.getValue();
	}
	public void setUnOpenPhaseStatus(Integer unOpenPhaseStatus) {
		this.unOpenPhaseStatus = unOpenPhaseStatus;
	}
	public Integer getClosePhaseStatus() {
		return PhaseStatus.CLOSE.getValue();
	}
	public void setClosePhaseStatus(Integer closePhaseStatus) {
		this.closePhaseStatus = closePhaseStatus;
	}
	public Integer getOpenPhaseStatus() {
		return PhaseStatus.OPEN.getValue();
	}
	public void setOpenPhaseStatus(Integer openPhaseStatus) {
		this.openPhaseStatus = openPhaseStatus;
	}
	public Date getStartSaleTime() {
		return startSaleTime;
	}
	public void setStartSaleTime(Date startSaleTime) {
		this.startSaleTime = startSaleTime;
	}
	public Date getEndSaleTime() {
		return endSaleTime;
	}
	public void setEndSaleTime(Date endSaleTime) {
		this.endSaleTime = endSaleTime;
	}
	public Date getEndTicketTime() {
		return endTicketTime;
	}
	public void setEndTicketTime(Date endTicketTime) {
		this.endTicketTime = endTicketTime;
	}
	public Date getDrawTime() {
		return drawTime;
	}
	public void setDrawTime(Date drawTime) {
		this.drawTime = drawTime;
	}
	public Date getBatchStartTime() {
		return batchStartTime;
	}
	public void setBatchStartTime(Date batchStartTime) {
		this.batchStartTime = batchStartTime;
	}
	public YesNoStatus getYesStatus() {
		return yesStatus;
	}
	public void setYesStatus(YesNoStatus yesStatus) {
		this.yesStatus = yesStatus;
	}
	public YesNoStatus getNoStatus() {
		return noStatus;
	}
	public void setNoStatus(YesNoStatus noStatus) {
		this.noStatus = noStatus;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getTerminalStatus() {
		return terminalStatus;
	}
	public void setTerminalStatus(String terminalStatus) {
		this.terminalStatus = terminalStatus;
	}
	public List<TerminalStatus> getTerminalStatusList() {
		return TerminalStatus.getItems();
	}
	public void setTerminalStatusList(List<TerminalStatus> terminalStatusList) {
		this.terminalStatusList = terminalStatusList;
	}
	public String getForsaleStatus() {
		return forsaleStatus;
	}
	public void setForsaleStatus(String forsaleStatus) {
		this.forsaleStatus = forsaleStatus;
	}

	public Phase getPhaseObj() {
		return phaseObj;
	}

	public void setPhaseObj(Phase phaseObj) {
		this.phaseObj = phaseObj;
	}
}
