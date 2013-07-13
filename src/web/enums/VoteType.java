package web.enums;

import java.util.ArrayList;
import java.util.List;

import com.lehecai.core.IntegerBeanLabelItem;

public class VoteType extends IntegerBeanLabelItem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static VoteType SINGLETYPE = new VoteType("单选",1);
	public final static VoteType MULTIPLETTYPE = new VoteType("多选",2);
	
	protected VoteType(String name, int value) {
		super(VoteType.class.getName(),name, value);
	}
	
	public static VoteType getItem(int value){
		return (VoteType)VoteType.getResult(VoteType.class.getName(), value);
	}
	public static List<VoteType> list;
     static{
        list = new ArrayList <VoteType>();
        list.add(SINGLETYPE);
        list.add(MULTIPLETTYPE);
    }
}
