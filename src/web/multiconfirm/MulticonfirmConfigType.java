package web.multiconfirm;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class MulticonfirmConfigType extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;
	public final static MulticonfirmConfigType ALL = new MulticonfirmConfigType("全部", 0);
	public final static MulticonfirmConfigType DEFAULT = new MulticonfirmConfigType("通用配置", 1);
	public final static MulticonfirmConfigType SPECIAL = new MulticonfirmConfigType("特殊配置", 2);
	 
	 protected MulticonfirmConfigType(String name, int value) {
		super(MulticonfirmConfigType.class.getName(), name, value);
	}
		
	public static MulticonfirmConfigType getItem(int value){
		return (MulticonfirmConfigType)MulticonfirmConfigType.getResult(MulticonfirmConfigType.class.getName(), value);
	}
	public static List<MulticonfirmConfigType> list;
     static{
        list = new ArrayList<MulticonfirmConfigType>();
        list.add(ALL);
        list.add(DEFAULT);
        list.add(SPECIAL);
    }
}
