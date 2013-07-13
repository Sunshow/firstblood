package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class TextType extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;
	public final static TextType PLAINTYPE = new TextType("纯文本",1);
	public final static TextType HTMLTYPE = new TextType("富文本",2);
	 
	 protected TextType(String name, int value) {
			super(TextType.class.getName(),name, value);
		}
		
	public static TextType getItem(int value){
		return (TextType)TextType.getResult(TextType.class.getName(), value);
	}
	public static List<TextType> list;
     static{
        list = new ArrayList<TextType>();
        list.add(PLAINTYPE);
        list.add(HTMLTYPE);
    }
}
