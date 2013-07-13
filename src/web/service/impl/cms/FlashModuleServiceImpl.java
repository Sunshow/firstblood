package web.service.impl.cms;

import com.lehecai.admin.web.domain.cms.FlashModule;
import com.lehecai.admin.web.service.cms.FlashModuleService;
import com.lehecai.admin.web.utils.JsonUtil;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlashModuleServiceImpl implements FlashModuleService {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * 读取数据
	 */
	@Override
	public List<FlashModule> moduleList(String filename) {
		List<FlashModule> list = new ArrayList<FlashModule>();

        if (!CoreFileUtils.isExist(filename)) {
            CoreFileUtils.createFile(filename, "", CharsetConstant.CHARSET_UTF8);
        }

		File categoryFile = new File(filename);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(categoryFile));
			String tempString = null;
			String jsonString = "";
			while((tempString = reader.readLine()) != null){
				jsonString += tempString;
			}
			reader.close();
			
			if (StringUtils.isNotEmpty(jsonString)) {
				list = getListByJson(jsonString);
			}
		} catch(Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
            logger.error("读取文件内容解析数据出错", e);
		}
		return orderViewList(list);
	}
	
	@SuppressWarnings("unchecked")
	private List<FlashModule> getListByJson(String jsonString) throws Exception{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		return JsonUtil.getList4Json(jsonArray.toString(), FlashModule.class);
	}
	
	/**
	 * 添加数据
	 */
	@Override
	public void manage(List<FlashModule> flashModule, String filename) {
		try{
			JSONArray json = JSONArray.fromObject(orderViewList(flashModule));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("data", json);
            CoreFileUtils.createFile(filename, jsonObject.toString(), CharsetConstant.CHARSET_UTF8);
        } catch(Exception e){
            logger.error("写入文件出错", e);
        }
	}

	private List<FlashModule> orderViewList(List<FlashModule> flashModuleList){
		
		if(flashModuleList == null)
			return null;

		List<FlashModule> newList = new ArrayList<FlashModule>();
		try {
			FlashModule[] array = flashModuleList.toArray(new FlashModule[flashModuleList.size()]);
			for (int i = 0; i < array.length; i++) {
				for (int j = i+1; j < array.length; j++) {
					if (array[i].getOrderView() > array[j].getOrderView()) {
						FlashModule temp = array[i];
						array[i] = array[j];
						array[j] = temp;
					}
				}
			}
			for (int i = 0; i < array.length; i++) {
				newList.add(array[i]);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return newList;
	}
}

