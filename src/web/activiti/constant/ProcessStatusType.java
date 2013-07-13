package web.activiti.constant;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.core.IntegerBeanLabelItem;

/**
 * 
 * @author He Wang
 *
 */
public class ProcessStatusType extends IntegerBeanLabelItem {
	private static final Logger logger = LoggerFactory.getLogger(ProcessStatusType.class.getName());
	private static final long serialVersionUID = 5959923813363953414L;
	
	private static List<ProcessStatusType> items = new ArrayList<ProcessStatusType>();

	protected ProcessStatusType(String name, int value) {
		super(ProcessStatusType.class.getName(), name, value);
		items.add(this);
	}
	
	public static ProcessStatusType getItem(int value){
		try {
			return (ProcessStatusType)ProcessStatusType.getResult(ProcessStatusType.class.getName(), value);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static List<ProcessStatusType> getItems() {
		return items;
	}

	public static Logger getLogger(ProcessStatusType leaveStatusType) {
		return LoggerFactory.getLogger("process_" + leaveStatusType.getValue());
	}
	
	public final static ProcessStatusType HANDLE = new ProcessStatusType("处理中", 1);
	public final static ProcessStatusType COMPLETE = new ProcessStatusType("完成", 2);
	public final static ProcessStatusType REJECT = new ProcessStatusType("拒绝", 3);

}

