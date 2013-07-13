package web.service.member;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.api.user.Member;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.AutoFollowType;
import com.lehecai.core.lottery.LotteryType;

public interface AutoFollowService {
	
	/**
	 * 查询用户跟单信息
	 * @return
	 */
	public Map<String, Object> queryAutoFollowList(Member member, PageBean pageBean)
		throws ApiRemoteCallFailedException;

	/**
	 * 查询用户跟具体跟单人的详细信息
	 * @return
	 */
	public Map<String, Object> queryAutoFollowInfoList(Long fuid, Long tuid, PageBean pageBean)
		throws ApiRemoteCallFailedException;

	/**
	 * 添加自动跟单
	 * @param fuid
	 * @param tuid
	 * @param lotteryType
	 * @param autoFollowType
	 * @param numPerphase
	 * @param unitAmount
	 * @param cancelBelowAmount
	 * @throws ApiRemoteCallFailedException
	 */
	public void addAutoFollow(Long fuid, Long tuid, LotteryType lotteryType, AutoFollowType autoFollowType,
                              Integer numPerphase, Double unitAmount, Double cancelBelowAmount) throws ApiRemoteCallFailedException;

	/**
	 * 取消自动跟单
	 * @param fuid
	 * @param tuid
	 * @param lotteryType
	 * @throws ApiRemoteCallFailedException
	 */
	public void delAutoFollow(Long fuid, Long tuid, LotteryType lotteryType) throws ApiRemoteCallFailedException;
}
