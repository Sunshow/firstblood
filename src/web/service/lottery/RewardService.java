/**
 * 
 */
package web.service.lottery;

import net.sf.json.JSONObject;

import com.lehecai.core.lottery.LotteryType;

/**
 * 获取派奖状态
 * @author qatang
 *
 */
public interface RewardService {
	public JSONObject getRewardStatus(LotteryType lotteryType, String taskId);
}
