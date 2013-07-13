package com.bjgl.web.utils;

import java.util.Iterator;
import java.util.Map;


public class StringUtil {
	
    public static String[] split(String str, char delim) {
        
        if(str == null) return null;
        if(str.trim().equals("")) return new String[0];
        
        int count = 1;
        char c[] = str.toCharArray();
        for(int i = 0; i < c.length; i++) {
            if(c[i] == delim) {
                count++;
            }
        }

        String s[] = new String[count];
        int index = 0;
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < c.length; i++) {
            if(c[i] == delim) {
                s[index] = sb.toString();
                sb = new StringBuffer();
                index ++;
            } else {
                sb.append(c[i]);
            }
        }
        s[index] = sb.toString();
        return s;
    }
    @SuppressWarnings("unchecked")
	public final static String getStringByActionHashMap(Map map) {
		StringBuffer sb = new StringBuffer();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()){
			Object key = it.next();
			Object value = map.get(key);
			if(value instanceof String[]){
				String [] values = (String[])value;
				
				StringBuffer subSb = new StringBuffer();
				if(values!=null){
					for(int i=0;i<values.length;i++){
						subSb.append(values[i]).append("|");
					}
					if(subSb.length()>0){
						subSb.deleteCharAt(subSb.length()-1);
					}
				}
				sb.append(key.toString()).append("=").append(subSb.toString()).append("&");
			}

		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
    public static int getByteLength(String str){
		int lng = 0;
		char[] tempChar = str.toCharArray();
		for(int k = 0; k < tempChar.length;k ++ ){
			String s = String.valueOf(tempChar[k]);
			byte[] b = s.getBytes();
			lng += b.length;
		}
		return lng;
	}
    /**
     * 判断字符串是否为整数
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
    	if(str==null){
    		return false;
    	}
    	if(str.matches("\\d*")){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * 判断是否包含有全角
     */
    public static boolean hasAngle (String str) {
    	if (str == null || str.equals("")) {
    		return false;
    	}
    	
    	char[] array = str.toCharArray();
    	if(str.getBytes().length != array.length){
    		return true;
		}
    	return false;
    }
    
    /**
     * 全角转半角
     */
    public static String toHalfAngle (String str) {
    	if (str == null || str.equals("")) {
    		return null;
    	}
    	
    	char[] array = str.toCharArray();
    	for (int i = 0; i < array.length; i++) {
			if (array[i] == '\u3000') {	//十六进制，相当于十进制的12288，即全角空格
				array[i] = ' ';
			} else if (array[i] > '\uFF00' && array[i] < '\uFF5F') { //其他字符半角与全角均相差65248
				array[i] = (char) (array[i] - 65248);
			}
		}
    	
    	return new String(array);
    }
    
    /**
     * 半角转全角
     * @param input
     * @return
     */
    public static String toAngle(String str) {
    	if (str == null || str.equals("")) {
    		return null;
    	}
    	
    	char[] array = str.toCharArray();   
  	  	for (int i = 0; i < array.length; i++) {   
  	  		if (array[i] == 32) {   
  	  			array[i] = (char) 12288;
  	  		} else if (array[i] < 127){
  	  			array[i] = (char) (array[i] + 65248);
  	  		}
  	  	}
  	  	return new String(array);
    }
}