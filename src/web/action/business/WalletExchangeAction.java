package web.action.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.service.business.WalletExchangeService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.core.api.user.Wallet;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;

/**
 * 钱包兑换Action
 * @author yanweijie
 *
 */
public class WalletExchangeAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(WalletExchangeAction.class);

	private WalletExchangeService walletExchangeService;
	private MemberService memberService;
	
	private List<Wallet> wallets;
	
	private Long uid;
	private String userName;
	private int srcWallet;
	private int desWallet;
	private double amount;
	
	private double balance;
	
	private String msg;
	
	public String handle() {
		logger.info("进入钱包兑换");
		srcWallet = WalletType.ALL.getValue();
		desWallet = WalletType.ALL.getValue();
		return "inputForm";
	}

	public String manage() {
		logger.info("进入兑换钱包开始");
		
		if (uid == null || uid == 0L) {
			logger.error("会员编号为空");
			super.setErrorMessage("会员编号不能为空");
			return "failure";
		} 
		if (srcWallet == -1) {
			logger.error("来源钱包为空");
			super.setErrorMessage("来源钱包不能为空");
			return "failure";
		}
		if (desWallet == -1) {
			logger.error("目标钱包为空");
			super.setErrorMessage("目标钱包不能为空");
			return "failure";
		}
		if (amount == 0d) {
			logger.error("金额为空");
			super.setErrorMessage("金额不能为空");
			return "failure";
		}

		boolean exchangeResult = false;
		try {
			exchangeResult = walletExchangeService.exchangeWallet(uid, srcWallet, desWallet, amount);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("兑换钱包，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		
		if (!exchangeResult) {
			logger.error("钱包兑换失败");
			super.setErrorMessage("钱包兑换失败");
			return "failure";
		} else {
			logger.info(uid + " 编号对应的会员 " + WalletType.getItem(srcWallet).getName() + " 兑换 " + WalletType.getItem(desWallet).getName() + " " + amount + "元");
		}
		
		super.setForwardUrl("/business/walletExchange.do?action=findWallets&uid=" + uid +"&srcWallet=" + srcWallet + "&desWallet=" + desWallet + "&amount=" +amount);
		logger.info("兑换钱包结束");
		return "forward";
	}
	
	
	public String findWallets () {
		logger.info("进入查询会员钱包");
		if ((userName == null || "".equals(userName.trim())) 
				&& (uid == null || uid == 0L)) {
			logger.error("用户名、编号都为空");
			super.setErrorMessage("请填写用户名或者编号");
			return "failure";
		}
		
		if (uid == null || uid == 0L) {
			try {
				userName = userName.trim();
				uid = memberService.getIdByUserName(userName);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询会员编码，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
		} else {
			try {
				userName = memberService.getUserNameById(uid);//根据会员编号查询会员名
			} catch (ApiRemoteCallFailedException e) {
				logger.error("查询会员名，api调用异常，{}", e.getMessage());
				super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
				return "failure";
			}
		}
		try {
			wallets = memberService.getWallets(uid);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("查询会员钱包，api调用异常，{}", e.getMessage());
			super.setErrorMessage("api调用异常，请联系技术人员!原因：" + e.getMessage());
			return "failure";
		}
		
		if (wallets == null || wallets.size() == 0) {
			logger.info("会员钱包列表没有数据");
			srcWallet = WalletType.ALL.getValue();
			desWallet = WalletType.ALL.getValue();
		} else {
			for (Wallet wallet : wallets) {
				if (wallet.getType() == WalletType.CASH) {
					balance = wallet.getBalance();
				}
			}
			if (uid != null && uid != 0L && srcWallet != -1 && desWallet != -1 && amount != 0L) {
				msg = uid + " 编号对应的会员 " + WalletType.getItem(srcWallet).getName() + " 兑换 " + WalletType.getItem(desWallet).getName() + " " + amount + "元";
			} else {
				srcWallet = WalletType.ALL.getValue();
				desWallet = WalletType.ALL.getValue();
			}
		}
		logger.info("查询会员钱包结束");
		return "inputForm";
	}
	
	public List<WalletType> getWalletTypeList () {
		return WalletType.getItems();
	}
	
	public WalletExchangeService getWalletExchangeService() {
		return walletExchangeService;
	}

	public void setWalletExchangeService(WalletExchangeService walletExchangeService) {
		this.walletExchangeService = walletExchangeService;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSrcWallet() {
		return srcWallet;
	}

	public void setSrcWallet(int srcWallet) {
		this.srcWallet = srcWallet;
	}

	public int getDesWallet() {
		return desWallet;
	}

	public void setDesWallet(int desWallet) {
		this.desWallet = desWallet;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public List<Wallet> getWallets() {
		return wallets;
	}

	public void setWallets(List<Wallet> wallets) {
		this.wallets = wallets;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
