/**
 * 
 */
package web.service.lottery;

import net.sf.json.JSONObject;

/**
 * 手动开奖
 * @author qatang
 *
 */
public interface ManuallyDrawService {
	public JSONObject draw(String planId);
}
