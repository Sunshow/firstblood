package web.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsUtil {
	 public static boolean checkPhoneNo(String phoneNo){ 
		 String regexNum = "[0-9]{11}";
		 Pattern   pnum   =   Pattern.compile(regexNum);  
	     Matcher   mnum   =   pnum.matcher(phoneNo); 
	     if (!mnum.find()) {
	    	 return false;
	     }
	     String regex = "(?:1[3458]\\d)-?\\d{5}(\\d{3}|\\*{3})";  
	     Pattern p = Pattern.compile(regex);  
	     Matcher m = p.matcher(phoneNo);  
	     return m.find();  
	 }
}
