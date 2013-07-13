package web.service.impl.cms;

import com.lehecai.admin.web.domain.cms.FocusCategory;
import com.lehecai.admin.web.service.cms.FocusCategoryService;
import com.lehecai.admin.web.utils.JsonUtil;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FocusCategoryServiceImpl implements FocusCategoryService {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * 读取数据
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<FocusCategory> categoryList(String filename) {
		List<FocusCategory> list = new ArrayList<FocusCategory>();

		if (!CoreFileUtils.isExist(filename)) {
			CoreFileUtils.createFile(filename, "", CharsetConstant.CHARSET_UTF8);
		}
		
		File categoryFile = new File(filename);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(categoryFile));
			String tempString = null;
			String jsonString = "";
			while ((tempString = reader.readLine()) != null) {
                jsonString += tempString;
			}
			reader.close();

			if (StringUtils.isNotEmpty(jsonString)) {
				list = JsonUtil.getList4Json(jsonString, FocusCategory.class);
			}
		} catch (Exception e) {
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
	
	/**
	 * 添加数据
	 */
	@Override
	public void manage(List<FocusCategory> focusCategory, String filename) {
		try {
			JSONArray json = JSONArray.fromObject(focusCategory);
			CoreFileUtils.createFile(filename, json.toString(), CharsetConstant.CHARSET_UTF8);
		} catch (Exception e) {
            logger.error("写入文件出错", e);
		}
	}
	
	@Override
	public void delFile(String fileName) {
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = request.getSession().getServletContext().getRealPath("/");
		File file = new File(path+fileName);
		if (file.exists()){
			file.delete();
		}
	}
}
