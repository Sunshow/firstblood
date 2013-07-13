package web.service.weibo;

import java.util.List;

import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.core.api.bean.ApiResultBean;
import com.lehecai.core.lottery.WeiboType;


public interface WeiboService {
	/**
	 * 发表微博
	 * @param content
	 * @return
	 */
	public List<ApiResultBean> publish(String content, List<TokenInfo> tokenInfoList);
	/**
	 * 令牌信息入库
	 * @param token
	 * @param tokenSecret
	 */
	public void manageToken(TokenInfo tokenInfo);
	/**
	 * 根据用户编码和微博类型得到令牌实体
	 * @param uid
	 * @param weiboType
	 * @return
	 */
	public TokenInfo getToken(String uid, WeiboType weiboType);
	/**
	 * 根据微博类型得到令牌列表
	 * @param weiboType
	 * @return
	 */
	public List<TokenInfo> getTokenList(WeiboType weiboType);
}
