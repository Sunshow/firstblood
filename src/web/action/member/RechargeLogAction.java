package web.action.member;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.export.RechargeLogExport;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.RechargeLogService;
import com.lehecai.admin.web.utils.PageUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.RechargeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.BankType;
import com.lehecai.core.lottery.OperationStatus;
import com.lehecai.core.lottery.RechargeType;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.util.CoreNumberUtil;
import com.lehecai.core.util.CoreStringUtils;
import org.apache.commons.lang.StringUtils;
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

public class RechargeLogAction extends BaseAction {
    private static final long serialVersionUID = 2436161530465382824L;
    private final Logger logger = LoggerFactory.getLogger(RechargeLogAction.class);

    private RechargeLogService rechargeLogService;
    private MemberService memberService;
    private List<RechargeLog> rechargeLogs;

    private RechargeLog rechargeLog;

    private String id;			//流水ID

    private String username;	//用户名
    private String payNo;		//支付编号
    private Integer minAmount;	//充值最小金额
    private Integer maxAmount;	//充值最大金额
    private Integer minPayAmount;	//到账最小金额
    private Integer maxPayAmount;	//到账最大金额
    private String rechargeType;//充值类型
    private String bankType;	//银行
    private Integer walletType; //钱包类型编码
    private String status;		//操作状态
    private String sourceId;	//渠道来源
    private Date cbeginDate;	//发起充值时间开始
    private Date cendDate;		//发起充值时间结束
    private Date sbeginDate;	//充值成功时间开始
    private Date sendDate;		//充值成功时间结束
    private String orderStr;	//排序字段

    private String orderView;	//排序方式
    private String requestInfo;

    private String responseInfo;
    private Map<String, String> orderStrMap;	//排序字段列表

    private Map<String, String> orderViewMap;	//排序方式
    private InputStream inputStream;

    private String fileName;
    private String totalAmount;//总金额

    private String daoZhangTotalAmount;//总到账金额
    public String handle() {
        logger.info("进入查询充值流水数据");

        if (cbeginDate == null) {
            cbeginDate = this.getLastDate();
        }
        return "list";
    }
    
    public String requestInfo() {
        logger.info("进入查询充值流水日志请求信息");
        try{
            rechargeLog = rechargeLogService.getInfo(id);
        }catch(ApiRemoteCallFailedException e){
            logger.error("根据流水Id查询充值流水日志，api调用异常，{}", e.getMessage());
            super.setErrorMessage("根据流水Id查询充值流水日志，api调用异常，" + e.getMessage());
            return "failure";
        }
        if (rechargeLog == null) {
            logger.error("根据流水Id查询充值流水日志，没有满足条件的结果");
            super.setErrorMessage("根据流水Id查询充值流水日志，没有满足条件的结果");
            return "failure";
        }
        if (!StringUtils.isEmpty(rechargeLog.getRequestInfo())) {
        	rechargeLog.setRequestInfo(CoreStringUtils.unicodeToString(rechargeLog.getRequestInfo()));
        }
        return "requestInfo";
    }

    public String responseInfo() {
        logger.info("进入查询充值流水日志返回信息");
        try{
            rechargeLog = rechargeLogService.getInfo(id);
        }catch(ApiRemoteCallFailedException e){
            logger.error("根据流水Id查询充值流水日志，api调用异常，{}", e.getMessage());
            super.setErrorMessage("根据流水Id查询充值流水日志，api调用异常，" + e.getMessage());
            return "failure";
        }
        if (rechargeLog == null) {
            logger.error("根据流水Id查询充值流水日志，没有满足条件的结果");
            super.setErrorMessage("根据流水Id查询充值流水日志，没有满足条件的结果");
            return "failure";
        }
        if (!StringUtils.isEmpty(rechargeLog.getReturnInfo())) {
        	rechargeLog.setReturnInfo(CoreStringUtils.unicodeToString(rechargeLog.getReturnInfo()));
        }
        return "responseInfo";
    }

    @SuppressWarnings("unchecked")
    public String query() {
        logger.info("进入查询充值流水数据");
        HttpServletRequest request = ServletActionContext.getRequest();

        if (cbeginDate == null) {
            cbeginDate = this.getLastDate();
        }

        if (username != null && !username.equals("")) {
            Member m = new Member();
            try {
                m = memberService.get(username);
            } catch (ApiRemoteCallFailedException e) {
                logger.error("根据用户名查询用户，api调用异常，{}", e.getMessage());
                super.setErrorMessage("根据用户名查询用户，api调用异常，" + e.getMessage());
                return "failure";
            }
            if (m == null) {
                logger.error("根据用户名查询用户，用户不存在");
                return "list";
            }
        }

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

        Map<String, Object> map = null;
        try {
            map = rechargeLogService.getResult(	id,				//流水ID
                                                username,		//用户名
                                                minAmount,		//充值最小金额
                                                maxAmount,		//充值最大金额
                                                minPayAmount,	//到账最小金额
                                                maxPayAmount,	//到账最大金额
                                                payNo,			//支付编号
                                                rechargeType,	//充值类型
                                                bankType,		//银行
                                                toSearchWalletTypeList,     //钱包类型
                                                cbeginDate,		//发起充值时间开始
                                                cendDate,		//发起充值时间结束
                                                sbeginDate,		//充值成功时间开始
                                                sendDate,		//充值成功时间结束
                                                getStatus(), 		//操作状态
                                                sourceId, 		//渠道来源
                                                getOrderStr(),	//排序字段
                                                getOrderView(),	//排序方式
                                                super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("获取充值流水数据，api调用异常，{}", e.getMessage());
        }
        if (map != null) {
            rechargeLogs = (List<RechargeLog>)map.get(Global.API_MAP_KEY_LIST);
            PageBean pageBean = (PageBean)map.get(Global.API_MAP_KEY_PAGEBEAN);
            super.setPageString(PageUtil.getPageString(request, pageBean));
            super.setSimplePageString(PageUtil.getSimplePageString(pageBean));
        }

        Map<String, Object> statisticsMap = null;
        try {
            statisticsMap = rechargeLogService.getRechargeStatistics(id,			//流水ID
                                                                    username,		//用户名
                                                                    minAmount,		//充值最小金额
                                                                    maxAmount,		//充值最大金额
                                                                    minPayAmount,	//到账最小金额
                                                                    maxPayAmount,	//到账最大金额
                                                                    payNo,			//支付编号
                                                                    rechargeType,	//充值类型
                                                                    bankType,		//银行
                                                                    toSearchWalletTypeList,     //钱包类型
                                                                    cbeginDate,		//发起充值时间开始
                                                                    cendDate,		//发起充值时间结束
                                                                    sbeginDate,		//充值成功时间开始
                                                                    sendDate,		//充值成功时间结束
                                                                    getStatus(), 		//操作状态
                                                                    sourceId 		//渠道来源
            );
        } catch (ApiRemoteCallFailedException e) {
            logger.error("统计充值流水数据，API调用异常，{}", e.getMessage());
        }
        if (statisticsMap != null) {
            Object amountObj = statisticsMap.get(Global.API_MAP_KEY_AMOUNT);
            Object payAmountObj = statisticsMap.get(Global.API_MAP_KEY_PAYAMOUNT);
            if (amountObj == null) {
                logger.info("充值流水总金额为空");
            } else {
                double amountDou = 0;
                try {
                    amountDou = Double.parseDouble(amountObj.toString());
                } catch (Exception e) {
                    logger.error("充值流水总金额转换成double类型异常，{}", e);
                    super.setErrorMessage("充值流水总金额转换成double类型异常");
                    return "failure";
                }
                totalAmount = CoreNumberUtil.formatNumBy2Digits(amountDou);
                if(totalAmount == null || "".equals(totalAmount)){
                    logger.error("格式化充值流水总金额异常");
                    super.setErrorMessage("格式化充值流水总金额异常");
                    return "failure";
                }
            }

            if (payAmountObj == null) {
                logger.info("充值流水总到账金额为空");
            } else {
                double payAmountDou = 0;
                try {
                    payAmountDou = Double.parseDouble(payAmountObj.toString());
                } catch (Exception e) {
                    logger.error("充值流水总到账金额转换成double类型异常，{}", e);
                    super.setErrorMessage("充值流水总到账金额转换成double类型异常");
                    return "failure";
                }
                daoZhangTotalAmount = CoreNumberUtil.formatNumBy2Digits(payAmountDou);
                if (daoZhangTotalAmount == null || "".equals(daoZhangTotalAmount)) {
                    logger.error("格式化充值流水总到账金额异常");
                    super.setErrorMessage("格式化充值流水总到账金额异常");
                    return "failure";
                }
            }
        } else {
            logger.error("统计金额异常");
            super.setErrorMessage("统计金额异常");
            return "failure";
        }
        logger.info("查询充值流水数据结束");
        return "list";
    }

    @SuppressWarnings("unchecked")
    public String export() {
        logger.info("开始导出充值流水数据");
        PageBean pageBean = super.getPageBean();
        pageBean.setPageSize(10000);//max 10000 items

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

        Map<String, Object> map = null;
        try {
            map = rechargeLogService.getResult(	id,				//流水ID
                                                username,		//用户名
                                                minAmount,		//充值最小金额
                                                maxAmount,		//充值最大金额
                                                minPayAmount,	//到账最小金额
                                                maxPayAmount,	//到账最大金额
                                                payNo,			//支付编号
                                                rechargeType,	//充值类型
                                                bankType,		//银行
                                                toSearchWalletTypeList,     //钱包类型
                                                cbeginDate,		//发起充值时间开始
                                                cendDate,		//发起充值时间结束
                                                sbeginDate,		//充值成功时间开始
                                                sendDate,		//充值成功时间结束
                                                getStatus(), 		//操作状态
                                                sourceId, 		//渠道来源
                                                getOrderStr(),	//排序字段
                                                getOrderView(),	//排序方式
                                                super.getPageBean());
        } catch (ApiRemoteCallFailedException e) {
            logger.error("获取充值流水数据，API调用异常，{}", e.getMessage());
        }
        if (map != null) {
            rechargeLogs = (List<RechargeLog>)map.get(Global.API_MAP_KEY_LIST);
            try {
                Workbook workBook = RechargeLogExport.export(rechargeLogs);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workBook.write(os);
                inputStream  = new ByteArrayInputStream(os.toByteArray());
                this.fileName = (new Date()).getTime() + ".xls";
            } catch (IOException e) {
                logger.error("文件输出流写入错误，{}", e);
                super.setErrorMessage("文件输出流写入错误");
                return "failure";
            } catch (Exception e) {
                logger.error("生成excel文件时错误，{}", e);
                super.setErrorMessage("生成excel文件时错误");
                return "failure";
            }
            return "download";
        }
        super.setErrorMessage("生成excel文件时错误");
        logger.info("导出充值流水数据结束");
        return "failure";
    }

    public List<RechargeLog> getRechargeLogs() {
        return rechargeLogs;
    }

    public void setRechargeLogs(List<RechargeLog> rechargeLogs) {
        this.rechargeLogs = rechargeLogs;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Integer getMinAmount() {
        return minAmount;
    }
    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }
    public Integer getMaxAmount() {
        return maxAmount;
    }
    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }
    public String getPayNo() {
        return payNo;
    }
    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }
    public String getRechargeType() {
        return rechargeType;
    }
    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }
    public String getBankType() {
        return bankType;
    }
    public void setBankType(String bankType) {
        this.bankType = bankType;
    }
    public Date getCbeginDate() {
        return cbeginDate;
    }
    public void setCbeginDate(Date cbeginDate) {
        this.cbeginDate = cbeginDate;
    }
    public Date getCendDate() {
        return cendDate;
    }
    public void setCendDate(Date cendDate) {
        this.cendDate = cendDate;
    }
    public Date getSbeginDate() {
        return sbeginDate;
    }
    public void setSbeginDate(Date sbeginDate) {
        this.sbeginDate = sbeginDate;
    }
    public Date getSendDate() {
        return sendDate;
    }
    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }
    public String getStatus() {
        if (status == null || "".equals(status)) {
            status = OperationStatus.SUCCESSFUL.getValue() + "";
        }
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public String getOrderStr() {
        if(orderStr == null && !"".equals(orderStr)){
            orderStr = RechargeLog.ORDER_CREATED_TIME;
        }
        return orderStr;
    }
    public void setOrderStr(String orderStr) {
        this.orderStr = orderStr;
    }
    public String getOrderView() {
        if(orderView == null && !"".equals(orderView)){
            orderView = ApiConstant.API_REQUEST_ORDER_DESC;
        }
        return orderView;
    }
    public void setOrderView(String orderView) {
        this.orderView = orderView;
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
    public void setRechargeLogService(RechargeLogService rechargeLogService) {
        this.rechargeLogService = rechargeLogService;
    }
    public Map<String, String> getOrderStrMap() {
        orderStrMap = new HashMap<String, String>();
        orderStrMap.put(RechargeLog.ORDER_LOG_ID, "流水号");
        orderStrMap.put(RechargeLog.ORDER_PAY_AMOUT, "到账金额");
        orderStrMap.put(RechargeLog.ORDER_CREATED_TIME, "发起充值时间");
        return orderStrMap;
    }
    public Map<String, String> getOrderViewMap() {
        orderViewMap = new HashMap<String, String>();
        orderViewMap.put(ApiConstant.API_REQUEST_ORDER_ASC, "升序");
        orderViewMap.put(ApiConstant.API_REQUEST_ORDER_DESC, "降序");
        return orderViewMap;
    }
    public List<OperationStatus> getOperationStatusItems(){
        return OperationStatus.getItems();
    }
    public List<BankType> getBankTypeItems(){
        return BankType.getItems();
    }
    public List<RechargeType> getRechargeTypeItems(){
        return RechargeType.getItems();
    }
    public Integer getMinPayAmount() {
        return minPayAmount;
    }
    public void setMinPayAmount(Integer minPayAmount) {
        this.minPayAmount = minPayAmount;
    }
    public Integer getMaxPayAmount() {
        return maxPayAmount;
    }
    public void setMaxPayAmount(Integer maxPayAmount) {
        this.maxPayAmount = maxPayAmount;
    }
    public String getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getDaoZhangTotalAmount() {
        return daoZhangTotalAmount;
    }
    public void setDaoZhangTotalAmount(String daoZhangTotalAmount) {
        this.daoZhangTotalAmount = daoZhangTotalAmount;
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

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    public RechargeType getEventGiftRechargeType() {
        return RechargeType.EVENTGIFT;
    }
    
    public RechargeType getGiftRechargeType() {
    	return RechargeType.GIFT;
    }

    public String getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(String requestInfo) {
        this.requestInfo = requestInfo;
    }

    public String getResponseInfo() {
        return responseInfo;
    }

    public void setResponseInfo(String responseInfo) {
        this.responseInfo = responseInfo;
    }

    public RechargeLog getRechargeLog() {
        return rechargeLog;
    }

    public void setRechargeLog(RechargeLog rechargeLog) {
        this.rechargeLog = rechargeLog;
    }
}
