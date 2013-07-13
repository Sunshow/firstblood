package web.action.link;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.link.FriendLink;
import com.lehecai.admin.web.service.link.FriendLinkService;

/**
 * 友情链接action
 * @author yanweijie
 *
 */
public class FriendLinkAction extends BaseAction {
	private static final long serialVersionUID = -8990266966449545363L;
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	private FriendLinkService friendLinkService;
	
	private List<FriendLink> friendLinkList;		//友情链接列表
	private FriendLink friendLink;					//友情链接对象

	/**
	 * 查询所有友情链接
	 * @return
	 */
	public String handle() {
		logger.info("进入查询友情链接列表");
		
		PageBean pageBean = super.getPageBean();
		pageBean.setPageFlag(false);
		friendLinkList = friendLinkService.findFriendLinkList(null, pageBean);//查询所有友情链接
		if (friendLinkList == null || friendLinkList.size() == 0) {
			logger.info("暂且没有友情链接数据");
		}
		
		logger.info("查询友情链接列表结束");
		return "list";
	}
	
	/**
	 * 查看友情链接详细信息
	 */
	public String view() {
		logger.info("进入查询友情链接详细信息");
		
		if (friendLink != null && (friendLink.getId() != null && friendLink.getId() != 0L)) {
			friendLink = friendLinkService.get(friendLink.getId());	//根据友情链接编号查询友情链接
		} else {
			logger.error("查询友情链接详细信息，编码为空");
			super.setErrorMessage("查询友情链接详细信息，编码为空");
			return "failure";
		}
		
		logger.info("查询友情链接详细信息结束");
		return "view";
	}
	
	/**
	 * 转向添加/修改友情链接
	 * @return
	 */
	public String input() {
		logger.info("进入输入友情链接信息");
		if (friendLink != null && (friendLink.getId() != null 
				&& friendLink.getId() != 0L)) {
			friendLink = friendLinkService.get(friendLink.getId());//根据友情链接编号查询友情链接信息
		} else {
			friendLink = new FriendLink();
			friendLink.setOnIndex(true);
			friendLink.setHighlight(true);
			friendLink.setOpenNew(true);
			friendLink.setValid(true);
		}
		return "inputForm";
	}
	
	/**
	 * 添加/修改友情链接
	 * @return
	 */
	public String manage() {
		logger.info("进入更新友情链接");
		if (friendLink != null) {
			if (friendLink.getName() == null || "".equals(friendLink.getName())) {
				logger.error("更新友情链接，友情链接名称为空");
				super.setErrorMessage("更新友情链接，友情链接名称不能为空");
				return "failure";
			}
			friendLinkService.merge(friendLink);
		} else {
			logger.error("更新友情链接，提交表单为空");
			super.setErrorMessage("更新友情链接，提交表单不能为空");
			return "failure";
		}
		
		super.setForwardUrl("/agent/friendLink.do");
		logger.info("更新友情链接结束");
		return "forward";
	}
	
	/**
	 * 删除友情链接
	 * @return
	 */
	public String del() {
		logger.info("进入删除友情链接");
		if (friendLink != null && (friendLink.getId() != null 
				&& friendLink.getId() != 0L)) {
			friendLinkService.del(friendLink.getId());
		} else {
			logger.error("删除友情链接，编码为空");
			super.setErrorMessage("删除友情链接，编码不能为空");
			return "failure";
		}
		
		super.setForwardUrl("/agent/friendLink.do");
		logger.info("删除友情链接结束");
		return "forward";
	}
	
	public FriendLinkService getFriendLinkService() {
		return friendLinkService;
	}

	public void setFriendLinkService(FriendLinkService friendLinkService) {
		this.friendLinkService = friendLinkService;
	}

	public List<FriendLink> getFriendLinkList() {
		return friendLinkList;
	}

	public void setFriendLinkList(List<FriendLink> friendLinkList) {
		this.friendLinkList = friendLinkList;
	}

	public FriendLink getFriendLink() {
		return friendLink;
	}

	public void setFriendLink(FriendLink friendLink) {
		this.friendLink = friendLink;
	}
	
}
