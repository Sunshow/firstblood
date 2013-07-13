package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class ExecuteType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static ExecuteType DATATYPE = new ExecuteType("连接请求模板生成",1);
	public final static ExecuteType DIRECTTYPE = new ExecuteType("直接生成",2);
	public final static ExecuteType JSONTYPE = new ExecuteType("输入数据模板生成",3);
	
	protected ExecuteType(String name, int value) {
		super(ExecuteType.class.getName(),name, value);
	}
	
	public static ExecuteType getItem(int value){
		return (ExecuteType)ExecuteType.getResult(ExecuteType.class.getName(), value);
	}
	public static List<ExecuteType> list;
     static{
        list = new ArrayList <ExecuteType>();
        list.add(DATATYPE);
        list.add(DIRECTTYPE);
        list.add(JSONTYPE);
    }
}
