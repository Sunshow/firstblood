package web.service.cdn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.domain.cdn.CdnCacheItem;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteItem;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreStringUtils;


public class ChinaNetCenterCdnService {
	
	private String cdnUrl;
	
	private String user;
	
	private String pswd;
	
	private static ChinaNetCenterCdnService instance = new ChinaNetCenterCdnService();

	
	private ChinaNetCenterCdnService() {
		
	}
	
	public static ChinaNetCenterCdnService getInstance() {
		return instance;
	}

	private static final Logger logger = LoggerFactory.getLogger(ChinaNetCenterCdnService.class);


	final static public String CDN_DIRS_NUM = "dirsnum";

	final static public String CDN_URLS_NUM = "urlsnum";
	
	final static public String CDN_USER = "username";
	
	final static public String CDN_PSWD = "passwd";
	
	final static public String CDN_URLS = "url";
	
	final static public String CDN_LIST_SEPARATOR = ";";
	
	final static public String CDN_DIRS = "dir";
	
	final static public String CDN_SUCCESS = "success append purge tasks...";
	
	final static public String SUCCESS = "0";//失败
	
	final static public String CONNECTION = "1";//链接上
	
	final static public String EXCEPTION = "2";//有异常
	
	final static public String FAILED = "9999";
	
	final static public String RESP_CODE = "RespCode";
	
	final static public String RESP_SUCCESS_URLS_NUM = "successUrlsNum";
	
	final static public String RESP_AMOUNT_URLS_NUM = "amountUrlsNum";
	
	final static public String RESP_SUCCESS_DIRS_NUM = "successDirsNum";
	
	final static public String RESP_AMOUNT_DIRS_NUM = "amountDirsNum";
	
	final static public String RESP_URLEXCEED = "urlExceed";
	
	final static public String RESP_DIREXCEED = "dirExceed";
	
	final static public String RESP_SITE = "respSite";
	
	final static public String RESP_FAILED_MSG = "RespMsg";
	
	final static public String RESP_MSG = "RespMsg";
	
	public static HashMap<String, Map<String, String>> request(List<CdnWebsiteItem> siteList, List<CdnCacheItem> itemList) {
		HashMap<String, Map<String, String>> retMap = new HashMap<String, Map<String, String>>();
		if (siteList == null || siteList.isEmpty()) {
			//站点为空，无法获得请求地址
			Map<String, String> map = new HashMap<String, String>();
			map.put(ChinaNetCenterCdnService.RESP_CODE, ChinaNetCenterCdnService.FAILED);
			map.put(ChinaNetCenterCdnService.RESP_MSG, "无法获得站点地址！");
			retMap.put("null", map);
			return retMap;
		}
		
		for (CdnWebsiteItem cdnCacheWebsite : siteList) {
			URL url = null;
			HttpURLConnection connection = null;
			BufferedReader reader = null;
			try {
				Map<String, String> map = getStringByList(cdnCacheWebsite.getSiteUrl(), itemList);
				
				String urlStr = getUrlString(ChinaNetCenterCdnService.getInstance().getCdnUrl(), map);
				logger.info("向{}请求URL:{}", cdnCacheWebsite.getName(), urlStr);
				url = new URL(urlStr);
				connection = (HttpURLConnection)url.openConnection();
				connection.setConnectTimeout(30000);
				connection.setReadTimeout(135000);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				
				connection.connect();
				
				if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
					logger.error("从地址({})获取响应失败：{}", url, connection.getResponseMessage());
					Map<String, String> maps = new HashMap<String, String>();
					maps.put(ChinaNetCenterCdnService.RESP_CODE, ChinaNetCenterCdnService.EXCEPTION);
					maps.put(ChinaNetCenterCdnService.RESP_SITE, cdnCacheWebsite.getName());
					maps.put(ChinaNetCenterCdnService.RESP_MSG, "读取地址:" + url + " 错误:" + connection.getResponseCode());
					retMap.put(cdnCacheWebsite.getId()+"", maps);
					logger.error("{读取地址:{} 错误:{}", url, connection.getResponseCode());
					continue;
				}
				
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
				
				String message = reader.readLine();
				
				if (message != null && ChinaNetCenterCdnService.CDN_SUCCESS.equals(message)) {
					logger.info("{}提交成功!, message={}", url, message);
					String sUrls = map.get(ChinaNetCenterCdnService.CDN_URLS);
					String sDirs = map.get(ChinaNetCenterCdnService.CDN_DIRS);
					String urlExceed = map.get(ChinaNetCenterCdnService.CDN_URLS_NUM);
					String dirExceed = map.get(ChinaNetCenterCdnService.CDN_DIRS_NUM);
					retMap.put(cdnCacheWebsite.getId()+"", putSiteMsg(cdnCacheWebsite.getName(), map.get(ChinaNetCenterCdnService.CDN_URLS_NUM), sUrls, map.get(ChinaNetCenterCdnService.CDN_DIRS_NUM), sDirs, urlExceed, dirExceed, "请求成功！"));
				} else {
					logger.error("{}提交失败!, message={}", url, message);
					Map<String, String> maps = new HashMap<String, String>();
					maps.put(ChinaNetCenterCdnService.RESP_CODE, ChinaNetCenterCdnService.EXCEPTION);
					maps.put(ChinaNetCenterCdnService.RESP_SITE, cdnCacheWebsite.getName());
					maps.put(ChinaNetCenterCdnService.RESP_MSG, url+"提交失败!");
					retMap.put(cdnCacheWebsite.getId()+"", maps);
				}
				
			} catch (Exception e) {
				Map<String, String> maps = new HashMap<String, String>();
				maps.put(ChinaNetCenterCdnService.RESP_CODE, ChinaNetCenterCdnService.EXCEPTION);
				maps.put(ChinaNetCenterCdnService.RESP_SITE, cdnCacheWebsite.getName());
				maps.put(ChinaNetCenterCdnService.RESP_MSG, "请求地址发生异常:" + url + "," + e.getMessage());
				retMap.put(cdnCacheWebsite.getId()+"", maps);
				logger.error("请求地址({})发生异常:",url,e);
			}
		}
		
		return retMap;
	}

	private static Map<String, String> putSiteMsg(String siteName, String aUrls, String sUrls, String aDirs, String sDirs, String urlExceed, String dirExceed, String msg) throws IOException {
		Map<String, String> siteMap = new HashMap<String, String>();
		logger.info("请求CDN相应结果=============》siteName:{"+siteName+"}, aUrls:{"+aUrls+"}, aDirs:{"+aDirs+"}, sUrls:{"+sUrls+"}, sDirs:{"+sDirs+"}, urlExceed:{"+urlExceed+"}, dirExceed:{"+dirExceed+"}");
		siteMap.put(ChinaNetCenterCdnService.RESP_CODE, ChinaNetCenterCdnService.CONNECTION);
		siteMap.put(ChinaNetCenterCdnService.RESP_SITE, siteName);
		siteMap.put(ChinaNetCenterCdnService.RESP_AMOUNT_URLS_NUM, aUrls);
		siteMap.put(ChinaNetCenterCdnService.RESP_AMOUNT_DIRS_NUM, aDirs);
		siteMap.put(ChinaNetCenterCdnService.RESP_SUCCESS_URLS_NUM, sUrls);
		siteMap.put(ChinaNetCenterCdnService.RESP_SUCCESS_DIRS_NUM, sDirs);
		siteMap.put(RESP_URLEXCEED, urlExceed);
		siteMap.put(ChinaNetCenterCdnService.RESP_DIREXCEED, dirExceed);
		siteMap.put(ChinaNetCenterCdnService.RESP_MSG, msg);
		return siteMap;
	}
	
	public static String getUrlString(String url, Map<String, String> map){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(url).append("?")
		.append(ChinaNetCenterCdnService.CDN_USER).append("=").append(ChinaNetCenterCdnService.getInstance().getUser()).append("&")
		.append(ChinaNetCenterCdnService.CDN_PSWD).append("=").append(map.get(ChinaNetCenterCdnService.CDN_PSWD));
		if (map.get(ChinaNetCenterCdnService.CDN_URLS) != null && !"".equals(map.get(ChinaNetCenterCdnService.CDN_URLS))) {
			stringBuffer.append("&").append(map.get(ChinaNetCenterCdnService.CDN_URLS));
		}
		if (map.get(ChinaNetCenterCdnService.CDN_DIRS) != null && !"".equals(map.get(ChinaNetCenterCdnService.CDN_DIRS))) {
			stringBuffer.append("&").append(map.get(ChinaNetCenterCdnService.CDN_DIRS));
		}
		logger.info("CDN请求URL：{}", stringBuffer.toString());
		return stringBuffer.toString();
	}
	
	protected static String preprocessUrl(String url) {
		if (url == null) {
			return "";
		}
		url = url.trim();
		if (url.startsWith("http://")) {
			url = url.substring("http://".length());
		}
		return url;
	}

	/**
	 * cdn缓存刷新链接拼接
	 * @return
	 */
	private static Map<String, String> getStringByList(String siteUrl, List<CdnCacheItem> itemList){
		StringBuffer urlsBuffer = new StringBuffer();
		StringBuffer dirsBuffer = new StringBuffer();
		StringBuffer passwordBuffer = new StringBuffer();
		int urlsnum = 0;
		int dirsnum = 0;
		if (itemList == null || itemList.isEmpty() || siteUrl == null || siteUrl.isEmpty()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(ChinaNetCenterCdnService.CDN_URLS_NUM, "0");
			map.put(ChinaNetCenterCdnService.CDN_DIRS_NUM, "0");
			map.put(ChinaNetCenterCdnService.CDN_URLS, "");
			map.put(ChinaNetCenterCdnService.CDN_DIRS, "");
			return map;
		} else {
			siteUrl = preprocessUrl(siteUrl);
			for (int i=0; i<itemList.size(); i++) {
				CdnCacheItem tempItem = itemList.get(i);
				if (tempItem.getUrls() != null && !tempItem.getUrls().isEmpty()) {
					urlsnum++;
					urlsBuffer.append(siteUrl).append(tempItem.getUrls());
				}
				if (tempItem.getDirs() != null && !tempItem.getDirs().isEmpty()) {
					dirsnum++;
					dirsBuffer.append(siteUrl).append(tempItem.getDirs());
				}
				if (i != itemList.size()-1) {
					urlsBuffer.append(ChinaNetCenterCdnService.CDN_LIST_SEPARATOR);
					dirsBuffer.append(ChinaNetCenterCdnService.CDN_LIST_SEPARATOR);
				}
			}
		}
		
		String urlsStr = urlsBuffer.toString();
		String dirsStr = dirsBuffer.toString();
		
		passwordBuffer.append(ChinaNetCenterCdnService.getInstance().getUser())
			.append(ChinaNetCenterCdnService.getInstance().getPswd())
			.append(urlsStr).append(dirsStr);
		
		Map<String, String> map = new HashMap<String, String>();
		if (urlsnum > 0) {
			map.put(ChinaNetCenterCdnService.CDN_URLS, ChinaNetCenterCdnService.CDN_URLS + "=" + urlsStr);
		} 
		if (dirsnum > 0) {
			map.put(ChinaNetCenterCdnService.CDN_DIRS, ChinaNetCenterCdnService.CDN_DIRS + "=" + dirsStr);
		}
		map.put(ChinaNetCenterCdnService.CDN_URLS_NUM, String.valueOf(urlsnum));
		map.put(ChinaNetCenterCdnService.CDN_DIRS_NUM, String.valueOf(dirsnum));
		map.put(ChinaNetCenterCdnService.CDN_PSWD, CoreStringUtils.md5(passwordBuffer.toString(), CharsetConstant.CHARSET_UTF8));
		
		return map;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPswd() {
		return pswd;
	}

	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public static void setInstance(ChinaNetCenterCdnService instance) {
		ChinaNetCenterCdnService.instance = instance;
	}

	public String getCdnUrl() {
		return cdnUrl;
	}

	public void setCdnUrl(String cdnUrl) {
		this.cdnUrl = cdnUrl;
	}

}
