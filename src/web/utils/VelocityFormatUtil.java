/**
 * 
 */
package web.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;



/**
 * @author qatang
 *
 */
public class VelocityFormatUtil {
	
	public String floor(Number number) {
		if (number != null) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setRoundingMode(RoundingMode.FLOOR);
			numberFormat.setMaximumFractionDigits(0);
			return numberFormat.format(number);
		}
		return null;
	}

	public String ceil(Number number){
		if (number != null) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setRoundingMode(RoundingMode.CEILING);
			numberFormat.setMaximumFractionDigits(0);
			return numberFormat.format(number);
		}
		return null;
	}
	public static void main(String[] args){
		VelocityFormatUtil velocityFormatUtil = new VelocityFormatUtil();
		System.out.println(velocityFormatUtil.floor(13.94));
	}
}
