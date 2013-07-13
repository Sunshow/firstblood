package web.action.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
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
 * 吉林快3彩期同步
 */
public class Jlk3PhaseSynchronizeAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private HttpServletRequest request;
	private PhaseService phaseService;
	private List<Phase> phases;
	
	private String phaseStr;				//被修改彩期号
	private String startSaleTimeStr;		//被修改开始销售时间
	private String endSaleTimeStr;			//被修改结束销售时间
	private String endTicketTimeStr;		//被修改停止出票时间
	private String drawTimeStr;				//被修改开奖时间
	
	private int updatePhaseCount=10;        //默认初始值为0时一次更新20条彩期数据，此处设置为默认更新10条
	private int offsetInterval;             //每期之间的偏移量，单位是秒
	private List<Phase> failedPhaseList;    //放在这里供错误处理调用
	private Date endSaleTime;               //获取到的停止销售时间
	private static long PHASE_PERIOD_NORMAL = 600000L;	// 常规每期销售10分钟
	
	//查询参数区
	private Integer lotteryTypeValue = LotteryType.JLK3.getValue();//吉林快3
	
	/**
	 * 跳转到彩期列表查询页面
	 * @return
	 */
	public String handle() {
		
	   return "list";
	}
	/**
	 * 获取彩期列表
	 * @return
	 */
	public String findOnsalePhaseList() {
		logger.info("进入查询吉林快3在售的彩期列表");
		PhaseType phaseType = null;
		if (lotteryTypeValue == null || lotteryTypeValue == 0) {
			logger.error("查询彩期的彩种类型为空");
			super.setErrorMessage("查询彩期的彩种类型为空");
			return "failure";
		}
		phaseType = PhaseType.getItem(LotteryType.getItem(lotteryTypeValue));	//彩种类型
		try {
			phases = phaseService.findVenderOnSalePhases(phaseType, BIService.KMInfo_Phase_Synchronous);
			if (phases == null || phases.isEmpty()) {
				throw new ApiRemoteCallFailedException("未取到在售彩期列表");
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询吉林快3在售的彩期列表，获取的彩期列表为null，{}",e.getMessage());
			super.setErrorMessage("获取彩期列表为失败，失败信息：" + e.getMessage());
			return "failure";
		}
		
		LotteryType lotteryType = LotteryType.JLK3;
		// 先查找当前彩期            
		Phase specifiedPhase = phaseService.get(lotteryType, phases.get(0).getPhase());
		if (specifiedPhase == null) {
			try {
				throw new ApiRemoteCallFailedException("查找指定彩期" + phases.get(0).getPhase() + "出错");
			} catch (ApiRemoteCallFailedException e) {
				e.printStackTrace();
			}
		}
		endSaleTime=phases.get(0).getEndSaleTime();
		//设置更新彩期的条数
		PageBean pageBean = new PageBean();
		pageBean.setPageSize(updatePhaseCount);
		
		List<Phase> updatePhaseList = null;
		if (failedPhaseList != null && !failedPhaseList.isEmpty()) {
			// 如果有失败列表，说明是失败重试，只重试失败的部分
			updatePhaseList = failedPhaseList;
		} else {
			// 计算偏移
			long specified = endSaleTime.getTime();
			long dbtime = specifiedPhase.getEndSaleTime().getTime();
			
			long offset = specified - dbtime;
		
			// 如果获取彩期数据的开奖时间已经过期,则过滤掉这条信息
			updatePhaseList = new ArrayList<Phase>();

			// 获取后续的若干彩期
			try {
				updatePhaseList = phaseService.findByPhaseNoBetween(lotteryType, specifiedPhase.getPhase(), null, pageBean);
				if (updatePhaseList == null || updatePhaseList.isEmpty()) {
					throw new ApiRemoteCallFailedException("查找吉林快3彩期出错");
				}
			} catch (ApiRemoteCallFailedException e) {
				e.printStackTrace();
			}
			
			// 根据offset校准第一期
			Phase pingPhase = updatePhaseList.get(0);
			addOffsetTimeToPhase(pingPhase, offset);
			
			Phase pongPhase = null;
			
			// 根据前一期修正后面的彩期
			for (int i = 1, imax = updatePhaseList.size(); i < imax; i ++) {
				if (pongPhase != null) {
					pingPhase = pongPhase;
				}
				pongPhase = updatePhaseList.get(i);
				
				String pingPhaseDate = CoreDateUtils.formatDate(pingPhase.getEndSaleTime());
				String pongPhaseDate = CoreDateUtils.formatDate(pongPhase.getEndSaleTime());

				// 如果不是同一天的彩期
				// 每天的第一期仅更新开售时间, 我们内部需要保持彩期开售时间的连续性
				if (!pingPhaseDate.equals(pongPhaseDate)) {
					
					Calendar longEndTimeCalendar = Calendar.getInstance();
					longEndTimeCalendar.setTime(pingPhase.getEndSaleTime());
					pongPhase.setStartSaleTime(longEndTimeCalendar.getTime()); // 以上一期的停止销售时间作为本期开售时间
					
					// 后续彩期不作处理
					break;
				}
				
				long period =PHASE_PERIOD_NORMAL;
				Calendar longEndTimeCalendar = Calendar.getInstance();
				longEndTimeCalendar.setTime(pingPhase.getEndSaleTime());
				longEndTimeCalendar.add(Calendar.SECOND, offsetInterval);
				pongPhase.setStartSaleTime(longEndTimeCalendar.getTime());	// 以上一期的停止销售时间作为本期开售时间
				
				Calendar endSaleCalendar = Calendar.getInstance();
				endSaleCalendar.setTime(pongPhase.getStartSaleTime());
				endSaleCalendar.add(Calendar.MILLISECOND, (int)period);
				
				pongPhase.setEndSaleTime(endSaleCalendar.getTime());
				pongPhase.setEndTicketTime(endSaleCalendar.getTime());
				pongPhase.setDrawTime(endSaleCalendar.getTime());
			}
		}
		//把查询到满足条件的彩期返回到页面
		phases.clear();
		for (Phase perphase : updatePhaseList) {
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(perphase.getEndSaleTime());
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			
			if(hour == 21 && minute >= 40 || hour > 21) {
				break;
				
			}else{
				if(perphase.getPhase().endsWith("001")){
					break;
				}
			    phases.add(perphase);
			}
		}
		return "list";
	}
	private void addOffsetTimeToPhase(Phase phase, long offset) {
		Calendar startSaleCalendar = Calendar.getInstance();
		startSaleCalendar.setTime(phase.getStartSaleTime());
		startSaleCalendar.add(Calendar.MILLISECOND, (int)offset);
		phase.setStartSaleTime(startSaleCalendar.getTime());
		
		Calendar endSaleCalendar = Calendar.getInstance();
		endSaleCalendar.setTime(phase.getEndSaleTime());
		endSaleCalendar.add(Calendar.MILLISECOND, (int)offset);
		phase.setEndSaleTime(endSaleCalendar.getTime());
		
		Calendar endTicketCalendar = Calendar.getInstance();
		endTicketCalendar.setTime(phase.getEndTicketTime());
		endTicketCalendar.add(Calendar.MILLISECOND, (int)offset);
		phase.setEndTicketTime(endTicketCalendar.getTime());
		
		Calendar drawCalendar = Calendar.getInstance();
		drawCalendar.setTime(phase.getDrawTime());
		drawCalendar.add(Calendar.MILLISECOND, (int)offset);
		phase.setDrawTime(drawCalendar.getTime());
	}

	/**
	 * 批量修改
	 * @return
	 */
	public String batchUpdate() {
		logger.info("进入批量修改吉林快3彩期");
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
	
	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}
	public List<Phase> getPhases() {
		return phases;
	}

	public void setPhases(List<Phase> phases) {
		this.phases = phases;
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

	public int getUpdatePhaseCount() {
		return updatePhaseCount;
	}

	public void setUpdatePhaseCount(int updatePhaseCount) {
		this.updatePhaseCount = updatePhaseCount;
	}

	public int getOffsetInterval() {
		return offsetInterval;
	}

	public void setOffsetInterval(int offsetInterval) {
		this.offsetInterval = offsetInterval;
	}

	public List<Phase> getFailedPhaseList() {
		return failedPhaseList;
	}

	public void setFailedPhaseList(List<Phase> failedPhaseList) {
		this.failedPhaseList = failedPhaseList;
	}

	public void setEndSaleTime(Date endSaleTime) {
		this.endSaleTime = endSaleTime;
	}
	public Date getEndSaleTime() {
		return endSaleTime;
	}
}
