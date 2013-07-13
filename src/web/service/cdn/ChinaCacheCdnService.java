package web.service.cdn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.domain.cdn.CdnCacheItem;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteItem;


public class ChinaCacheCdnService {
	
	private String cdnUrl;
	
	private String user;
	
	private String pswd;
	
	private static ChinaCacheCdnService instance = new ChinaCacheCdnService();

	
	private ChinaCacheCdnService() {
		
	}
	
	public static ChinaCacheCdnService getInstance() {
		return instance;
	}

	private static final Logger logger = LoggerFactory.getLogger(ChinaCacheCdnService.class);

	final static public String CDN_WRITE_STRING = "writeString";

	final static public String CDN_DIRS_NUM = "dirsnum";

	final static public String CDN_URLS_NUM = "urlsnum";
	
	final static public String CDN_USER = "user";
	
	final static public String CDN_PSWD = "pswd";
	
	final static public String CDN_URLS = "urls";
	
	final static public String CDN_OK = "ok=ok";
	
	final static public String CDN_LIST_SEPARATOR = "%0D%0A";
	
	final static public String CDN_DIRS = "dirs";
	
	final static public String CDN_FAILED = "failed";
	
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
			//TODO 站点为空，无法获得请求地址
			Map<String, String> map = new HashMap<String, String>();
			map.put(ChinaCacheCdnService.RESP_CODE, ChinaCacheCdnService.FAILED);
			map.put(ChinaCacheCdnService.RESP_MSG, "无法获得站点地址！");
			retMap.put("null", map);
			return retMap;
		}
		
		for (CdnWebsiteItem cdnCacheWebsite : siteList) {
			URL url = null;
			HttpURLConnection connection = null;
			BufferedReader reader = null;
			try {
				Map<String, String> map = getStringByList(cdnCacheWebsite.getSiteUrl(), itemList);
				
				String headerUrl = getHeaderString(ChinaCacheCdnService.getInstance().getCdnUrl());
				String writeString = headerUrl + "&" + map.get(ChinaCacheCdnService.CDN_WRITE_STRING);
				logger.info("向{}请求URL:{}", cdnCacheWebsite.getName(), writeString);
				url = new URL(writeString);
				connection = (HttpURLConnection)url.openConnection();
				connection.setConnectTimeout(30000);
				connection.setReadTimeout(135000);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				
				connection.connect();
				
				if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
					logger.error("从地址({})获取响应失败：{}", url, connection.getResponseMessage());
					Map<String, String> maps = new HashMap<String, String>();
					maps.put(ChinaCacheCdnService.RESP_CODE, ChinaCacheCdnService.EXCEPTION);
					maps.put(ChinaCacheCdnService.RESP_SITE, cdnCacheWebsite.getName());
					maps.put(ChinaCacheCdnService.RESP_MSG, "读取地址:" + url + " 错误:" + connection.getResponseCode());
					retMap.put(cdnCacheWebsite.getId()+"", maps);
					logger.error("{读取地址:{} 错误:{}", url, connection.getResponseCode());
					continue;
				}
				
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
				
				StringBuffer sb = new StringBuffer();
				
				String temp = null;
				while ((temp = reader.readLine()) != null) {
					sb.append(temp);
				}
				
				SAXBuilder builder = new SAXBuilder();
				Document doc = null;
				Reader in = new StringReader(sb.toString());
				doc = builder.build(in);
				Element root = doc.getRootElement();
				String message = root.getTextTrim();
				if (message != null && !ChinaCacheCdnService.CDN_FAILED.equals(message)) {
					logger.info("{}提交成功!", url);
					String sUrls = root.getChild("url").getText();
					String sDirs = root.getChild("dir").getText();
					String urlExceed = root.getChild("urlExceed").getText();
					String dirExceed = root.getChild("dirExceed").getText();
					retMap.put(cdnCacheWebsite.getId()+"", putSiteMsg(cdnCacheWebsite.getName(), map.get(ChinaCacheCdnService.CDN_URLS_NUM), sUrls, map.get(ChinaCacheCdnService.CDN_DIRS_NUM), sDirs, urlExceed, dirExceed, "请求成功！"));
				} else {
					logger.error("{}提交失败!", url);
					Map<String, String> maps = new HashMap<String, String>();
					maps.put(ChinaCacheCdnService.RESP_CODE, ChinaCacheCdnService.EXCEPTION);
					maps.put(ChinaCacheCdnService.RESP_SITE, cdnCacheWebsite.getName());
					maps.put(ChinaCacheCdnService.RESP_MSG, url+"提交失败!");
					retMap.put(cdnCacheWebsite.getId()+"", maps);
				}
				
			} catch (Exception e) {
				Map<String, String> maps = new HashMap<String, String>();
				maps.put(ChinaCacheCdnService.RESP_CODE, ChinaCacheCdnService.EXCEPTION);
				maps.put(ChinaCacheCdnService.RESP_SITE, cdnCacheWebsite.getName());
				maps.put(ChinaCacheCdnService.RESP_MSG, "请求地址发生异常:" + url + "," + e.getMessage());
				retMap.put(cdnCacheWebsite.getId()+"", maps);
				logger.error("请求地址({})发生异常:",url,e);
			}
		}
		
		return retMap;
	}

	private static Map<String, String> putSiteMsg(String siteName, String aUrls, String sUrls, String aDirs, String sDirs, String urlExceed, String dirExceed, String msg) throws IOException {
		Map<String, String> siteMap = new HashMap<String, String>();
		logger.info("请求CDN相应结果=============》siteName:{"+siteName+"}, aUrls:{"+aUrls+"}, aDirs:{"+aDirs+"}, sUrls:{"+sUrls+"}, sDirs:{"+sDirs+"}, urlExceed:{"+urlExceed+"}, dirExceed:{"+dirExceed+"}");
		siteMap.put(ChinaCacheCdnService.RESP_CODE, ChinaCacheCdnService.CONNECTION);
		siteMap.put(ChinaCacheCdnService.RESP_SITE, siteName);
		siteMap.put(ChinaCacheCdnService.RESP_AMOUNT_URLS_NUM, aUrls);
		siteMap.put(ChinaCacheCdnService.RESP_AMOUNT_DIRS_NUM, aDirs);
		siteMap.put(ChinaCacheCdnService.RESP_SUCCESS_URLS_NUM, sUrls);
		siteMap.put(ChinaCacheCdnService.RESP_SUCCESS_DIRS_NUM, sDirs);
		siteMap.put(RESP_URLEXCEED, urlExceed);
		siteMap.put(ChinaCacheCdnService.RESP_DIREXCEED, dirExceed);
		siteMap.put(ChinaCacheCdnService.RESP_MSG, msg);
		return siteMap;
	}
	
	public static String getHeaderString(String url){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(url).append("?")
		.append(ChinaCacheCdnService.CDN_USER).append("=").append(ChinaCacheCdnService.getInstance().getUser()).append("&")
		.append(ChinaCacheCdnService.CDN_PSWD).append("=").append(ChinaCacheCdnService.getInstance().getPswd()).append("&")
		.append(ChinaCacheCdnService.CDN_OK);
		logger.info("CDN请求headerURL：{}", stringBuffer.toString());
		return stringBuffer.toString();
	}

	/**
	 * cdn缓存刷新链接拼接
	 * @return
	 */
	private static Map<String, String> getStringByList(String siteUrl, List<CdnCacheItem> itemList){
		StringBuffer urlsBuffer = new StringBuffer();
		StringBuffer dirsBuffer = new StringBuffer();
		int urlsnum = 0;
		int dirsnum = 0;
		if (itemList == null || itemList.isEmpty() || siteUrl == null || siteUrl.isEmpty()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(ChinaCacheCdnService.CDN_URLS_NUM, "0");
			map.put(ChinaCacheCdnService.CDN_DIRS_NUM, "0");
			map.put(ChinaCacheCdnService.CDN_WRITE_STRING, "");
			return map;
		} else {
			urlsBuffer.append(ChinaCacheCdnService.CDN_URLS).append("=");
			dirsBuffer.append(ChinaCacheCdnService.CDN_DIRS).append("=");
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
					urlsBuffer.append(ChinaCacheCdnService.CDN_LIST_SEPARATOR);
					dirsBuffer.append(ChinaCacheCdnService.CDN_LIST_SEPARATOR);
				}
			}
		}
		String writeString = "";
		if (urlsnum > 0 && dirsnum > 0) {
			writeString = urlsBuffer.toString()+"&"+dirsBuffer.toString();
		} else if (urlsnum > 0) {
			writeString = urlsBuffer.toString();
		} else if (dirsnum > 0) {
			writeString = dirsBuffer.toString();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(ChinaCacheCdnService.CDN_URLS_NUM, String.valueOf(urlsnum));
		map.put(ChinaCacheCdnService.CDN_DIRS_NUM, String.valueOf(dirsnum));
		map.put(ChinaCacheCdnService.CDN_WRITE_STRING, writeString);
		
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

	public static void setInstance(ChinaCacheCdnService instance) {
		ChinaCacheCdnService.instance = instance;
	}

	public String getCdnUrl() {
		return cdnUrl;
	}

	public void setCdnUrl(String cdnUrl) {
		this.cdnUrl = cdnUrl;
	}

}
