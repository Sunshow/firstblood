package web.domain.business;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.core.IntegerBeanLabelItem;

public class PayPasswordType extends IntegerBeanLabelItem {

	private static final long serialVersionUID = -8013812345633302001L;
	private static final Logger logger = LoggerFactory.getLogger(PayPasswordType.class.getName());
	
	private static List<PayPasswordType> items = new ArrayList<PayPasswordType>();
	private static List<PayPasswordType> queryItems = new ArrayList<PayPasswordType>();
	
	protected PayPasswordType(String name, int value, boolean queryOnly) {
		super(PayPasswordType.class.getName(), name, value);
		
		queryItems.add(this);
		if (!queryOnly) {
			items.add(this);
		}
	}
	
	protected PayPasswordType(String name, int value) {
		this(name, value, false);
	}
	
	public static PayPasswordType getItem(int value){
		try {
			return (PayPasswordType)PayPasswordType.getResult(PayPasswordType.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static List<PayPasswordType> getItems() {
		return items;
	}
	
	public static List<PayPasswordType> getItemsForQuery() {
		return queryItems;
	}

	public static final PayPasswordType ALL = new PayPasswordType("全部", -1, true);
	
	public static final PayPasswordType YES = new PayPasswordType("验证支付密码", 1);
	public static final PayPasswordType NO = new PayPasswordType("修改支付密码", 2);
}
