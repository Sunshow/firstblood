/**
 * 
 */
package web.action.weibo.tencent;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.admin.web.service.weibo.WeiboService;
import com.lehecai.core.lottery.WeiboType;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;

/**
 * @author qatang
 *
 */
public class AuthorizeAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private static final long serialVersionUID = 1L;
	
	private final WeiboType weiboType = WeiboType.TENCENT;
	
	private String backUrl;
	private String consumerKey;
	private String consumerSecret;
	
	private String code; // 用来换取accesstoken的授权码，有效期为10分钟
	private String openid;// 用户统一标识，可以唯一标识一个用户
	private String openkey;// 与openid对应的用户key，是验证openid身份的验证密钥
	
	private WeiboService tencentWeiboService;
	
	public String handle() {
		return null;
	}
	/**
	 * 申请授权
	 * @return
	 */
	public String request() {
		OAuthV2 oAuth = new OAuthV2();
		oAuth.setClientId(consumerKey);
		oAuth.setClientSecret(consumerSecret);
		oAuth.setRedirectUri(backUrl);
		
		// 获取request token
		String authorizationUrl = OAuthV2Client.generateAuthorizationURL(oAuth);
		
		if (!StringUtils.isEmpty(authorizationUrl)) {
			HttpServletRequest request = ServletActionContext.getRequest();
			request.getSession().setAttribute("oauth", oAuth);
			super.setForwardUrl(authorizationUrl);
			return "forward";
		}
		logger.error("tencent微博申请授权失败，authorizationUrl为空");
		super.setErrorMessage("tencent微博申请授权失败，authorizationUrl为空");
		return "failure";
	}
	
	/**
	 * 授权通过回调方法
	 * @return
	 */
	public String callback() {
		if (code == null || openid == null || openkey == null) {
			logger.error("tencent微博授权通过回调错误，参数为空");
			super.setErrorMessage("tencent微博授权通过回调错误，参数为空");
			return "failure";
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		OAuthV2 oAuth = (OAuthV2)request.getSession().getAttribute("oauth");
		oAuth.setAuthorizeCode(code);
		oAuth.setOpenid(openid);
		oAuth.setOpenkey(openkey);
		oAuth.setGrantType("authorize_code");
       try {
           OAuthV2Client.accessToken(oAuth);
        } catch (Exception e) {
           logger.error(e.getMessage(), e);
        }
       
       //检查是否正确取得access token
       if (oAuth.getStatus() == 3) {
    	   logger.error("tencent微博获取授权错误，Get Access Token failed");
		   super.setErrorMessage("tencent微博获取授权错误，Get Access Token failed");
		   return "failure";
        }
		
       if (oAuth != null) {
			TokenInfo tokenInfo = null;
			if (!StringUtils.isEmpty(oAuth.getOpenid())) {
				tokenInfo = tencentWeiboService.getToken(oAuth.getOpenid(), weiboType);
			}
			if (tokenInfo == null) {
				tokenInfo = new TokenInfo();
				tokenInfo.setUid(oAuth.getOpenid());
				tokenInfo.setToken(oAuth.getAccessToken());
				tokenInfo.setTokenSecret("");
				tokenInfo.setExpiresIn(StringUtils.isEmpty(oAuth.getExpiresIn()) ? 0L : Long.parseLong(oAuth.getExpiresIn()));
				tokenInfo.setWeiboType(weiboType);
				tokenInfo.setValid(true);
				tokenInfo.setCreateTime(new Date());
				tokenInfo.setUpdateTime(new Date());
			} else {
				tokenInfo.setToken(oAuth.getAccessToken());
				tokenInfo.setExpiresIn(StringUtils.isEmpty(oAuth.getExpiresIn()) ? 0L : Long.parseLong(oAuth.getExpiresIn()));
				tokenInfo.setUpdateTime(new Date());
			}
			tencentWeiboService.manageToken(tokenInfo);
			return "success";
		}
		logger.error("tencent微博获取accessToken时错误，accessToken为空");
		super.setErrorMessage("tencent微博获取accessToken时错误，accessToken为空");
		return "failure";
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getOpenkey() {
		return openkey;
	}
	public void setOpenkey(String openkey) {
		this.openkey = openkey;
	}
}
