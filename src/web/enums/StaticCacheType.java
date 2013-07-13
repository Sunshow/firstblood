package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class StaticCacheType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static StaticCacheType IDTYPE = new StaticCacheType("id",1);
	public final static StaticCacheType SLUGTYPE = new StaticCacheType("slug",2);
	
	protected StaticCacheType(String name, int value) {
		super(StaticCacheType.class.getName(),name, value);
	}
	
	public static StaticCacheType getItem(int value){
		return (StaticCacheType)StaticCacheType.getResult(StaticCacheType.class.getName(), value);
	}
	
	public static List<StaticCacheType> list;
     static{
        list = new ArrayList<StaticCacheType>();
        list.add(IDTYPE);
        list.add(SLUGTYPE);
    }
}
