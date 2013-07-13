package web.service.weibo;

import java.util.List;

import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.domain.weibo.WeiboLottery;
import com.lehecai.core.lottery.WeiboType;


public interface WeiboLotteryService {
	/**
	 * 添加微博分享彩种记录
	 */
	void addWeiboLottery(WeiboLottery weiboLottery);
	
	/**
	 * 根据微博编号查询微博分享彩种记录列表
	 */
	List<WeiboLottery> findWeiboLotteryList(String uid, WeiboType weiboType);
	
	/**
	 * 删除微博分享彩种记录
	 */
	void deleteWeiboLottery(WeiboLottery weiboLottery);
	
	/**
	 * 查询所有认证信息
	 */
	List<TokenInfo> findTokenInfoList();
}
