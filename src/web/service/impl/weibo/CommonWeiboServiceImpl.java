/**
 * 
 */
package web.service.impl.weibo;

import java.util.List;

import com.lehecai.admin.web.dao.weibo.WeiboDao;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author qatang
 *
 */
public abstract class CommonWeiboServiceImpl extends AbstractWeiboServiceImpl {
	protected WeiboType weiboType;
	
	private WeiboDao weiboDao;

	@Override
	public void manageToken(TokenInfo tokenInfo) {
		weiboDao.merge(tokenInfo);
	}
	
	@Override
	public TokenInfo getToken(String uid, WeiboType weiboType) {
		return weiboDao.getToken(uid, weiboType);
	}
	
	@Override
	public List<TokenInfo> getTokenList(WeiboType weiboType) {
		return weiboDao.getTokenList(weiboType);
	}
	
	public WeiboDao getWeiboDao() {
		return weiboDao;
	}

	public void setWeiboDao(WeiboDao weiboDao) {
		this.weiboDao = weiboDao;
	}

}
