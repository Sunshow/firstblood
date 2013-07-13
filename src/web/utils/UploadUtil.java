package web.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.lehecai.admin.web.config.GlobalConfig;
import com.lehecai.admin.web.domain.cms.Resource;

public abstract class UploadUtil {
	protected final static Logger logger = LoggerFactory.getLogger(UploadUtil.class.getName());
	
	public static final String NEWS_HTTP = "http:";
	public static final String IMG_REG = "<[\\s]*?img[^>]*?>";
	
	public static String IMG_STATIC = "{%IMG_STATIC%}";
	public static String UPLOAD_URL = "/upload/";
	public static String IMG_URL = "";
	
	
	/**
	 * 由于有多个配置指向newsAction，在xml中配置会有较多地方需要更改，于是直接获取该值进行处理。
	 */
	static {
		synchronized (logger) {
			InputStream in = GlobalConfig.class.getClassLoader().getResourceAsStream("admin.properties");
			Properties properties = new Properties();
			try {
				properties.load(in);
				IMG_URL = properties.getProperty("cms.url.img");
				UPLOAD_URL = properties.getProperty("cms.upload.savedir");
				in.close();
			} catch (Exception e) {
				logger.error("读取配置文件config.properties失败", e);
			}
		}
	}
	
	/**
	 * 展示时替换resource中已经存在{variable}的数据,在生成html时需要替换路径
	 * @param content
	 * @return
	 */
	public static String replaceContentFromDB (String content) {
		String tempStr = content;
		if (tempStr != null && !IMG_URL.equals("")) {
			try {
				Pattern pattern = Pattern.compile(IMG_REG, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					String originalStr = matcher.group();
					String replaceStr = StringUtils.replace(originalStr, IMG_STATIC, IMG_URL);
					tempStr = StringUtils.replace(tempStr, originalStr, replaceStr);
				}
			} catch (Exception e) {
				return content;
			}
		}
		return tempStr;
	}
	
	/**
	 * 保存时替换(手动操作)
	 * @param content
	 * @return
	 */
	public static String replaceContentToDB (String content) {
		if (content != null && !IMG_URL.equals("")) {
			Pattern pattern = Pattern.compile(IMG_REG, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				String originalStr = matcher.group();
				String replaceStr = StringUtils.replace(StringUtils.replace(originalStr, IMG_URL, IMG_STATIC), "/"+UPLOAD_URL, UPLOAD_URL);
				content = StringUtils.replace(content, originalStr, replaceStr);
			}
		}
		return content;
	}
	
	/**
	 * 手动更新所有数据库中资源图片，在其前面加上前缀，方便其生成
	 * @param content
	 * @return
	 */
	public static String replaceResourceOldData (Map<String, Resource> resourceMap, String content) {
		if (content != null && !IMG_URL.equals("")) {
			//获取所有img标签
			Pattern pattern = Pattern.compile(IMG_REG, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				String originalStr = matcher.group();
				//转为小写
				String caseInsensitiveStr = originalStr.toLowerCase();
				//对非http引用的img进行处理
				if (caseInsensitiveStr.indexOf(NEWS_HTTP) < 0) {
					//如果该标签内还没有使用{variable}，需要判断是否能在其前面增加{variable}
					if (originalStr.indexOf(IMG_STATIC) < 0) {
						Pattern srcPattern = Pattern.compile("src[\\s]*=[\\s]*[\"\']([^\"\']+)[\"\']", Pattern.CASE_INSENSITIVE);
						Matcher srcMatcher = srcPattern.matcher(originalStr);
						if (srcMatcher.find()) {
							String src = srcMatcher.group(0);
							src = src.replace("src", "").replace("SRC", "").replace("=", "").replace("\"", "").replace("'", "").trim();
							//如果资源库中存在的图片
							if (resourceMap.get(src) != null) {
								String replaceStr = StringUtils.replace(originalStr, src, IMG_STATIC + src);
								content = StringUtils.replace(content, originalStr, replaceStr);
							//如果以/upload打头的img路径则增加{variable}
							} else if (originalStr.indexOf(UPLOAD_URL) >= 0) {
								Pattern uploadPattern = Pattern.compile("src[\\s]*=[\\s]*[\"\']" + UPLOAD_URL + "([^\"\']+)[\"\']", Pattern.CASE_INSENSITIVE);
								Matcher uploadMatcher = uploadPattern.matcher(originalStr);
								if (uploadMatcher.find()) {
									String replaceStr = StringUtils.replace(originalStr, UPLOAD_URL, IMG_STATIC + UPLOAD_URL);
									content = StringUtils.replace(content, originalStr, replaceStr);
								}
							}
						}
					}
				}
			}
		}
		return content;
	}
	

	/**
	 * 更新全部新闻时,如果<img>中存在variable,那么直接替换为domain,用于生成html
	 * @param content
	 * @return
	 */
	public static String replaceContentForMakeHtml (Map<String, Resource> resourceMap, String content) {
		String tempStr = content;
		if (tempStr != null && !IMG_URL.equals("")) {
			try {
				tempStr = StringUtils.replace(tempStr, IMG_STATIC, IMG_URL);
			} catch (Exception e) {
				logger.error("更新全部新闻时,替换variable为domain是出现错误，原因{}", e.getMessage());
				return content;
			}
		}
		return tempStr;
	}
	
	/**
	 * 更新全部新闻时,如果<img>中存在variable,那么直接替换为domain,用于生成html
	 * @param content
	 * @return
	 */
	public static String replacePathForPreview (String path) {
		String tempStr = path;
		if (tempStr != null && !IMG_URL.equals("")) {
			try {
				if (tempStr.indexOf(IMG_STATIC) > -1) {
					tempStr = StringUtils.replace(path, IMG_STATIC, IMG_URL);
				}
			} catch (Exception e) {
				logger.error("更新全部新闻时,替换variable为domain是出现错误，原因{}", e.getMessage());
				return path;
			}
		}
		return tempStr;
	}
	
	public static Map<String, Resource> resourceMapping(List<Resource> resourceList) {
		Map<String, Resource> map = new HashMap<String, Resource>();
		if (resourceList != null) {
			for (Resource resource : resourceList) {
				String path = resource.getPath();
				if (path != null) {
					//因为resource中path已经更改为变量方式，所以此处需替换
					path = path.replace(IMG_URL, "");
					map.put(path, resource);
				}
			}
		}
		return map;
	}
}
