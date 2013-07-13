package web.multiconfirm.confirm;

import java.util.HashMap;
import java.util.Map;

public class MulticonfirmConfirmFactory {
	private Map<String, AbstractMulticonfirmConfirm> factoryMap = new HashMap<String, AbstractMulticonfirmConfirm>();
	
	public AbstractMulticonfirmConfirm getHandler(String key) {
		return factoryMap.get(key);
	}

	public Map<String, AbstractMulticonfirmConfirm> getFactoryMap() {
		return factoryMap;
	}

	public void setFactoryMap(Map<String, AbstractMulticonfirmConfirm> factoryMap) {
		this.factoryMap = factoryMap;
	}

}
