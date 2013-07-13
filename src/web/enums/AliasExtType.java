package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class AliasExtType extends IntegerBeanLabelItem{

	private static final long serialVersionUID = 1L;
	
	public static final AliasExtType DEFAULT = new AliasExtType("默认", 0);
	public static final AliasExtType LONG_NAME = new AliasExtType("长名", 1);
	public static final AliasExtType SHORT_NAME = new AliasExtType("短名", 2);
	
	protected AliasExtType(String name, int value) {
		super(AliasExtType.class.getName(),name, value);
	}
	
	public static AliasExtType getItem(int value){
		return (AliasExtType)AliasExtType.getResult(AliasExtType.class.getName(), value);
	}
	
	public static List<AliasExtType> list;
     static{
        list = new ArrayList<AliasExtType>();

    }
}
