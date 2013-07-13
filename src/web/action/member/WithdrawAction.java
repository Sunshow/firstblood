package web.action.member;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.UserSessionBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.enums.AuditStatus;
import com.lehecai.admin.web.export.WithdrawExport;
import com.lehecai.admin.web.service.business.BankService;
import com.lehecai.admin.web.service.member.MemberConsumptionService;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.RechargeLogService;
import com.lehecai.admin.web.service.member.WithdrawService;
import com.lehecai.admin.web.service.user.ProvinceCityService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.ExcelUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.City;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.MemberConsumption;
import com.lehecai.core.api.user.Province;
import com.lehecai.core.api.user.WithdrawLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.lottery.WithdrawStatus;
import com.lehecai.core.lottery.WithdrawType;
import com.lehecai.core.util.CoreNumberUtil;
import com.lehecai.core.util.CoreStringUtils;
import com.opensymphony.xwork2.Action;

public class WithdrawAction extends BaseAction {
	private static final long serialVersionUID = 2436161530465382824L;
	private final Logger logger = LoggerFactory.getLogger(WithdrawAction.class);
	
	public final static String API_MAP_KEY_LOG = "log";
	public final static String API_MAP_KEY_TIME = "time";
	
	public final static String EXPORT_NAME_ALIPAY = "alipay";
	public final static String EXPORT_NAME_YEEPAY = "yeepay";
	public final static String EXPORT_NAME_SHENGPAY = "shengpay";
	public final static int SIX_LENGTH = 6;
	public final static int THIRD_LENGTH = 3;
	public final static int SECOND_LENGTH = 2;
	public final static int FIRST_LENGTH = 1;
	
	public final static String WITHDRAW_SAME_CARD_STR = "{card}";
	public final static String WITHDRAW_SAME_CARD_NUM = "{cardNum}";
	
	
	public final static String WITHDRAW_SAME_NAME_STR = "{name}";
	public final static String WITHDRAW_SAME_NAME_NUM = "{nameNum}";
	
	//更新状态为待人工处理操作类型-客服操作
	public final static int OPERATION_TYPE_UPDATE_REFUND = 1;
	//更新内部备注是操作类型-财务操作
	public final static int OPERATION_TYPE_UPDATE_REMARK = 2;
	
	private WithdrawService withdrawService;
	private BankService bankService;
	private MemberConsumptionService memberConsumptionService;
	private MemberService memberService;
	private RechargeLogService rechargeLogService;

	private WithdrawLog withdraw;

	private List<WithdrawLog> withdraws;
	private List<MemberConsumption> memberConsumptionList;
	
	private String withdrawLogIdStr;
	private String withdrawLogRemarkStr;
	private int delayedDay;

	private String withdrawId;
	private Long userid;
	private String username;
	private String idData;
	private Integer withdrawStatusId;
	private String bankCardno;
	private List<String> bankTypeValues;
	private Integer withdrawTypeId;
	private Date beginDate;
	private Date endDate;
	private Date beginSuccessDate;
	private Date endSuccessDate;
	private int isExport = YesNoStatus.ALL.getValue();
	private String exportName;

	private String batchNo;

	private String orderStr;
	private String orderView;

	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;

	private InputStream inputStream;
	private String fileName;

	private String alipayAccount;
	private String totalAmount;
	private String totalFee;

	private List<AuditStatus> auditStatus;
	
	private Integer seconds;//倒计时
	
	private ProvinceCityService provinceCityService;
	
	private List<Province> provinces;
	
	private List<City> cities;
	
	private Integer provinceId;
	private Integer walletTypeId;
	
	private List<WalletType> walletTypeList;
	
	private String actionType = "default";
	private String userName;
	private String withdrawIdForExport;
	private Integer chkBank;
	
	private File excelFile;
	
	private double successAmount;
	private double failAmount;
	
	public String handle() {
		logger.info("进入会员汇款数据查询");
		if (beginDate == null) {
			beginDate = getDefaultQueryBeginDate();
		}
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		userName = userSessionBean.getUser().getName();
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入会员汇款数据查询");
		HttpServletRequest request = ServletActionContext.getRequest();
		UserSessionBean userSessionBean = (UserSessionBean) super.getSession().get(Global.USER_SESSION);
		userName = userSessionBean.getUser().getName();
		if (beginDate == null) {
			beginDate = getDefaultQueryBeginDate();
		}
		
		if (beginDate != null && endDate != null) {
			if (!DateUtil.isSameMonth(beginDate, endDate)) {
				logger.info("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		
		List<String> withdrawStatus = new ArrayList<String>();
		if (withdrawStatusId != null && withdrawStatusId != WithdrawStatus.ALL.getValue()) {
			withdrawStatus.add(String.valueOf(withdrawStatusId));
		}
		WithdrawType wt = withdrawTypeId == null ? null : WithdrawType.getItem(withdrawTypeId);
		WalletType walletType = walletTypeId == null ? null : WalletType.getItem(walletTypeId);
		if (walletType == null && this.getWalletTypes() != null && !this.getWalletTypes().isEmpty()) {
			walletType = this.getWalletTypes().get(0);
		}
		Map<String, Object> map;
		try {
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(100);
			map = withdrawService.getResult(withdrawId, username, idData, withdrawStatus, bankCardno, bankTypeValues, wt,
					walletType, beginDate, endDate, beginSuccessDate, endSuccessDate, YesNoStatus.getItem(isExport), batchNo, getOrderStr(), getOrderView(), pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			withdraws = (List<WithdrawLog>) map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean) map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
			if (withdraws != null && withdraws.size() > 0) {
				for (WithdrawLog withdraw : withdraws) {
					if (!CoreStringUtils.isNull(withdraw.getDescription())) {
						withdraw.setDescription(HtmlUtils.htmlUnescape(withdraw.getDescription()));
					}
				}
			}
		}
		
		WithdrawStatus ws = withdrawStatusId == null ? null : WithdrawStatus.getItem(withdrawStatusId);
		Map<String, Object> statisticsMap;
		try {
			statisticsMap = withdrawService.getWithdrawStatistics(withdrawId, username, idData, ws, bankCardno, bankTypeValues, wt,
					walletType, beginDate, endDate, beginSuccessDate, endSuccessDate, YesNoStatus.getItem(isExport), batchNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (statisticsMap != null) {
			Object amountObj = statisticsMap.get(WithdrawLog.QUERY_AMOUNT);
			Object feeObj = statisticsMap.get(WithdrawLog.QUERY_FEE);
			logger.info("格式化之前金额:{}",amountObj);
			if (amountObj == null) {
				logger.info("统计金额为空");
			} else {
				double amountDou = 0;
				try {
					amountDou = Double.parseDouble(amountObj.toString());
				} catch (Exception e) {
					logger.error("统计金额转换成double类型异常，{}", e);
					super.setErrorMessage("统计金额转换成double类型异常");
					return "failure";
				}
				totalAmount = CoreNumberUtil.formatNumBy2Digits(amountDou);
				if (totalAmount == null || "".equals(totalAmount)) {
					logger.error("格式化统计金额异常");
					super.setErrorMessage("格式化统计金额异常");
					return "failure";
				}
				logger.info("格式化之后金额:{}",totalAmount);
			}
			if (feeObj == null) {
				logger.info("统计手续费为空");
			} else {
				double feeDou = 0;
				try {
					feeDou = Double.parseDouble(feeObj.toString());
				} catch (Exception e) {
					logger.error("统计手续费转换成double类型异常，{}", e);
					super.setErrorMessage("统计手续费转换成double类型异常");
					return "failure";
				}
				totalFee = CoreNumberUtil.formatNumBy2Digits(feeDou);
				if (totalFee == null || "".equals(totalFee)) {
					logger.error("格式化统计手续费异常");
					super.setErrorMessage("格式化统计手续费异常");
					return "failure";
				}
				logger.info("格式化之后手续费:{}",totalAmount);
			}
		} else {
			logger.error("统计金额、手续费异常");
			super.setErrorMessage("统计金额、手续费异常");
			return "failure";
		}
		logger.info("查询会员汇款数据结束");
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String view() {
		logger.info("进入查询会员汇款数据详细信息");

		if (withdraw == null || withdraw.getId() == null || withdraw.getId().equals("")) {
			logger.error("会员汇款编码为空");
			super.setErrorMessage("会员汇款编码为空");
			return "failure";
		}
		
		Map<String, Object> map = null;
		try {
			map = withdrawService.getResult(withdraw.getId(),null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			withdraws = (List<WithdrawLog>) map.get(Global.API_MAP_KEY_LIST);
			if (withdraws != null && withdraws.size() != 0) {
				withdraw = withdraws.get(0);
			}
		}
		
		Map<String,Object> provinceMap = null;
		try {
			provinceMap = provinceCityService.getProvinceList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (provinceMap != null) {
			if (provinceMap.get(Global.API_MAP_KEY_LIST)!= null) {
				provinces =(List<Province>)provinceMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}

		Map<String,Object> cityMap = null;
		try {
			int p = withdraw.getProvinceId() == 0 ? 11 : withdraw.getProvinceId();
			cityMap = provinceCityService.getCityListByProvince(p);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (cityMap != null) {
			if (cityMap.get(Global.API_MAP_KEY_LIST)!= null) {
				cities =(List<City>)cityMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}
		
		if (provinces == null || provinces.size() == 0) {
			logger.error("查询省市信息异常");
			super.setErrorMessage("查询省市信息异常");
			return "failure";
		}
		return "view";
	}
	
	@SuppressWarnings("unchecked")
	public String view1() {
		logger.info("进入查询会员汇款数据详细信息");

		if (withdraw == null || withdraw.getId() == null || withdraw.getId().equals("")) {
			logger.error("会员汇款编码为空");
			super.setErrorMessage("会员汇款编码为空");
			return "failure";
		}
		
		Map<String, Object> map = null;
		try {
			map = withdrawService.getResult(withdraw.getId(),null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			withdraws = (List<WithdrawLog>) map.get(Global.API_MAP_KEY_LIST);
			if (withdraws != null && withdraws.size() != 0) {
				withdraw = withdraws.get(0);
				Map<String, String> paramMap = new HashMap<String, String>();
				auditStatus = this.checkAuditStatus(withdraw.getId(), paramMap);
				if (auditStatus == null) {
					logger.error("检查 {} 的审核状态异常", withdraw.getId());
					super.setErrorMessage("检查 " + withdraw.getId() + " 的审核状态异常");
					return "failure";
				}
			}
		}
		
		Map<String,Object> provinceMap = null;
		try {
			provinceMap = provinceCityService.getProvinceList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (provinceMap != null) {
			if (provinceMap.get(Global.API_MAP_KEY_LIST)!= null) {
				provinces =(List<Province>)provinceMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}

		Map<String,Object> cityMap = null;
		try {
			int p = withdraw.getProvinceId() == 0 ? 11 : withdraw.getProvinceId();
			cityMap = provinceCityService.getCityListByProvince(p);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (cityMap != null) {
			if (cityMap.get(Global.API_MAP_KEY_LIST)!= null) {
				cities =(List<City>)cityMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}
		
		if (provinces == null || provinces.size() == 0) {
			logger.error("查询省市信息异常");
			super.setErrorMessage("查询省市信息异常");
			return "failure";
		}
		return "view";
	}
	
	@SuppressWarnings("unchecked")
	public String viewProvinceAndCity() {
		logger.info("进入查询会员汇款省市详细信息");

		if (withdraw == null || withdraw.getId() == null || withdraw.getId().equals("")) {
			logger.error("会员汇款编码为空");
			super.setErrorMessage("会员汇款编码为空");
			return "failure";
		}
		
		Map<String, Object> map = null;
		try {
			map = withdrawService.getResult(withdraw.getId(), null, null, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			withdraws = (List<WithdrawLog>) map.get(Global.API_MAP_KEY_LIST);
			if (withdraws != null && withdraws.size() != 0) {
				withdraw = withdraws.get(0);
			}
		}
		
		Map<String,Object> provinceMap = null;
		try {
			provinceMap = provinceCityService.getProvinceList();
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (provinceMap != null) {
			if (provinceMap.get(Global.API_MAP_KEY_LIST)!= null) {
				provinces =(List<Province>)provinceMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}

		Map<String,Object> cityMap = null;
		try {
			int p = withdraw.getProvinceId() == 0 ? 11 : withdraw.getProvinceId();
			cityMap = provinceCityService.getCityListByProvince(p);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
		}
		if (cityMap != null) {
			if (cityMap.get(Global.API_MAP_KEY_LIST)!= null) {
				cities =(List<City>)cityMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}
		
		if (provinces == null || provinces.size() == 0) {
			logger.error("查询省市信息异常");
			super.setErrorMessage("查询省市信息异常");
			return "failure";
		}
		return "inputForm";
	}
	
	@SuppressWarnings("unchecked")
	public String getCitiesByProvince () {
		logger.info("进入根据省信息查询市信息");
		int rc = 0;
		String msg = "";
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject json = new JSONObject();
		if (provinceId == null || provinceId == 0) {
			logger.error("查询市信息异常，省ID为空 ");
			rc = 1;
			msg = "查询市信息异常，省ID为空 ";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		} 
		
		Map<String,Object> cityMap = null;
		try {
			cityMap = provinceCityService.getCityListByProvince(provinceId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询省信息异常，{}", e.getMessage());
			rc = 1;
			msg = "API调用异常，请联系技术人员! ";
			json.put("code", rc);
			json.put("msg", msg);
			writeRs(response, json);
			return Action.NONE;
		}
		if (cityMap != null) {
			if (cityMap.get(Global.API_MAP_KEY_LIST)!= null) {
				cities =(List<City>)cityMap.get(Global.API_MAP_KEY_LIST) ;
			}
		}
		JSONArray jsonArray = new JSONArray();
		for (City c : cities) {
			JSONObject j = new JSONObject();
			j.put("key", c.getCityId());
			j.put("name", c.getCityName());
			jsonArray.add(j);
		}
		json.put("code", rc);
		json.put("msg", msg);
		json.put("data", jsonArray.toString());
		writeRs(response, json);
		return Action.NONE;
	}
	
	public String viewAuditStatus() {
		logger.info("进入查询审核状态");

		JSONObject rs = new JSONObject();
		
		if (withdraw == null || withdraw.getId() == null || withdraw.getId().equals("")) {
			rs.put("code", 1);
			rs.put("msg", "会员汇款编码为空");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		//查看审核状态
		Map<String, Object> map = null;
		try {
			map = withdrawService.checkAuditStatusNew(withdraw.getId());
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			rs.put("msg", "获取状态失败");
		}
		
		if (map == null) {
			rs.put("code", 1);
			rs.put("msg", "检查 " + withdraw.getId() + " 审核状态异常");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		if (map.get(ApiConstant.API_RESPONSE_CODE_NAME) == null) {
			rs.put("code", 1);
			rs.put("msg", "检查 " + withdraw.getId() + " 审核状态异常");
			super.writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		if ((Integer)map.get(ApiConstant.API_RESPONSE_CODE_NAME) == ApiConstant.RC_SUCCESS) {
			rs.put("msg", "审核通过");
		} else {
			rs.put("msg", (String)map.get(ApiConstant.API_RESPONSE_MESSAGE_NAME));
		}
		rs.put("code", 0);
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String export() {
		logger.info("进入导出会员汇款数据");
		if (StringUtils.isEmpty(withdrawIdForExport)) {
			logger.error("api调用异常，请联系技术人员!原因:未能获取ids值");
			super.setErrorMessage("api调用异常，请联系技术人员!原因:未能获取ids值");
			return "failure";
		}
		
		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(10000);// max 10000 items

		Map<String, Object> map;
		try {
			map = withdrawService.getResultByIds(withdrawIdForExport, pageBean, orderStr, orderView);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			withdraws = (List<WithdrawLog>) map.get(Global.API_MAP_KEY_LIST);
			try {
				Workbook workBook = null;
				String fileName = "";
				if (exportName != null && EXPORT_NAME_ALIPAY.equals(exportName)) {
					workBook = WithdrawExport.exportAlipay(withdraws, alipayAccount, actionType);
					fileName = EXPORT_NAME_ALIPAY + (new Date()).getTime() + ".xls";
				} else if (exportName != null && EXPORT_NAME_YEEPAY.equals(exportName)) {
					workBook = WithdrawExport.exportYeepay(withdraws, actionType);
					fileName = EXPORT_NAME_YEEPAY + (new Date()).getTime() + ".xls";
				} else if (exportName != null && EXPORT_NAME_SHENGPAY.equals(exportName)) {
					if (withdraws != null && withdraws.size() > 3000) {
						logger.error("盛付通：单个文件记录数不能超过3000条；");
						super.setErrorMessage("盛付通：单个文件记录数不能超过3000条；");
						return "failure";
					}
					workBook = WithdrawExport.exportShengpay(withdraws, actionType);
					fileName = EXPORT_NAME_SHENGPAY + (new Date()).getTime() + ".xls";
				} else {
					workBook = WithdrawExport.export(withdraws);
					fileName = (new Date()).getTime() + ".xls";
				}
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				workBook.write(os);
				inputStream = new ByteArrayInputStream(os.toByteArray());
				this.fileName = fileName;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("文件输出流写入错误");
				return "failure";
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				super.setErrorMessage("生成excel文件时错误");
				return "failure";
			}
			logger.info("导出会员汇款数据结束");
			return "download";
		} else {
			logger.info("导出会员汇款数据结束");
			return "failure";
		}
	}
	
	public String handleAccountCheck() {
		logger.info("进入对账单上传页面");
		super.setSuccessMessage("");
		return "importForm";
	}
	
	@SuppressWarnings("unchecked")
	public String dealAccountCheckExcel() {
		logger.info("进入上传对账单");
		if (excelFile == null) {
			logger.error("上传文件为空");
			super.setErrorMessage("上传文件为空");
			super.setForwardUrl("/member/withdraw.do?action=handleAccountCheck");
			return "failure";
		}
		Workbook workbook = ExcelUtil.createWorkbook(excelFile);
		if (workbook == null) {
			logger.error("请上传xls或者xlsx格式的文件");
			super.setErrorMessage("请上传xls或者xlsx格式的文件");
			super.setForwardUrl("/member/withdraw.do?action=handleAccountCheck");
		}
		Sheet sheet = workbook.getSheetAt(0);
		if (sheet == null) {
			logger.error("获取Excel表Sheet错误");
			super.setErrorMessage("获取Excel表Sheet错误");
			super.setForwardUrl("/member/withdraw.do?action=handleAccountCheck");
		}
		if (sheet.getPhysicalNumberOfRows() <= 1) {
			logger.error("上传文件格式不对");
			super.setErrorMessage("上传文件格式不对");
			super.setForwardUrl("/member/withdraw.do?action=handleAccountCheck");
		}
		String batchNoStr = ExcelUtil.getCellValue(sheet.getRow(3).getCell(0));
		String[] batchNoArray = StringUtils.split(batchNoStr, "[");
		String batchNo = "";
		if (batchNoArray.length == 2) {
			batchNo = batchNoArray[1];
			batchNo = StringUtils.replace(batchNo, "]", "").trim();
		}
		if (StringUtils.isEmpty(batchNo)) {
			logger.error("上传文件中未能获取批次号");
			super.setErrorMessage("上传文件中未能获取批次号");
			super.setForwardUrl("/member/withdraw.do?action=handleAccountCheck");
		}
		
		List<WithdrawLog> exsitList = new ArrayList<WithdrawLog>();
		List<WithdrawLog> excelList = new ArrayList<WithdrawLog>();
		Map<String, Object> map;
		try {
			List<String> withdrawStatus = new ArrayList<String>();
			withdrawStatus.add(String.valueOf(WithdrawStatus.REMITTING.getValue()));
			PageBean pageBean = super.getPageBean();
			pageBean.setPageSize(Integer.MAX_VALUE);
			map = withdrawService.getResult(null, null, null, withdrawStatus, null, null, null,
					null, null, null, null, null, YesNoStatus.YES, batchNo, null, null, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			exsitList = (List<WithdrawLog>) map.get(Global.API_MAP_KEY_LIST);
		}
		
		for (int i = 10; i < sheet.getPhysicalNumberOfRows(); i++) {
			try {
				WithdrawLog log = new WithdrawLog();
				String memberSerialNumber = ExcelUtil.getCellValue(sheet.getRow(i).getCell(0));
				Integer.valueOf(memberSerialNumber);
				log.setMemberSerialNumber(ExcelUtil.getCellValue(sheet.getRow(i).getCell(0)));
				log.setBankRealname(ExcelUtil.getCellValue(sheet.getRow(i).getCell(1)));
				log.setBankCardno(ExcelUtil.getCellValue(sheet.getRow(i).getCell(2)));
				log.setBankName(ExcelUtil.getCellValue(sheet.getRow(i).getCell(3)));
				log.setProvinceName(ExcelUtil.getCellValue(sheet.getRow(i).getCell(4)));
				log.setCityName(ExcelUtil.getCellValue(sheet.getRow(i).getCell(5)));
				log.setBankBranch(ExcelUtil.getCellValue(sheet.getRow(i).getCell(6)));
				Double amount = Double.valueOf(ExcelUtil.getCellValue(sheet.getRow(i).getCell(7)));
				log.setAmount(amount);
				String resultStr = ExcelUtil.getCellValue(sheet.getRow(i).getCell(9)).trim();
				if (resultStr.equals("处理成功")) {
					log.setResultStatus(YesNoStatus.YES);
				} else if(resultStr.equals("处理失败")){
					log.setResultStatus(YesNoStatus.NO);
				}
				log.setWithdrawSerialNumber(ExcelUtil.getCellValue(sheet.getRow(i).getCell(10)));
				String remitReturnStr = ExcelUtil.getCellValue(sheet.getRow(i).getCell(11)).trim();
				if (remitReturnStr.equals(YesNoStatus.NO.getName())) {
					log.setRemitReturnStatus(YesNoStatus.NO);
				}
				log.setDescription(ExcelUtil.getCellValue(sheet.getRow(i).getCell(12)));
				log.setId(ExcelUtil.getCellValue(sheet.getRow(i).getCell(13)));
				
				excelList.add(log);
			} catch (Exception e) {
				break;
			}
		}
		withdraws = checkWithdrawList(exsitList, excelList);
		successAmount = 0.0;
		failAmount = 0.0;
		if (withdraws != null) {
			for (WithdrawLog log : withdraws) {
				Double amount = log.getAmount();
				if (log.getResultStatus() != null) {
					if (log.getResultStatus().getValue() == YesNoStatus.YES.getValue()) {
						successAmount += amount;
					} else if (log.getResultStatus().getValue() == YesNoStatus.NO.getValue()) {
						failAmount += amount;
					}
				}
			}
			if (successAmount != 0.0 || failAmount != 0.0) {
				totalAmount = successAmount + failAmount + "";
			}
		}
		super.setSuccessMessage("");
		return "importForm";
	}
	
	public String batchDealStatus() {
		logger.info("进入上传对账单");
		int succNum = 0;
		if (withdraws != null) {
			for (WithdrawLog log : withdraws) {
				if (log.getResultStatusValue() == YesNoStatus.YES.getValue()) {
					try {
						boolean flag = withdrawService.remit(log.getId(), log.getWithdrawSerialNumber());
						if(!flag){
							logger.error("变更汇款状态为汇款成功失败，请联系管理员!");
						} else {
							succNum++;
						}
					} catch (ApiRemoteCallFailedException e) {
						logger.error(e.getMessage(),e);
					}
				} else if(log.getResultStatusValue() == YesNoStatus.NO.getValue()) {
					try {
						boolean flag = withdrawService.remitFailure(log.getId());
						if(!flag){
							logger.error("变更汇款状态为汇款失败失败，请联系管理员!");
						} else {
							flag = withdrawService.updateDescription(log.getId(), log.getDescription());
							if (flag) {
								flag = withdrawService.updateRemark(log.getId(), log.getRemark(), OPERATION_TYPE_UPDATE_REMARK);
								if (flag) {
									succNum++;	
								}
							}
						}
					} catch (ApiRemoteCallFailedException e) {
						logger.error(e.getMessage(),e);
					}
				}
			}
			super.setSuccessMessage("本次操作成功" + succNum + "笔，如与传入笔数不符请手动核对。");
			withdraws = new ArrayList<WithdrawLog>();
		} else {
			super.setSuccessMessage("数据为空");
		}
		return "importForm";
	}
	
	private List<WithdrawLog> checkWithdrawList(List<WithdrawLog> exsitList, List<WithdrawLog> excelList) {
		List<WithdrawLog> checkedList = new ArrayList<WithdrawLog>();
		Map<String, WithdrawLog> exsitMap = new HashMap<String, WithdrawLog>();
		for (WithdrawLog exsitLog : exsitList) {
			String id = exsitLog.getId();
			exsitMap.put(id, exsitLog);
		}
		for (WithdrawLog importLog : excelList) {
			String id = importLog.getId();
			WithdrawLog exsitLog = exsitMap.get(id);
			if (exsitLog != null) {
				double fee = exsitLog.getFee() == null ? 0.0 : exsitLog.getFee();
				double exsitAmount = exsitLog.getAmount() - fee;
				if (exsitLog.getBankRealname().equals(importLog.getBankRealname()) && exsitLog.getBankCardno().equals(importLog.getBankCardno()) && exsitAmount == importLog.getAmount()) {
					//失败时需要保留以前的备注，追加新的备注。
					if (importLog.getResultStatus() != null && importLog.getResultStatus().getValue() == YesNoStatus.NO.getValue()) {
						String exsitRemark = exsitLog.getRemark() == null ? "" : exsitLog.getRemark();
						String exsitDescription = exsitLog.getDescription() == null ? "" : exsitLog.getDescription();
						importLog.setRemark(exsitRemark + ";" + exsitDescription);
					}
					checkedList.add(importLog);
				}
			}
		}
		return checkedList;
	}
	
	/**
	 * 批量变更汇款状态为开始处理状态
	 * @return
	 */
	public String batchHandling() {
		logger.info("进入批量变更汇款状态为开始处理状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.handling(withdrawLogId);
				if(!flag){
					logger.error("变更汇款状态为开始处理状态失败");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为开始处理状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为开始处理状态失败，请联系管理员");
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为开始处理状态结束");
		return null;
	}
	
	/**
	 * 批量汇款审核
	 * @return
	 */
	public String batchAudit() {
		logger.info("进入汇款审核");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			
			//查看审核状态
			Map<String, Object> map = null;
			try {
				map = withdrawService.checkAuditStatusNew(withdrawLogId);
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(), e);
				rs.put("msg", "汇款编码为空");
			}
			
			if (map == null) {
				logger.error("检查审核状态时返回值map异常");
				jsonObject.put("code", 1);
				jsonObject.put("msg", "检查审核状态时返回值map异常，请联系管理员");
				return null;
			}
			if (map.get(ApiConstant.API_RESPONSE_CODE_NAME) == null) {
				logger.error("检查审核状态时返回值map中code为空异常");
				jsonObject.put("code", 1);
				jsonObject.put("msg", "检查审核状态异常，请联系管理员");
				return null;
			}
			//审核通过
			if ((Integer)map.get(ApiConstant.API_RESPONSE_CODE_NAME) == ApiConstant.RC_SUCCESS) {
				try {
					boolean auditResult = withdrawService.approve(withdrawLogId, WithdrawStatus.APPROVE);
					if(!auditResult){
						logger.error("变更汇款状态为批准状态失败");
						jsonObject.put("code", 1);
						jsonObject.put("msg", "变更汇款状态为批准状态失败，请联系管理员");
					}
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(),e);
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为批准状态失败，请联系管理员");
				}
			} else {
				jsonObject.put("code", 1);
				String auditStatuStr = (String)map.get(ApiConstant.API_RESPONSE_MESSAGE_NAME);
				jsonObject.put("auditStatuStr", auditStatuStr);
				try {
					withdrawService.updateRemark(withdrawLogId, auditStatuStr, OPERATION_TYPE_UPDATE_REMARK);
				} catch (ApiRemoteCallFailedException e) {
					logger.error("API修改备注异常，{}", e.getMessage());
					return null;
				}
				//jsonObject.put("auditStatus", JSONArray.fromObject(jsonList));
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("汇款审核结束");
		return null;
	}

	/**
	 * 检查审核状态
	 * @param withdrawLogId
	 * @return
	 */
	private List<AuditStatus> checkAuditStatus (String withdrawLogId, Map<String,String> paramMap) {
		Map<String, Object> map = null;
		try {
			map = withdrawService.checkAuditStatus(withdrawLogId);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return null;
		}
		
		if (map != null) {
			List<AuditStatus> auditStatusList = new ArrayList<AuditStatus>();
			JSONArray tempJSONArray = (JSONArray)map.get(Global.API_MAP_KEY_LIST);
			List<AuditStatus> booleanItems = AuditStatus.getBooleanItemsForCheck();
			List<AuditStatus> intItems = AuditStatus.getIntItemsForCheck();
			for (int i = 0;i < tempJSONArray.size();i++) {
				JSONObject obj = (JSONObject)tempJSONArray.get(i);
				for (AuditStatus s : booleanItems) {
					if (obj.getBoolean(s.getValue()+"")) {
						auditStatusList.add(s);
					}
				}
				for (AuditStatus s : intItems) {
					Object tempObj = obj.get(s.getValue()+"");
					if (tempObj instanceof String) {
						if (s.getValue() == AuditStatus.WITHDRAW_SAME_CARD_24HOURS.getValue()) {
							paramMap.put(WITHDRAW_SAME_CARD_NUM, obj.getString(s.getValue()+""));
						} else {
							paramMap.put(WITHDRAW_SAME_NAME_NUM, obj.getString(s.getValue()+""));
						}
						auditStatusList.add(s);
					}
				}
			}
			
			return auditStatusList;
		}
		
		return null;
	}
	
	/**
	 * 批量变更汇款状态为批准状态
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String batchApprove() {
		logger.info("进入批量变更汇款状态为批准状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.approve(withdrawLogId, WithdrawStatus.APPROVE);
				if(!flag){
					logger.error("变更汇款状态为批准状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为批准状态失败，请联系管理员!");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为批准状态失败，请联系管理员!");
			}
			Map<String, Object> map = null;
			try {
				map = withdrawService.getResult(withdrawLogId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询汇款记录异常，{}", e.getMessage());
				jsonObject.put("code", 1);
				jsonObject.put("msg", "API查询汇款记录异常，请联系管理员!");
			}
			if (map != null) {
				List<WithdrawLog> withdrawLogList = (List<WithdrawLog>)map.get(Global.API_MAP_KEY_LIST);
				if (withdrawLogList != null && withdrawLogList.size() > 0) {
					WithdrawLog tempWithdrawLog = withdrawLogList.get(0);
					jsonObject.put("exportStatus", tempWithdrawLog.getExportStatus().getValue());
				}
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为批准状态结束");
		return null;
	}
	
	

	/**
	 * 批量变更汇款状态为拒绝状态
	 * @return
	 */
	public String batchRefuse() {
		logger.info("进入变更汇款状态为拒绝状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			
			try {
				boolean flag = withdrawService.refuse(withdrawLogId);
				if(!flag){
					logger.error("批量变更汇款状态为拒绝状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为拒绝状态失败，请联系管理员!");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为拒绝状态失败，请联系管理员!");
			}
			
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}

		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为拒绝状态");
		return null;
	}
	
	/**
	 * 批量变更汇款状态为推迟状态
	 * @return
	 */
	public String batchDelay() {
		logger.info("进入批量变更汇款状态为推迟状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		List<String> withdrawLogExtras = Arrays.asList(StringUtils.split(withdrawLogRemarkStr, "{_$_}"));
		for (int i = 0;i < withdrawLogIds.size() ; i ++) {
			String withdrawLogId = withdrawLogIds.get(i);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.delay(withdrawLogId,delayedDay);
				if(flag){
					String withdrawLogExtra = withdrawLogExtras.get(i);
					if (withdrawLogExtra != null && !withdrawLogExtra.equals("")) {
						flag = withdrawService.updateRemark(withdrawLogId, withdrawLogExtra, OPERATION_TYPE_UPDATE_REMARK);
					}
					if (!flag) {
						logger.error("变更汇款状态为推迟状态失败");
						jsonObject.put("code", 1);
						jsonObject.put("msg", "变更汇款状态为推迟状态失败，请联系管理员");
					}
				} else {
					logger.error("变更汇款状态为推迟状态失败");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为推迟状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为推迟状态失败，请联系管理员");
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		
		logger.info("批量变更汇款状态为推迟状态结束");
		return null;
	}

	/**
	 * 批量变更汇款状态为汇款成功状态
	 * @return
	 */
	public String batchRemit() {
		logger.info("进入批量变更汇款状态为已汇款状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.remit(withdrawLogId, null);
				if(!flag){
					logger.error("变更汇款状态为已汇款状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为已汇款状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为已汇款状态失败，请联系管理员");
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为已汇款状态结束");
		return null;
	}
	
	/**
	 * 批量变更汇款状态为已汇款至充值来源状态
	 * @return
	 */
	public String batchRemitRechargeSource() {
		logger.info("进入批量变更汇款状态为已汇款至充值来源状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.remitRechargeSource(withdrawLogId);
				if(!flag){
					logger.error("变更汇款状态为已已汇款至充值来源至充值来源状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为已汇款至充值来源状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为已汇款至充值来源状态失败，请联系管理员");
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为已汇款至充值来源状态结束");
		return null;
	}
	
	/**
	 * 批量变更汇款状态为汇款退票状态
	 * @return
	 */
	public String batchRemitReturn() {
		logger.info("进入批量变更汇款状态为汇款退票状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.remitReturn(withdrawLogId);
				if(!flag){
					logger.error("变更汇款状态为汇款退票状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为汇款退票状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为汇款退票状态失败，请联系管理员");
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为汇款退票状态结束");
		return null;
	}
	
	/**
	 * 批量变更汇款状态为退款退票状态
	 * @return
	 */
	public String batchRefundReturn() {
		logger.info("进入批量变更汇款状态为退款退票状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.refundReturn(withdrawLogId);
				if(!flag){
					logger.error("变更汇款状态为退款退票状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为退款退票状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为退款退票状态失败，请联系管理员");
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为退款退票状态结束");
		return null;
	}
	
	/**
	 * 批量变更汇款状态为汇款失败状态
	 * @return
	 */
	public String batchRemitFailure() {
		logger.info("进入批量变更汇款状态为汇款失败状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		List<String> withdrawLogExtras = Arrays.asList(withdrawLogRemarkStr.split(","));
		for (int i = 0; i < withdrawLogIds.size(); i++) {
			String withdrawLogId = withdrawLogIds.get(i);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.remitFailure(withdrawLogId);
				if(flag){
					String withdrawLogExtra = withdrawLogExtras.get(i);
					if (withdrawLogExtra != null && !withdrawLogExtra.equals("")) {
						flag = withdrawService.updateRemark(withdrawLogId, withdrawLogExtra, OPERATION_TYPE_UPDATE_REMARK);
					}
					if (!flag) {
						logger.error("批量变更汇款状态为汇款失败状态失败，请联系管理员!");
						jsonObject.put("code", 1);
						jsonObject.put("msg", "变更汇款状态为汇款失败状态失败，请联系管理员");
					}
				} else {
					logger.error("批量变更汇款状态为汇款失败状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为汇款失败状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为汇款失败状态失败，请联系管理员");
			}
			
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		
		logger.info("批量变更汇款状态为汇款失败状态结束");
		return null;
	}
	
	/**
	 * 批量变更汇款状态为退款失败状态
	 * @return
	 */
	public String batchRefundFailure() {
		logger.info("进入批量变更汇款状态为退款失败状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		List<String> withdrawLogExtras = Arrays.asList(withdrawLogRemarkStr.split(","));
		for (int i = 0; i < withdrawLogIds.size(); i++) {
			String withdrawLogId = withdrawLogIds.get(i);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.refundFailure(withdrawLogId);
				if(flag){
					String withdrawLogExtra = withdrawLogExtras.get(i);
					if (withdrawLogExtra != null && !withdrawLogExtra.equals("")) {
						flag = withdrawService.updateRemark(withdrawLogId, withdrawLogExtra, OPERATION_TYPE_UPDATE_REMARK);
					}
					if (!flag) {
						logger.error("批量变更汇款状态为退款失败状态失败，请联系管理员!");
						jsonObject.put("code", 1);
						jsonObject.put("msg", "变更汇款状态为退款失败状态失败，请联系管理员");
					}
				} else {
					logger.error("批量变更汇款状态为退款失败状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为退款失败状态失败，请联系管理员");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为退款失败状态失败，请联系管理员");
			}
			
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		
		logger.info("批量变更汇款状态为退款失败状态结束");
		return null;
	}
	
	/**
	 * 修改汇款记录是否导出
	 */
	public String updateExportStatus () {
		logger.info("进入批量修改汇款记录是否导出");
		
		HttpServletResponse response = ServletActionContext.getResponse();
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(response, rs);
			return null;
		}
		if (batchNo == null || batchNo.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "导出批次号为空");
			writeRs(response, rs);
			return null;
		}
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		YesNoStatus exportStatus = YesNoStatus.getItem(isExport);
		
		String ids = null;
		try {
			ids = withdrawService.updateExportStatus(withdrawLogIds, exportStatus, batchNo);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API修改汇款记录是否导出异常，{}", e.getMessage());
			rs.put("code", 1);
			rs.put("msg", "API修改汇款记录是否导出异常，请联系管理员");
			
			super.writeRs(response, rs);
			return null;
		}
		
		JSONObject jsonObject = null;
		if (ids != null && !ids.equals("")) {
			jsonObject = JSONObject.fromObject(ids);
			if (jsonObject != null && !jsonObject.isNullObject()) {
				rs.put("successIds",jsonObject.get("success"));
				rs.put("failIds", jsonObject.get("fail"));
			}
		}
		
		super.writeRs(response, rs);
		return null;
	}
	
	/**
	 * 修改备注
	 * @return
	 */
	public String updateRemark () {
		logger.info("进入修改备注");
		
		if (withdraw == null) {
			logger.error("汇款记录编码和备注为空");
			super.setErrorMessage("汇款记录编码和备注不能为空");
			return "failure";
		}
		if (withdraw.getId() == null || withdraw.getId().equals("")) {
			logger.error("汇款记录编码为空");
			super.setErrorMessage("汇款记录编码不能为空");
			return "failure";
		}
		if (withdraw.getRemark() == null || withdraw.getRemark().equals("")) {
			logger.error("汇款记录备注为空");
			super.setErrorMessage("汇款记录备注不能为空");
			return "failure";
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 0);
		try {
			boolean flag = withdrawService.updateRemark(withdraw.getId(), withdraw.getRemark(), OPERATION_TYPE_UPDATE_REMARK);
			if(!flag){
				logger.error("修改备注失败，请联系管理员!");
				jsonObject.put("code", 1);
				jsonObject.put("msg", "修改备注失败，请联系管理员");
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			jsonObject.put("code", 1);
			jsonObject.put("msg", "修改备注失败，请联系管理员");
		}
		
		writeRs(ServletActionContext.getResponse(), jsonObject);
		
		logger.info("修改备注结束");
		return null;
	}
	
	/**
	 * 查询会员消费数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String viewMemberConsumption() {
		if (userid == null || userid == 0L) {
			logger.error("会员编码为空");
			super.setErrorMessage("会员编码不能为空");
			return "failure";
		}
		
		Member member = null;
		try {
			member = memberService.get(userid);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询会员信息异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		
		if (member == null) {
			logger.error("{}编码对应的会员不存在", userid);
			super.setErrorMessage(userid + "编码对应的会员不存在");
			return "failure";
		}
		
		Map<String,Object> map = null;
		try {
			map = memberConsumptionService.getResult(null, userid, null, null, null, null, null, null);
		} catch (ApiRemoteCallFailedException e) {
			logger.error("API查询会员消费数据异常，{}", e.getMessage());
			super.setErrorMessage("API调用异常，请联系技术人员!" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			if (map.get(Global.API_MAP_KEY_LIST)!= null) {
				memberConsumptionList =(List<MemberConsumption>)map.get(Global.API_MAP_KEY_LIST) ;
			}
		}
		return "view_memberConsumption";
	}
	
	public String updateProvinceAndCity() {
		logger.info("进入修改汇款审核省市信息");
		
		if (withdraw == null || withdraw.getId() == null || "".equals(withdraw.getId())) {
			logger.error("会员汇款编码为空");
			super.setErrorMessage("会员汇款编码为空");
			return "failure";
		}

		try {
			withdrawService.updateProvinceAndCity(withdraw);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(), e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		super.setForwardUrl("/member/withdraw.do");
		return "forward";
	}
	
	/**
	 * 客服退款操作-待人工处理
	 * @return
	 */
	public String waitForArtificial() {
		logger.info("进入客服退款操作-待人工处理");
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 0);
		
		if (withdraw == null) {
			logger.error("汇款记录编码和备注为空");
			jsonObject.put("code", 1);
			jsonObject.put("msg", "汇款记录编码和备注为空");
		}
		if (withdraw.getId() == null || withdraw.getId().equals("")) {
			logger.error("汇款记录编码为空");
			jsonObject.put("code", 1);
			jsonObject.put("msg", "汇款记录编码为空");
		}
		if (withdraw.getRemark() == null || withdraw.getRemark().equals("")) {
			logger.error("汇款记录备注为空");
			jsonObject.put("code", 1);
			jsonObject.put("msg", "汇款记录备注为空");
		}
		
		try {
			boolean flag = withdrawService.updateRemark(withdraw.getId(), withdraw.getRemark(), OPERATION_TYPE_UPDATE_REFUND);
			if(!flag){
				logger.error("修改备注失败，请联系管理员!");
				jsonObject.put("code", 1);
				jsonObject.put("msg", "修改备注失败，请联系管理员");
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			jsonObject.put("code", 1);
			jsonObject.put("msg", "修改备注失败，请联系管理员");
		}
		
		writeRs(ServletActionContext.getResponse(), jsonObject);
		
		logger.info("客服退款操作-待人工处理结束");
		return null;
	}
	
	/**
	 * 财务退款操作-退款批准（状态更新为退款批准,此时前台还可以撤销）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String batchRefundApprove() {
		logger.info("进入批量变更汇款状态为批准状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.approve(withdrawLogId, WithdrawStatus.REFUND_APPROVED);
				if(!flag){
					logger.error("变更汇款状态为退款批准状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为退款批准状态失败，请联系管理员!");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为退款批准状态失败，请联系管理员!");
			}
			Map<String, Object> map = null;
			try {
				map = withdrawService.getResult(withdrawLogId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询汇款记录异常，{}", e.getMessage());
				jsonObject.put("code", 1);
				jsonObject.put("msg", "API查询汇款记录异常，请联系管理员!");
			}
			if (map != null) {
				List<WithdrawLog> withdrawLogList = (List<WithdrawLog>)map.get(Global.API_MAP_KEY_LIST);
				if (withdrawLogList != null && withdrawLogList.size() > 0) {
					WithdrawLog tempWithdrawLog = withdrawLogList.get(0);
					jsonObject.put("exportStatus", tempWithdrawLog.getExportStatus().getValue());
				}
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为退款批准状态结束");
		return null;
	}
	
	/**
	 * 财务退款操作-汇款（状态更新为正在汇款，此时前台已经不允许撤销了。）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String batchWithdraw() {
		logger.info("进入批量变更汇款状态为正在汇款状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.remitting(withdrawLogId, WithdrawStatus.REMITTING);
				if(!flag){
					logger.error("变更汇款状态为正在汇款状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为正在汇款状态失败，请联系管理员!");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为正在汇款状态失败，请联系管理员!");
			}
			Map<String, Object> map = null;
			try {
				map = withdrawService.getResult(withdrawLogId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询汇款记录异常，{}", e.getMessage());
				jsonObject.put("code", 1);
				jsonObject.put("msg", "API查询汇款记录异常，请联系管理员!");
			}
			if (map != null) {
				List<WithdrawLog> withdrawLogList = (List<WithdrawLog>)map.get(Global.API_MAP_KEY_LIST);
				if (withdrawLogList != null && withdrawLogList.size() > 0) {
					WithdrawLog tempWithdrawLog = withdrawLogList.get(0);
					jsonObject.put("exportStatus", tempWithdrawLog.getExportStatus().getValue());
				}
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为批准状态结束");
		return null;
	}
	
	
	/**
	 * 财务退款操作-退款（状态更新为正在退款，此时前台已经不允许撤销了。）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String batchRefund() {
		logger.info("进入批量变更汇款状态为正在退款状态");
		
		JSONObject rs = new JSONObject();
		rs.put("code", 0);
		
		if (withdrawLogIdStr == null || withdrawLogIdStr.equals("")) {
			rs.put("code", 1);
			rs.put("msg", "汇款编码为空");
			writeRs(ServletActionContext.getResponse(), rs);
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		
		List<String> withdrawLogIds = Arrays.asList(withdrawLogIdStr.split(","));
		for (String withdrawLogId : withdrawLogIds) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			try {
				boolean flag = withdrawService.remitting(withdrawLogId, WithdrawStatus.REFUNDING);
				if(!flag){
					logger.error("变更汇款状态为批准状态失败，请联系管理员!");
					jsonObject.put("code", 1);
					jsonObject.put("msg", "变更汇款状态为批准状态失败，请联系管理员!");
				}
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				jsonObject.put("code", 1);
				jsonObject.put("msg", "变更汇款状态为批准状态失败，请联系管理员!");
			}
			Map<String, Object> map = null;
			try {
				map = withdrawService.getResult(withdrawLogId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API查询汇款记录异常，{}", e.getMessage());
				jsonObject.put("code", 1);
				jsonObject.put("msg", "API查询汇款记录异常，请联系管理员!");
			}
			if (map != null) {
				List<WithdrawLog> withdrawLogList = (List<WithdrawLog>)map.get(Global.API_MAP_KEY_LIST);
				if (withdrawLogList != null && withdrawLogList.size() > 0) {
					WithdrawLog tempWithdrawLog = withdrawLogList.get(0);
					jsonObject.put("exportStatus", tempWithdrawLog.getExportStatus().getValue());
				}
			}
			jsonObject.put("id", withdrawLogId);
			
			jsonArray.add(jsonObject);
		}
		
		rs.put("data", jsonArray);
		writeRs(ServletActionContext.getResponse(), rs);
		
		logger.info("批量变更汇款状态为正在退款状态结束");
		return null;
	}
	
	/**
	 * 修改用户备注
	 * @return
	 */
	public String updateDescription () {
		logger.info("进入修改用户备注");
		
		if (withdraw == null) {
			logger.error("汇款记录编码和备注为空");
			super.setErrorMessage("汇款记录编码和备注不能为空");
			return "failure";
		}
		if (withdraw.getId() == null || withdraw.getId().equals("")) {
			logger.error("汇款记录编码为空");
			super.setErrorMessage("汇款记录编码不能为空");
			return "failure";
		}
		if (withdraw.getDescription() == null || withdraw.getDescription().equals("")) {
			logger.error("汇款记录用户备注为空");
			super.setErrorMessage("汇款记录用户备注不能为空");
			return "failure";
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 0);
		try {
			boolean flag = withdrawService.updateDescription(withdraw.getId(), withdraw.getDescription());
			if(!flag){
				logger.error("修改用户备注失败，请联系管理员!");
				jsonObject.put("code", 1);
				jsonObject.put("msg", "修改用户备注失败，请联系管理员");
			}
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			jsonObject.put("code", 1);
			jsonObject.put("msg", "修改用户备注失败，请联系管理员");
		}
		
		writeRs(ServletActionContext.getResponse(), jsonObject);
		
		logger.info("修改用户备注结束");
		return null;
	}
	
	public List<YesNoStatus> getYesNoStatus () {
		return YesNoStatus.getItemsForQuery();
	}
	
	public WithdrawService getWithdrawService() {
		return withdrawService;
	}
	public void setWithdrawService(WithdrawService withdrawService) {
		this.withdrawService = withdrawService;
	}
	public MemberConsumptionService getMemberConsumptionService() {
		return memberConsumptionService;
	}
	public void setMemberConsumptionService(
			MemberConsumptionService memberConsumptionService) {
		this.memberConsumptionService = memberConsumptionService;
	}
	public MemberService getMemberService() {
		return memberService;
	}
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	public RechargeLogService getRechargeLogService() {
		return rechargeLogService;
	}

	public void setRechargeLogService(RechargeLogService rechargeLogService) {
		this.rechargeLogService = rechargeLogService;
	}

	public String getOrderStr() {
		if (orderStr == null && !"".equals(orderStr)) {
			orderStr = WithdrawLog.ORDER_TIMELINE;
		}
		return orderStr;
	}
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}
	public String getOrderView() {
		if (orderView == null && !"".equals(orderView)) {
			orderView = ApiConstant.API_REQUEST_ORDER_DESC;
		}
		return orderView;
	}
	public void setOrderView(String orderView) {
		this.orderView = orderView;
	}
	public Map<String, String> getOrderStrMap() {
		orderStrMap = new HashMap<String, String>();
		orderStrMap.put(WithdrawLog.ORDER_TIMELINE, "申请时间");
		orderStrMap.put(WithdrawLog.ORDER_SUCCESS_TIME, "汇/退款时间");
		orderStrMap.put(WithdrawLog.ORDER_AMOUNT, "金额");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public WithdrawLog getWithdraw() {
		return withdraw;
	}
	public void setWithdraw(WithdrawLog withdraw) {
		this.withdraw = withdraw;
	}
	public List<WithdrawLog> getWithdraws() {
		return withdraws;
	}
	public void setWithdraws(List<WithdrawLog> withdraws) {
		this.withdraws = withdraws;
	}
	public List<MemberConsumption> getMemberConsumptionList() {
		return memberConsumptionList;
	}
	public void setMemberConsumptionList(
			List<MemberConsumption> memberConsumptionList) {
		this.memberConsumptionList = memberConsumptionList;
	}
	public String getWithdrawLogIdStr() {
		return withdrawLogIdStr;
	}
	public void setWithdrawLogIdStr(String withdrawLogIdStr) {
		this.withdrawLogIdStr = withdrawLogIdStr;
	}
	public String getWithdrawLogRemarkStr() {
		return withdrawLogRemarkStr;
	}
	public void setWithdrawLogRemarkStr(String withdrawLogRemarkStr) {
		this.withdrawLogRemarkStr = withdrawLogRemarkStr;
	}
	public int getDelayedDay() {
		return delayedDay;
	}
	public void setDelayedDay(int delayedDay) {
		this.delayedDay = delayedDay;
	}
	public List<WithdrawStatus> getWithdrawStatuses() {
		return WithdrawStatus.getItems();
	}
	public List<BankType> getBankTypes() {
		return BankType.getItems();
	}
	public List<BankType> getBankTypesT0() {
		List<BankType> bankT0 = new ArrayList<BankType>();
		bankT0.add(BankType.ICBC);
		bankT0.add(BankType.CMBC);
		bankT0.add(BankType.CBC);
		bankT0.add(BankType.ABC);
		return bankT0;
	}
	public List<WithdrawType> getWithdrawTypes() {
		return WithdrawType.getItems();
	}
	public YesNoStatus getYesStatus () {
		return YesNoStatus.YES;
	}
	public YesNoStatus getNoStatus () {
		return YesNoStatus.NO;
	}
	public String getWithdrawId() {
		return withdrawId;
	}
	public void setWithdrawId(String withdrawId) {
		this.withdrawId = withdrawId;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getIsExport() {
		return isExport;
	}
	public void setIsExport(int isExport) {
		this.isExport = isExport;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getIdData() {
		return idData;
	}
	public void setIdData(String idData) {
		this.idData = idData;
	}
	public Integer getWithdrawStatusId() {
		return withdrawStatusId;
	}
	public void setWithdrawStatusId(Integer withdrawStatusId) {
		this.withdrawStatusId = withdrawStatusId;
	}
	public String getBankCardno() {
		return bankCardno;
	}
	public void setBankCardno(String bankCardno) {
		this.bankCardno = bankCardno;
	}
	public List<String> getBankTypeValues() {
		return bankTypeValues;
	}
	public void setBankTypeValues(List<String> bankTypeValues) {
		this.bankTypeValues = bankTypeValues;
	}
	public Integer getWithdrawTypeId() {
		return withdrawTypeId;
	}
	public void setWithdrawTypeId(Integer withdrawTypeId) {
		this.withdrawTypeId = withdrawTypeId;
	}
	public String getAlipayAccount() {
		return alipayAccount;
	}
	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}
	public WithdrawStatus getApplyingWithdrawStatus(){
		return WithdrawStatus.APPLYING;
	}
	public WithdrawStatus getHandlingWithdrawStatus(){
		return WithdrawStatus.HANDLING;
	}
	public WithdrawStatus getApproveWithdrawStatus(){
		return WithdrawStatus.APPROVE;
	}
	public WithdrawStatus getRefuseWithdrawStatus(){
		return WithdrawStatus.REFUSE;
	}
	public WithdrawStatus getRemittedWithdrawStatus(){
		return WithdrawStatus.REMITTED;
	}
	public WithdrawStatus getDelayedOneDayWithdrawStatus(){
		return WithdrawStatus.DELAYEDFOR1DAY;
	}
	public WithdrawStatus getDelayedFifteenDayWithdrawStatus(){
		return WithdrawStatus.DELAYEDFOR15DAYS;
	}
	public WithdrawStatus getDelayedWithdrawStatus(){
		return WithdrawStatus.DELAYED;
	}
	
	public WithdrawStatus getAuditWithdrawStatus(){
		return WithdrawStatus.AUDIT;
	}

	//汇款退票
	public WithdrawStatus getRemittanceReturnStatus(){
		return WithdrawStatus.REMITTANCE_RETURN;
	}
	
	//汇款失败
	public WithdrawStatus getRemittanceFailureStatus(){
		return WithdrawStatus.REMITFAILURE;
	}
	
	//已退款至充值来源
	public WithdrawStatus getRemittedRechargeSourceStatus(){
		return WithdrawStatus.REMITTED_RECHARGE_SOURCE;
	}
	
	//未通过自动审核
	public WithdrawStatus getNotPassStatus(){
		return WithdrawStatus.NOT_PASS;
	}
	//待人工处理
	public WithdrawStatus getWaitForArtificialStatus(){
		return WithdrawStatus.WAIT_FOR_ARTIFICIAL;
	}
	//正在退款
	public WithdrawStatus getRefundingStatus(){
		return WithdrawStatus.REFUNDING;
	}
	//正在汇款
	public WithdrawStatus getRemittingStatus(){
		return WithdrawStatus.REMITTING;
	}
	//退款处理
	public WithdrawStatus getRefundApprovedStatus(){
		return WithdrawStatus.REFUND_APPROVED;
	}
	//退款失败
	public WithdrawStatus getRefundFailureStatus(){
		return WithdrawStatus.REFUND_FAILURE;
	}
	//退款退票
	public WithdrawStatus getRefundReturnStatus(){
		return WithdrawStatus.REFUND_RETURN;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<AuditStatus> getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(List<AuditStatus> auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getExportName() {
		return exportName;
	}

	public void setExportName(String exportName) {
		this.exportName = exportName;
	}
	
	public Integer getSeconds() {
		if (seconds == null) {
			seconds = 1800;
		}
		return seconds;
	}
	public void setSeconds(Integer seconds) {
		this.seconds = seconds;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	public AuditStatus getFirstWithdrawAuditStatus() {
		return AuditStatus.FIRST_WITHDRAW;
	}

	public ProvinceCityService getProvinceCityService() {
		return provinceCityService;
	}

	public void setProvinceCityService(ProvinceCityService provinceCityService) {
		this.provinceCityService = provinceCityService;
	}

	public List<Province> getProvinces() {
		return this.provinces;
	}
	
	public List<City> getCities() {
		if (cities == null || cities.size() == 0) {
			cities = new ArrayList<City>();
		}
		return this.cities;
	}

	public Integer getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}
	
	public List<WalletType> getWalletTypes() {
		if (this.walletTypeList == null) {
			this.walletTypeList = new ArrayList<WalletType>();
		}
		if (this.walletTypeList.isEmpty()) {
			this.walletTypeList.add(WalletType.CASH);
		}
		return this.walletTypeList;
	}
	
	public void setWalletTypeStr(String s) {
		this.walletTypeList = new ArrayList<WalletType>();
		if (s == null) {
			return;
		}
		String[] tmp = StringUtils.split(s, ",");
		for (String string : tmp) {
			try {
				int val = Integer.parseInt(string);
				WalletType walletType = WalletType.getItem(val);
				if (walletType != null) {
					this.walletTypeList.add(walletType);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public Integer getWalletTypeId() {
		return walletTypeId;
	}

	public void setWalletTypeId(Integer walletTypeId) {
		this.walletTypeId = walletTypeId;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setWithdrawIdForExport(String withdrawIdForExport) {
		this.withdrawIdForExport = withdrawIdForExport;
	}

	public String getWithdrawIdForExport() {
		return withdrawIdForExport;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setBeginSuccessDate(Date beginSuccessDate) {
		this.beginSuccessDate = beginSuccessDate;
	}

	public Date getBeginSuccessDate() {
		return beginSuccessDate;
	}

	public void setEndSuccessDate(Date endSuccessDate) {
		this.endSuccessDate = endSuccessDate;
	}

	public Date getEndSuccessDate() {
		return endSuccessDate;
	}

	public void setChkBank(Integer chkBank) {
		this.chkBank = chkBank;
	}

	public Integer getChkBank() {
		return chkBank;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public File getExcelFile() {
		return excelFile;
	}

	public double getSuccessAmount() {
		return successAmount;
	}

	public void setSuccessAmount(double successAmount) {
		this.successAmount = successAmount;
	}

	public double getFailAmount() {
		return failAmount;
	}

	public void setFailAmount(double failAmount) {
		this.failAmount = failAmount;
	}

}
