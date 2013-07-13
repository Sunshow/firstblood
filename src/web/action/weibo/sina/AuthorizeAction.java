/**
 * 
 */
package web.action.weibo.sina;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weibo4j.Oauth;
import weibo4j.http.AccessToken;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.service.weibo.WeiboService;
import com.lehecai.core.lottery.WeiboType;

/**
 * @author qatang
 *
 */
public class AuthorizeAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private final WeiboType weiboType = WeiboType.SINA;
	
	private final String CLIENT_ID = "client_ID";//weibo4j sdk 固定字段，不能改变
	private final String CLIENT_SERCRET = "client_SERCRET";//weibo4j sdk 固定字段，不能改变
	private final String REDIRECT_URI = "redirect_URI";//weibo4j sdk 固定字段，不能改变
	
	private String code;
	
	private String backUrl;
	private String consumerKey;
	private String consumerSecret;
	
	private WeiboService sinaWeiboService;
	
	public void init() {
		WeiboConfig.updateProperties(CLIENT_ID, consumerKey);
		WeiboConfig.updateProperties(CLIENT_SERCRET, consumerSecret);
		WeiboConfig.updateProperties(REDIRECT_URI, backUrl);
	}
	
	public String handle() {
		return null;
	}
	
	/**
	 * 申请授权
	 * 由于回调地址需绑定域名，调试前先修改hosts 127.0.0.1 admin.lehecai.com
	 * @return
	 */
	public String request() {
		Oauth oauth = new Oauth();
		String requestAuthorizeCodeUrl = null;
		try {
			requestAuthorizeCodeUrl = oauth.authorize("code");
		} catch (WeiboException e) {
			logger.error(e.getMessage(), e);
		}
		if (!StringUtils.isEmpty(requestAuthorizeCodeUrl)) {
			super.setForwardUrl(requestAuthorizeCodeUrl);
			return "forward";
		}
		logger.error("sina微博申请授权错误，requestAuthorizeCodeUrl为空");
		super.setErrorMessage("sina微博申请授权错误，requestAuthorizeCodeUrl为空");
		return "failure";
	}
	
	/**
	 * 授权通过回调方法
	 * V2没有token_secret字段返回
	 * 
	 * accessToken返回值：
	 * access_token string 	用于调用access_token，接口获取授权后的access token。
	 * expires_in 	string 	access_token的生命周期。expires_in（单位：秒）值就是access_token的生命周期。过期时间 = 用户授权时间 + 授权有效期。 
	 * remind_in 	string 	access_token的生命周期（该参数即将废弃，开发者请使用expires_in）。
	 * uid 	        string 	当前授权用户的UID。 
	 * 
	 * @return
	 */
	public String callback() {
		if (code == null) {
			logger.error("sina微博授权通过回调错误，code为空");
			super.setErrorMessage("sina微博授权通过回调错误，code为空");
			return "failure";
		}
		Oauth oauth = new Oauth();
		AccessToken accessToken = null;
		try {
			accessToken = oauth.getAccessTokenByCode(code);
		} catch (WeiboException e) {
			logger.error(e.getMessage(), e);
		}

		if (accessToken != null) {
			TokenInfo tokenInfo = null;
			if (!StringUtils.isEmpty(accessToken.getUid())) {
				tokenInfo = sinaWeiboService.getToken(accessToken.getUid(), weiboType);
			}
			if (tokenInfo == null) {
				tokenInfo = new TokenInfo();
				tokenInfo.setUid(accessToken.getUid());
				tokenInfo.setToken(accessToken.getAccessToken());
				tokenInfo.setTokenSecret("");
				tokenInfo.setExpiresIn(StringUtils.isEmpty(accessToken.getExpireIn()) ? 0L : Long.parseLong(accessToken.getExpireIn()));
				tokenInfo.setWeiboType(weiboType);
				tokenInfo.setValid(true);
				tokenInfo.setCreateTime(new Date());
				tokenInfo.setUpdateTime(new Date());
			} else {
				tokenInfo.setToken(accessToken.getAccessToken());
				tokenInfo.setExpiresIn(StringUtils.isEmpty(accessToken.getExpireIn()) ? 0L : Long.parseLong(accessToken.getExpireIn()));
				tokenInfo.setUpdateTime(new Date());
			}
			sinaWeiboService.manageToken(tokenInfo);
			return "success";
		}
		logger.error("sina微博获取accessToken时错误，accessToken为空");
		super.setErrorMessage("sina微博获取accessToken时错误，accessToken为空");
		return "failure";
	}
	
	public String getBackUrl() {
		return backUrl;
	}
	
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	
	public WeiboService getSinaWeiboService() {
		return sinaWeiboService;
	}
	
	public void setSinaWeiboService(WeiboService sinaWeiboService) {
		this.sinaWeiboService = sinaWeiboService;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
