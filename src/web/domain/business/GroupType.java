package web.domain.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.core.IntegerBeanLabelItem;

public class GroupType extends IntegerBeanLabelItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6765362583371120093L;

	private static final Logger logger = LoggerFactory.getLogger(GroupType.class.getName());
	
	private static List<GroupType> _items = new ArrayList<GroupType>();
	
	private static List<GroupType> items;

	protected GroupType(String name, int value) {
		super(GroupType.class.getName(), name, value);
		_items.add(this);
	}
	
	public static GroupType getItem(int value){
		try {
			return (GroupType)GroupType.getResult(GroupType.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * @return 所有赔偿方式的列表
	 */
	public static List<GroupType> getItems() {
		return items;
	}

	public final static GroupType CPSZH = new GroupType("默认", 1);
	public final static GroupType REGISTER_USERNAME = new GroupType("注册用户名", 2);
	public final static GroupType PROGRAM_TITLE_DESCRIPTION = new GroupType("方案标题和描述", 3);
	public final static GroupType PHONE_NUMBER = new GroupType("手机号", 4);
	
	static {
		synchronized (_items) {
			items = Collections.unmodifiableList(_items);
		}
	}
}