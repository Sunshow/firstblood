package web.service.impl.cms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.domain.cms.FlashCategory;
import com.lehecai.admin.web.service.cms.FlashCategoryService;
import com.lehecai.admin.web.utils.JsonUtil;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;

public class FlashCategoryServiceImpl implements FlashCategoryService {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * 读取数据
	 */
	@Override
	public List<FlashCategory> categoryList(String filename) {
		List<FlashCategory> list = new ArrayList<FlashCategory>();

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
				jsonString+=tempString;
			}
			reader.close();
			
			if (StringUtils.isNotEmpty(jsonString)) {
				list = getListByJson(jsonString);
			}
		} catch(Exception e){
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    logger.error(e1.getMessage(), e1);
                }
            }
            logger.error("读取文件内容解析数据出错", e);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private List<FlashCategory> getListByJson(String jsonString) throws Exception{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		return JsonUtil.getList4Json(jsonArray.toString(), FlashCategory.class);
	}
	
	/**
	 * 添加数据
	 */
	@Override
	public void manage(List<FlashCategory> flashCategory, String filename) {
		try {
			JSONArray json = JSONArray.fromObject(flashCategory);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("data", json);
            CoreFileUtils.createFile(filename, jsonObject.toString(), CharsetConstant.CHARSET_UTF8);
		} catch(Exception e){
            logger.error("写入文件出错", e);
		}
	}
	
	@Override
	public void delFile(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
	}
}
