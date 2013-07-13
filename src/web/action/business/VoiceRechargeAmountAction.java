package web.action.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.business.VoiceRechargeAmountService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.business.VoiceRechargeAmount;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;
import com.opensymphony.xwork2.Action;

/**
 * 语音充值限额管理
 * @author He Wang
 *
 */
public class VoiceRechargeAmountAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(VoiceRechargeAmountAction.class);
	
	private VoiceRechargeAmountService voiceRechargeAmountService;
	private MemberService memberService;
	
	private List<YesNoStatus> statusList;
	private List<VoiceRechargeAmount> voiceRechargeAmountList;
	
	//语音充值限额对象
	private VoiceRechargeAmount voiceRechargeAmount;
	private Long id;
	//状态
	private Integer statusValue;
	//钱包类型
	private Integer walletTypeValue;
	
	private List<WalletType> walletTypeList;
	
	private String ids;
	
	


	/**
	 * 条件并分页查询语音充值限额
	 * @return
	 */
	public String handle() {
		logger.info("进入查询所有语音充值限额");
		walletTypeList = new ArrayList<WalletType>();
		walletTypeList.add(WalletType.ALL);
		walletTypeList.add(WalletType.CASH);
		statusList = YesNoStatus.getItemsForQuery();
		statusValue = YesNoStatus.YES.getValue();
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询所有语音充值限额");
		HttpServletRequest request = ServletActionContext.getRequest();
        Map<String, Object> map = null;
        walletTypeList = new ArrayList<WalletType>();
		walletTypeList.add(WalletType.ALL);
		walletTypeList.add(WalletType.CASH);
		statusList = YesNoStatus.getItemsForQuery();
        if (walletTypeValue != null) {
        	voiceRechargeAmount.setStatus(YesNoStatus.getItem(statusValue));
        }
        try {
            map = voiceRechargeAmountService.queryVoiceRechargeAmountList(voiceRechargeAmount, super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("查询语音充值限额,api调用异常" + e.getMessage());
            super.setErrorMessage("查询语音充值限额,api调用异常" + e.getMessage());
            return "failure";
        }
        if (map == null || map.size() == 0) {
            logger.error("API查询语音充值限额为空");
            super.setErrorMessage("API查询语音充值限额为空");
            return "failure";
        }
        voiceRechargeAmountList = ((List<VoiceRechargeAmount>) map.get(Global.API_MAP_KEY_LIST));

        PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
        super.setPageString(PageUtil.getPageString(request, pageBean));
        super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
        logger.info("查询语音充值限额结束");
		return "list";
	}
	
	public String input() {
		logger.info("进入添加语音充值限额");
		walletTypeList = new ArrayList<WalletType>();
		walletTypeList.add(WalletType.CASH);
		statusList = YesNoStatus.getItems();
		
		if (voiceRechargeAmount == null || voiceRechargeAmount.getId() == null) {
			voiceRechargeAmount = new VoiceRechargeAmount();
			return "inputForm";
		} else {
			try {
				voiceRechargeAmount = voiceRechargeAmountService.get(voiceRechargeAmount.getId());
			} catch (ApiRemoteCallFailedException e) {
	            logger.error("查询语音充值限额,api调用异常" + e.getMessage());
	            super.setErrorMessage("查询语音充值限额,api调用异常" + e.getMessage());
	            return "failure";
	        }
		}
		return "inputForm";
	}
	
	public String batchOperate() {
		logger.info("进入批量操作语音充值限额");
		JSONObject rs = new JSONObject();
		rs.put("flag", "0");
		if(ids == null) {
			logger.error("批量操作语音充值限额ids为空");
			rs.put("msg", "批量操作语音充值限额ids为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		String[] idArray = StringUtils.split(ids, ",");
		if (idArray == null || idArray.length == 0) {
			logger.error("批量操作语音充值限额ids所转化数组为空");
			rs.put("msg", "批量操作语音充值限额ids所转化数组为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		if (statusValue == null) {
			logger.error("批量操作语音充值限额操作方式为空");
			rs.put("msg", "批量操作语音充值限额操作方式为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		YesNoStatus status = YesNoStatus.getItem(statusValue);
		if (status == null) {
			logger.error("批量操作语音充值限额操作方式数据不正确");
			rs.put("msg", "批量操作语音充值限额操作方式数据不正确");
			writeRs(ServletActionContext.getResponse(), rs);
			return Action.NONE;
		}
		try {
			voiceRechargeAmountService.batchOperate(idArray, status);
			rs.put("flag", "1");
		} catch (ApiRemoteCallFailedException e) {
			logger.error("批量操作语音充值限额,api调用异常" + e.getMessage());
			rs.put("msg", "批量操作语音充值限额,api调用异常" + e.getMessage());
		}
		writeRs(ServletActionContext.getResponse(), rs);
		logger.info("批量操作语音充值限额结束");
		return Action.NONE;
	}
	
	public String manage() {
		logger.info("进入保存语音充值限额");
		
		if (voiceRechargeAmount == null) {
			logger.error("语音充值限额不能为空");
			super.setErrorMessage("语音充值限额不能为空");
			return "failure";
		}
		if (StringUtils.isEmpty(voiceRechargeAmount.getUserName())) {
			logger.error("用户名不能为空");
			super.setErrorMessage("用户名不能为空");
			return "failure";
		}
		if (walletTypeValue == null || walletTypeValue <= 0) {
			logger.error("钱包类型不能为空");
			super.setErrorMessage("钱包类型不能为空");
			return "failure";
		}
		if (WalletType.getItem(walletTypeValue) == null) {
			logger.error("钱包类型错误，未能找到该钱包类型");
			super.setErrorMessage("钱包类型错误，未能找到该钱包类型");
			return "failure";
		}
		if (statusValue == null || statusValue < 0) {
			logger.error("状态不能为空");
			super.setErrorMessage("状态不能为空");
			return "failure";
		}
		if (YesNoStatus.getItem(statusValue) == null) {
			logger.error("状态错误，未能找到该状态");
			super.setErrorMessage("状态错误，未能找到状态");
			return "failure";
		}
		if (voiceRechargeAmount.getAmount() == null) {
			logger.error("金额不能为空");
			super.setErrorMessage("金额不能为空");
			return "failure";
		}
		try {
			Member member = memberService.get(voiceRechargeAmount.getUserName());
			if (member == null) {
				logger.error("未能找到该用户名对应用户");
				super.setErrorMessage("未能找到该用户名对应用户");
				return "failure";
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("api调用异常" + e.getMessage());
            super.setErrorMessage("api调用异常" + e.getMessage());
            return "failure";
		} catch (Exception e) {
			logger.error("api调用出现运行时异常" + e.getMessage());
            super.setErrorMessage("api调用出现运行时异常" + e.getMessage());
            return "failure";
		}
		try {
			voiceRechargeAmount.setStatus(YesNoStatus.getItem(statusValue));
			voiceRechargeAmount.setWalletType(WalletType.getItem(walletTypeValue));
			if (voiceRechargeAmount.getId() == null || voiceRechargeAmount.getId() <= 0) {
				voiceRechargeAmountService.add(voiceRechargeAmount);
			} else {
				voiceRechargeAmountService.update(voiceRechargeAmount);
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error("api调用异常" + e.getMessage());
            super.setErrorMessage("api调用异常" + e.getMessage());
            return "failure";
		} catch (Exception e) {
			logger.error("api调用出现运行时异常" + e.getMessage());
            super.setErrorMessage("api调用出现运行时异常" + e.getMessage());
            return "failure";
		}
		
		logger.info("保存语音充值限额结束");
        super.setSuccessMessage("保存语音充值限额成功");
        super.setForwardUrl("/business/voiceRechargeAmount.do");
        return "success";
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setVoiceRechargeAmountService(VoiceRechargeAmountService voiceRechargeAmountService) {
		this.voiceRechargeAmountService = voiceRechargeAmountService;
	}

	public VoiceRechargeAmountService getVoiceRechargeAmountService() {
		return voiceRechargeAmountService;
	}

	public void setVoiceRechargeAmount(VoiceRechargeAmount voiceRechargeAmount) {
		this.voiceRechargeAmount = voiceRechargeAmount;
	}

	public VoiceRechargeAmount getVoiceRechargeAmount() {
		return voiceRechargeAmount;
	}

	public void setVoiceRechargeAmountList(List<VoiceRechargeAmount> voiceRechargeAmountList) {
		this.voiceRechargeAmountList = voiceRechargeAmountList;
	}

	public List<VoiceRechargeAmount> getVoiceRechargeAmountList() {
		return voiceRechargeAmountList;
	}

	public void setStatusList(List<YesNoStatus> statusList) {
		this.statusList = statusList;
	}

	public List<YesNoStatus> getStatusList() {
		return statusList;
	}
	
	public List<WalletType> getWalletTypeList() {
		return walletTypeList;
	}

	public void setStatusValue(Integer statusValue) {
		this.statusValue = statusValue;
	}

	public Integer getStatusValue() {
		return statusValue;
	}

	public void setWalletTypeValue(Integer walletTypeValue) {
		this.walletTypeValue = walletTypeValue;
	}

	public Integer getWalletTypeValue() {
		return walletTypeValue;
	}

	public void setWalletTypeList(List<WalletType> walletTypeList) {
		this.walletTypeList = walletTypeList;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getIds() {
		return ids;
	}
}
