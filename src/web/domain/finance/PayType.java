package web.domain.finance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.core.IntegerBeanLabelItem;

public class PayType extends IntegerBeanLabelItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6765362583371120093L;

	private static final Logger logger = LoggerFactory.getLogger(PayType.class.getName());
	
	private static List<PayType> _items = new ArrayList<PayType>();
	
	private static List<PayType> items;

	protected PayType(String name, int value) {
		super(PayType.class.getName(), name, value);
		_items.add(this);
	}
	
	public static PayType getItem(int value){
		try {
			return (PayType)PayType.getResult(PayType.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * @return 所有赔偿方式的列表
	 */
	public static List<PayType> getItems() {
		return items;
	}

	public final static PayType CPSZH = new PayType("出票商帐户", 1);
	public final static PayType OTHER = new PayType("其他", 2);
	
	static {
		synchronized (_items) {
			items = Collections.unmodifiableList(_items);
		}
	}
}