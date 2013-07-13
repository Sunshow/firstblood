package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class SwcFilterItem extends IntegerBeanLabelItem{
	private static final long serialVersionUID = 1L;

	
	public final static SwcFilterItem PMF_FILTER_ALL = new SwcFilterItem("开启全部选项",0xffff);
	public final static SwcFilterItem PMF_FILTER_NUMERIC = new SwcFilterItem("去掉 [0-9] 的数字",1);
	public final static SwcFilterItem PMF_FILTER_ALPHABETS = new SwcFilterItem("去掉 [a-zA-Z]",2);
	public final static SwcFilterItem PMF_FILTER_SYMBOLS = new SwcFilterItem("去掉 `~!@#$%^&*()_=-+[]{}|\\;:\'(\")<>,./?｀～！＠◎- ＃￥％...＆※×（）－—＋＝§÷】【『』''(\"\")；：，《。》、？＼｜",4);
	public final static SwcFilterItem PMF_FILTER_SPACES = new SwcFilterItem("删去空字符 空格, \t \n \r",8);
	public final static SwcFilterItem PMF_FILTER_CONTROLS = new SwcFilterItem("删去控制字符 chr(0) - chr(31)",16);
	public final static SwcFilterItem PMF_FILTER_CONVFW = new SwcFilterItem("全角转半角，只包括数字、字母", 128);
	public final static SwcFilterItem PMF_FILTER_INVERSE = new SwcFilterItem("反转html实体到字符", 256);
	public final static SwcFilterItem PMF_FILTER_NONE = new SwcFilterItem("不预处理",0);
	
	protected SwcFilterItem(String name, int value) {
		super(SwcFilterItem.class.getName(), name, value);
	}
	
	public static SwcFilterItem getItem(int value){
		return (SwcFilterItem)SwcFilterItem.getResult(SwcFilterItem.class.getName(), value);
	}
	
	public static List<SwcFilterItem> list;
     static{
        list = new ArrayList<SwcFilterItem>();
        list.add(PMF_FILTER_ALL);
        list.add(PMF_FILTER_NUMERIC);
        list.add(PMF_FILTER_ALPHABETS);
        list.add(PMF_FILTER_SYMBOLS);
        list.add(PMF_FILTER_SPACES);
        list.add(PMF_FILTER_CONTROLS);
        list.add(PMF_FILTER_CONVFW);
        list.add(PMF_FILTER_INVERSE);
        list.add(PMF_FILTER_NONE);
    }
}
