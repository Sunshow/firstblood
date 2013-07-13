package com.bjgl.web.constant;

public class Global {
	public final static String USER_SESSION = "USER_SESSION";
	
	public final static String API_MAP_KEY_LIST = "list";
	public final static String API_MAP_KEY_PAGEBEAN = "pageBean";
	/**
	 * 结果封装对象key  对应ResultBean对象
	 */
	public final static String API_MAP_KEY_RESULTBEAN = "resultBean";
	
	public final static String API_MAP_KEY_AMOUNT = "amount"; //存放总金额的键名
	
	public final static String API_MAP_KEY_PAYAMOUNT = "postAmount"; //存放税后金额的键名
	
	public final static String API_MAP_KEY_POSTTAXPRIZE = "postTaxPrize"; //存放税后金额的键名
	
	public final static String API_MAP_KEY_UID = "uid"; //存放钱包兑换操作的会员的键名
	public final static String API_MAP_KEY_FROM_WALLET = "fromWallet"; //存放钱包兑换来源的键名
	public final static String API_MAP_KEY_TO_WALLET = "toWallet"; //存放钱包兑换目标的键名
	
	public final static String KEY_TITLE = "title";//福彩3D小贴士标题
	public final static String KEY_CONTENT = "content";//福彩3D小贴士内容
	public final static String KEY_TARGET = "target";//福彩3D小贴士指标
	public final static String KEY_CURRENT_OMIT = "currentOmit";//福彩3D小贴士当前遗漏
	public final static String KEY_HISTORY_VALUE = "historyValue";//福彩3D小贴士历史峰值
	public final static String KEY_UPDATE_TIME = "updateTime";//福彩3D小贴士上次更新时间
	
	public final static String API_MAP_KEY_WORD = "word";				//敏感词
	public final static String API_MAP_KEY_FILTER_STRING = "filterStr";	//过滤字符串
	
	public final static String KEY_ACCOUNT = "account";					//充值账户
	public final static String KEY_AMOUNT = "amount";					//充值金额
	
	public final static Integer BATCH_DEAL_NUM = 20; //批处理单次处理量
}
