package web.action.member;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.export.ConsumptionExport;
import com.lehecai.admin.web.service.member.ConsumptionService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.ConsumptionLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.TransType;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.util.CoreNumberUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ConsumptionAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(ConsumptionAction.class);
	private static final long serialVersionUID = 2436161530465382824L;
	
	private ConsumptionService consumptionService;
	
	private ConsumptionLog consumption;
	
	private List<ConsumptionLog> consumptions;
	
	private Integer lotteryTypeId;
	private Integer transTypeId;
	private Integer walletType;
	private String username;			//账户名
	private Date beginDate;				//交易起始时间
	private Date endDate;				//交易结束时间
	private String logId;				//钱包流水号
	private String orderId;				//订单编号
	private String planId;				//方案编号
	
	private String orderStr;			//排序字段
	private String orderView;			//排序方式
	
	private Map<String, String> orderStrMap;
	private Map<String, String> orderViewMap;
	
	private InputStream inputStream;
	private String fileName;
	
	private String totalAmount;			//总到账金额
	
	public String handle() {
		logger.info("进入查询钱包流水数据");
		if (beginDate == null) {
			beginDate = this.getLastDate();
		}
		return "list";
	}
	
	@SuppressWarnings("unchecked")
	public String query() {
		logger.info("进入查询钱包流水数据");
		HttpServletRequest request = ServletActionContext.getRequest();
		
		if (beginDate == null) {
			beginDate = this.getLastDate();
		}
		
		if (beginDate != null && endDate != null) {
			if (!DateUtil.isSameMonth(beginDate, endDate)) {
				logger.error("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);	//彩票种类
		TransType tt = transTypeId == null ? null : TransType.getItem(transTypeId);			//交易类型
		
		List<WalletType> toSearchWalletTypeList = new ArrayList<WalletType>();
		
		if (walletType != null && walletType != WalletType.ALL.getValue() && walletType != WalletType.LEHECAI.getValue()) {
			WalletType toSearchWalletType = null;
			try {
				toSearchWalletType = WalletType.getItem(walletType);
				toSearchWalletTypeList.add(toSearchWalletType);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		if (toSearchWalletTypeList.isEmpty()) {
			for (WalletType walletType : walletTypeList) {
				if (walletType.getValue() == WalletType.ALL.getValue()) {
					continue;
				}
				toSearchWalletTypeList.add(walletType);
			}
		}
		
		
		Map<String, Object> map;
		try {
			map = consumptionService.getResult(lt,
					tt, toSearchWalletTypeList, username, beginDate, endDate, logId, orderId,
					planId, getOrderStr(), getOrderView(), super.getPageBean());//多条件分页查询钱包流水
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			consumptions = (List<ConsumptionLog>)map.get(Global.API_MAP_KEY_LIST);
			PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
			super.setPageString(PageUtil.getPageString(request, pageBean));
			super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
		}
		
		Map<String, Object> statisticsMap;
		try {
			statisticsMap = consumptionService.getConsumptionStatistics(lt,
						tt, toSearchWalletTypeList, username, beginDate, endDate, logId, orderId,
						planId);
			
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {
			Object amountObj = statisticsMap.get(Global.API_MAP_KEY_AMOUNT);
			double amountDou = 0.00D;
			try {
				amountDou = Double.parseDouble(String.valueOf(amountObj));
			} catch (Exception e) {
				logger.error("钱包流水总金额转换成double类型异常",e);
				super.setErrorMessage("钱包流水总金额转换成double类型异常");
				return "failure";
			}
			totalAmount = CoreNumberUtil.formatNumBy2Digits(amountDou);
			if (totalAmount == null || "".equals(totalAmount)) {
				logger.error("格式化钱包流水总金额异常");
				super.setErrorMessage("格式化钱包流水总金额异常");
				return "failure";
			}
		}
		logger.info("查询钱包流水数据结束");
		return "list";
	}
	
	public String view() {
		logger.info("进入查询钱包流水详情");
		if (consumption != null && !"".equals(consumption.getLogId())) {
			try {
				consumption = consumptionService.get(consumption.getLogId());
			} catch (ApiRemoteCallFailedException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
				return "failure";
			}
		}else{
			return "failure";
		}
		logger.info("查询钱包流水详情结束");
		return "view";
	}
	
	@SuppressWarnings("unchecked")
	public String export() {
		logger.info("进入导出钱包流水数据");
		if (beginDate != null && endDate != null) {			
			if (!DateUtil.isSameMonth(beginDate, endDate)) {
				logger.error("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				super.setErrorMessage("开始时间和结束时间必须为同一年同一月，不支持跨年月查询!");
				return "failure";
			}
		}
		
		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(10000);//max 10000 items
		
		LotteryType lt = lotteryTypeId == null ? null : LotteryType.getItem(lotteryTypeId);
		TransType tt = transTypeId == null ? null : TransType.getItem(transTypeId);
		
		List<WalletType> toSearchWalletTypeList = new ArrayList<WalletType>();
		
		if (walletType != null && walletType != WalletType.ALL.getValue() && walletType != WalletType.LEHECAI.getValue()) {
			WalletType toSearchWalletType = null;
			try {
				toSearchWalletType = WalletType.getItem(walletType);
				toSearchWalletTypeList.add(toSearchWalletType);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		if (toSearchWalletTypeList.isEmpty()) {
			for (WalletType walletType : walletTypeList) {
				if (walletType.getValue() == WalletType.ALL.getValue()) {
					continue;
				}
				toSearchWalletTypeList.add(walletType);
			}
		}
		
		Map<String, Object> map;
		try {
			map = consumptionService.getResult(lt,
					tt, toSearchWalletTypeList, username, beginDate, endDate, logId, orderId,
					planId, orderStr, orderView, pageBean);
		} catch (ApiRemoteCallFailedException e) {
			logger.error(e.getMessage(),e);
			super.setErrorMessage("api调用异常，请联系技术人员!原因:" + e.getMessage());
			return "failure";
		}
		if (map != null) {	
			consumptions = (List<ConsumptionLog>)map.get(Global.API_MAP_KEY_LIST);
			try {
				Workbook workBook = ConsumptionExport.export(consumptions);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				workBook.write(os);
				inputStream  = new ByteArrayInputStream(os.toByteArray());
				this.fileName = (new Date()).getTime() + ".xls";
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("文件输出流写入错误");
				return "failure";
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				super.setErrorMessage("API返回数据有误，生成excel文件时错误");
				return "failure";
			}
		}
		return "download";
	}
	
	public ConsumptionService getConsumptionService() {
		return consumptionService;
	}
	public void setConsumptionService(ConsumptionService consumptionService) {
		this.consumptionService = consumptionService;
	}
	public String getOrderStr() {
		if (orderStr == null && !"".equals(orderStr)) {
			orderStr = ConsumptionLog.ORDER_CREATED_TIME;
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
		orderStrMap.put(ConsumptionLog.ORDER_LOG_ID, "钱包流水号");
		orderStrMap.put(ConsumptionLog.ORDER_CREATED_TIME, "交易时间");
		return orderStrMap;
	}
	public Map<String, String> getOrderViewMap() {
		orderViewMap = new HashMap<String, String>();
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
		orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
		return orderViewMap;
	}
	public ConsumptionLog getConsumption() {
		return consumption;
	}
	public void setConsumption(ConsumptionLog consumption) {
		this.consumption = consumption;
	}
	public List<ConsumptionLog> getConsumptions() {
		return consumptions;
	}
	public void setConsumptions(List<ConsumptionLog> consumptions) {
		this.consumptions = consumptions;
	}
	public List<LotteryType> getLotteryTypes() {
		return LotteryType.getItems();
	}
	public List<TransType> getTransTypes() {
		return TransType.getItems();
	}
	public Integer getLotteryTypeId() {
		return lotteryTypeId;
	}
	public void setLotteryTypeId(Integer lotteryTypeId) {
		this.lotteryTypeId = lotteryTypeId;
	}
	public Integer getTransTypeId() {
		return transTypeId;
	}
	public void setTransTypeId(Integer transTypeId) {
		this.transTypeId = transTypeId;
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
	public String getLogId() {
		return logId;
	}
	public void setLogId(String logId) {
		this.logId = logId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPlanId() {
		return planId;
	}
	public void setPlanId(String planId) {
		this.planId = planId;
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
	public TransType getRechargeTransType() {
		return TransType.RECHARGE;
	}
	public TransType getRechargeManuallyTransType() {
		return TransType.RECHARGE_MANUALLY;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getWalletType() {
		return walletType;
	}

	public void setWalletType(Integer walletType) {
		this.walletType = walletType;
	}
	
	private static List<WalletType> walletTypeList;
	static {
		walletTypeList = new ArrayList<WalletType>();
	
		List<WalletType> allWalletTypeList = WalletType.getItems();
		for (WalletType walletType : allWalletTypeList) {
			if (walletType.getValue() == WalletType.LEHECAI.getValue()) {
				continue;
			}
			walletTypeList.add(walletType);
		}
	}
	
	public List<WalletType> getWalletTypeList() {
		return walletTypeList;
	}
}
