/**
 * 
 */
package web.action.weibo.sohu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.service.weibo.WeiboService;
import com.lehecai.core.lottery.WeiboType;
import com.lehecai.core.util.CharsetConstant;

/**
 * @author qatang
 *
 */
public class AuthorizeAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	private final WeiboType weiboType = WeiboType.SOHU;
	
	private String backUrl;
	private String oauth_verifier;
	private String consumerKey;
	private String consumerSecret;
	
	private WeiboService tencentWeiboService;
	
	public String handle() {
		return null;
	}
	/**
	 * 申请授权
	 * @return
	 */
	public String request() {
		OAuthConsumer consumer = new DefaultOAuthConsumer(consumerKey, consumerSecret);
	    OAuthProvider provider = new DefaultOAuthProvider("http://api.t.sohu.com/oauth/request_token",
	                "http://api.t.sohu.com/oauth/access_token",
	                "http://api.t.sohu.com/oauth/authorize?hd=default");
	    
	    String authUrl = null;
		
		// 获取request token
		try {
			authUrl = provider.retrieveRequestToken(consumer, backUrl);
		} catch (Exception e) {
			logger.error("sohu微博申请授权错误，Get Request Token failed");
			super.setErrorMessage("sohu微博申请授权错误，Get Request Token failed");
			return "failure";
		}
		
		if (authUrl == null || authUrl.equals("")) {
			logger.error("sohu微博申请授权错误，Get Request Token failed");
			super.setErrorMessage("sohu微博申请授权错误，Get Request Token failed");
			return "failure";
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		request.getSession().setAttribute("oauthConsumer", consumer);
		super.setForwardUrl(authUrl);
		return "forward";
	}
	/**
	 * 授权通过回调方法
	 * @return
	 */
	public String callback() {
		if (oauth_verifier == null) {
			logger.error("sohu微博授权通过回调错误，oauth_verifier为空");
			super.setErrorMessage("sohu微博授权通过回调错误，oauth_verifier为空");
			return "failure";
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		OAuthConsumer consumer = (OAuthConsumer)request.getSession().getAttribute("oauthConsumer");
		
		OAuthProvider provider = new DefaultOAuthProvider("http://api.t.sohu.com/oauth/request_token",
                "http://api.t.sohu.com/oauth/access_token",
                "http://api.t.sohu.com/oauth/authorize?hd=default");
		// 获取access token
		try {
			provider.retrieveAccessToken(consumer, oauth_verifier.trim());
		} catch (Exception e) {
			logger.error("sohu微博获取授权错误，Get Access Token failed");
			super.setErrorMessage("sohu微博获取授权错误，Get Access Token failed");
			return "failure";
		}
		
		if (consumer != null && consumer.getToken() != null && consumer.getTokenSecret() != null) {
			String user_json = null;
			try {
				URL url = new URL("http://api.t.sohu.com/account/verify_credentials.json");
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				consumer.sign(httpURLConnection);
				httpURLConnection.connect();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), CharsetConstant.CHARSET_UTF8));
				user_json = reader.readLine();
			} catch (Exception e) {
				logger.error("sohu微博获取用户信息时错误", e);
				super.setErrorMessage("sohu微博获取用户信息时错误:" + e.getMessage());
				return "failure";
			}
			if (user_json == null || user_json.equals("")) {
				logger.error("sohu微博获取用户信息时错误，未得到用户信息");
				super.setErrorMessage("sohu微博获取用户信息时错误，未得到用户信息");
				return "failure";
			}
			
			JSONObject obj = null ;
			try {
				obj = JSONObject.fromObject(user_json);
			} catch (Exception e) {
				logger.error("sohu微博获取用户信息时错误，转换成json对象错误", e);
				super.setErrorMessage("sohu微博获取用户信息时错误，转换成json对象错误");
				return "failure";
			}
			if (obj == null || obj.isNullObject()) {
				logger.error("sohu微博获取用户信息时错误，转换成json对象错误");
				super.setErrorMessage("sohu微博获取用户信息时错误，转换成json对象错误");
				return "failure";
			}
			if (obj.containsKey("id")) {
				TokenInfo tokenInfo = null;
				String id = obj.getString("id");
				if (id != null && !id.equals("")) {
					tokenInfo = tencentWeiboService.getToken(id, weiboType);
				}
				if (tokenInfo == null) {
					tokenInfo = new TokenInfo();
					tokenInfo.setUid(id);
					tokenInfo.setToken(consumer.getToken());
					tokenInfo.setTokenSecret(consumer.getTokenSecret());
					tokenInfo.setWeiboType(weiboType);
					tokenInfo.setValid(true);
				} else {
					tokenInfo.setUpdateTime(new Date());
				}
				tencentWeiboService.manageToken(tokenInfo);
				
				return "success";
			}
		}
		logger.error("sohu微博获取accessToken时错误，accessToken为空");
		super.setErrorMessage("sohu微博获取accessToken时错误，accessToken为空");
		return "failure";
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	public String getOauth_verifier() {
		return oauth_verifier;
	}
	public void setOauth_verifier(String oauthVerifier) {
		oauth_verifier = oauthVerifier;
	}
	public WeiboService getTencentWeiboService() {
		return tencentWeiboService;
	}
	public void setTencentWeiboService(WeiboService tencentWeiboService) {
		this.tencentWeiboService = tencentWeiboService;
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
