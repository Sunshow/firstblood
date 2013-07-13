package web.action.weibo.sina;

import com.lehecai.core.IntegerBeanLabelItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SinaWeiboApiErrorType extends IntegerBeanLabelItem {
	private static final long serialVersionUID = -6566794272491284149L;

	private static final Logger logger = LoggerFactory.getLogger(SinaWeiboApiErrorType.class.getName());
	
	private static List<SinaWeiboApiErrorType> _items = new ArrayList<SinaWeiboApiErrorType>();
	
	private static List<SinaWeiboApiErrorType> items;

	protected SinaWeiboApiErrorType(String name, int value) {
		super(SinaWeiboApiErrorType.class.getName(), name, value);
		_items.add(this);
	}
	
	public static SinaWeiboApiErrorType getItem(int value){
		try {
			return (SinaWeiboApiErrorType)SinaWeiboApiErrorType.getResult(SinaWeiboApiErrorType.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static List<SinaWeiboApiErrorType> getItems() {
		return items;
	}

	public final static SinaWeiboApiErrorType ALL = new SinaWeiboApiErrorType("全部", -1);
	
	public final static SinaWeiboApiErrorType ACCESS_TOKEN_EXPIRED = new SinaWeiboApiErrorType("access_token过期", 21332);
	
	static {
		synchronized (_items) {
			items = Collections.unmodifiableList(_items);
		}
	}
}

