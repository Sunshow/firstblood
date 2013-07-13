package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class AliasDataProvider extends IntegerBeanLabelItem{

	private static final long serialVersionUID = 1L;
	
	public static final AliasDataProvider FOOTBALL310 = new AliasDataProvider("football310", 1);
	public static final AliasDataProvider PLOT = new AliasDataProvider("plot", 2);
	
	
	protected AliasDataProvider(String name, int value) {
		super(AliasDataProvider.class.getName(),name, value);
	}
	
	public static AliasDataProvider getItem(int value){
		return (AliasDataProvider)AliasDataProvider.getResult(AliasDataProvider.class.getName(), value);
	}
	
	public static List<AliasDataProvider> list;
     static{
        list = new ArrayList<AliasDataProvider>();

    }
}
