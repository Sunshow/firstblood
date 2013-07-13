package web.utils;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.service.base.SiteConfigService;
import com.lehecai.core.api.base.SiteConfig;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

public class VelocityUtil {
	private final static Logger logger = LoggerFactory.getLogger(VelocityUtil.class);
	
	protected static Object __velocity_lock__ = new Object();
	protected static Object __write_lock__ = new Object();
	
	public SiteConfigService siteConfigService;
	
	private static Object __DateTool__ = new VelocityDateUtil();
	private static Object __NumberTool__ = new NumberTool();
	private static Object __ConvertTool__ = new ConvertUtil();
	private static Object __FormatTool__ = new VelocityFormatUtil();
	
	private static SiteConfig __SiteConfig__;
	
	protected void bindDefaultContext(VelocityContext context) {
		context.put("DateTool", __DateTool__);//定义模板中$date，需引入velocity-tools-1.4.jar
		context.put("NumberTool", __NumberTool__);
		context.put("ConvertTool", __ConvertTool__);
		context.put("FormatTool", __FormatTool__);
		
		synchronized (this) {
			if (__SiteConfig__ == null) {
				try {
					__SiteConfig__ = siteConfigService.getSiteConfig();
				} catch (ApiRemoteCallFailedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (__SiteConfig__ != null) {
				context.put("ROOT_JS", __SiteConfig__.getRootJS());
				context.put("ROOT_CSS", __SiteConfig__.getRootCSS());
				context.put("ROOT_IMG", __SiteConfig__.getRootIMG());
				context.put("ROOT_STATIC", __SiteConfig__.getRootSTATIC());
				context.put("ROOT_URL", __SiteConfig__.getRootURL());
				context.put("ROOT_WAPCSS", __SiteConfig__.getRootWAPCSS());
				context.put("ROOT_WAPIMG", __SiteConfig__.getRootWAPIMG());
				context.put("ROOT_WAPJS", __SiteConfig__.getRootWAPJS());
			}
		}
	}
	
	public void build(String path, String templateStr, Map<String, Object> map) throws Exception {
		if (path == null || "".equals(path)) {
			throw new Exception("VelocityUtil.build的path参数为空(生成文件路径为空)");
		}
		if (templateStr == null || "".equals(path)) {
			throw new Exception("VelocityUtil.build的templateStr参数为空(模板为空)");
		}
		if (map == null) {
			throw new Exception("VelocityUtil.build的map参数为空(渲染对象map为空)");
		}
		logger.info("Enter VelocityUtil");
		logger.info("Velocity filePath:{}",path);
		logger.info("Velocity template:{}",templateStr);
		
		Template template = null;
		synchronized (__velocity_lock__) {
			Velocity.setProperty("input.encoding", "utf-8");
			Velocity.setProperty("output.encoding", "utf-8");
			Velocity.setProperty("resource.loader", "string");
			Velocity.setProperty("string.resource.loader.class", "com.lehecai.admin.web.utils.StringResourceLoader");
			logger.info("Init Velocity");
			try {
				Velocity.init();
			} catch (Exception e) {
				logger.error("Velocity对象初始化失败");
				throw new Exception("Velocity对象初始化失败", e);
			}
			
			logger.info("Get Template");
			try {
				template = Velocity.getTemplate(templateStr);
			} catch (Exception e) {
				logger.error("velocity模板初始化失败,templateStr:{}", templateStr);
				throw new Exception("velocity模板初始化失败", e);
			}
		}
	    logger.info("Init complete");

	    if (template == null){
	    	logger.error("velocity模板初始化后为空");
	    	throw new Exception("velocity模板初始化后为空");
	    }
	    logger.info("Get Template success");
	    
		StringWriter writer = new StringWriter();
	    
		VelocityContext context = new VelocityContext(map);
		
		bindDefaultContext(context);
		
 		logger.info("merge");
 		try {
			template.merge(context, writer);
		} catch (Exception e) {
			logger.error("velocity模板渲染失败");
			throw new Exception("velocity模板渲染失败", e);
		}
 		logger.info("merge success");
	 	logger.info("write to file");
		boolean flag = false;
		
		synchronized (__write_lock__) {
			flag = FileUtil.write(path, writer.toString());
		}
		
		if (!flag) {
			logger.error("写文件失败,path:{}", path);
			throw new Exception("写文件失败");
		}
		logger.info("write to file success");
		writer.flush();
		writer.close();
		logger.info("Exit VelocityUtil");
	}

	public SiteConfigService getSiteConfigService() {
		return siteConfigService;
	}

	public void setSiteConfigService(SiteConfigService siteConfigService) {
		this.siteConfigService = siteConfigService;
	}
}
