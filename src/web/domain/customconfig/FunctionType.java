/**
 * 
 */
package web.domain.customconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.core.IntegerBeanLabelItem;

/**
 * @author chirowong
 *
 */
public class FunctionType extends IntegerBeanLabelItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4430282787375258977L;

	private static final Logger logger = LoggerFactory.getLogger(FunctionType.class.getName());
	
	private static List<FunctionType> _items = new ArrayList<FunctionType>();
	
	private static List<FunctionType> items;

	protected FunctionType(String name, int value) {
		super(FunctionType.class.getName(), name, value);
		_items.add(this);
	}
	
	public static FunctionType getItem(int value){
		try {
			return (FunctionType)FunctionType.getResult(FunctionType.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * @return 所有功能的列表
	 */
	public static List<FunctionType> getItems() {
		return items;
	}
	
	static {
		synchronized (_items) {
			items = Collections.unmodifiableList(_items);
		}
	}

	//出票商对账单是否有权限修改已经入账的工单
	public final static FunctionType TERMINALACCOUNTCHECK = new FunctionType("出票商对账单", 1);
	//彩金派送是否有权限向现金账户派送
	public final static FunctionType GIFTREWARDSCHECK = new FunctionType("彩金派送", 2);
}
