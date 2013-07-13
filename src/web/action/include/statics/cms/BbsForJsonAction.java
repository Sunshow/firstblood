package web.action.include.statics.cms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.core.util.CoreFetcherUtils;
import com.opensymphony.xwork2.Action;

public class BbsForJsonAction  extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(NewsForJsonAction.class);
	private String bid;
	public String handle() {
		logger.info("进入获取新闻json数据");
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		String url = null;
		if(bid!=null&&!"".equals(bid)){
			url = "http://bbs.lehecai.com/api.php?mod=js&bid=" + bid;
		}else{
			return null;
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		JSONArray jsonArray = new JSONArray();
	
		String data = null;
		String encoding = "GBK";
		String logHeader = "==抓取==开始==";
		
		Map<String, String> headerParams = new HashMap<String, String>();
		headerParams.put("Referer", "http://www.lehecai.com/");
		headerParams.put("user-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8");
		
		data = CoreFetcherUtils.URLGet(url, null, encoding);
		
		if (data == null || data.indexOf("404 Not Found") > 0 || data.isEmpty()) {
			logger.error(logHeader + "data is null or 404 Not Found");
			return null;
		}	
		Parser parser= Parser.createParser(data,  encoding);
		String filterName = "a";
		TagNameFilter tableFilter = new TagNameFilter(filterName);
		NodeList nodeList = null;
		try {
			nodeList = parser.extractAllNodesThatMatch(tableFilter);
			if(nodeList == null){
				rc = 1;
				message = "操作失败";
			}else{
				for(int i=0;i<nodeList.size();i++){
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("title", nodeList.elementAt(i).toPlainTextString().trim());
					LinkTag linkTag = (LinkTag) nodeList.elementAt(i);
					jsonObject.put("link", linkTag.extractLink().trim().replace("amp;", "").replace("http://bbs.lehecai.com/", ""));
					jsonArray.add(jsonObject);
				}
			}
			JSONObject json = new JSONObject();
			json.put("code", rc);
			json.put("message", message);
			if(jsonArray==null){
				json.put("data", "");
			}else{
				json.put("data", jsonArray);
			}
			super.writeRs(response, json);
			
			logger.info("结束获取新闻json数据");
						
			return Action.NONE;
		} catch (ParserException e2){
				logger.error("数据解析错误=="+e2.getMessage(), e2);
				return null;
			}
	}
	
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}

}
