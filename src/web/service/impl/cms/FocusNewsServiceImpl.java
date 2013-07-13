package web.service.impl.cms;

import com.lehecai.admin.web.domain.cms.FocusLine;
import com.lehecai.admin.web.domain.cms.FocusNews;
import com.lehecai.admin.web.service.cms.FocusNewsService;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FocusNewsServiceImpl implements FocusNewsService {
	
	@Override
	public List<FocusLine> focusLineList(String filename) {
		List<FocusLine> list = new ArrayList<FocusLine>();
		
		if (!CoreFileUtils.isExist(filename)) {
			CoreFileUtils.createFile(filename, "", CharsetConstant.CHARSET_UTF8);
		}
		
		File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			String jsonString = "";
			while ((tempString = reader.readLine()) != null) {
				jsonString += tempString;
			}
			reader.close();
			if (StringUtils.isNotEmpty(jsonString)) {
				list = getListByJson(jsonString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<FocusLine> getListByJson(String jsonString){
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		JSONObject tempJsonObject;
		Object pojoValue;
		List<FocusLine> list = new ArrayList<FocusLine>();
		for(int i = 0; i < jsonArray.size(); i++){
			tempJsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(tempJsonObject,FocusLine.class);
			
			FocusLine fl = (FocusLine)pojoValue;
			JSONArray ja = JSONArray.fromObject(fl.getFocusNews());
			fl.setFocusNews(new ArrayList());
			JSONObject jo;
			Object newsObject;
			for(int j =0; j < ja.size(); j++){
				jo = ja.getJSONObject(j);
				newsObject = JSONObject.toBean(jo, FocusNews.class);
				FocusNews fn = (FocusNews)newsObject;
				fl.getFocusNews().add(fn);
			}
			list.add(fl);
		}
		return list;
	}
	
	@Override
	public void lineManage(List<FocusLine> lineList, String filename) {
		try{
			try {//排序
				Collections.sort(lineList);
			} catch (Exception e) {}
			JSONArray json = JSONArray.fromObject(lineList);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("data",json);
			
			CoreFileUtils.createFile(filename, jsonObject.toString(), CharsetConstant.CHARSET_UTF8);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
