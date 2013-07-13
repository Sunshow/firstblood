package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class TemplateType extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;
	public final static TemplateType VMTYPE = new TemplateType("vm文件",1);
	public final static TemplateType DATATYPE = new TemplateType("模板内容",2);
	public final static TemplateType SOURCETYPE = new TemplateType("源数据输出",3);
	public final static TemplateType JSONPTYPE = new TemplateType("JSONP输出",4);
	 
	 protected TemplateType(String name, int value) {
			super(TemplateType.class.getName(),name, value);
		}
		
	public static TemplateType getItem(int value){
		return (TemplateType)TemplateType.getResult(TemplateType.class.getName(), value);
	}
	public static List<TemplateType> list;
     static{
        list = new ArrayList<TemplateType>();
        list.add(VMTYPE);
        list.add(DATATYPE);
        list.add(SOURCETYPE);
        list.add(JSONPTYPE);
    }
}
