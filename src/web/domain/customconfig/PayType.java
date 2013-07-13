/**
 * 
 */
package web.domain.customconfig;

import java.io.Serializable;

/**
 * 支付类型
 * @author chirowong
 *
 */
public class PayType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5211092492535056846L;
	private Integer id;//编码
	private String name;//支付类型名称
	private boolean valid;//是否有效
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
