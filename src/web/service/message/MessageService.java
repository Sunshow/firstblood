/**
 * 
 */
package web.service.message;

import java.util.List;

/**
 * @author chirowong
 *
 */
public interface MessageService {
	/*Message get(Long messageID);
	void save(Message message);*/
	void saveMessage(String content, List<Long> userList, List<Long> roleList);
}
