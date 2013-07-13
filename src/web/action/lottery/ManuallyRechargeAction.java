package web.action.lottery;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.ManuallyRechargeService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;
import com.opensymphony.xwork2.Action;

public class ManuallyRechargeAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(ManuallyRechargeAction.class);

	private ManuallyRechargeService manuallyRechargeService;
	private MemberService memberService;

	private String account;
	private Double amount;
	private String orderId;
	private String payNo;
	private String bankTypeId;
	private Integer walletType;
	private Integer manuallyRechargeType;
	
	private static List<WalletType> manuallyRechargeWalletTypes = new ArrayList<WalletType>();
	
	static {
		List<WalletType> walletTypeAllList = WalletType.getItems();
		for (WalletType walletType : walletTypeAllList) {
			if (walletType.getValue() == WalletType.ALL.getValue()
					|| walletType.getValue() == WalletType.SYSTEM.getValue()
					|| walletType.getValue() == WalletType.CASH.getValue()) {
				continue;
			}
			manuallyRechargeWalletTypes.add(walletType);
		}
	}

	public String handle() {
		logger.info("进入人工充值和补单输入页面");
		return "inputForm";
	}

	public String manage() {
		logger.info("进入人工充值和补单");
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession()
				.get(Global.USER_SESSION);
		if (account == null || "".equals(account)) {
			logger.error("充值账户为空");
			super.setErrorMessage("要充值账户不能为空");
			return "failure";
		} else {
			Long sUser;
			try {
				sUser = memberService.getIdByUserName(account);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("api调用异常，请联系技术人员原因:" + e.getMessage());
				return "failure";
			}
			if (sUser == null || sUser == 0) {
				logger.error("充值账户不存在");
				super.setErrorMessage("充值账户不存在");
				return "failure";
			}
		}
		if (amount == null || amount == 0.00D) {
			logger.error("充值金额为空");
			super.setErrorMessage("充值金额不能为空");
			return "failure";
		} else if (amount < 0.00D) {
			logger.error("充值金额小于0");
			super.setErrorMessage("充值金额不能小于0");
			return "failure";
		} else if (amount > 1000000.00D) {
			logger.error("充值金额不能于1000000");
			super.setErrorMessage("充值金额不能大于1000000");
			return "failure";
		}
		
		if (manuallyRechargeType == null || manuallyRechargeType == 0) {
			logger.error("操作类型为空");
			super.setErrorMessage("操作类型不能为空");
			return "failure";
		}
		if (manuallyRechargeType == ManuallyRechargeType.RESUPPLY_ORDER
				.getValue()
				&& (orderId == null || orderId.isEmpty())) {
			logger.error("补单操作时订单编码为空");
			super.setErrorMessage("补单操作时订单编码不能为空");
			return "failure";
		}
		
		ManuallyRechargeType mrt = manuallyRechargeType == null ? null : ManuallyRechargeType.getItem(manuallyRechargeType);
		
		WalletType wt = null;
		if (mrt != null && mrt.getValue() == ManuallyRechargeType.PRESENT_REFUND.getValue()) {
			wt = walletType == null ? null : WalletType.getItem(walletType);
		}
		try {
			manuallyRechargeService.recharge(account, amount,
					orderId, payNo, userSessionBean.getUser(), wt, mrt, bankTypeId, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage(e.getMessage());
			return "failure";
		}
		
		logger.info("对["+account+"]进行"+mrt.getName()+amount+"元成功");
		super.setSuccessMessage("对["+account+"]进行"+mrt.getName()+amount+"元成功");
		logger.info("人工充值和补单结束");
		return "success";
	}

	public String check() {
		logger.info("进入检验账户名");
		HttpServletResponse response = ServletActionContext.getResponse();
		boolean flag = false;
		Long sUser;
		try {
			sUser = memberService.getIdByUserName(account);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (sUser != null && sUser != 0) {
			flag = true;
		}
		PrintWriter out = null;
		response.setContentType("text/html; charset=utf-8");
		try {
			out = response.getWriter();
			// 不能用println，会多打出一个换行
			out.print(flag);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("检验账户名结束");
		return Action.NONE;
	}

	//银行列表
	public List<BankType> getBankTypeItems(){
		return BankType.getItems();
	}
	
	public List<ManuallyRechargeType> getManuallyRechargeTypes() {
		return ManuallyRechargeType.getValidItems();
	}

	public ManuallyRechargeService getManuallyRechargeService() {
		return manuallyRechargeService;
	}

	public void setManuallyRechargeService(
			ManuallyRechargeService manuallyRechargeService) {
		this.manuallyRechargeService = manuallyRechargeService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public Integer getManuallyRechargeType() {
		return manuallyRechargeType;
	}

	public void setManuallyRechargeType(Integer manuallyRechargeType) {
		this.manuallyRechargeType = manuallyRechargeType;
	}

	public String getBankTypeId() {
		return bankTypeId;
	}

	public void setBankTypeId(String bankTypeId) {
		this.bankTypeId = bankTypeId;
	}

	public Integer getWalletType() {
		return walletType;
	}

	public void setWalletType(Integer walletType) {
		this.walletType = walletType;
	}

	public ManuallyRechargeType getPresentRefundType() {
		return ManuallyRechargeType.PRESENT_REFUND;
	}
	
	public List<WalletType> getManuallyRechargeWalletTypes() {
		return manuallyRechargeWalletTypes;
	}
}
