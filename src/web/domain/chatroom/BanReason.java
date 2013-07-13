/**
 * 
 */
package web.domain.chatroom;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.core.IntegerBeanLabelItem;

/**
 * @author chirowong
 */
public class BanReason extends IntegerBeanLabelItem {

	private static final long serialVersionUID = -8013814461233302001L;

	private static final Logger logger = LoggerFactory.getLogger(BanReason.class.getName());
	
	private static List<BanReason> items = new ArrayList<BanReason>();
	private static List<BanReason> queryItems = new ArrayList<BanReason>();
	
	protected BanReason(String name, int value, boolean queryOnly) {
		super(BanReason.class.getName(), name, value);
		
		queryItems.add(this);
		if (!queryOnly) {
			items.add(this);
		}
	}
	
	protected BanReason(String name, int value) {
		this(name, value, false);
	}
	
	public static BanReason getItem(int value){
		try {
			return (BanReason)BanReason.getResult(BanReason.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static List<BanReason> getItems() {
		return items;
	}
	
	public static List<BanReason> getItemsForQuery() {
		return queryItems;
	}

	public static final BanReason BWMYY = new BanReason("不文明用语", 1);
	public static final BanReason LJGG = new BanReason("垃圾广告", 2);
	public static final BanReason QZXX = new BanReason("欺诈信息", 3);
	public static final BanReason SQXX = new BanReason("色情信息", 4);
	public static final BanReason FDYL = new BanReason("反动言论", 5);
	public static final BanReason OTHER = new BanReason("其他", 99);

}
