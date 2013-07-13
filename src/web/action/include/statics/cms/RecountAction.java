package web.action.include.statics.cms;

import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.News;
import com.lehecai.admin.web.service.cms.NewsService;
import com.opensymphony.xwork2.Action;

public class RecountAction extends BaseAction {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private static final long serialVersionUID = 2524999332385073306L;

	private NewsService newsService;
	
	private String json;
	private Long id;
	
	public String handle() {
		logger.info("开始更新新闻访问次数");
		logger.info("json={}, newsService={}", json, newsService);
		if (json == null || json.equals("")) {
			logger.error("recountAction更新新闻访问次数错误：json参数为空");
			return Action.NONE;
		}
		JSONObject obj = null;
		try {
			obj = JSONObject.fromObject(json);
		} catch (Exception e) {
			logger.error("recountAction更新新闻访问次数错误：{}", json, e);
			return Action.NONE;
		}
		if (obj == null || obj.isNullObject()) {
			logger.error("recountAction更新新闻访问次数错误：jsonObject为空,json={}", json);
			return Action.NONE;
		}
		for (Iterator<?> iterator = obj.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			
			long click = 0L;
			long uvData = 0L;
			try {
				//从json对象里用jar包提供的getLong取得upload_id,由于精度问题,转换出现误差,获取不到正确结果,修改成获取字符串再转成long
				JSONObject tempObject = (JSONObject)obj.getJSONObject(key);
				if (tempObject != null && !tempObject.isEmpty()) {
					click = Long.parseLong(tempObject.getString("click"));
					uvData = Long.parseLong(tempObject.getString("visit"));
				}
			} catch (Exception e) {
				logger.error("读取点击数错误:key={}, value={}", key, obj.getJSONArray(key));
				continue;
			}
			//点击数为0时uv也应该为0
			if (click == 0L) {
				continue;
			}
			News news = newsService.get(Long.valueOf(key));
			if (news == null) {
				logger.error("recountAction更新新闻访问次数错误：未查询到news_id={}的新闻", key);
				continue;
			}
			click = click + (news.getClick() == null ? 0 : news.getClick());
			if (uvData != 0L) {
				uvData = uvData + (news.getUvData() == null ? 0 : news.getUvData());
				news.setUvData(uvData);
			}
			news.setClick(click);
			newsService.manage(news);
		}
		logger.info("结束更新新闻访问次数");
		return Action.NONE;
	}
	
	public String get(){
		logger.info("开始获取新闻访问次数");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject obj = new JSONObject();
		
		if (id == null || id.longValue() == 0) {
			logger.error("recountAction获取新闻访问次数错误：id参数为空");
			rc = 1;
			message = "recountAction获取新闻访问次数错误：id参数为空";
		} else {
			News news = newsService.get(id);
			if (news == null) {
				logger.error("recountAction获取新闻访问次数错误：未查询到news_id={}的新闻", id.toString());
				rc = 1;
				message = "recountAction获取新闻访问次数错误：未查询到news_id=" + id + "的新闻";
			} else {
				obj.put(id + "", news.getClick() == null ? 0L : news.getClick());
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", obj);
		
		super.writeRs(response, json);
		
		logger.info("结束获取新闻访问次数");
		return Action.NONE;
	}
	
	public String getvisit(){
		logger.info("开始获取新闻访问uv数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONObject obj = new JSONObject();
		
		if (id == null || id.longValue() == 0) {
			logger.error("recountAction获取新闻访问uv数据错误：id参数为空");
			rc = 1;
			message = "recountAction获取新闻访问uv数据错误：id参数为空";
		} else {
			News news = newsService.get(id);
			if (news == null) {
				logger.error("recountAction获取新闻访问uv数据错误：未查询到news_id={}的新闻", id.toString());
				rc = 1;
				message = "recountAction获取新闻访问uv数据错误：未查询到news_id=" + id + "的新闻";
			} else {
				obj.put(id + "", news.getUvData() == null ? 0L : Long.valueOf(news.getUvData()));
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		json.put("data", obj);
		
		super.writeRs(response, json);
		
		logger.info("结束获取新闻访问uv数据");
		return Action.NONE;
	}
	
	public NewsService getNewsService() {
		return newsService;
	}

	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
