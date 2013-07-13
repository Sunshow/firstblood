package com.bjgl.web.enums;

import com.bjgl.web.bean.IntegerBeanLabelItem;

import java.util.ArrayList;
import java.util.List;

public class LogType extends IntegerBeanLabelItem {
	private static final long serialVersionUID = 1L;
	public final static LogType OPERATIONTYPE = new LogType("操作日志",1);
	public final static LogType EXCPTIONTYPE = new LogType("异常日志",2);
	
	protected LogType(String name, int value) {
		super(LogType.class.getName(),name, value);
	}
	
	public static LogType getItem(int value){
		return (LogType)LogType.getResult(LogType.class.getName(), value);
	}
	public static List<LogType> list;
     static{
        list = new ArrayList<LogType>();
        list.add(OPERATIONTYPE);
        list.add(EXCPTIONTYPE);
    }
}
