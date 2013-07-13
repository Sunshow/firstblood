/**
 * 
 */
package web.service.impl.weibo;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.http.HttpParameters;

import com.lehecai.admin.web.domain.weibo.TokenInfo;
import com.lehecai.core.api.bean.ApiResultBean;
import com.lehecai.core.lottery.WeiboType;
import com.lehecai.core.util.CharsetConstant;

/**
 * @author qatang
 *
 */
public class SohuWeiboServiceImpl extends CommonWeiboServiceImpl {
	protected WeiboType weiboType = WeiboType.SOHU;
	private String consumerKey;
	private String consumerSecret;
	
	@Override
	public List<ApiResultBean> publish(String content, List<TokenInfo> tokenInfoList) {
		
		List<ApiResultBean> list = new ArrayList<ApiResultBean>();
		if (tokenInfoList != null && tokenInfoList.size() > 0) {
			for (TokenInfo tokenInfo : tokenInfoList) {
				ApiResultBean bean = new ApiResultBean();
		        try {
		        	OAuthConsumer consumer = new DefaultOAuthConsumer(
							consumerKey, consumerSecret);
			        consumer.setTokenWithSecret(tokenInfo.getToken(), tokenInfo.getTokenSecret());
			        
		        	URL url = new URL("http://api.t.sohu.com/statuses/update.json");
		        	HttpURLConnection request = (HttpURLConnection) url.openConnection();
		        	
		        	request.setDoOutput(true);
		        	request.setRequestMethod("POST");
		        	HttpParameters para = new HttpParameters();
		        	//由于sohu api傻逼，需要先将微博内容中的+替换成urlencode后的编码,要不然+在微博中显示不出来，比如东方6+1
		        	String status = URLEncoder.encode(content.replaceAll("\\+", "%2B"), CharsetConstant.CHARSET_UTF8).replaceAll("\\+", "%20");
		        	para.put("status", status);
		        	
		        	consumer.setAdditionalParameters(para);
					consumer.sign(request);
					
					OutputStream ot = request.getOutputStream();
					ot.write(("status=" + URLEncoder.encode(content.replaceAll("\\+", "%2B"), CharsetConstant.CHARSET_UTF8)).getBytes());
					ot.flush();
					ot.close();
					
					request.connect();
					if (request.getResponseCode() == 200) {
						logger.info("[{}]的uid={}发表微博成功:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), content});
						bean.setResult(true);
						bean.setMessage("[" + weiboType.getName() + "]的uid= " + tokenInfo.getUid() + " 发表微博成功: " + content);
					} else {
						logger.error("[{}]的uid={}发表微博失败:错误代码为{},信息：{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), request.getResponseCode(), request.getResponseMessage()});
						bean.setResult(false);
						bean.setMessage("[" + weiboType.getName() + "]的uid= " + tokenInfo.getUid() + " 发表微博失败:错误代码为" + request.getResponseCode() + ",信息：" + request.getResponseMessage());
					}
				} catch (Exception e) {
					logger.error("[{}]的uid={}发表微博失败:{}", new Object[]{weiboType.getName(), tokenInfo.getUid(), content});
					logger.error(e.getMessage(), e);
					bean.setResult(false);
					bean.setMessage("[" + weiboType.getName() + "]的uid= " + tokenInfo.getUid() + " 发表微博失败:" + content);
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
