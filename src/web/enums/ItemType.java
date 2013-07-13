package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class ItemType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static ItemType TEXTTYPE = new ItemType("文本",1);
	public final static ItemType LINKTYPE = new ItemType("链接",2);
	public final static ItemType YESNOTYPE = new ItemType("是否",3);
	
	protected ItemType(String name, int value) {
		super(ItemType.class.getName(),name, value);
	}
	
	public static ItemType getItem(int value){
		return (ItemType)ItemType.getResult(ItemType.class.getName(), value);
	}
	
	public static List<ItemType> list;
     static{
        list = new ArrayList<ItemType>();
        list.add(TEXTTYPE);
        list.add(LINKTYPE);
        list.add(YESNOTYPE);
    }
}
