package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class SmsType extends IntegerBeanLabelItem{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static SmsType PHONE = new SmsType("手机号",1);
	public final static SmsType USERNAME = new SmsType("用户名",2);
	
	protected SmsType(String name, int value) {
		super(SmsType.class.getName(), name, value);
	}
	
	public static SmsType getItem(int value){
		return (SmsType)SmsType.getResult(SmsType.class.getName(), value);
	}
	
	public static List<SmsType> list;
     static{
        list = new ArrayList<SmsType>();
        list.add(PHONE);
        list.add(USERNAME);
    }
}
