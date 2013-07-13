package web.service.impl.link;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.link.FriendLinkDao;
import com.lehecai.admin.web.domain.link.FriendLink;
import com.lehecai.admin.web.service.link.FriendLinkService;

public class FriendLinkServiceImpl implements FriendLinkService{
	
	private FriendLinkDao friendLinkDao = null;
	
	/**
	 * 查询所有友情链接
	 */
	public List<FriendLink> findFriendLinkList (FriendLink friendLink, PageBean pageBean) {
		return friendLinkDao.findFriendLinkList(friendLink, pageBean);
		
	}
	
	/**
	 * 根据友情链接编号查询友情链接
	 * @param id 友情链接编号
	 */
	public FriendLink get (Long id) {
		return friendLinkDao.get(id);
	}
	
	/**
	 * 添加/修改友情链接
	 * @param friendlink 友情链接对象
	 */
	public void merge (FriendLink friendLink) {		
		friendLinkDao.merge(friendLink);
	}
	
	/**
	 * 删除友情链接
	 */
	public void del(Long id){
		friendLinkDao.del(friendLinkDao.get(id));
	}

	public FriendLinkDao getFriendLinkDao() {
		return friendLinkDao;
	}
	public void setFriendLinkDao(FriendLinkDao friendLinkDao) {
		this.friendLinkDao = friendLinkDao;
	}
}
