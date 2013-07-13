package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class ContentType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static ContentType NEWSTYPE = new ContentType("新闻",1);
	public final static ContentType LINKTYPE = new ContentType("链接",2);
	
	protected ContentType(String name, int value) {
		super(ContentType.class.getName(),name, value);
	}
	
	public static ContentType getItem(int value){
		return (ContentType)ContentType.getResult(ContentType.class.getName(), value);
	}
	
	public static List<ContentType> list;
     static{
        list = new ArrayList<ContentType>();
        list.add(NEWSTYPE);
        list.add(LINKTYPE);
    }
}
