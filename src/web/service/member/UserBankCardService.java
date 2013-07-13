package web.service.member;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.api.user.UserBankCard;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public interface UserBankCardService {
	
	/**
	 * 查询用户银行卡信息
	 * @return
	 */
	public Map<String, Object> queryBankCardList(Member member, PageBean pageBean)
		throws ApiRemoteCallFailedException;
	
	/**
	 * 根据ID查询用户银行卡信息
	 * @return
	 */
	public Map<String, Object> queryBankCardListById(Long bankCardId)
		throws ApiRemoteCallFailedException;
	
	/**
	 * 删除用户银行卡信息
	 * @return
	 */
	public boolean delBankCard(Long bankCardId)	throws ApiRemoteCallFailedException;

	/**
	 * 绑定用户银行卡信息
	 * @return
	 */
	public boolean lock(UserBankCard userBankCard) throws ApiRemoteCallFailedException;
	
	/**
	 * 修改用户银行卡信息
	 * @return
	 */
	public boolean manageBankCard(UserBankCard userBankcard) throws ApiRemoteCallFailedException;

	/**
	 * 解除用户的IVR卡绑定
	 */
	boolean unlock(UserBankCard userBankcard) throws ApiRemoteCallFailedException;
	
}
