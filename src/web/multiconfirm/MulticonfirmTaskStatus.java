package web.multiconfirm;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class MulticonfirmTaskStatus extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;
	public final static MulticonfirmTaskStatus ALL = new MulticonfirmTaskStatus("全部", 0);
	public final static MulticonfirmTaskStatus OPEN = new MulticonfirmTaskStatus("开启", 1);
	public final static MulticonfirmTaskStatus PASSED = new MulticonfirmTaskStatus("通过", 2);
	public final static MulticonfirmTaskStatus FAILED = new MulticonfirmTaskStatus("未通过", 3);
	public final static MulticonfirmTaskStatus TIMEOUT = new MulticonfirmTaskStatus("超时", 4);
	 
	 protected MulticonfirmTaskStatus(String name, int value) {
		super(MulticonfirmTaskStatus.class.getName(), name, value);
	}
		
	public static MulticonfirmTaskStatus getItem(int value){
		return (MulticonfirmTaskStatus)MulticonfirmTaskStatus.getResult(MulticonfirmTaskStatus.class.getName(), value);
	}
	public static List<MulticonfirmTaskStatus> list;
     static{
        list = new ArrayList<MulticonfirmTaskStatus>();
        list.add(ALL);
        list.add(OPEN);
        list.add(PASSED);
        list.add(FAILED);
        list.add(TIMEOUT);
    }
}
