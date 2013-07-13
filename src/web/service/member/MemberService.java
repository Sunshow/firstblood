package web.service.member;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.MemberBasic;
import com.lehecai.core.api.user.Wallet;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.MemberStatus;
import com.lehecai.core.lottery.WalletType;

public interface MemberService {
	
	public Map<String, Object> getResult(String userName, String name,
                                         String phone, String email, Date rbeginDate, Date rendDate,
                                         Date lbeginDate, Date lendDate, String source,
                                         String orderStr, String orderView, boolean rechargered, PageBean pageBean)
			throws ApiRemoteCallFailedException;
	public Member get(Long uid) throws ApiRemoteCallFailedException;
	public List<Wallet> getWallets(Long uid) throws ApiRemoteCallFailedException;
	public Member get(String userName) throws ApiRemoteCallFailedException;
	//模糊查询
	public Map<String, Object> fuzzyQueryResult(Long uid, String userName, String name,
                                                String phone, String email, String idData, Date rbeginDate,
                                                Date rendDate, Date lbeginDate, Date lendDate,
                                                String source, String orderStr, String orderView, boolean rechargered, PageBean pageBean)
			throws ApiRemoteCallFailedException;
	/**
	 * 更新用户信息
	 * @param member	
	 * @param prop
	 * @param value
	 * @return
	 */
	public ResultBean update(Long uid, String prop, String value, MemberStatus ms) throws ApiRemoteCallFailedException;
	
	/**
	 * 重置会员密码
	 * @param uid
	 */
	public String resetPassword(Long uid) throws ApiRemoteCallFailedException;
	/**
	 * 冻结钱包
	 * @param uid 会员编码
	 * @param walletType 钱包类型
	 * @param amount 金额
	 */
	public Long freezeWallet(Long uid, WalletType walletType, Double amount, String remark, Long frozenUid) throws ApiRemoteCallFailedException;
	/**
	 * 通过uid获得username
	 * @param uid
	 */
	public String getUserNameById(Long uid) throws ApiRemoteCallFailedException;
	/**
	 * 通过username获得uid
	 * @param uid
	 */
	public Long getIdByUserName(String userName) throws ApiRemoteCallFailedException;
	/**
	 * 设置特殊会员
	 * @param uid
	 */
	public void updateSpecialMember(List<String> userNames) throws ApiRemoteCallFailedException;
	/**
	 * 得到特殊会员列表
	 * @param uid
	 */
	public List<MemberBasic> listSpecialMembers() throws ApiRemoteCallFailedException;
	/**
	 * 得到会员指定钱包类型的钱包
	 * @param uid
	 */
	public Wallet getWallet(Long uid, WalletType walletType) throws ApiRemoteCallFailedException;
	/**
	 * 得到特殊会员现金钱包
	 * @param uid
	 */
	public List<Member> getSpecialMemberWallet(List<MemberBasic> list) throws ApiRemoteCallFailedException;
	/**
	 * 根据uids列表查询对应的member列表
	 * @param uids
	 */
	public List<Member> getMembersByUids(List<String> uids) throws ApiRemoteCallFailedException;
	/**
	 * 根据usernames列表查询对应的uids列表
	 * @param usernames
	 */
	public List<String> getUidsByUsernames(List<String> usernames) throws ApiRemoteCallFailedException;
	/**
	 * 根据usernames列表查询对应的uids列表
	 * @param usernames
	 */
	public Map<String, String> getUidsByUsernamesForConvert(List<String> usernames)
			throws ApiRemoteCallFailedException;
	
	/**
	 * 重置会员支付密码
	 * @param uid
	 */
	public String resetPayPassword(Long uid) throws ApiRemoteCallFailedException;
	
	//模糊查询
	public Map<String, Object> fuzzyQueryResultByCreditLevel(Long uid, String userName, String name,
                                                             String phone, String email, String idData, Date rbeginDate,
                                                             Date rendDate, Date lbeginDate, Date lendDate,
                                                             String source, String orderStr, String orderView, boolean rechargered, Integer level, PageBean pageBean)
			throws ApiRemoteCallFailedException;
}