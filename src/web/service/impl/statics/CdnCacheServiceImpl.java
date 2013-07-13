package web.service.impl.statics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.domain.cdn.CdnCacheGroup;
import com.lehecai.admin.web.domain.cdn.CdnCacheItem;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteGroup;
import com.lehecai.admin.web.domain.cdn.CdnWebsiteItem;
import com.lehecai.admin.web.service.statics.CdnCacheService;
import com.lehecai.admin.web.utils.FileUtil;

public class CdnCacheServiceImpl implements CdnCacheService {

	/**
	 * 读取数据
	 */
	@Override
	public List<CdnCacheGroup> cdnCacheGroupList(String jsonPath, String fileName) {
		List<CdnCacheGroup> list = new ArrayList<CdnCacheGroup>();
		
		File categoryFile = new File(path(jsonPath,fileName));
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(categoryFile));
			String tempString = null;
			String jsonString = "";
			while((tempString = reader.readLine()) != null){
				jsonString+=tempString;
			}
			reader.close();
			
			if(jsonString != null && !"".equals(jsonString)){
				list = getCdnCacheListByJson(jsonString);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 读取数据
	 */
	@Override
	public List<CdnWebsiteGroup> cdnWebsiteGroupList(String jsonPath, String fileName) {
		List<CdnWebsiteGroup> list = new ArrayList<CdnWebsiteGroup>();

		File categoryFile = new File(path(jsonPath,fileName));
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(categoryFile));
			String tempString = null;
			String jsonString = "";
			while((tempString = reader.readLine()) != null){
				jsonString+=tempString;
			}
			reader.close();
			
			if(jsonString != null && !"".equals(jsonString)){
				list = getCdnWebsiteListByJson(jsonString);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private List<CdnCacheGroup> getCdnCacheListByJson(String jsonString){
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List<CdnCacheGroup> list = new ArrayList<CdnCacheGroup>();
		for(int i = 0; i < jsonArray.size(); i++){
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject,CdnCacheGroup.class);
			
			CdnCacheGroup fl = (CdnCacheGroup)pojoValue;
			JSONArray ja = JSONArray.fromObject(fl.getItemList());
			fl.setItemList(new ArrayList());
			JSONObject jo;
			Object newsObject;
			for(int j =0; j < ja.size(); j++){
				jo = ja.getJSONObject(j);
				newsObject = JSONObject.toBean(jo, CdnCacheItem.class);
				CdnCacheItem fn = (CdnCacheItem)newsObject;
				fl.getItemList().add(fn);
			}
			list.add(fl);
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	private List<CdnWebsiteGroup> getCdnWebsiteListByJson(String jsonString){
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List<CdnWebsiteGroup> list = new ArrayList<CdnWebsiteGroup>();
		for(int i = 0; i < jsonArray.size(); i++){
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject,CdnWebsiteGroup.class);
			
			CdnWebsiteGroup fl = (CdnWebsiteGroup)pojoValue;
			JSONArray ja = JSONArray.fromObject(fl.getItemList());
			fl.setItemList(new ArrayList());
//			Object newsObject;
			for(int j =0; j < ja.size(); j++){
				JSONObject jo;
				jo = ja.getJSONObject(j);
//				newsObject = JSONObject.toBean(jo, CdnWebsiteItem.class);
//				CdnWebsiteItem fn = (CdnWebsiteItem)newsObject;
				CdnWebsiteItem fn = CdnWebsiteItem.convertFromJSONObject(jo);
				fl.getItemList().add(fn);
			}
			list.add(fl);
		}
		return list;
	}
	
	/**
	 * 添加数据
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void manage(List cdnCacheTarget, String jsonPath, String fileName) {
		try{
			JSONArray json = JSONArray.fromObject(cdnCacheTarget);
			createFile(path(jsonPath,fileName),json.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public String path(String jsonPath,String jsonFile){
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = request.getSession().getServletContext().getRealPath("/");
		
		FileUtil.mkdir(path+jsonPath);
		path+=jsonPath+jsonFile;
		
		File categoryFile = new File(path);
		if(!categoryFile.exists()){
			createFile(path,"");
		}
		return path;
	}
	
	@Override
	public void createFile(String fileName,String content){
		try{
			File file = new File(fileName);
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void delFile(String fileName) {
		HttpServletRequest request = ServletActionContext.getRequest();
		String path = request.getSession().getServletContext().getRealPath("/");
		File file = new File(path+fileName);
		if(file.exists()){
			file.delete();
		}
	}
}
