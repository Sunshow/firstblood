package web.action.business;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lehecai.core.util.CoreMathUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.lottery.ManuallyRechargeService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.utils.ExcelUtil;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ManuallyRechargeType;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.util.CoreNumberUtil;

public class BatchSendCashAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(BatchSendCashAction.class);
	
	private MemberService memberService;
	private ManuallyRechargeService manuallyRechargeService;
	
	private static List<WalletType> walletTypeList = new ArrayList<WalletType>();				//现金钱包类型列表
	
	static {
		walletTypeList.add(WalletType.CASH);
	}
	
	private File excelFile;
	private List<Map<String, String>> resultList;
	
	private Integer walletTypeValue = WalletType.CASH.getValue();								//现金钱包类型
	private List<String> accounts;																//充值账户
	private List<Double> amounts;																//充值金额
	private Integer manuallyRechargeTypeValue = ManuallyRechargeType.RECHARGE_MANUALLY.getValue(); //默认为直接冲钱
	
	public String handle() {
		logger.info("进入批量现金赠送上传excel文件页面");
		return "inputForm";
	}
	
	/**
	 * 确认上传内容
	 * @return
	 */
	public String confirm() {
		logger.info("进入确认上传文件内容");
		if (excelFile == null) {
			logger.error("上传文件为空");
			super.setErrorMessage("上传文件为空");
			return "failure";
		}
		
		Workbook workbook = ExcelUtil.createWorkbook(excelFile);
		if (workbook == null) {
			logger.error("上传的excel文件格式不对");
			super.setErrorMessage("请上传xls或者xlsx格式的文件");
			return "failure";
		}
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			logger.error("获取Excel表Sheet错误");
			super.setErrorMessage("获取Excel表Sheet错误");
			return "failure";
		}
		
		if (sheet.getPhysicalNumberOfRows() <= 1) {
			logger.error("上传文件格式不对");
			super.setErrorMessage("上传文件格式不对");
			return "failure";
		}
		
		logger.info("rows.count：{}", sheet.getPhysicalNumberOfRows());
		
		Map<String, String> map = null;
		resultList = new ArrayList<Map<String, String>>();
		
		map = new HashMap<String, String>();
		map.put(Global.KEY_ACCOUNT, ExcelUtil.getCellValue(sheet.getRow(0).getCell(0)));		//充值账户头信息
		map.put(Global.KEY_AMOUNT, ExcelUtil.getCellValue(sheet.getRow(0).getCell(1)));		//充值金额头信息
		resultList.add(map);
		
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			map = new HashMap<String, String>();
			String account = null;
			try {
				account = ExcelUtil.getCellValue(sheet.getRow(i).getCell(0));		//充值账户
			} catch (Exception e) {
				logger.error("获取充值账户异常，{}", e);
				continue;
			}
			if (account != null && !account.trim().equals("")) {
				logger.info("充值账户：{}", account);
				Long uid = null;
				try {
					uid = memberService.getIdByUserName(account.trim());	//根据用户名查询用户编码
				} catch (ApiRemoteCallFailedException e) {
					logger.error("根据用户名查询用户编码，api调用异常，{}", e.getMessage());
					continue;
				}
				if (uid == null || uid == 0L) {
					logger.error("充值账户不存在");
					continue;
				}
			} else {
				logger.error("充值账户为空");
				continue;
			}
			map.put(Global.KEY_ACCOUNT, account);
			
			Double amountD = null;
			try {
				amountD = CoreMathUtils.keepPointRound2Bit(sheet.getRow(i).getCell(1).getNumericCellValue());
			} catch (Exception e) {
				logger.error("获取充值金额异常，{}", e);
				continue;
			}
			if (amountD == null || amountD == 0.00D) {
				logger.error("充值金额为空");
				continue;
			} else if (amountD < 0.00D) {
				logger.error("充值金额小于0");
				continue;
			} else if (amountD > 1000000.00D) {
				logger.error("充值金额大于1000000");
				continue;
			}
			map.put(Global.KEY_AMOUNT, String.valueOf(amountD));
			
			resultList.add(map);
		}
		
		ExcelUtil.closeExcel();
		
		return "inputForm";
	}

	
	public String manage() {
		Object object = getSession().get("struts.token");
		if(object  == null) {
			super.setErrorMessage("现金已发");
			return "failure";
		}
		getSession().remove("struts.token");
		logger.info("进入派送现金");
		if (accounts == null || amounts == null || accounts.size() == 0 || amounts.size() == 0) {
			logger.error("充值账户或者充值金额为空");
			super.setErrorMessage("充值账户和充值金额都不能为空");
			return "failure";
		}
		
		resultList = new ArrayList<Map<String, String>>();
		
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);	//充值人（登录用户）
		ManuallyRechargeType manuallyRechargeType = ManuallyRechargeType.getItem(manuallyRechargeTypeValue);					//手工充值类型
		WalletType walletType = WalletType.getItem(walletTypeValue);										//现金钱包类型
		for (int i = 0; i < accounts.size(); i++) {
			Map<String, String> resultMap = new HashMap<String, String>(1);
			String account = accounts.get(i);									//充值账户
			Double amount = amounts.get(i);										//充值金额
			
			Long uid = null;
			try {
				uid = memberService.getIdByUserName(account);//通过用户名获取用户编码
			} catch (ApiRemoteCallFailedException e) {
				logger.error("通过用户名获取用户编码，API调用异常，{}", e.getMessage());
				continue;
			}
			if (uid == null || uid == 0L) {
				logger.error("通过用户名获取的用户编码为空");
				continue;
			}
			try {
				manuallyRechargeService.recharge(account, amount,
						null, null, userSessionBean.getUser(), walletType, manuallyRechargeType, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("赠送现金，api调用异常，{}", e.getMessage());
				resultMap.put(account, "赠送现金时，api调用异常：" + e.getMessage());
				resultList.add(resultMap);
				continue;
			}
			
			logger.info("{}账户赠送现金{}成功", account, CoreNumberUtil.formatNumBy2Digits(amount));
			resultMap.put(account, "赠送现金" + CoreNumberUtil.formatNumBy2Digits(amount) + "成功");
			resultList.add(resultMap);
		}
		return "success";
	}
	
	/**
	 * 获取手工充值类型
	 */
	public List<ManuallyRechargeType> getManuallyRechargeTypes() {
		List<ManuallyRechargeType> manuallyRechargeTypes = new ArrayList<ManuallyRechargeType>();
		manuallyRechargeTypes.add(ManuallyRechargeType.RESUPPLY_ORDER);
		manuallyRechargeTypes.add(ManuallyRechargeType.RECHARGE_MANUALLY);
		manuallyRechargeTypes.add(ManuallyRechargeType.REPLENISH_REWARD);
		manuallyRechargeTypes.add(ManuallyRechargeType.COMMISSION);
		manuallyRechargeTypes.add(ManuallyRechargeType.COMPENSATE);
		manuallyRechargeTypes.add(ManuallyRechargeType.OTHERS);
		return manuallyRechargeTypes;
	}
	
	/**
	 * 获取现金钱包类型列表
	 * @return
	 */
	public List<WalletType> getWalletTypeList() {
		return walletTypeList;
	}

	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public ManuallyRechargeService getManuallyRechargeService() {
		return manuallyRechargeService;
	}

	public void setManuallyRechargeService(
			ManuallyRechargeService manuallyRechargeService) {
		this.manuallyRechargeService = manuallyRechargeService;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public List<Map<String, String>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, String>> resultList) {
		this.resultList = resultList;
	}

	public Integer getWalletTypeValue() {
		return walletTypeValue;
	}

	public void setWalletTypeValue(Integer walletTypeValue) {
		this.walletTypeValue = walletTypeValue;
	}

	public List<String> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<String> accounts) {
		this.accounts = accounts;
	}

	public List<Double> getAmounts() {
		return amounts;
	}

	public void setAmounts(List<Double> amounts) {
		this.amounts = amounts;
	}
	
	public Integer getManuallyRechargeTypeValue() {
		return manuallyRechargeTypeValue;
	}

	public void setManuallyRechargeTypeValue(Integer manuallyRechargeTypeValue) {
		this.manuallyRechargeTypeValue = manuallyRechargeTypeValue;
	}
}
