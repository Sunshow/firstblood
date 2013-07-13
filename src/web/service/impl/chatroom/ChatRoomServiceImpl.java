/**
 * 
 */
package web.service.impl.chatroom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.bean.ResultBean;
import com.lehecai.admin.web.constant.Global;
import com.lehecai.admin.web.domain.chatroom.ChatRoom;
import com.lehecai.admin.web.domain.chatroom.ChatRoomAccountBanState;
import com.lehecai.admin.web.domain.chatroom.ChatRoomApiConstant;
import com.lehecai.admin.web.domain.chatroom.ChatRoomApiRequest;
import com.lehecai.admin.web.domain.chatroom.ChatRoomApiRequestService;
import com.lehecai.admin.web.domain.chatroom.ChatRoomApiResponse;
import com.lehecai.admin.web.domain.chatroom.ChatRoomBan;
import com.lehecai.admin.web.domain.chatroom.ChatRoomConstants;
import com.lehecai.admin.web.domain.chatroom.ChatRoomMessage;
import com.lehecai.admin.web.domain.chatroom.ChatRoomNotice;
import com.lehecai.admin.web.service.chatroom.ChatRoomService;
import com.lehecai.core.api.ApiConstant;
import com.lehecai.core.exception.ApiRemoteCallFailedException;

/**
 * @author chirowong
 *
 */
public class ChatRoomServiceImpl implements ChatRoomService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ChatRoomApiRequestService chatRoomApiRequestService;
	
	public ChatRoomApiRequestService getChatRoomApiRequestService() {
		return chatRoomApiRequestService;
	}

	public void setChatRoomApiRequestService(
			ChatRoomApiRequestService chatRoomApiRequestService) {
		this.chatRoomApiRequestService = chatRoomApiRequestService;
	}

	@Override
	public Map<String, Object> queryChatRoomList(ChatRoom chatRoom,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用聊天室查询API");
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPageSize(pageBean.getPageSize());
		}
		request.setActionType(chatRoom.getActionType());
		logger.info("Request Query String: {}", request.toQueryString());
		
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用聊天室查询API失败");
			throw new ApiRemoteCallFailedException("调用聊天室查询API失败");
		}
/*		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用聊天室查询API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用聊天室查询API请求出错," + response.getMessage());
		}*/
		logger.info("结束调用聊天室查询API");
		List<ChatRoom> chatRoomList = ChatRoom.convertFromJSONArray(response.getArrayData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getCount();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, chatRoomList);
		
		return map;
	}
	
	@Override
	public ChatRoom getChatRoomInfo(ChatRoom chatRoom) throws ApiRemoteCallFailedException {
		logger.info("进入调用获取聊天室信息API");
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		request.setActionType(chatRoom.getActionType());
		request.setChatRoom(chatRoom);
		logger.info("Request Query String: {}", request.toQueryString());
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用聊天室查询API失败");
			throw new ApiRemoteCallFailedException("调用聊天室查询API失败");
		}
		logger.info("结束调用聊天室查询API");
		
		ChatRoom chatRoomResult = ChatRoom.convertFromJSONObject(response.getData());
		return chatRoomResult;
	}
	
	@Override
	public ChatRoomNotice getChatRoomNoticeInfo(ChatRoom chatRoom) throws ApiRemoteCallFailedException {
		logger.info("进入调用获取聊天室信息API");
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		request.setActionType(chatRoom.getActionType());
		request.setChatRoom(chatRoom);
		logger.info("Request Query String: {}", request.toQueryString());
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用聊天室查询API失败");
			throw new ApiRemoteCallFailedException("调用聊天室查询API失败");
		}
		logger.info("结束调用聊天室查询API");
		
		ChatRoomNotice noticeResult = ChatRoomNotice.convertFromJSONObject(response.getData());
		return noticeResult;
	}
	
	@Override
	public Map<String, Object> queryChatRoomBanList(ChatRoom chatRoom,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用聊天室查询API");
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPageSize(pageBean.getPageSize());
		}
		request.setActionType(chatRoom.getActionType());
		logger.info("Request Query String: {}", request.toQueryString());
		
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用聊天室查询API失败");
			throw new ApiRemoteCallFailedException("调用聊天室查询API失败");
		}
/*		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用聊天室查询API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用聊天室查询API请求出错," + response.getMessage());
		}*/
		logger.info("结束调用聊天室查询API");
		List<ChatRoomBan> chatRoomBanList = ChatRoomBan.convertFromJSONArray(response.getArrayData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getCount();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, chatRoomBanList);
		
		return map;
	}
	
	@Override
	public Map<String, Object> queryChatRoomAccountBanState(ChatRoom chatRoom,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用聊天室查询API");
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		request.setActionType(chatRoom.getActionType());
		request.setChatRoom(chatRoom);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用聊天室查询API失败");
			throw new ApiRemoteCallFailedException("调用聊天室查询API失败");
		}
		logger.info("结束调用聊天室查询API");
		ChatRoomAccountBanState chatRoomAccountBanState = ChatRoomAccountBanState.convertFromJSONObject(response.getData());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_LIST, chatRoomAccountBanState);
		
		return map;
	}
	
	@Override
	public Map<String, Object> queryChatRoomNoticeList(ChatRoom chatRoom,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用聊天室查询API");
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPageSize(pageBean.getPageSize());
		}
		request.setActionType(chatRoom.getActionType());
		logger.info("Request Query String: {}", request.toQueryString());
		
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用聊天室查询API失败");
			throw new ApiRemoteCallFailedException("调用聊天室查询API失败");
		}
/*		if (response.getCode() != ApiConstant.RC_SUCCESS) {
			logger.error("调用聊天室查询API请求出错, rc={}, message={}", response.getCode(), response.getMessage());
			throw new ApiRemoteCallFailedException("调用聊天室查询API请求出错," + response.getMessage());
		}*/
		logger.info("结束调用聊天室查询API");
		List<ChatRoomNotice> chatRoomNoticeList = ChatRoomNotice.convertFromJSONArray(response.getArrayData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getCount();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, chatRoomNoticeList);
		
		return map;
	}

	@Override
	public ResultBean manageChatRoom(ChatRoom chatRoom)
			throws ApiRemoteCallFailedException {
		logger.info("进入调用API更新聊天室信息");
		ResultBean resultBean = new ResultBean();
		if(chatRoom == null){
			logger.error("聊天室信息为空");
			resultBean.setResult(false);
			resultBean.setMessage("聊天室信息为空");
			return resultBean;
		}
		
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setActionType(chatRoom.getActionType());
		try {
			request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
			request.setChatRoom(chatRoom);
		} catch (Exception e) {
			logger.error("更新聊天室信息解析失败!");
			resultBean.setResult(false);
			resultBean.setMessage("更新聊天室信息解析失败");
			return resultBean;
		}
		
		logger.info("Request Query String: {}", request.toQueryString());
		
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (!StringUtils.isEmpty(response.getMessage())) {
			logger.error(ChatRoomConstants.errorCode.get(response.getMessage()));
			resultBean.setResult(false);
			resultBean.setMessage(ChatRoomConstants.errorCode.get(response.getMessage()));
			return resultBean;
		}else{
			logger.info("聊天室更新成功!");
			resultBean.setResult(true);
			resultBean.setMessage("聊天室更新成功");
			return resultBean;
		}
	}
	@Override
	public void manageMessage(ChatRoom chatRoom)
			throws ApiRemoteCallFailedException{
		logger.info("进入调用API更新消息");
		
		if(chatRoom == null){
			logger.error("聊天室信息为空");
		}		
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setActionType(chatRoom.getActionType());
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		request.setChatRoom(chatRoom);
		logger.info("Request Query String: {}", request.toQueryString());
		ChatRoomApiResponse response = new ChatRoomApiResponse();
		try {
			response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
			
		} catch (ApiRemoteCallFailedException e) {
			logger.error("更新聊天室信息解析失败!");
		}
		if (!StringUtils.isEmpty(response.getMessage())) {
			logger.error(ChatRoomConstants.errorCode.get(response.getMessage()));
		}else{
			logger.info("消息更新成功!");
		}
	}
	
	@Override
	public Map<String, Object> queryChatRoomMessageList(ChatRoom chatRoom,
			PageBean pageBean) throws ApiRemoteCallFailedException {
		logger.info("进入调用聊天室查询信息API");
		ChatRoomApiRequest request = new ChatRoomApiRequest();
		request.setUrl(ChatRoomApiConstant.API_URL_CHAT_ROOM);
		if (pageBean != null) {
			request.setPage(pageBean.getPage());
			request.setPageSize(pageBean.getPageSize());
		}
		request.setActionType(chatRoom.getActionType());
		request.setChatRoom(chatRoom);
		logger.info("Request Query String: {}", request.toQueryString());
		
		ChatRoomApiResponse response = chatRoomApiRequestService.request(request, ApiConstant.API_REQUEST_TIME_OUT_DEFAULT);
		if (response == null) {
			logger.error("调用聊天室查询信息API失败");
			throw new ApiRemoteCallFailedException("调用聊天室查询信息API失败");
		}
		logger.info("结束调用聊天室查询信息API");
		List<ChatRoomMessage> chatRoomMessageList = ChatRoomMessage.convertFromJSONArray(response.getArrayData());
		
		if (pageBean != null && pageBean.isPageFlag()) {
			int totalCount = response.getCount();
			pageBean.setCount(totalCount);
			
			int pageCount = 0;//页数
			if ( pageBean.getPageSize() != 0 ) {
	            pageCount = totalCount / pageBean.getPageSize();
	            if (totalCount % pageBean.getPageSize() != 0) {
	                pageCount ++;
	            }
	        }
			pageBean.setPageCount(pageCount);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Global.API_MAP_KEY_PAGEBEAN, pageBean);
		map.put(Global.API_MAP_KEY_LIST, chatRoomMessageList);
		
		return map;
	}
}
