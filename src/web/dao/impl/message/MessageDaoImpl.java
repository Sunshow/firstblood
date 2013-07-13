/**
 * 
 */
package web.dao.impl.message;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.dao.message.MessageDao;
import com.lehecai.admin.web.domain.message.Message;
import com.lehecai.admin.web.domain.message.MessageReceiver;

/**
 * @author chirowong
 *
 */
public class MessageDaoImpl extends HibernateDaoSupport implements MessageDao {

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.message.MessageDao#get(java.lang.Long)
	 */
	@Override
	public Message get(Long messageID) {
		return getHibernateTemplate().get(Message.class, messageID);
	}

	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.dao.message.MessageDao#save(com.lehecai.admin.web.domain.message.Message)
	 */
	@Override
	public void save(Message message) {
		// TODO Auto-generated method stub
		getHibernateTemplate().save(message);
	}
	
	public void save(Message message,List<Long> messageReceiverList) {
		Message _message = getHibernateTemplate().merge(message);
		if(_message != null){
			for(Long _messageReceiverID : messageReceiverList){
				MessageReceiver _messageRecevicer = new MessageReceiver();
				_messageRecevicer.setMessageID(_message.getMessageID());
				_messageRecevicer.setUserID(_messageReceiverID);
				_messageRecevicer.setFlag(MessageReceiver.MESSAGE_NO_READ);//默认为未阅读
				getHibernateTemplate().save(_messageRecevicer);
			}
		}
	}


}
