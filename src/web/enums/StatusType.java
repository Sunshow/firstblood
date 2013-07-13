package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class StatusType extends IntegerBeanLabelItem{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static StatusType SENDINGTYPE = new StatusType("正在发送",1);
	public final static StatusType SUCCESSTYPE = new StatusType("成功",2);
	public final static StatusType FAILURETYPE = new StatusType("失败",3);
	public final static StatusType WAITINGTYPE = new StatusType("待发送",4);
	 
	 protected StatusType(String name, int value) {
			super(StatusType.class.getName(),name, value);
		}
		
	public static StatusType getItem(int value){
		return (StatusType)StatusType.getResult(StatusType.class.getName(), value);
	}
	public static List<StatusType> list;
     static{
        list = new ArrayList<StatusType>();
        list.add(SENDINGTYPE);
        list.add(SUCCESSTYPE);
        list.add(FAILURETYPE);
        list.add(WAITINGTYPE);
    }
}
