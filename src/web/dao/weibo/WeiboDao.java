/**
 * 
 */
package web.dao.weibo;

import java.util.List;

import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.domain.weibo.WeiboLottery;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author qatang
 *
 */
public interface WeiboDao {
	/**
	 * 更新认证信息
	 * @param tokenInfo
	 */
	void merge(TokenInfo tokenInfo);
	/**
	 * 根据微博类型及用户编号查询认证信息
	 * @param uid
	 * @param weiboType
	 * @return
	 */
	TokenInfo getToken(String uid, WeiboType weiboType);
	/**
	 * 根据微博类型查询认证列表
	 * @param weiboType
	 * @return
	 */
	List<TokenInfo> getTokenList(WeiboType weiboType);
	/**
	 * 查询所有认证
	 * @param weiboType
	 * @return
	 */
	List<TokenInfo> findTokenInfoList();
	
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
}
