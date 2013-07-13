/**
 * 
 */
package web.dao.message;

import java.util.List;

import com.lehecai.admin.web.domain.message.Message;

/**
 * @author chirowong
 *
 */
public interface MessageDao {
	void save(Message message);
	Message get(Long messageID);
	void save(Message message, List<Long> messageReceiverList);
}
