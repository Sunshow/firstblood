package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class ModelType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static ModelType SMS = new ModelType("手机",1);
	public final static ModelType MAIL = new ModelType("邮件",2);
	
	protected ModelType(String name, int value) {
		super(ModelType.class.getName(), name, value);
	}
	
	public static ModelType getItem(int value){
		return (ModelType)ModelType.getResult(ModelType.class.getName(), value);
	}
	
	public static List<ModelType> list;
     static{
        list = new ArrayList<ModelType>();
        list.add(SMS);
        list.add(MAIL);
    }
}
