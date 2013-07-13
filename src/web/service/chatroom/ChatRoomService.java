/**
 * 
 */
package web.service.chatroom;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.domain.chatroom.ChatRoom;
import com.lehecai.admin.web.domain.chatroom.ChatRoomNotice;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * 聊天室管理
 * @author chirowong
 *
 */
public interface ChatRoomService {
	/**
	 * 聊天室列表
	 */
	public Map<String, Object> queryChatRoomList(ChatRoom chatRoom, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 获取禁言用户列表
	 * @param chatRoom
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> queryChatRoomBanList(ChatRoom chatRoom, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 获取禁言用户状态
	 * @param chatRoom
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> queryChatRoomAccountBanState(ChatRoom chatRoom, PageBean pageBean) throws ApiRemoteCallFailedException;
	/**
	 * 获取公告列表
	 * @param chatRoom
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	public Map<String, Object> queryChatRoomNoticeList(ChatRoom chatRoom, PageBean pageBean) throws ApiRemoteCallFailedException;
	
	/**
	 * 增加聊天室、修改聊天室、关闭聊天室、移除消息、用户禁言、停止禁言、增加公告、移除公告
	 */
	public ResultBean manageChatRoom(ChatRoom chatRoom) throws ApiRemoteCallFailedException;
	
	public void manageMessage(ChatRoom chatRoom) throws ApiRemoteCallFailedException;
	
	public ChatRoom getChatRoomInfo(ChatRoom chatRoom) throws ApiRemoteCallFailedException;
	
	public ChatRoomNotice getChatRoomNoticeInfo(ChatRoom chatRoom) throws ApiRemoteCallFailedException;
	
	public Map<String, Object> queryChatRoomMessageList(ChatRoom chatRoom, PageBean pageBean) throws ApiRemoteCallFailedException;
}
