package web.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * @function 日期处理工具
 * @author Emergency Team
 * @since 2009-6-3 
 */
public class DateUtil {
	
	public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE = "yyyy-MM-dd";
	
	/**
	 * @function 将日期转换成字符串
	 * @param date 日期
	 * @return 日期字符串
	 */
	public static String formatDate(Date date) {
		if(date==null){
			date = Calendar.getInstance().getTime();
		}
		return formatDate(date,DATE);
	}
	
	/**
	 * @function 将日期转换成字符串
	 * @param date 日期
	 * @param pattern 格式
	 * @return 日期字符串
	 */
	public static String formatDate(Date date,String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * @function 将字符串转换成日期
	 * @param date 日期形式的字符串
	 * @return 日期
	 */
	public static Date createDateFromString(String date){
		if(date==null) return null;
		String[] d = date.split("-");
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, Integer.parseInt(d[0]));
		ca.set(Calendar.MONTH, Integer.parseInt(d[1])-1);
		ca.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d[2]));
		return ca.getTime();
	}
	
	/**
	 * @function 获取带有年份的向量
	 * @return 向量
	 */
	@SuppressWarnings("unchecked")
	public static Vector getYears() {
		Calendar cd = Calendar.getInstance();
	 	Vector years = new Vector();
		for(int i = cd.get(Calendar.YEAR); i >= 2000; i --){
			years.add(new Integer(i));
		}
		return years;
	}
	
	/**
	 * @function 获取带有年份的向量
	 * @param begin 开始年份
	 * @param end 结束年份
	 * @return 向量
	 */
	@SuppressWarnings("unchecked")
	public static Vector getYears(int begin, int end) {
		Vector years = new Vector();
		for (int i = end; i >= begin; i --) {
			years.add(new Integer(i));
		}
		return years;
	}
	
	/**
	 * @function 获取带有年份的向量
	 * @param begin 开始年份
	 * @return 向量
	 */
	@SuppressWarnings("unchecked")
	public static Vector getYears(int begin) {
		Calendar cd = Calendar.getInstance();
	 	Vector years = new Vector();
		for(int i = cd.get(Calendar.YEAR); i >= begin; i --){
			years.add(new Integer(i));
		}
		return years;
	}
	
	/**
	 * @function 获取带有月份的向量
	 * @return 向量
	 */
	@SuppressWarnings("unchecked")
	public static Vector getMonths() {
    	Vector months = new Vector(12);
		for(int i=1;i<=12;i++){
			months.add(new Integer(i));
		}
		return months;
	}
	
	/**
	 * @function 获取带有月份的向量
	 * @param begin 开始月份
	 * @return 向量
	 */
	@SuppressWarnings("unchecked")
	public static Vector getMonths(int begin){
		Calendar cd = Calendar.getInstance();
		Vector months = new Vector();
		for(int i = cd.get(Calendar.MONTH); i>=begin; i--){
			months.add(new Integer(i));
		}
		return months;
	}
	
	/**
	 * @function 获取当前日期的上一天
	 * @return 日期
	 */
	public static Date getLatestDay() {
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DATE, - 1);
		return cd.getTime();
	}
	/**
	 * @function 获得最近一个周四
	 * @return 日期
	 *
	 */
	public static Date getLatestThursday(){
		Calendar cd = Calendar.getInstance();
		cd.setTime(DateUtil.parseDate(DateUtil.formatDate(cd.getTime())));
		
        // 获得最近一个周四
        int day = cd.get(Calendar.DAY_OF_WEEK);
        if (day >= 5) {
        	cd.add(Calendar.DATE, -day + 5);
        } else {
            cd.add(Calendar.DATE, -day - 2);
        }
        return cd.getTime();
	}
	/**
	 * @function 获得最近一个周五
	 * @return 日期
	 * @throws Exception
	 */
	public static Date getLatestFriday(){
		Calendar cd = Calendar.getInstance();
		cd.setTime(DateUtil.parseDate(DateUtil.formatDate(cd.getTime())));
		
        // 获得最近一个周五
        int day = cd.get(Calendar.DAY_OF_WEEK);
        if (day >= 6) {
        	cd.add(Calendar.DATE, -day + 6);
        } else {
            cd.add(Calendar.DATE, -day - 1);
        }
        return cd.getTime();
	}
	
	/**
	 * @function 获取四季向量
	 * @return 向量
	 */
	@SuppressWarnings("unchecked")
	public static Vector getSeason(){
		Vector season = new Vector(4);
		for(int i=1;i<=4;i++){
			season.add(new Integer(i));
		}
		return season;
	}
	
	/**
	 * @function 获取当前日期的上一天
	 * @return 日期
	 */
	public static Date getYesterday() {
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DATE, - 1);
		return cd.getTime();
	}
	
	/**
	 * @function 如果date比当前时间早，返回true；反之返回false
	 * @param date 日期
	 * @return boolean
	 */
	public static boolean isBypastTime(Date date) {
		Calendar cd1 = Calendar.getInstance();
		Calendar cd2 = Calendar.getInstance();
		cd2.setTime(date);
		return cd1.after(cd2);
	}
	
	/**
	 * 获得最近的旬报日期
	 * @param Calendar date 日期
	 * @return Date
	 */
	public static Date getNearestTensDay(Calendar date){
		Calendar nearestTensDay = date;
		int day = date.get(Calendar.DATE);
		if(day >= 21){
			nearestTensDay.set(Calendar.DATE, 21);
		}else if(day >= 11){
			nearestTensDay.set(Calendar.DATE, 11);
		}else{
			nearestTensDay.set(Calendar.DATE, 1);
		}
		return nearestTensDay.getTime();
	}
	
	/**
	 * 获得未来最近的旬8日日期（储备糖用）
	 * @param Calendar date
	 * @return Date
	 */
	public static Date getNearestTensEightDay(Calendar date){
		Calendar nearestTensDay = date;
		int day = date.get(Calendar.DATE);
		if(day >= 28){
			nearestTensDay.set(Calendar.DATE, 28);
		}else if(day >= 18){
			nearestTensDay.set(Calendar.DATE, 18);
		}else if(day >= 8){
			nearestTensDay.set(Calendar.DATE, 8);
		}else{
			nearestTensDay.add(Calendar.MONTH, -1);
			nearestTensDay.set(Calendar.DATE, 28);
		}
		return nearestTensDay.getTime();
	}
	
	/**
	 * 获得过去最近的上报日期（储备糖用）
	 * @param Calendar date
	 * @return Date
	 */
	public static Date getLastestTensEightDay(Calendar date){
		Calendar nearestTensDay = date;
		int day = date.get(Calendar.DATE);
		if(day >= 19){
			nearestTensDay.set(Calendar.DATE, 18);
		}else if(day >= 9){
			nearestTensDay.set(Calendar.DATE, 8);
		}else{
			nearestTensDay.add(Calendar.MONTH, -1);
			nearestTensDay.set(Calendar.DATE, 28);
		}
		return nearestTensDay.getTime();
	}
	
    /**
     * 将字符串转换为日期
     * @param dateStr 日期形式的字符串
     * @return 日期
     */
    public static Date parseDate(String dateStr){
		return parseDate(dateStr,DATE);
	}
    
    /**
     * 将字符串转换为日期
     * @param dateStr 日期形式的字符串
     * @param pattern 日期类型
     * @return 日期
     */
    public static Date parseDate(String dateStr,String pattern){
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);			
			try {
				return sdf.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				return null;
			}		
	}
    
	/**
	 * 判断是否为每旬的第一天
	 * @param date 日期
	 * @return boolean
	 */
	public static boolean isTendaysTime(Date date){				
		int year=0;
		int month=0;
		int day=0;
		int nowyear=0;
		int nowmonth=0;
		int nowday=0;
		
		Calendar cd1 = Calendar.getInstance();
		Calendar cd2 = Calendar.getInstance();
		cd2.setTime(date);		
		year=cd2.get(Calendar.YEAR);
		month=cd2.get(Calendar.MONTH)+1;
		day=cd2.get(Calendar.DAY_OF_MONTH);
		
		nowyear=cd1.get(Calendar.YEAR);
		nowmonth=cd1.get(Calendar.MONTH)+1;
		nowday=cd1.get(Calendar.DAY_OF_MONTH);
		
		if(year<nowyear){
			return true;
		}
		else if(year==nowyear){
			if(month>nowmonth){
				return false;
			}
			else if(month<nowmonth){
				return true;
			}
			else if(month==nowmonth){    //当前月
				if(day<=1&&day<=10){             //上旬
					if(nowday>=9){
						return true;
					}
				}
				else if(day>=11&&day<=20){        //中旬
					if(nowday>=19){
						return true;
					}
				}
				else if(day>20){
					if(month==2){
						if(nowday>=25){
							return true;
						}
					}
					else{
						if(nowday>=29){
							return true;
						}
					}					
				}
			}
		}
		return false;
	}
	
	/**
	 * @function 获取四季向量
	 * @return 向量
	 */	
	@SuppressWarnings("unchecked")
	public static Vector getSeasons(){
		Vector seasons = new Vector(4);
		for(int i=1;i<=4;i++){
			seasons.add(new Integer(i));
		}
		return seasons;
	}
	
	/**
	 * 获取离ca最近的一个旬开始日期
	 * @param ca 日期
	 * @return 日期
	 */
	public static Date getLastTenDay(Calendar ca){
		Calendar lastCa = Calendar.getInstance();
		lastCa = ca;
		if(ca.get(Calendar.DATE)==1){
			lastCa.add(Calendar.MONTH, -1);
			lastCa.set(Calendar.DATE, 21);
		}else{
			lastCa.add(Calendar.DATE, -10);
		}
		return lastCa.getTime();
	}
	
	/**
	 * @param rptDate 日期
	 * @return boolean
	 */
	public static boolean  isAddableDay(Calendar rptDate){
		Calendar nowDate=Calendar.getInstance();
		Calendar endDate=Calendar.getInstance();
		Calendar endDate1=Calendar.getInstance();
		if(endDate.get(Calendar.DAY_OF_MONTH)==31){
			endDate.add(Calendar.DAY_OF_MONTH, -11);
			endDate1.add(Calendar.DAY_OF_MONTH, -21);
		}else{
			endDate.add(Calendar.DAY_OF_MONTH, -9);
			endDate1.add(Calendar.DAY_OF_MONTH, -18);
		}
//		Calendar addableDay=Calendar.getInstance();
//		Date date3=DateUtils.getNearestTensDay(endDate);
//		addableDay.setTime(date3);
		String date=DateUtil.formatDate(DateUtil.getNearestTensDay(rptDate));
		String date1=DateUtil.formatDate(DateUtil.getNearestTensDay(nowDate));
		String date2=DateUtil.formatDate(DateUtil.getNearestTensDay(endDate));
		String date3=DateUtil.formatDate(DateUtil.getNearestTensDay(endDate1));
		
		return date.equals(date1)||date.equals(date2)||date.equals(date3);
		//rptDate.before(addableDay);
	}
	
	/**
	 * 判断rptDate是否可用
	 * @param rptDate 日期
	 * @return boolean
	 */
	public static boolean isInputAble(Calendar rptDate){
		Calendar nowDate = Calendar.getInstance();
		Calendar endDate = rptDate;
		if((endDate.get(Calendar.DAY_OF_WEEK)==5)||(endDate.get(Calendar.DAY_OF_WEEK)==6)){//周四or周五
			endDate.add(Calendar.DATE, 4);
		}else if(endDate.get(Calendar.DAY_OF_WEEK)==7){//周六
			endDate.add(Calendar.DATE, 3);
		}else{//周日,一,二,三
			endDate.add(Calendar.DATE, 2);
		}
		Date now = nowDate.getTime();
		Date end = endDate.getTime();
		if(parseDate(formatDate(now,"yyyy-MM-dd")).after(parseDate(formatDate(end,"yyyy-MM-dd")))){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean isSameMonth(Date firstDate, Date secondDate){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
			cal1.setTime(firstDate);
			cal2.setTime(secondDate);
			
		if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)){
			if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
				return true;
			}
		}
		return false;
	}
	
	public static long getTimtstamp(){
		Date date = new Date();
		return date.getTime();
	}
	
	/**
	 * amount为正数时，返回date的amount天后的日期
	 * amount为负数时，返回date的amount天前的日期
	 * @param date
	 * @param amount
	 * @return
	 */
	public static Date add(Date date, int amount){
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		cd.add(Calendar.DATE, amount);
		return cd.getTime();
	}
	
}
