package web.action.partner;

import java.util.ArrayList;
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
import com.lehecai.admin.web.service.partner.PartnerDataApiService;
import com.lehecai.admin.web.service.user.SourceService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.partner.PartnerDataApiLottery;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.opensymphony.xwork2.Action;

public class PartnerDataApiLotteryAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(PartnerDataApiLotteryAction.class);
	
	private PartnerDataApiService partnerDataApiService;
	private SourceService sourceService;
	
	private PartnerDataApiLottery partnerDataApiLottery;
	private List<PartnerDataApiLottery> partnerDataApiLotteryList;
	private Integer lotteryTypeValue;
	private Boolean createFlag;
	private List<LotteryType> lotteryTypeList;
	
	@SuppressWarnings("unchecked")
	public String handle() {
		logger.info("进入查询合作商数据项彩种列表");
		HttpServletRequest request = ServletActionContext.getRequest();
		lotteryTypeList = LotteryType.getItems();
		Map<String, Object> map = null;
		if (partnerDataApiLottery == null || partnerDataApiLottery.getDataApiId() == null || partnerDataApiLottery.getDataApiId() <= 0) {
			super.setErrorMessage("数据项ID为空");
			super.setForwardUrl("/agent/partnerDataApi.do");
			return "failure";
		}
		try {
			PageBean page = super.getPageBean();
			page.setPageSize(20);
			map = partnerDataApiService.getPartnerDataApiLotteryResult(partnerDataApiLottery, page);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			partnerDataApiLotteryList = (List<PartnerDataApiLottery>)map.get(Global.API_MAP_KEY_LIST);
			if (partnerDataApiLotteryList != null) {
				for (PartnerDataApiLottery lottery : partnerDataApiLotteryList) {
					lottery.setCategory(lottery.getCategory() == null ? "" : lottery.getCategory().replace("\n", "<br/>"));
					lottery.setCustomShowName(lottery.getCustomShowName() == null ? "" : lottery.getCustomShowName().replace("\n", "<br/>"));
					lottery.setPhaseType(lottery.getPhaseType() == null ? "" : lottery.getPhaseType().replace("\n", "<br/>"));
					lottery.setDescription(lottery.getDescription() == null ? "" : lottery.getDescription().replace("\n", "<br/>"));
				}
			}
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
		}
		logger.info("查询合作商数据项列表结束");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String input() {
		logger.info("进入输入合作商数据项彩种信息");
		if (partnerDataApiLottery == null || partnerDataApiLottery.getDataApiId() == null ) {
			super.setErrorMessage("数据项ID为空");
			super.setForwardUrl("/agent/partnerDataApi.do");
			return "failure";
		}
		createFlag = true;
		
		Map<String, Object> map = null;
		try {
			//partnerDataApiLottery.setLotteryType(LotteryType.getItem(lotteryTypeValue));
			map = partnerDataApiService.getPartnerDataApiLotteryResult(partnerDataApiLottery, super.getPageBean());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (map != null) {
			if (lotteryTypeValue != null && lotteryTypeValue > 0) {
				partnerDataApiLotteryList = (List<PartnerDataApiLottery>)map.get(Global.API_MAP_KEY_LIST);
				if (partnerDataApiLotteryList == null || partnerDataApiLotteryList.size() == 0) {
					logger.error("未能获取数据项id为{},彩种类型为{}该彩种", partnerDataApiLottery.getDataApiId(), LotteryType.getItem(lotteryTypeValue).getName());
					super.setErrorMessage("未能获取数据项id为" + partnerDataApiLottery.getDataApiId() + ",彩种类型为" + LotteryType.getItem(lotteryTypeValue).getName()+"该彩种");
					return "failure";
				}
				for (PartnerDataApiLottery item : partnerDataApiLotteryList) {
					if (item.getLotteryType().getValue() == lotteryTypeValue) {
						partnerDataApiLottery = item;
						createFlag = false;
					}
				}
			} else {
				partnerDataApiLotteryList = (List<PartnerDataApiLottery>)map.get(Global.API_MAP_KEY_LIST);
				lotteryTypeList = getLotteryTypeItemsNoStatic();
				if (partnerDataApiLotteryList != null && partnerDataApiLotteryList.size() > 0) {
					for (PartnerDataApiLottery item : partnerDataApiLotteryList) {
						for (LotteryType lotteryType : lotteryTypeList) {
							if (lotteryType.getValue() == item.getLotteryType().getValue()) {
								lotteryTypeList.remove(lotteryType);
								break;
							}
						}
					}
				}
			}
		} else {
			lotteryTypeList = this.getLotteryTypeItemsNoStatic();
		}
		logger.info("输入合作商数据项彩种信息");
		return "inputForm";
	}
	
	public String manage() {
		logger.info("进入保存合作商数据项彩种");
		if (partnerDataApiLottery == null || partnerDataApiLottery.getDataApiId() == null || partnerDataApiLottery.getDataApiId() < 0) {
			super.setErrorMessage("数据项ID为空");
			return "failure";
		}
		try {
			if (lotteryTypeValue == null) {
				super.setErrorMessage("彩种为空");
				return "failure";
			}
			partnerDataApiLottery.setLotteryType(LotteryType.getItem(lotteryTypeValue));
			if (createFlag == null || createFlag) {
				partnerDataApiService.createPartnerDataApiLottery(partnerDataApiLottery);
			} else {
				partnerDataApiService.updatePartnerDataApiLottery(partnerDataApiLottery);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
	
		logger.info("保存合作商数据项彩种结束");
		super.setForwardUrl("/agent/partnerDataApiLottery.do?partnerDataApiLottery.dataApiId="+partnerDataApiLottery.getDataApiId());
		return "success";
	}
	
	public String del() {
		logger.info("进入删除合作商数据项彩种");
		JSONObject rs = new JSONObject();
		rs.put("flag", "0");
		if (partnerDataApiLottery == null || partnerDataApiLottery.getDataApiId() == null || partnerDataApiLottery.getDataApiId() < 0) {
			rs.put("msg", "数据项ID为空");
		}
		try {
			if (lotteryTypeValue == null) {
				rs.put("msg", "彩种为空");
			}
			partnerDataApiLottery.setLotteryType(LotteryType.getItem(lotteryTypeValue));
			partnerDataApiService.delLottery(partnerDataApiLottery);
			rs.put("flag", "1");
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			rs.put("msg", "api调用异常，请联系技术人员!" + e.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("删除合作商数据项彩种结束");
		return Action.NONE;
	}
	
	public PartnerDataApiService getPartnerDataApiService() {
		return partnerDataApiService;
	}

	public void setPartnerDataApiService(
			PartnerDataApiService partnerDataApiService) {
		this.partnerDataApiService = partnerDataApiService;
	}
	
	private List<LotteryType> getLotteryTypeItemsNoStatic() {
		List<LotteryType> lotteryList = new ArrayList<LotteryType>();
		for (LotteryType lotteryType : LotteryType.getItems()) {
			if (lotteryType.getValue() != LotteryType.ALL.getValue()) {
				lotteryList.add(lotteryType);
			}
		}
		return lotteryList;
	}

	public void setSourceService(SourceService sourceService) {
		this.sourceService = sourceService;
	}

	public SourceService getSourceService() {
		return sourceService;
	}


	public void setPartnerDataApiLottery(PartnerDataApiLottery partnerDataApiLottery) {
		this.partnerDataApiLottery = partnerDataApiLottery;
	}


	public PartnerDataApiLottery getPartnerDataApiLottery() {
		return partnerDataApiLottery;
	}


	public void setPartnerDataApiLotteryList(
			List<PartnerDataApiLottery> partnerDataApiLotteryList) {
		this.partnerDataApiLotteryList = partnerDataApiLotteryList;
	}


	public List<PartnerDataApiLottery> getPartnerDataApiLotteryList() {
		return partnerDataApiLotteryList;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setCreateFlag(Boolean createFlag) {
		this.createFlag = createFlag;
	}

	public Boolean getCreateFlag() {
		return createFlag;
	}

	public void setLotteryTypeList(List<LotteryType> lotteryTypeList) {
		this.lotteryTypeList = lotteryTypeList;
	}

	public List<LotteryType> getLotteryTypeList() {
		return lotteryTypeList;
	}

}
