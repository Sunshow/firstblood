package web.service.member;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.api.user.WithdrawBlacklist;
import com.lehecai.core.api.user.WithdrawBlacklistType;
import com.lehecai.core.api.user.WithdrawLog;
import com.lehecai.core.api.user.WithdrawWhitelist;
import com.lehecai.core.api.user.WithdrawWhitelistType;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.WalletType;
import com.lehecai.core.lottery.WithdrawStatus;
import com.lehecai.core.lottery.WithdrawType;

public interface WithdrawService {

	Map<String, Object> getResult(String id, String username, String idData,
                                  List<String> withdrawStatus, String bankCardno, List<String> bankTypeValues,
                                  WithdrawType withdrawType, WalletType walletType, Date beginDate, Date endDate, Date beginSuccessDate, Date endSuccessDate, YesNoStatus isExport, String batchNo,
                                  String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	Map<String, Object> getWithdrawStatistics(String id, String username, String idData,
                                              WithdrawStatus withdrawStatus, String bankCardno, List<String> bankTypeValues,
                                              WithdrawType withdrawType, WalletType walletType, Date beginDate, Date endDate, Date beginSuccessDate, Date endSuccessDate, YesNoStatus isExport, String batchNo) throws ApiRemoteCallFailedException;
	/**
	 * 修改省市信息
	 * @param withdraw
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean updateProvinceAndCity(WithdrawLog withdraw) throws ApiRemoteCallFailedException;
	/**
	 * 修改提款状态为处理中状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean handling(String withdrawLogId) throws ApiRemoteCallFailedException;
	/**
	 * 修改提款状态为提款审核
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean audit(String withdrawLogId) throws ApiRemoteCallFailedException;
	/**
	 * 修改提款状态为推迟处理状态
	 * @param withdrawLogId	提款编码
	 * @param day 推迟天数
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean delay(String withdrawLogId, int day) throws ApiRemoteCallFailedException;
	/**
	 * 修改提款状态为批准状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean approve(String withdrawLogId, WithdrawStatus withdrawStatus) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为拒绝状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean refuse(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为已提款状态
	 * @param withdrawLogId
	 * @param withdrawSerialNumber
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean remit(String withdrawLogId, String withdrawSerialNumber) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为已提款至充值来源状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean remitRechargeSource(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为提款失败状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean remitFailure(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为退款失败状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean refundFailure(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为提款退票状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean remitReturn(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为退款退票状态
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean refundReturn(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款记录是否导出
	 * @param withdrawLogId
	 * @param remark
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	String updateExportStatus(List<String> withdrawLogIds, YesNoStatus exportStatus, String batchNo) throws ApiRemoteCallFailedException;
	
	/**
	 * 更改内部备注或置为退款受理状态
	 * @param withdrawLogId
	 * @param remark
	 * @param operationType
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean updateRemark(String withdrawLogId, String remark, Integer operationType) throws ApiRemoteCallFailedException;
	Map<String, Object> checkAuditStatus(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 2012-12-21
	 * @param withdrawLogId
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> checkAuditStatusNew(String withdrawLogId) throws ApiRemoteCallFailedException;
	
	/**
	 * 查询黑名单
	 * @param detail
	 * @param type
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> getBlacklistResult(String detail, WithdrawBlacklistType type, PageBean pageBean) throws ApiRemoteCallFailedException;

	/**
	 * 新增黑名单
	 * @param blacklist
	 * @throws ApiRemoteCallFailedException
	 */
	void addBlacklist(WithdrawBlacklist blacklist) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除黑名单
	 * @param blacklist
	 * @throws ApiRemoteCallFailedException
	 */
	void deleteBlacklist(WithdrawBlacklist blacklist) throws ApiRemoteCallFailedException;
	
	/**
	 * 查询白名单
	 * @param detail
	 * @param type
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> getWhitelistResult(String detail, WithdrawWhitelistType type, PageBean pageBean) throws ApiRemoteCallFailedException;

	/**
	 * 新增白名单
	 * @param whiteList
	 * @throws ApiRemoteCallFailedException
	 */
	void addWhitelist(WithdrawWhitelist whitelist) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除白名单
	 * @param whiteList
	 * @throws ApiRemoteCallFailedException
	 */
	void deleteWhitelist(WithdrawWhitelist whitelist) throws ApiRemoteCallFailedException;
	
	/**
	 * 查询提款状态
	 * @param withdrawId
	 * @param userName
	 * @param amount
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> queryAuditStatus(String withdrawId, String userName, Double amount) throws ApiRemoteCallFailedException;
	
	/**
	 * 更改用户备注
	 * @param withdrawLogId
	 * @param description
	 * @param operationType
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean updateDescription(String withdrawLogId, String description) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改提款状态为开始退款或开始提款
	 * @param withdrawLogId
	 * @param withdrawStatus
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean remitting(String withdrawLogId, WithdrawStatus withdrawStatus) throws ApiRemoteCallFailedException;
	
	/**
	 * 根据ids查询结果
	 * @param ids
	 * @param pageBean
	 * @param orderStr
	 * @param orderView
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> getResultByIds(String ids, PageBean pageBean, String orderStr, String orderView) throws ApiRemoteCallFailedException;
}