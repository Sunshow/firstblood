package web.service.impl.member;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.service.member.MemberService;
import com.lehecai.admin.web.service.member.WithdrawService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.api.ApiRequest;
import com.lehecai.core.api.ApiRequestService;
import com.lehecai.core.api.ApiResponse;
import com.lehecai.core.api.user.WithdrawBlacklist;
import com.lehecai.core.api.user.WithdrawBlacklistType;
import com.lehecai.core.api.user.WithdrawLog;
import com.lehecai.core.api.user.WithdrawWhitelist;
import com.lehecai.core.api.user.WithdrawWhitelistType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.lottery.WithdrawStatus;
import com.lehecai.core.lottery.WithdrawType;

public class WithdrawServiceImpl implements WithdrawService {
	private final Logger logger = LoggerFactory.getLogger(WithdrawServiceImpl.class);
	
	private ApiRequestService apiRequestService;
	private ApiRequestService apiWriteRequestService;
	private MemberService memberService;

	@Override
	public Map<String, Object> getResult(String id, String username, String idData,
			List<String> withdrawStatus, String bankCardno, List<String> bankTypeValues,
			WithdrawType withdrawType, WalletType walletType, Date beginDate, Date endDate, Date beginSuccessDate, Date endSuccessDate, YesNoStatus isExport, String batchNo,
			String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取提款审核数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_QUERY);
		
		if (id != null && !id.equals("")) {
			request.setParameter(WithdrawLog.QUERY_ID, id);
		}
		if (username != null && !"".equals(username)) {
			Long uid = null;
			try {
				uid = memberService.getIdByUserName(username);
			} catch (ApiRemoteCallFailedException e) {
				logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
			}
			if (uid != null && uid.longValue() != 0) {
				request.setParameter(WithdrawLog.QUERY_UID, String.valueOf(uid.longValue()));
			} else {
				logger.info("用户名不存在!返回空记录!");
				return null;
			}
		}
		if (idData != null && !"".equals(idData)) {
			request.setParameter(WithdrawLog.QUERY_ID_DATA, idData);
		}
		if (withdrawStatus != null && withdrawStatus.size() > 0) {
			request.setParameterIn(WithdrawLog.QUERY_STATUS, withdrawStatus);
		}
		if (bankCardno != null && !"".equals(bankCardno)) {
			request.setParameter(WithdrawLog.QUERY_BANK_CARDNO, bankCardno);
		}
		if (bankTypeValues != null && bankTypeValues.size() != 0) {
			request.setParameterIn(WithdrawLog.QUERY_BANK_TYPE, bankTypeValues);
		}
		if (withdrawType != null && withdrawType.getValue() != WithdrawType.ALL.getValue()) {
			request.setParameter(WithdrawLog.QUERY_WITHDRAW_TYPE, withdrawType.getValue()+"");
		}
		if (walletType != null && walletType.getValue() != WalletType.ALL.getValue()) {
			request.setParameter(WithdrawLog.QUERY_WALLET_TYPE, walletType.getValue()+"");
		}
		if (beginDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_TIMELINE,
					DateUtil.formatDate(beginDate, DateUtil.DATETIME), null);
		}
		if (endDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_TIMELINE,
					null, DateUtil.formatDate(endDate, DateUtil.DATETIME));
		}
		if (beginSuccessDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_SUCCESS_TIME,
					DateUtil.formatDate(beginSuccessDate, DateUtil.DATETIME), null);
		}
		if (endSuccessDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_SUCCESS_TIME,
					null, DateUtil.formatDate(endSuccessDate, DateUtil.DATETIME));
		}
		if (isExport != null && isExport.getValue() != YesNoStatus.ALL.getValue()) {
			request.setParameter(WithdrawLog.QUERY_IS_EXPORT, isExport.getValue() + "");
		}
		if (batchNo != null && !batchNo.equals("")) {
			request.setParameter(WithdrawLog.QUERY_BATCH_NO, batchNo);
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr, orderView);
		}
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			// request.setPagesize(ApiConstant.API_REQUEST_PAGESIZE_DEFAULT);
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取提款审核数据失败");
			throw new ApiRemoteCallFailedException("API获取提款审核数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取提款审核数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取提款审核数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取提款审核数据为空, message={}", response.getMessage());
			return null;
		}
		List<WithdrawLog> list = WithdrawLog.convertFromJSONArray(response.getData());
		if (pageBean != null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;// 页数
			if (pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
				if (totalCount % pageBean.getPageSize() != 0) {
					pageCount++;
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
	public Map<String, Object> getWithdrawStatistics(String id, String username, String idData,
			WithdrawStatus withdrawStatus, String bankCardno, List<String> bankTypeValues,
			WithdrawType withdrawType, WalletType walletType, Date beginDate, Date endDate, Date beginSuccessDate, Date endSuccessDate, YesNoStatus isExport, String batchNo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询提款审核统计数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_STATS_SUM);

		if (id != null && !id.equals("")) {
			request.setParameter(WithdrawLog.QUERY_ID, id);
		}
		if (username != null && !"".equals(username)) {
			Long uid = null;
			try {
				uid = memberService.getIdByUserName(username);
			} catch (Exception e) {
				logger.error("API根据用户名获取用户ID异常!{}", e.getMessage());
			}
			if (uid != null && uid.longValue() != 0) {
				request.setParameter(WithdrawLog.QUERY_UID, String.valueOf(uid.longValue()));
			} else {
				logger.info("用户名不存在！返回空记录！");
				return null;
			}
		}
		if (idData != null && !"".equals(idData)) {
			request.setParameter(WithdrawLog.QUERY_ID_DATA, idData);
		}
		if (withdrawStatus != null && withdrawStatus.getValue() != WithdrawStatus.ALL.getValue()) {
			request.setParameter(WithdrawLog.QUERY_STATUS, withdrawStatus.getValue()+"");
		}
		if (bankCardno != null && !"".equals(bankCardno)) {
			request.setParameter(WithdrawLog.QUERY_BANK_CARDNO, bankCardno);
		}
		if (bankTypeValues != null && bankTypeValues.size() != 0) {
			request.setParameterIn(WithdrawLog.QUERY_BANK_TYPE, bankTypeValues);
		}
		if (withdrawType != null && withdrawType.getValue() != WithdrawType.ALL.getValue()) {
			request.setParameter(WithdrawLog.QUERY_WITHDRAW_TYPE, withdrawType.getValue()+"");
		}
		if (walletType != null && walletType.getValue() != WalletType.ALL.getValue()) {
			request.setParameter(WithdrawLog.QUERY_WALLET_TYPE, walletType.getValue()+"");
		}
		if (beginDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_TIMELINE,
					DateUtil.formatDate(beginDate, DateUtil.DATETIME), null);
		}
		if (endDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_TIMELINE,
					null, DateUtil.formatDate(endDate, DateUtil.DATETIME));
		}
		if (beginSuccessDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_SUCCESS_TIME,
					DateUtil.formatDate(beginSuccessDate, DateUtil.DATETIME), null);
		}
		if (endSuccessDate != null) {
			request.setParameterBetween(WithdrawLog.QUERY_SUCCESS_TIME,
					null, DateUtil.formatDate(endSuccessDate, DateUtil.DATETIME));
		}
		if (isExport != null && isExport.getValue() != YesNoStatus.ALL.getValue()) {
			request.setParameter(WithdrawLog.QUERY_IS_EXPORT, isExport.getValue() + "");
		}
		if (batchNo != null && !batchNo.equals("")) {
			request.setParameter(WithdrawLog.QUERY_BATCH_NO, batchNo);
		}
		logger.info("Request Query String: {}", request.toQueryString());

		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取提款审核数据统计失败");
			throw new ApiRemoteCallFailedException("API获取提款审核数据统计失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取提款审核数据统计请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取提款审核数据统计请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取提款审核数据统计为空, message={}", response.getMessage());
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		JSONObject jsonObj = response.getData().getJSONObject(0);
		
		map.put(WithdrawLog.QUERY_AMOUNT, "0");
		map.put(WithdrawLog.QUERY_FEE, "0");
		if (jsonObj != null && !jsonObj.isNullObject()) {
			if (jsonObj.get("amount") != null) {
				map.put(WithdrawLog.QUERY_AMOUNT, jsonObj.get("amount"));
			}
			if (jsonObj.get("fee") != null) {
				map.put(WithdrawLog.QUERY_FEE, jsonObj.get("fee"));
			}
		}
		return map;
	}

	@Override
	public boolean handling(String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为开始处理状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_HANDLING);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为开始处理状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为开始处理状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为开始处理状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean audit(String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为提款审核状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_AUDIT);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为提款审核状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为提款审核状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API变更提款状态为提款审核状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean approve(String withdrawLogId, WithdrawStatus withdrawStatus) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为批准状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_APPROVE);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.QUERY_STATUS, withdrawStatus.getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为批准状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为批准状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API变更提款状态为批准状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 推迟
	 * @param withdrawLogId 提款编码
	 * @param day 推迟天数
	 */
	@Override
	public boolean delay(String withdrawLogId, int day) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为推迟处理状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_DELAYED);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.SET_DELAY_DAY, day + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为推迟处理状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为推迟处理状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为推迟处理状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean refuse(String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为拒绝状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REFUSE);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为拒绝状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为拒绝状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为拒绝状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean remit(String withdrawLogId, String withdrawSerialNumber) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为已提款成功状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REMITTED);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		if (StringUtils.isNotEmpty(withdrawSerialNumber)) {
			request.setParameter(WithdrawLog.SERIALNUMBER, withdrawSerialNumber);
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为已提款状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为已提款状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为已提款状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean remitRechargeSource(String withdrawLogId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为已已提款至充值来源至充值来源状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REMIT_RECHARGE_SOURCE);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为已提款至充值来源状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为已提款至充值来源状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API变更提款状态为已提款至充值来源状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 修改提款状态为提款退票状态
	 */
	public boolean remitReturn (String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为提款退票状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REMIT_REFUND);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.QUERY_STATUS, WithdrawStatus.REMITTANCE_RETURN.getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为提款退票状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为提款退票状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为提款退票状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 修改提款状态为退款退票状态
	 */
	public boolean refundReturn (String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为退款退票状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REMIT_REFUND);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.QUERY_STATUS, WithdrawStatus.REFUND_RETURN.getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为退款退票状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为退款退票状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为退款退票状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean remitFailure(String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为提款失败状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REMITFAILURE);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.QUERY_STATUS, WithdrawStatus.REMITFAILURE.getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为提款失败状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为提款失败状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为提款失败状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean refundFailure(String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为退款失败状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REMITFAILURE);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.QUERY_STATUS, WithdrawStatus.REFUND_FAILURE.getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为退款失败状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为退款失败状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API变更提款状态为退款失败状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 修改提款记录是否导出
	 * @param withdrawLogId
	 * @param remark
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public String updateExportStatus (List<String> withdrawLogIds, YesNoStatus exportStatus, String batchNo) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改提款记录是否导出");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_IS_EXPORT_UPDATE);
		
		request.setParameterIn(WithdrawLog.QUERY_ID, withdrawLogIds);
		request.setParameterForUpdate(WithdrawLog.SET_IS_EXPORT, exportStatus.getValue() + "");
		request.setParameterForUpdate(WithdrawLog.SET_BATCH_NO, batchNo);
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改提款记录是否导出失败");
			throw new ApiRemoteCallFailedException("API修改提款记录是否导出失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API修改提款记录是否导出请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API修改提款记录是否导出请求异常");
		}
		if (response.getData() == null) {
			logger.info("API修改提款记录是否导出响应数据为空");
			return null;
		}
		
		return response.getData().getJSONObject(0).toString();
	}
	
	/**
	 * 修改备注
	 */
	public boolean updateRemark(String withdrawLogId, String remark, Integer operationType) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改备注");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_REMARK_UPDATE);
		if (operationType == null || operationType < 0) {
			logger.error("api更改备注参数错误，必须制定操作方式。");
			throw new ApiRemoteCallFailedException("api更改备注参数错误，必须制定操作方式。");
		}
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.QUERY_WITHDRAW_TYPE, operationType + "");
		request.setParameterForUpdate(WithdrawLog.SET_WITHDRAW_REMARK, remark);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改备注失败");
			throw new ApiRemoteCallFailedException("API修改备注失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API修改备注请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	public Map<String, Object> checkAuditStatus(String withdrawLogId) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查看审核状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_AUDIT_STATUS_CHECK);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API查看审核状态失败");
			throw new ApiRemoteCallFailedException("API查看审核状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API查看审核状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("API查看审核状态请求异常");
		}
		if (response.getData() == null) {
			logger.error("API查看审核状态响应为空");
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, response.getData());
		
		return map;
	}
	
	@Override
	public Map<String, Object> checkAuditStatusNew(String withdrawLogId)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查看审核状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_AUDIT_STATUS_CHECK_NEW);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		
		logger.info("Request Query String: {}", request.toQueryString());
		Map<String, Object> map = new HashMap<String, Object>();
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API查看审核状态失败");
			throw new ApiRemoteCallFailedException("API查看审核状态失败");
		}
		if (response.getMessage() == null) {
			logger.error("API查看审核状态响应为空");
			return null;
		}
		
		//如果code=0，说明审核通过
		if (response.getCode() == ApiConstant.RC_SUCCESS) {
			map.put(ApiConstant.API_RESPONSE_CODE_NAME, ApiConstant.RC_SUCCESS);
		} else {
			map.put(ApiConstant.API_RESPONSE_CODE_NAME, ApiConstant.RC_FAILURE);
		}
		
		map.put(ApiConstant.API_RESPONSE_MESSAGE_NAME, response.getMessage());
		return map;
	}
	
	@Override
	public boolean updateProvinceAndCity(WithdrawLog withdraw)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改省市信息");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_AREA_UPDATE_PROVINCE_CITY);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdraw.getId());
		request.setParameterForUpdate(WithdrawLog.SET_PROVINCE, withdraw.getProvinceId() + "");
		request.setParameterForUpdate(WithdrawLog.SET_CITY, withdraw.getCityId() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改省市信息失败");
			throw new ApiRemoteCallFailedException("API修改省市信息失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API修改省市信息请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public Map<String, Object> getBlacklistResult(String detail, WithdrawBlacklistType type,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用API查询黑名单列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BLACKLIST_QUERY);
		if (detail != null && !detail.equals("")) {
			request.setParameter(WithdrawBlacklist.QUERY_DETAIL, detail);
		}
		if (type != null && type.getValue() != WithdrawBlacklistType.ALL.getValue()) {
			request.setParameter(WithdrawBlacklist.QUERY_TYPE, type.getValue()+"");
		}

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
	
		if (response == null) {
			logger.error("API获取黑名单列表失败");
			throw new ApiRemoteCallFailedException("API获取黑名单列表失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取黑名单列表请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取黑名单列表请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取黑名单列表为空, message={}", response.getMessage());
			return null;
		}
		List<WithdrawBlacklist> list = WithdrawBlacklist.convertFromJSONArray(response.getData());
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
	public void addBlacklist(WithdrawBlacklist blacklist) throws ApiRemoteCallFailedException {
		logger.info("进入调用API新增黑名单");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BLACKLIST_ADD);
		String detail = blacklist.getDetail();
		if (detail == null || detail.equals("")) {
			logger.error("黑名单详细信息为空");
			throw new ApiRemoteCallFailedException("调用API新增黑名单失败");
		}
		WithdrawBlacklistType type = blacklist.getType();
		if (type == null || type.getValue() == WithdrawBlacklistType.ALL.getValue()) {
			logger.error("黑名单类型为空");
			throw new ApiRemoteCallFailedException("调用API新增黑名单失败");
		}
		request.setParameterForUpdate(WithdrawBlacklist.SET_DETAIL, blacklist.getDetail());
		request.setParameterForUpdate(WithdrawBlacklist.SET_TYPE, blacklist.getType().getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("调用API新增黑名单失败");
		    throw new ApiRemoteCallFailedException("调用API新增黑名单失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API新增黑名单请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API新增黑名单请求出错," + response.getMessage());
		}
		if (response.getTotal() == ApiConstant.API_FALSE) {
			throw new ApiRemoteCallFailedException("该数据已存在");
		}
		logger.info("结束调用新增黑名单API");
	}
	
	@Override
	public void deleteBlacklist(WithdrawBlacklist blacklist)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除黑名单");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_BLACKLIST_DELETE);
		String detail = blacklist.getDetail();
		if (detail == null || detail.equals("")) {
			logger.error("黑名单详细信息为空");
			throw new ApiRemoteCallFailedException("调用API删除黑名单失败");
		}
		WithdrawBlacklistType type = blacklist.getType();
		if (type == null || type.getValue() == WithdrawBlacklistType.ALL.getValue()) {
			logger.error("黑名单类型为空");
			throw new ApiRemoteCallFailedException("调用API删除黑名单失败");
		}
		request.setParameter(WithdrawBlacklist.SET_DETAIL, blacklist.getDetail());
		request.setParameter(WithdrawBlacklist.SET_TYPE, blacklist.getType().getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("调用API删除黑名单失败");
		    throw new ApiRemoteCallFailedException("调用API删除黑名单失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除黑名单请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API删除黑名单请求出错," + response.getMessage());
		}
		logger.info("结束调用新增黑名单API");
	}
	
	@Override
	public Map<String, Object> getWhitelistResult(String detail,
			WithdrawWhitelistType type, PageBean pageBean)
			throws ApiRemoteCallFailedException {

		logger.info("进入调用API查询白名单列表");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WHITELIST_QUERY);
		if (detail != null && !detail.equals("")) {
			request.setParameter(WithdrawBlacklist.QUERY_DETAIL, detail);
		}
		if (type != null && type.getValue() != WithdrawBlacklistType.ALL.getValue()) {
			request.setParameter(WithdrawBlacklist.QUERY_TYPE, type.getValue()+"");
		}

		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
	
		if (response == null) {
			logger.error("API获取白名单列表失败");
			throw new ApiRemoteCallFailedException("API获取黑名单列表失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取白名单列表请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取白名单列表请求异常");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取白名单列表为空, message={}", response.getMessage());
			return null;
		}
		List<WithdrawBlacklist> list = WithdrawBlacklist.convertFromJSONArray(response.getData());
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
	public void addWhitelist(WithdrawWhitelist whitelist)
			throws ApiRemoteCallFailedException {

		logger.info("进入调用API新增白名单");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WHITELIST_ADD);
		String detail = whitelist.getDetail();
		if (detail == null || detail.equals("")) {
			logger.error("白名单详细信息为空");
			throw new ApiRemoteCallFailedException("调用API新增白名单失败");
		}
		WithdrawWhitelistType type = whitelist.getType();
		if (type == null || type.getValue() == WithdrawWhitelistType.ALL.getValue()) {
			logger.error("白名单类型为空");
			throw new ApiRemoteCallFailedException("调用API新增白名单失败");
		}
		request.setParameterForUpdate(WithdrawBlacklist.SET_DETAIL, whitelist.getDetail());
		request.setParameterForUpdate(WithdrawBlacklist.SET_TYPE, whitelist.getType().getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("调用API新增白名单失败");
		    throw new ApiRemoteCallFailedException("调用API新增白名单失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API新增白名单请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API新增白名单请求出错," + response.getMessage());
		}
		if (response.getTotal() == ApiConstant.API_FALSE) {
			throw new ApiRemoteCallFailedException("该数据已存在");
		}
		logger.info("结束调用新增白名单API");
	
		
	}
	@Override
	public void deleteWhitelist(WithdrawWhitelist whitelist)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API删除白名单");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WHITELIST_DELETE);
		String detail = whitelist.getDetail();
		if (detail == null || detail.equals("")) {
			logger.error("白名单详细信息为空");
			throw new ApiRemoteCallFailedException("调用API删除白名单失败");
		}
		WithdrawWhitelistType type = whitelist.getType();
		if (type == null || type.getValue() == WithdrawBlacklistType.ALL.getValue()) {
			logger.error("白名单类型为空");
			throw new ApiRemoteCallFailedException("调用API删除白名单失败");
		}
		request.setParameter(WithdrawBlacklist.SET_DETAIL, whitelist.getDetail());
		request.setParameter(WithdrawBlacklist.SET_TYPE, whitelist.getType().getValue() + "");
		
		logger.info("Request Query String: {}", request.toQueryString());
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("调用API删除白名单失败");
		    throw new ApiRemoteCallFailedException("调用API删除白名单失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API删除白名单请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API删除白名单请求出错," + response.getMessage());
		}
		logger.info("结束调用新增白名单API");
		
	}
	
	@Override
	public Map<String, Object> queryAuditStatus(String withdrawId, String userName, Double amount)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API查看审核状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_CHECK_MONEY);
		if (!StringUtils.isEmpty(withdrawId)) {
			request.setParameter(WithdrawLog.QUERY_ID, withdrawId);
		} else {
			request.setParameter(WithdrawLog.QUERY_USERNAME, userName);
			BigDecimal original = new BigDecimal(amount); 
			request.setParameter(WithdrawLog.QUERY_AMOUNT, original.setScale(2, BigDecimal.ROUND_HALF_UP) + "");
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		Map<String, Object> map = new HashMap<String, Object>();
		ApiResponse response = apiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_LONG);
		if (response == null) {
			logger.error("API查看审核状态失败");
			throw new ApiRemoteCallFailedException("API查看审核状态失败");
		}
		if (response.getMessage() == null) {
			logger.error("API查看审核状态响应为空");
			return null;
		}
		
		//如果code=0，说明审核通过
		if (response.getCode() == ApiConstant.RC_SUCCESS) {
			map.put(ApiConstant.API_RESPONSE_CODE_NAME, ApiConstant.RC_SUCCESS);
			map.put(ApiConstant.API_RESPONSE_DATA_NAME, response.getData());
		} else {
			map.put(ApiConstant.API_RESPONSE_CODE_NAME, ApiConstant.RC_FAILURE);
		}
		
		map.put(ApiConstant.API_RESPONSE_MESSAGE_NAME, response.getMessage());
		return map;
	}
	
	/**
	 * 修改备注
	 */
	public boolean updateDescription(String withdrawLogId, String description) throws ApiRemoteCallFailedException {
		logger.info("进入调用API修改备注");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_UPDATE_DESCRIPTION);
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameterForUpdate(WithdrawLog.SET_DESCRIPTION, description);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API修改用户备注失败");
			throw new ApiRemoteCallFailedException("API修改用户备注失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.info("API修改用户备注请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean remitting(String withdrawLogId, WithdrawStatus withdrawStatus)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API变更提款状态为开始提款或开始退款状态");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_REMITTING);
		
		request.setParameter(WithdrawLog.QUERY_ID, withdrawLogId);
		request.setParameter(WithdrawLog.QUERY_STATUS, withdrawStatus.getValue() + "");
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiWriteRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("API变更提款状态为开始提款或开始退款状态失败");
			throw new ApiRemoteCallFailedException("API变更提款状态为开始提款或开始退款状态失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("API变更提款状态为开始提款或开始退款状态请求异常, rc={}, message={}", response.getCode(), response.getMessage());
			return false;
		}
		return true;
	}
	
	public ApiRequestService getApiRequestService() {
		return apiRequestService;
	}

	public void setApiRequestService(ApiRequestService apiRequestService) {
		this.apiRequestService = apiRequestService;
	}

	public ApiRequestService getApiWriteRequestService() {
		return apiWriteRequestService;
	}
	public void setApiWriteRequestService(ApiRequestService apiWriteRequestService) {
		this.apiWriteRequestService = apiWriteRequestService;
	}
	public MemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	@Override
	public Map<String, Object> getResultByIds(String ids, PageBean pageBean, String orderStr, String orderView)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API获取提款审核数据");
		ApiRequest request = new ApiRequest();
		request.setUrl(ApiConstant.API_URL_WITHDRAW_LOG_QUERY_MULTIPLE);
		
		if (StringUtils.isEmpty(ids)) {
			logger.error("API获取提款审核数据失败,ids为空");
			throw new ApiRemoteCallFailedException("API获取提款审核数据失败,ids为空");
		}
		request.setParameter(WithdrawLog.QUERY_IDS, ids);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPagesize(pageBean.getPageSize());
		}
		if (orderStr != null && !"".equals(orderStr) && orderView != null
				&& !"".equals(orderView)) {
			request.addOrder(orderStr, orderView);
		}
		logger.info("Request Query String: {}", request.toQueryString());
		
		ApiResponse response = apiRequestService.request(request,
				ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		
		if (response == null) {
			logger.error("API获取提款审核数据失败");
			throw new ApiRemoteCallFailedException("API获取提款审核数据失败");
		}
		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用API获取提款审核数据请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用API获取提款审核数据请求出错");
		}
		if (response.getData() == null || response.getData().isEmpty()) {
			logger.warn("API获取提款审核数据为空, message={}", response.getMessage());
			return null;
		}
		List<WithdrawLog> list = WithdrawLog.convertFromJSONArray(response.getData());
		if (pageBean != null) {
			int totalCount = response.getTotal();
			pageBean.setCount(totalCount);
			int pageCount = 0;// 页数
			if (pageBean.getPageSize() != 0) {
				pageCount = totalCount / pageBean.getPageSize();
				if (totalCount % pageBean.getPageSize() != 0) {
					pageCount++;
				}
			}
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, list);
		return map;
	}

}
