package web.multiconfirm.param;

import java.util.HashMap;
import java.util.Map;

public class MulticonfirmParamFactory {
	private Map<String, IMulticonfirmParam> factoryMap = new HashMap<String, IMulticonfirmParam>();
	
	public IMulticonfirmParam getHandler(String key) {
		return factoryMap.get(key);
	}

	public Map<String, IMulticonfirmParam> getFactoryMap() {
		return factoryMap;
	}

	public void setFactoryMap(Map<String, IMulticonfirmParam> factoryMap) {
		this.factoryMap = factoryMap;
	}

}
