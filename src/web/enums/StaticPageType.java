package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class StaticPageType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static StaticPageType DEFAULTTYPE = new StaticPageType("默认",1);
	public final static StaticPageType HTMLTYPE = new StaticPageType("html",2);
	public final static StaticPageType SHTMLTYPE = new StaticPageType("shtml",3);
	
	protected StaticPageType(String name, int value) {
		super(StaticPageType.class.getName(),name, value);
	}
	
	public static StaticPageType getItem(int value){
		return (StaticPageType)StaticPageType.getResult(StaticPageType.class.getName(), value);
	}
	public static List<StaticPageType> list;
     static{
        list = new ArrayList <StaticPageType>();
        list.add(DEFAULTTYPE);
        list.add(HTMLTYPE);
        list.add(SHTMLTYPE);
    }
}
