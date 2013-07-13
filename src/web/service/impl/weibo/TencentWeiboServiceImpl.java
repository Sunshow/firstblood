/**
 * 
 */
package web.service.impl.weibo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import com.lehecai.admin.web.action.weibo.sina.SinaWeiboApiErrorType;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.core.api.bean.ApiResultBean;
import com.lehecai.core.lottery.WeiboType;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;

/**
 * @author qatang
 *
 */
public class TencentWeiboServiceImpl extends CommonWeiboServiceImpl {
	protected WeiboType weiboType = WeiboType.TENCENT;
	private String consumerKey;
	private String consumerSecret;
	
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
						OAuthV2 oAuth = new OAuthV2();
						oAuth.setClientId(consumerKey);
						oAuth.setAccessToken(tokenInfo.getToken());
						oAuth.setOpenid(tokenInfo.getUid());
						oAuth.setOauthVersion(OAuthConstants.OAUTH_VERSION_2_A);
						
						TAPI tAPI = new TAPI(oAuth.getOauthVersion());
						try {
							String response = tAPI.add(oAuth, "json", content, oAuth.getClientIP());
							int rs = JSONObject.fromObject(response).getInt("errcode");
							if (rs == 0) {
								logger.info("[{}]的uid={}发表微博成功:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), content});
								bean.setResult(true);
								bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博成功:" + content);
							} else {
								logger.info("[{}]的uid={}发表微博失败:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), content});
								bean.setResult(true);
								bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博失败:" + content);
							}
						} catch (Exception e) {
							logger.error("[{}]的uid={}发表微博失败:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), content});
							logger.error(e.getMessage(), e);
							bean.setResult(false);
							bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博失败:" + content + "，原因：" + e.getMessage());
						}
					}
				} else {
					logger.error("[{}]的uid={}发表微博失败:access_token过期", new Object[]{weiboType.getName(), tokenInfo.getUid()});
					bean.setResult(false);
					bean.setMessage("[" + weiboType.getName() + "]的uid=" + tokenInfo.getUid() + "发表微博失败:access_token过期");
				}
				
				list.add(bean);
			}
		}
		return list;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
}
