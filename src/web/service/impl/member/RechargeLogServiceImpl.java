package web.service.impl.member;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.RechargeLogService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.RechargeLog;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RechargeLogServiceImpl implements RechargeLogService {
    private final Logger logger = LoggerFactory.getLogger(RechargeLogServiceImpl.class);

    private ApiRequestService apiRequestService;
    private MemberService memberService;

    @Override
    public Map<String, Object> getResult(String id, String username,
            Integer minAmount, Integer maxAmount,
            Integer minPayAmount, Integer maxPayAmount, String payNo,
            String rechargeType, String bankType, List<WalletType> walletTypeList, Date cbeginDate, Date cendDate,
            Date sbeginDate, Date sendDate, String status, String sourceId,
            String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
        logger.info("进入调用API获取充值流水数据");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_RECHARGE_LOG_QUERY);
        if (id != null && !"".equals(id)) {
            request.setParameter(RechargeLog.QUERY_LOG_ID, id);
        }
        if (username != null && !"".equals(username)) {
            Long uid = null;
            try {
                uid = memberService.getIdByUserName(username);
            } catch (Exception e) {
                logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
            }
            if (uid != null && uid.longValue() != 0) {
                request.setParameter(RechargeLog.QUERY_UID, String.valueOf(uid.longValue()));
            } else {
                logger.info("用户名不存在!返回空记录!");
                return null;
            }
        }
        if (cbeginDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_CREATED_TIME, DateUtil.formatDate(cbeginDate,DateUtil.DATETIME),null);
        }
        if (cendDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_CREATED_TIME, null,DateUtil.formatDate(cendDate,DateUtil.DATETIME));
        }
        if (sbeginDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_SUCCESS_TIME, DateUtil.formatDate(sbeginDate,DateUtil.DATETIME),null);
        }
        if (sendDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_SUCCESS_TIME, null,DateUtil.formatDate(sendDate,DateUtil.DATETIME));
        }
        if (minAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_AMOUNT, minAmount.toString() ,null);
        }
        if (maxAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_AMOUNT, null, maxAmount.toString());
        }
        if (minPayAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_PAY_AMOUNT, minPayAmount.toString() ,null);
        }
        if (maxPayAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_PAY_AMOUNT, null, maxPayAmount.toString());
        }
        if (payNo != null && !"".equals(payNo)) {
            request.setParameter(RechargeLog.QUERY_PAY_NO, payNo);
        }
        if (rechargeType != null && !"-1".equals(rechargeType)) {
            request.setParameter(RechargeLog.QUERY_RECHARGE_TYPE, rechargeType);
        }
        if (bankType != null && !"-1".equals(bankType)) {
            request.setParameter(RechargeLog.QUERY_BANK_ID, bankType);
        }
        if (walletTypeList != null && !walletTypeList.isEmpty()) {
            List<String> walletTypeStrList = new ArrayList<String>();
            for (WalletType walletType : walletTypeList) {
                walletTypeStrList.add(String.valueOf(walletType.getValue()));
            }
            request.setParameterIn(RechargeLog.QUERY_WALLET_TYPE, walletTypeStrList);
        }
        if (status != null && !"-1".equals(status)) {
            request.setParameter(RechargeLog.QUERY_STATUS, status);
        }
        if (sourceId != null && !"".equals(sourceId)) {
            request.setParameter(RechargeLog.QUERY_SOURCE_ID, sourceId);
        }

        request.addOrder(orderStr,orderView);
        request.setPage(pageBean.getPage());
        //request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
        request.setPagesize(pageBean.getPageSize());
        logger.info("Request Query String: {}", request.toQueryString());

        ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
        if (response == null) {
            logger.error("API获取充值流水数据失败");
            throw new ApiRemoteCallFailedException("API获取充值流水数据失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("API获取充值流水数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("API获取充值流水数据请求异常");
        }
        if (response.getData() == null || response.getData().isEmpty()) {
            logger.warn("API获取充值流水数据为空, message={}", response.getMessage());
            return null;
        }
        List<RechargeLog> list = RechargeLog.convertFromJSONArray(response.getData());
        if (pageBean != null) {
            int totalCount = response.getTotal();
            pageBean.setCount(totalCount);
            int pageCount = 0;//页数
            if (pageBean.getPageSize() != 0) {
                pageCount = totalCount / pageBean.getPageSize();
                if (totalCount % pageBean.getPageSize() != 0) {
                    pageCount ++;
                }
            }
            pageBean.setPageCount(pageCount);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
        map.put(Global.API_MAP_KEY_LIST, list);
        return map;
    }

    @Override
    public Map<String, Object> getRechargeStatistics(String id, String username,
            Integer minAmount, Integer maxAmount,
            Integer minPayAmount, Integer maxPayAmount, String payNo,
            String rechargeType, String bankType, List<WalletType> walletTypeList, Date cbeginDate, Date cendDate,
            Date sbeginDate, Date sendDate, String status, String sourceId) throws ApiRemoteCallFailedException {
        logger.info("进入调用API查询充值流水统计数据");
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_RECHARGE_STATS_SUM);
        if (id != null && !"".equals(id)) {
            request.setParameter(RechargeLog.QUERY_LOG_ID, id);
        }
        if (username != null && !"".equals(username)) {
            Long uid = null;
            try {
                uid = memberService.getIdByUserName(username);
            } catch (Exception e) {
                logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
            }
            if (uid != null && uid.longValue() != 0) {
                request.setParameter(RechargeLog.QUERY_UID, String.valueOf(uid.longValue()));
            } else {
                logger.info("用户名不存在!返回空记录!");
                return null;
            }
        }
        if (cbeginDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_CREATED_TIME, DateUtil.formatDate(cbeginDate,DateUtil.DATETIME),null);
        }
        if (cendDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_CREATED_TIME, null,DateUtil.formatDate(cendDate,DateUtil.DATETIME));
        }
        if (sbeginDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_SUCCESS_TIME, DateUtil.formatDate(sbeginDate,DateUtil.DATETIME),null);
        }
        if (sendDate != null) {
            request.setParameterBetween(RechargeLog.QUERY_SUCCESS_TIME, null,DateUtil.formatDate(sendDate,DateUtil.DATETIME));
        }
        if (minAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_AMOUNT, minAmount.toString() ,null);
        }
        if (maxAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_AMOUNT, null, maxAmount.toString());
        }
        if (minPayAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_PAY_AMOUNT, minPayAmount.toString() ,null);
        }
        if (maxPayAmount != null) {
            request.setParameterBetween(RechargeLog.QUERY_PAY_AMOUNT, null, maxPayAmount.toString());
        }
        if (payNo != null && !"".equals(payNo)) {
            request.setParameter(RechargeLog.QUERY_PAY_NO, payNo);
        }
        if (rechargeType != null && !"-1".equals(rechargeType)) {
            request.setParameter(RechargeLog.QUERY_RECHARGE_TYPE, rechargeType);
        }
        if (bankType != null && !"-1".equals(bankType)) {
            request.setParameter(RechargeLog.QUERY_BANK_ID, bankType);
        }
        if (walletTypeList != null && !walletTypeList.isEmpty()) {
            List<String> walletTypeStrList = new ArrayList<String>();
            for (WalletType walletType : walletTypeList) {
                walletTypeStrList.add(String.valueOf(walletType.getValue()));
            }
            request.setParameterIn(RechargeLog.QUERY_WALLET_TYPE, walletTypeStrList);
        }
        if (status != null && !"-1".equals(status)) {
            request.setParameter(RechargeLog.QUERY_STATUS, status);
        }
        if (sourceId != null && !"".equals(sourceId)) {
            request.setParameter(RechargeLog.QUERY_SOURCE_ID, sourceId);
        }

        logger.info("Request Query String: {}", request.toQueryString());

        ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
        if (response == null) {
            logger.error("API获取充值流水统计数据失败");
            throw new ApiRemoteCallFailedException("API获取充值流水统计数据失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("API获取充值流水统计数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("API获取充值流水统计数据请求异常");
        }
        if (response.getData() == null || response.getData().isEmpty()) {
            logger.error("API获取充值流水统计数据为空, message={}", response.getMessage());
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        JSONObject jsonObj = response.getData().getJSONObject(0);

        if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("amount") != null) {
            map.put(Global.API_MAP_KEY_AMOUNT, jsonObj.get("amount"));
        } else {
            map.put(Global.API_MAP_KEY_AMOUNT, "0");
        }
        if (jsonObj != null && !jsonObj.isNullObject() && jsonObj.get("pay_amount") != null) {
            map.put(Global.API_MAP_KEY_PAYAMOUNT, jsonObj.get("pay_amount"));
        } else {
            map.put(Global.API_MAP_KEY_PAYAMOUNT, "0");
        }
        return map;
    }

    @Override
    public RechargeLog getInfo(String id) throws ApiRemoteCallFailedException {
        ApiRequest request = new ApiRequest();
        request.setUrl(ApiConstant.API_URL_RECHARGE_LOG_QUERY);
        if (id != null && !"".equals(id)) {
            request.setParameter(RechargeLog.QUERY_LOG_ID, id);
        }
        ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
        if (response == null) {
            logger.error("API获取充值流水数据失败");
            throw new ApiRemoteCallFailedException("API获取充值流水数据失败");
        }
        if (response.getCode() != ApiConstant.RC_SUCCESS) {
            logger.error("API获取充值流水数据请求异常, rc={}, message={}", response.getCode(), response.getMessage());
            throw new ApiRemoteCallFailedException("API获取充值流水数据请求异常");
        }
        if (response.getData() == null || response.getData().isEmpty()) {
            logger.warn("API获取充值流水数据为空, message={}", response.getMessage());
            return null;
        }
        List<RechargeLog> list = RechargeLog.convertFromJSONArray(response.getData());
        return list.get(0);
    }

    public ApiRequestService getApiRequestService() {
        return apiRequestService;
    }
    public void setApiRequestService(ApiRequestService apiRequestService) {
        this.apiRequestService = apiRequestService;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }
}
