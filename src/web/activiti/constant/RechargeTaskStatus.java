package web.activiti.constant;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.core.IntegerBeanLabelItem;

public class RechargeTaskStatus extends IntegerBeanLabelItem {
	private static final Logger logger = LoggerFactory.getLogger(RechargeTaskStatus.class.getName());
	private static final long serialVersionUID = 5959923813363953414L;
	
	private static List<RechargeTaskStatus> items = new ArrayList<RechargeTaskStatus>();

	protected RechargeTaskStatus(String name, int value) {
		super(RechargeTaskStatus.class.getName(), name, value);
		items.add(this);
	}
	
	public static RechargeTaskStatus getItem(int value){
		try {
			return (RechargeTaskStatus)RechargeTaskStatus.getResult(RechargeTaskStatus.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static List<RechargeTaskStatus> getItems() {
		return items;
	}

	public static Logger getLogger(RechargeTaskStatus lotteryType) {
		return LoggerFactory.getLogger("process_" + lotteryType.getValue());
	}
	
	public final static RechargeTaskStatus ALL = new RechargeTaskStatus("全部", -1);
	
	public final static RechargeTaskStatus UNHANDLING = new RechargeTaskStatus("未处理", 1);
	public final static RechargeTaskStatus CLAIMED = new RechargeTaskStatus("已领取", 2);
	public final static RechargeTaskStatus HANDLING = new RechargeTaskStatus("处理中", 3);
	public final static RechargeTaskStatus NOT_ENOUGH = new RechargeTaskStatus("额度不足已退还", 4);
	public final static RechargeTaskStatus ENOUGH = new RechargeTaskStatus("满足额度待处理", 5);
	public final static RechargeTaskStatus NOT_RECEIVED = new RechargeTaskStatus("未到帐", 6);
	public final static RechargeTaskStatus RECEIVED = new RechargeTaskStatus("已到帐", 7);
	public final static RechargeTaskStatus SUCCESS = new RechargeTaskStatus("处理成功", 8);
	public final static RechargeTaskStatus FAILURE = new RechargeTaskStatus("处理失败", 9);
}

