package web.action.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.ticket.BettingLimitNumberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LimitNumberType;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PlayType;
import com.lehecai.core.lottery.ticket.BettingLimitNumber;

public class BettingLimitNumberAction extends BaseAction {
	private static final long serialVersionUID = 2116161532365382384L;
	private Logger logger = LoggerFactory.getLogger(BettingLimitNumberAction.class);
	
	private BettingLimitNumberService bettingLimitNumberService;
	
	private BettingLimitNumber bettingLimitNumber;
	
	private List<BettingLimitNumber> bettingLimitNumberList;
	
	private List<LotteryType> lotteryTypeList;
	private List<PlayType> playTypeList;
	private List<LimitNumberType> limitNumberTypeList;
	private List<YesNoStatus> statusList;
	
	private Integer lotteryTypeValue;
	private Integer playTypeValue;
	private Integer limitNumberTypeValue;
	private Integer statusTypeValue;
	private Integer id;
	private Boolean limitTypeFlag;
	private String limitValueTime;
	private String limitValuePhaseNo;

	public String handle() {
		logger.info("进入查询投注限号列表");
		limitNumberTypeList = LimitNumberType.getItemsForQuery();
		statusList = YesNoStatus.getItemsForQuery();
		limitNumberTypeList = LimitNumberType.getItemsForQuery();
		return "list";
	}

	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询投注限号信息");
		HttpServletRequest request = ServletActionContext.getRequest();
		limitNumberTypeList = LimitNumberType.getItemsForQuery();
		statusList = YesNoStatus.getItemsForQuery();
		limitNumberTypeList = LimitNumberType.getItemsForQuery();
		Map<String, Object> map;
		try {
			PageBean pageBean = super.getPageBean();
			if (bettingLimitNumber == null) {
				bettingLimitNumber = new BettingLimitNumber();
			}
			if (lotteryTypeValue != null && lotteryTypeValue > 0) {
				bettingLimitNumber.setLotteryType(LotteryType.getItem(lotteryTypeValue));
			}
			if (playTypeValue != null && playTypeValue > 0) {
				bettingLimitNumber.setPlayType(PlayType.getItem(playTypeValue));
			}
			if (limitNumberTypeValue != null && limitNumberTypeValue > 0) {
				bettingLimitNumber.setLimitNumberType(LimitNumberType.getItem(limitNumberTypeValue));
			}
			if (statusTypeValue != null && statusTypeValue != YesNoStatus.ALL.getValue()) {
				bettingLimitNumber.setStatus(YesNoStatus.getItem(statusTypeValue));
			}
			map = bettingLimitNumberService.getResult(bettingLimitNumber, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			bettingLimitNumberList = (List<BettingLimitNumber>) map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		logger.info("查询投注限号信息结束");
		return "list";
	}
	
	public String input() {
		logger.info("进入输入投注限号信息");
		limitNumberTypeList = LimitNumberType.getItems();
		statusList = YesNoStatus.getItems();
		if (id == null) {
			bettingLimitNumber = new BettingLimitNumber();
		} else {
			try {
				PageBean pageBean = super.getPageBean();
				pageBean.setPageSize(2);
				bettingLimitNumber = bettingLimitNumberService.get(id);
				if (bettingLimitNumber == null){
					logger.error("api调用异常，请联系技术人员!原因:{}", "根据id未能查询到相应条目");
					super.setErrorMessage("api调用异常，请联系技术人员!原因:根据id未能查询到相应条目");
					return "failure";
				}
				limitTypeFlag = getLimitNumberTypeFlag(bettingLimitNumber);
				limitNumberTypeValue = bettingLimitNumber.getLimitNumberType() == null ? null : bettingLimitNumber.getLimitNumberType().getValue();
				statusTypeValue = bettingLimitNumber.getStatus() == null ? null : bettingLimitNumber.getStatus().getValue();
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		}
		return "inputForm";
	}
	
	public String manage() {
		logger.info("进入输入投注限号信息");
		boolean flag = false;
		if (bettingLimitNumber == null) {
			logger.error("投注限号不能为空");
			super.setErrorMessage("投注限号不能为空");
			return "failure";
		}
		if (lotteryTypeValue == null || LotteryType.getItem(lotteryTypeValue) == null) {
			logger.error("投注限号彩种信息不能为空");
			super.setErrorMessage("投注限号中彩种信息不能为空");
			return "failure";
		}
		if (playTypeValue == null || PlayType.getItem(playTypeValue) == null) {
			logger.error("投注限号玩法信息不能为空");
			super.setErrorMessage("投注限号中玩法信息不能为空");
			return "failure";
		}
		if (limitNumberTypeValue == null || LimitNumberType.getItem(limitNumberTypeValue) == null) {
			logger.error("投注限号限号类型信息不能为空");
			super.setErrorMessage("投注限号限号类型信息不能为空");
			return "failure";
		}
		bettingLimitNumber.setLimitNumberType(LimitNumberType.getItem(limitNumberTypeValue));
		if (statusTypeValue == null || YesNoStatus.getItem(statusTypeValue) == null) {
			logger.error("投注限号是否有效信息不能为空");
			super.setErrorMessage("投注限号是否有效信息不能为空");
			return "failure";
		}
		if (StringUtils.isEmpty(limitValueTime) && StringUtils.isEmpty(limitValuePhaseNo)) {
			logger.error("投注限号限制内容不能为空");
			super.setErrorMessage("投注限号限制内容不能为空");
			return "failure";
		} else {
			boolean limitNumberTypeFlag = getLimitNumberTypeFlag(bettingLimitNumber);
			if (limitNumberTypeFlag) {
				if (StringUtils.isEmpty(limitValuePhaseNo)) {
					logger.error("当选择按期数限制投注时请选择期数");
					super.setErrorMessage("当选择按期数限制投注时请选择期数");
					return "failure";
				} else {
					bettingLimitNumber.setLimitValue(limitValuePhaseNo);
				}
			} else {
				if (!limitNumberTypeFlag && StringUtils.isEmpty(limitValueTime)) {
					logger.error("当选择按时间限制投注时请选择日期");
					super.setErrorMessage("当选择按时间限制投注时请选择日期");
					return "failure";
				} else {
					bettingLimitNumber.setLimitValue(limitValueTime);
				}
			}
		}
		if (StringUtils.isEmpty(bettingLimitNumber.getValue())) {
			logger.error("投注限号所限号码不能为空");
			super.setErrorMessage("投注限号所限号码不能为空");
			return "failure";
		}
		try {
			bettingLimitNumber.setLotteryType(LotteryType.getItem(lotteryTypeValue));
			bettingLimitNumber.setPlayType(PlayType.getItem(playTypeValue));
			bettingLimitNumber.setStatus(YesNoStatus.getItem(statusTypeValue));
			if (bettingLimitNumber.getId() == null) {
				flag = bettingLimitNumberService.add(bettingLimitNumber);
			} else {
				flag = bettingLimitNumberService.update(bettingLimitNumber);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}

		if (flag) {
			super.setSuccessMessage("投注限号信息更新成功");
			logger.info("投注限号信息更新成功");
			super.setForwardUrl("/ticket/bettingLimitNumber.do?action=query");
			return "success";
		} else {
			super.setErrorMessage("投注限号信息更新失败");
			logger.error("投注限号信息更新失败");
			return "failure";
		}
		
	}
	
	public String update() {
		logger.info("进入设置投注限号状态");
		bettingLimitNumber = new BettingLimitNumber();
		if (id == null) {
			logger.error("id为空");
			super.setErrorMessage("id为空");
			return "failure";
		}
		if (id <= 0) {
			logger.error("id必须为大于0的整数");
			super.setErrorMessage("id必须为大于0的整数");
			return "failure";
		}
		
		logger.info("设置投注限号状态结束");
		return "forward";
	}
	
	private Boolean getLimitNumberTypeFlag(BettingLimitNumber bettingLimitNumber) {
		Boolean flag = null;
		if (bettingLimitNumber.getLimitNumberType() != null && (bettingLimitNumber.getLimitNumberType().getValue() == LimitNumberType.DESIGNATION_PHASE.getValue() || bettingLimitNumber.getLimitNumberType().getValue() == LimitNumberType.END_DESIGNATION_PHASE.getValue())) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	public void setBettingLimitNumberService(BettingLimitNumberService bettingLimitNumberService) {
		this.bettingLimitNumberService = bettingLimitNumberService;
	}

	public BettingLimitNumberService getBettingLimitNumberService() {
		return bettingLimitNumberService;
	}

	public void setBettingLimitNumber(BettingLimitNumber bettingLimitNumber) {
		this.bettingLimitNumber = bettingLimitNumber;
	}

	public BettingLimitNumber getBettingLimitNumber() {
		return bettingLimitNumber;
	}

	public void setBettingLimitNumberList(List<BettingLimitNumber> bettingLimitNumberList) {
		this.bettingLimitNumberList = bettingLimitNumberList;
	}

	public List<BettingLimitNumber> getBettingLimitNumberList() {
		return bettingLimitNumberList;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}

	public List<LotteryType> getLotteryTypeList() {
		lotteryTypeList = new ArrayList<LotteryType>();
		lotteryTypeList.add(LotteryType.FC3D);
		lotteryTypeList.add(LotteryType.PL3);
		lotteryTypeList.add(LotteryType.X3D);
		return lotteryTypeList;
	}
	
	public LotteryType getLotteryTypeFc3d(){
		return LotteryType.FC3D;
	}


	public void setPlayTypeList(List<PlayType> playTypeList) {
		this.playTypeList = playTypeList;
	}


	public List<PlayType> getPlayTypeList() {
		playTypeList = new ArrayList<PlayType>();
		playTypeList.add(PlayType.FC3D_ZXDS);
		playTypeList.add(PlayType.FC3D_Z6DS);
		playTypeList.add(PlayType.FC3D_Z6HZ);
		playTypeList.add(PlayType.PL3_ZXDS);
		playTypeList.add(PlayType.PL3_Z6DS);
		playTypeList.add(PlayType.PL3_Z6HZ);
		playTypeList.add(PlayType.X3D_1DDS);
		return playTypeList;
	}

	public void setLimitNumberTypeList(List<LimitNumberType> limitNumberTypeList) {
		this.limitNumberTypeList = limitNumberTypeList;
	}

	public List<LimitNumberType> getLimitNumberTypeList() {
		return limitNumberTypeList;
	}

	public void setStatusList(List<YesNoStatus> statusList) {
		this.statusList = statusList;
	}

	public List<YesNoStatus> getStatusList() {
		return statusList;
	}

	public void setStatusTypeValue(Integer statusTypeValue) {
		this.statusTypeValue = statusTypeValue;
	}

	public Integer getStatusTypeValue() {
		return statusTypeValue;
	}

	public void setLimitNumberTypeValue(Integer limitNumberTypeValue) {
		this.limitNumberTypeValue = limitNumberTypeValue;
	}

	public Integer getLimitNumberTypeValue() {
		return limitNumberTypeValue;
	}

	public void setPlayTypeValue(Integer playTypeValue) {
		this.playTypeValue = playTypeValue;
	}

	public Integer getPlayTypeValue() {
		return playTypeValue;
	}

	public void setLimitTypeFlag(Boolean limitTypeFlag) {
		this.limitTypeFlag = limitTypeFlag;
	}

	public Boolean getLimitTypeFlag() {
		return limitTypeFlag;
	}
	
	public LimitNumberType getDesignationPhase() {
		return LimitNumberType.DESIGNATION_PHASE;
	}
	
	public LimitNumberType getEndDesignationPhase() {
		return LimitNumberType.END_DESIGNATION_PHASE;
	}

	public void setLimitValueTime(String limitValueTime) {
		this.limitValueTime = limitValueTime;
	}

	public String getLimitValueTime() {
		return limitValueTime;
	}

	public void setLimitValuePhaseNo(String limitValuePhaseNo) {
		this.limitValuePhaseNo = limitValuePhaseNo;
	}

	public String getLimitValuePhaseNo() {
		return limitValuePhaseNo;
	}

}
