package web.action.include.statics.link;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.action.include.statics.cms.NewsForJsonAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.link.FriendLink;
import com.lehecai.admin.web.service.link.FriendLinkService;
import com.opensymphony.xwork2.Action;

public class FriendLinkForJsonAction extends BaseAction{
	private final Logger logger = LoggerFactory.getLogger(NewsForJsonAction.class);
	private static final long serialVersionUID = 2524999332385073306L;

	private FriendLinkService friendLinkService;
	
	private Integer count = 10;//链接条数
	private Integer onIndex = 1;//是否在首页显示
	
	/**
	 * 查询所有友情链接数据封装json
	 * @return
	 */
	public String handle(){
		logger.info("开始获取友情链接json数据");
		
		Integer rc = 0;//0成功,1失败
		String message = "操作成功";
		
		PageBean pageBean = super.getPageBean();
		pageBean.setPageSize(count);//设置限时记录数
		FriendLink friendLinkTmp = null;
		if (onIndex == 1) {
			friendLinkTmp = new FriendLink();
			friendLinkTmp.setOnIndex(true);
		}
		List<FriendLink> friendLinks = friendLinkService.findFriendLinkList(friendLinkTmp, pageBean);//查询所有友情链接
		
		JSONArray jsonArray = null;
		if (friendLinks == null || friendLinks.size() == 0) {
			message = "暂且没有友情链接";
			logger.info("暂且没有友情链接");
		} else {
			jsonArray = new JSONArray();
			for (FriendLink friendLink : friendLinks) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", friendLink.getId());
				String viewName = friendLink.getName() == null ? "" : friendLink.getName();
				jsonObject.put("name", viewName);
				jsonObject.put("url", friendLink.getUrl() == null ? "" : friendLink.getUrl());
				jsonObject.put("orderView", friendLink.getOrderView());
				jsonObject.put("onIndex", friendLink.isOnIndex());
				jsonObject.put("highlight", friendLink.isHighlight());
				jsonObject.put("openNew", friendLink.isOpenNew());
				jsonObject.put("valid", friendLink.isValid());
				jsonObject.put("memo", friendLink.getMemo());
				jsonArray.add(jsonObject);
			}
		}
		
		JSONObject json = new JSONObject();
		json.put("code", rc);
		json.put("message", message);
		if (jsonArray == null) {
			json.put("data", "[]");
		} else {
			json.put("data", jsonArray);
		}
		
		HttpServletResponse response = ServletActionContext.getResponse();
		super.writeRs(response, json);
		
		logger.info("结束获取友情链接json数据");
		return Action.NONE;
	}
	
	
	public FriendLinkService getFriendLinkService() {
		return friendLinkService;
	}
	public void setFriendLinkService(FriendLinkService friendLinkService) {
		this.friendLinkService = friendLinkService;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getOnIndex() {
		return onIndex;
	}

	public void setOnIndex(Integer onIndex) {
		this.onIndex = onIndex;
	}
}
