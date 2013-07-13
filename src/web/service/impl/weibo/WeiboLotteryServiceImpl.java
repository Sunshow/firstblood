/**
 * 
 */
package web.service.impl.weibo;

import java.util.List;

import com.lehecai.admin.web.dao.weibo.WeiboDao;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.domain.weibo.WeiboLottery;
import com.lehecai.admin.web.service.weibo.WeiboLotteryService;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author qatang
 *
 */
public class WeiboLotteryServiceImpl implements WeiboLotteryService {
	
	private WeiboDao weiboDao;

	/**
	 * 添加微博分享彩种记录
	 */
	@Override
	public void addWeiboLottery(WeiboLottery weiboLottery) {
		weiboDao.addWeiboLottery(weiboLottery);
	}
	
	/**
	 * 根据微博编号查询微博分享彩种记录列表
	 */
	@Override
	public List<WeiboLottery> findWeiboLotteryList(String uid, WeiboType weiboType) {
		return weiboDao.findWeiboLotteryList(uid, weiboType);
	}
	
	/**
	 * 删除微博分享彩种记录
	 */
	@Override
	public void deleteWeiboLottery(WeiboLottery weiboLottery) {
		weiboDao.deleteWeiboLottery(weiboLottery);
	}
	
	/**
	 * 查询所有认证信息
	 */
	public List<TokenInfo> findTokenInfoList() {
		return weiboDao.findTokenInfoList();
	}
	
	public WeiboDao getWeiboDao() {
		return weiboDao;
	}

	public void setWeiboDao(WeiboDao weiboDao) {
		this.weiboDao = weiboDao;
	}

}
