package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class TaskType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static TaskType USERTASK = new TaskType("userTask", 1);
	public final static TaskType SERVICETASK = new TaskType("seviceTask", 2);
	
	protected TaskType(String name, int value) {
		super(TaskType.class.getName(),name, value);
	}
	
	public static TaskType getItem(int value){
		return (TaskType)TaskType.getResult(TaskType.class.getName(), value);
	}
	public static List<TaskType> list;
     static{
        list = new ArrayList <TaskType>();
        list.add(USERTASK);
        list.add(SERVICETASK);
    }
}
