/**
 * 
 */
package web.service.impl.message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.message.MessageDao;
import com.lehecai.admin.web.dao.user.UserDao;
import com.lehecai.admin.web.domain.message.Message;
import com.lehecai.admin.web.domain.user.User;
import com.lehecai.admin.web.service.message.MessageService;

/**
 * @author chirowong
 *
 */
public class MessageServiceImpl implements MessageService {
	private MessageDao messageDao;
	private UserDao userDao;


	public MessageDao getMessageDao() {
		return messageDao;
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public void saveMessage(String content, List<Long> userList,
			List<Long> roleList) {
		//获取roleList中的用户列表
		List<Long> receiverList = new ArrayList<Long>();
		if(roleList != null && roleList.size() > 0){
			PageBean pageBean = new PageBean();
			pageBean.setPageFlag(false);
			for(Long roleID : roleList){
				List<User> _usersByRole = userDao.list(null, null, null, null, roleID, "true", pageBean);
				if(_usersByRole != null && _usersByRole.size() > 0){
					for(User user : _usersByRole){
						receiverList.add(user.getId());
					}
				}
			}
		}
		
		if(userList != null && userList.size() > 0){
			receiverList.addAll(userList);
		}
		
		//去重
	    Set<Long> set = new HashSet<Long>();
        List<Long> removeDuplicateList = new ArrayList<Long>();
        for (Iterator<Long> iter = receiverList.iterator(); iter.hasNext();) {
            Long element = iter.next();
            if (set.add(element))
            	removeDuplicateList.add(element);
        }
        
        //构造message实体
        if(removeDuplicateList.size() > 0){
        	 Message message = new Message();
        	 Calendar ca = Calendar.getInstance();
        	 message.setMessageTime(ca.getTime());
        	 message.setMessageContent(content);
        	 messageDao.save(message,removeDuplicateList);
        }
	}
}
