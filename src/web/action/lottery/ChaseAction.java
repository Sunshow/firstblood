package web.action.lottery;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.ChaseService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.lottery.Chase;
import com.lehecai.core.api.lottery.ChaseDetail;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ChaseStatus;
import com.lehecai.core.lottery.ChaseType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.StopChaseType;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

public class ChaseAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(ChaseAction.class);
	
	private ChaseService chaseService;

	private Chase chase;
	private List<Chase> chases;
	private List<ChaseDetail> chaseDetails;
	
	private String chaseId;
	
	private String planId;
	private String username;
	private Integer lotteryTypeId;
	private Integer stopChaseType;
	private Integer chaseTypeValue;
	private Integer chaseStatus;

	private String content;
	
	private Date beginCreateTime;
	private Date endCreateTime;
	
	private String orderStr;
	private String orderView;
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	public String handle(){
		logger.info("进入追号查询");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query(){
		logger.info("进入追号查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (beginCreateTime == null) {
			beginCreateTime = getDefaultQueryBeginDate();
		}
		
		if (beginCreateTime != null && endCreateTime != null) {
			if (!DateUtil.isSameMonth(beginCreateTime, endCreateTime)) {
				logger.error("开始时间和结束时间不在同一年同一月");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询");
				return "failure";
			}
		}
		
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		ChaseStatus cs = chaseStatus == null ? null : ChaseStatus.getItem(chaseStatus);
		StopChaseType sct = stopChaseType == null ? null : StopChaseType.getItem(stopChaseType);
		ChaseType ct = chaseTypeValue == null ? null : ChaseType.getItem(chaseTypeValue);
		Map<String, Object> map;
		try {
			map = chaseService.getResult(chaseId, planId, username,
					lt, cs, sct, ct, beginCreateTime, endCreateTime, this.getOrderStr(), this
									.getOrderView(), super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if(map != null){			
			chases = (List<Chase>)map.get(Global.API_MAP_KEY_LIST);
			if (chases != null) {
				for (Chase c : chases) {
					c.setChaseInfo(Chase.convertChaseInfoFromJSON(c.getChaseInfo()));
				}
			}
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		logger.info("追号查询结束");
		return "list";
	}
	
	public String listDetail() {
		logger.info("进入追号详情查询");
		try {
			chaseDetails = chaseService.listDetail(chaseId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		super.setForwardUrl("/lottery/chase.do");
		logger.info("追号详情查询结束");
		return "listDetail";
	}
	
	public String chaseView(){
		logger.info("进入追号内容查询");
		try {
			chase = chaseService.get(chaseId);
			chase.setChaseInfo(Chase.convertChaseInfoFromJSON(chase.getChaseInfo()));
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		logger.info("查询追号内容结束");
		return "chaseView";
	}
	
	public String parseContent() {
		logger.info("进入解析追号方案内容");
		JSONObject object = new JSONObject();
		object.put("code", 1);
		object.put("content", "解析追号方案内容失败");
		if (content == null || "".equals(content)) {
			object.put("content", "请指定追号方案内容");
		} 
		else {
			try {
				String parseContent = chaseService.parseContent(LotteryType.getItem(lotteryTypeId), content);
				if (parseContent != null && !parseContent.isEmpty()) {
					object.put("code", 0);
					object.put("content", parseContent);
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				
			}
		}
		writeRs(ServletActionContext.getResponse(), object);
		logger.info("解析追号方案内容结束");
		return null;
	}
	
	public String getOrderStr() {
		if(orderStr == null || "".equals(orderStr)){
			orderStr = Chase.QUERY_ID;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if(orderView == null || "".equals(orderView)){
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(Chase.ORDER_ID, "编号");
		orderStrMap.put(Chase.ORDER_CREATED_TIME, "创建时间");
		orderStrMap.put(Chase.ORDER_AMOUNT, "方案金额");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	public void setOrderStrMap(Map<String, String> orderStrMap) {
		this.orderStrMap = orderStrMap;
	}
	public void setOrderViewMap(Map<String, String> orderViewMap) {
		this.orderViewMap = orderViewMap;
	}
	public List<LotteryType> getLotteryTypes(){
		return OnSaleLotteryList.getForQuery();
	}
	public List<ChaseStatus> getChaseStatuses(){
		return ChaseStatus.getItems();
	}
	public List<StopChaseType> getStopChaseTypes(){
		return StopChaseType.getItems();
	}
	
	public List<ChaseType> getChaseTypes(){
		return ChaseType.getItems();
	}

	public ChaseService getChaseService() {
		return chaseService;
	}

	public void setChaseService(ChaseService chaseService) {
		this.chaseService = chaseService;
	}

	public List<Chase> getChases() {
		return chases;
	}

	public void setChases(List<Chase> chases) {
		this.chases = chases;
	}

	public Integer getStopChaseType() {
		return stopChaseType;
	}

	public void setStopChaseType(Integer stopChaseType) {
		this.stopChaseType = stopChaseType;
	}

	public Integer getChaseStatus() {
		return chaseStatus;
	}

	public void setChaseStatus(Integer chaseStatus) {
		this.chaseStatus = chaseStatus;
	}
	public List<ChaseDetail> getChaseDetails() {
		return chaseDetails;
	}
	public void setChaseDetails(List<ChaseDetail> chaseDetails) {
		this.chaseDetails = chaseDetails;
	}
	public String getChaseId() {
		return chaseId;
	}
	public void setChaseId(String chaseId) {
		this.chaseId = chaseId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Chase getChase() {
		return chase;
	}
	public void setChase(Chase chase) {
		this.chase = chase;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public Date getBeginCreateTime() {
		return beginCreateTime;
	}

	public void setBeginCreateTime(Date beginCreateTime) {
		this.beginCreateTime = beginCreateTime;
	}

	public Date getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime;
	}
	
	public Integer getChaseTypeValue() {
		return chaseTypeValue;
	}

	public void setChaseTypeValue(Integer chaseTypeValue) {
		this.chaseTypeValue = chaseTypeValue;
	}

}
