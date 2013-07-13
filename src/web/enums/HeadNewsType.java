package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class HeadNewsType extends IntegerBeanLabelItem{

	private static final long serialVersionUID = 1L;
	
	public static final HeadNewsType HEADNEWS = new HeadNewsType("hewaNews", 1);
	public static final HeadNewsType NOHEADNEWS = new HeadNewsType("noHeadNews", 0);
	public static final HeadNewsType ALL = new HeadNewsType("all", -1);
	
	protected HeadNewsType(String name, int value) {
		super(HeadNewsType.class.getName(),name, value);
	}
	
	public static HeadNewsType getItem(int value){
		return (HeadNewsType)HeadNewsType.getResult(HeadNewsType.class.getName(), value);
	}
	
	public static List<HeadNewsType> list;
     static{
        list = new ArrayList<HeadNewsType>();

    }
}
