/**
 * 
 */
package web.service.impl.weibo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import com.lehecai.admin.web.action.weibo.sina.SinaWeiboApiErrorType;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.core.api.bean.ApiResultBean;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author qatang
 *
 */
public class SinaWeiboServiceImpl extends CommonWeiboServiceImpl {
	protected WeiboType weiboType = WeiboType.SINA;
	
	@Override
	public List<ApiResultBean> publish(String content, List<TokenInfo> tokenInfoList) {
		List<ApiResultBean> list = new ArrayList<ApiResultBean>();
		
		if (tokenInfoList != null && tokenInfoList.size() > 0) {
			for (TokenInfo tokenInfo : tokenInfoList) {
				Date updateTime = tokenInfo.getUpdateTime();
				Long expiresIn = tokenInfo.getExpiresIn();
				
				ApiResultBean bean = new ApiResultBean();
				if (updateTime != null && expiresIn != null) {
					Calendar cd = Calendar.getInstance();
					cd.setTime(updateTime);
					cd.add(Calendar.SECOND, Integer.valueOf(tokenInfo.getExpiresIn() + ""));
					
					if (System.currentTimeMillis() >= cd.getTimeInMillis()) {
						logger.error("[{}]的uid={}发表微博失败:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), SinaWeiboApiErrorType.ACCESS_TOKEN_EXPIRED.getName()});
						bean.setResult(false);
						bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博失败:" + SinaWeiboApiErrorType.ACCESS_TOKEN_EXPIRED.getName());
					} else {
						Timeline tm = new Timeline();
						tm.client.setToken(tokenInfo.getToken());
						try {
							Status status = tm.UpdateStatus(content);
							logger.info("[{}]的uid={}发表微博成功:{}", new Object[]{weiboType.getName(), status.getUser().getName(), content});
							bean.setResult(true);
							bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博成功:" + content);
						} catch (WeiboException e) {
							logger.error("[{}]的uid={}发表微博失败:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), content});
							logger.error(e.getMessage(), e);
							bean.setResult(false);
							bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博失败:" + content + "，原因：" + e.getMessage());
						}
					}
				} else {
					logger.error("[{}]的uid={}发表微博失败:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), SinaWeiboApiErrorType.ACCESS_TOKEN_EXPIRED.getName()});
					bean.setResult(false);
					bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博失败:" + SinaWeiboApiErrorType.ACCESS_TOKEN_EXPIRED.getName());
				}
				
				list.add(bean);
			}
		}
		return list;
	}
}
