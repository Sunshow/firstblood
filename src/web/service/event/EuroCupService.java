package web.service.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.core.api.event.EuroCupDraw;
import com.lehecai.core.api.event.EuroCupMatch;
import com.lehecai.core.api.event.EuroCupOrder;
import com.lehecai.core.api.event.EuroCupTeam;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 欧洲杯业务逻辑层接口
 * @author chirowong
 *
 */
public interface EuroCupService {
	
	/**
	 * 所有球队信息
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findTeamList(PageBean pageBean) throws ApiRemoteCallFailedException;

	/**
     * 更新球队信息
     * @param euroCupTeam
     * @return
     * @throws ApiRemoteCallFailedException
     */
	boolean updateTeamSp(EuroCupTeam euroCupTeam) throws ApiRemoteCallFailedException;
	
	/**
	 * 批量更新球队信息
	 * @param euroCupTeam
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public ResultBean batchUpdateTeamSp(List<EuroCupTeam> euroCupTeams);
	
	/**
	 * 所有赛程信息
	 * @param pageBean 分页信息
	 */
	Map<String, Object> findMatchList(PageBean pageBean) throws ApiRemoteCallFailedException;
	
    /**
     * 更新赛程信息
     * @param euroCupTeam
     * @return
     * @throws ApiRemoteCallFailedException
     */
	boolean updateMatch(EuroCupMatch euroCupMatch) throws ApiRemoteCallFailedException;
	
    /**
     * 更新赛程SP信息
     * @param euroCupTeam
     * @return
     * @throws ApiRemoteCallFailedException
     */
	ResultBean updateMatchSp(EuroCupMatch euroCupMatch) throws ApiRemoteCallFailedException;
	
	/**
	 * 批量更新赛程信息
	 * @param euroCupTeam
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public ResultBean batchUpdateMatch(List<EuroCupMatch> euroCupMatchs);
	
	/**
	 * 欧洲杯开奖
	 * @param euroCupTeamIds
	 * @param type 1-冠军开奖 2-四强开奖 3-八强开奖
	 * @return
	 */
	public ResultBean prize(String euroCupTeamIds, Integer type) throws ApiRemoteCallFailedException;
	
	/**
	 * 欧洲杯派奖
	 * @return
	 */
	public ResultBean payout() throws ApiRemoteCallFailedException;
	
	/**
	 * 欧洲杯订单信息
	 * @param euroCupOrder
	 * @param beginDate
	 * @param endDate
	 * @param orderStr
	 * @param orderView
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> findOrderList(EuroCupOrder euroCupOrder, Date beginDate, Date endDate,
                                      String orderStr, String orderView, PageBean pageBean) throws ApiRemoteCallFailedException;
	
    /**
     * 单场金币竞猜胜平负开奖
     * @param euroCupTeam
     * @return
     * @throws ApiRemoteCallFailedException
     */
	ResultBean prizeSfp(EuroCupMatch euroCupMatch) throws ApiRemoteCallFailedException;
	
    /**
     * 单场金币竞猜比分开奖
     * @param euroCupTeam
     * @return
     * @throws ApiRemoteCallFailedException
     */
	ResultBean prizeBf(EuroCupMatch euroCupMatch) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取开奖状态
	 */
	EuroCupDraw getPrizeResult() throws ApiRemoteCallFailedException;

	/**
	 * 派送金币
	 * @param amount 充值数量
	 * @param adminId 操作用户编码
	 * @param remark 备注
	 * @param userId 用户编码
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	ResultBean presentCoin(Integer amount, Long adminId, String remark, Long userId) throws ApiRemoteCallFailedException;
	
	/**
	 * 清空开奖状态
	 */
	ResultBean flushPrize() throws ApiRemoteCallFailedException;
	
	/**
	 * 获取模式-北单、竞彩
	 */
	ResultBean getMode() throws ApiRemoteCallFailedException;
	
	/**
	 * 获设置模式-北单、竞彩
	 */
	ResultBean setMode(String mode) throws ApiRemoteCallFailedException;
	
	/**
	 * 添加禁言用户
	 */
	ResultBean chatAdd(Long userId) throws ApiRemoteCallFailedException;
	
	/**
	 * 删除禁言用户
	 */
	ResultBean chatDelete(Long userId) throws ApiRemoteCallFailedException;
	
	/**
	 * 显示已禁言的用户列表
	 */
	Map<String, Object> chatList(Long userId, String userName, PageBean pageBean) throws ApiRemoteCallFailedException;
}
