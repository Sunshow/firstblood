package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class ProcessUserType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static ProcessUserType ALL = new ProcessUserType("全部", 0);
	public final static ProcessUserType ROLE = new ProcessUserType("角色", 1);
	public final static ProcessUserType USER = new ProcessUserType("用户", 2);
	
	protected ProcessUserType(String name, int value) {
		super(ProcessUserType.class.getName(),name, value);
	}
	
	public static ProcessUserType getItem(int value){
		return (ProcessUserType)ProcessUserType.getResult(ProcessUserType.class.getName(), value);
	}
	public static List<ProcessUserType> list;
     static{
        list = new ArrayList <ProcessUserType>();
        list.add(ALL);
        list.add(ROLE);
        list.add(USER);
    }
}
