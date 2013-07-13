package web.action.statics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.statics.StaticFragment;
import com.lehecai.admin.web.domain.statics.StaticFragmentModule;
import com.lehecai.admin.web.domain.statics.StaticFragmentModuleItem;
import com.lehecai.admin.web.enums.ItemType;
import com.lehecai.admin.web.service.statics.StaticFragmentModuleService;
import com.lehecai.admin.web.service.statics.StaticFragmentService;
import com.lehecai.core.YesNoStatus;
import com.lehecai.core.util.CharsetConstant;
import com.lehecai.core.util.CoreFileUtils;

/**
 * 静态碎片action
 * @author yanweijie
 *
 */
public class StaticFragmentAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(StaticFragmentAction.class);
	
	private static final String KEY_DATA = "data";
	
	private StaticFragmentService staticFragmentService;
	private StaticFragmentModuleService staticFragmentModuleService;

	private List<StaticFragment> staticFragmentList;
	private List<StaticFragmentModule> staticFragmentModuleList;
	private List<StaticFragmentModuleItem> staticFragmentModuleItemList;
	
	private StaticFragment staticFragment;
	private String rootDir;
	
	private String fragmentModulePath;
	
	private List<String[]> items;
	
	/**
	 * 查询所有静态碎片
	 * @return
	 */
	public String handle () {
		logger.info("进入查询所有静态碎片");
		staticFragmentList = staticFragmentService.findList();	//查询静态碎片列表
		
		return "list_fragment";
	}
	
	/**
	 * 输入静态碎片信息
	 * @return
	 */
	public String input () {
		logger.info("进入输入静态碎片信息");
		
		staticFragmentModuleList = staticFragmentModuleService.findModuleList();	//查询静态碎片模板列表
		
		if (staticFragment != null && staticFragment.getId() != null) {
			staticFragment = staticFragmentService.get(staticFragment.getId());	//根据静态碎片编码查询静态碎片
		}
		
		return "input_Fragment";
	}
	
	/**
	 * 添加修改静态碎片信息
	 * @return
	 */
	public String manage () {
		logger.info("进入添加修改静态碎片信息");
		if (staticFragment == null) {
			logger.error("静态碎片为空");
			super.setErrorMessage("静态碎片不能为空");
			return "failure";
		}
		if (staticFragment.getFragmentName() == null || staticFragment.getFragmentName().equals("")) {
			logger.error("静态碎片名称为空");
			super.setErrorMessage("静态碎片名称不能为空");
			return "failure";
		}
		if (staticFragment.getModuleId() == null || staticFragment.getModuleId() == 0L) {
			logger.error("静态碎片模板编码为空");
			super.setErrorMessage("静态碎片模板编码不能为空");
			return "failure";
		}
		if (staticFragment.getTargetUrl() == null || staticFragment.getTargetUrl().equals("")) {
			logger.error("静态碎片生成地址为空");
			super.setErrorMessage("静态碎片生成地址不能为空");
			return "failure";
		}
		StaticFragment tempStaticFragment = null;
		if (staticFragment.getId() != null && staticFragment.getId() != 0L) {
			tempStaticFragment = staticFragmentService.get(staticFragment.getId());//根据静态碎片编码查询静态碎片
		}
		String fragmentName = "";
		if (tempStaticFragment != null) {			//修改静态碎片
			if (!staticFragment.getFragmentName().equals(tempStaticFragment.getFragmentName())) {
				fragmentName = staticFragment.getFragmentName();
			}
		} else {									//添加静态碎片
			fragmentName = staticFragment.getFragmentName();
		}
		if (fragmentName != null && !fragmentName.equals("")) {
			tempStaticFragment = staticFragmentService.getByName(fragmentName);
			if (tempStaticFragment != null) {		//检验新添加的或者是有修改的静态名称是否已存在
				logger.error("名称 {} 已经存在!", fragmentName);
				super.setErrorMessage("名称  " + fragmentName + " 已经存在!");
				return "failure";
			}
		}
		staticFragmentService.merge(staticFragment);
		
		super.setForwardUrl("/statics/staticFragment.do");
		return "success";
	}
	
	/**
	 * 生成界面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String viewPage () {
		if (staticFragment == null || (staticFragment.getId() == null || staticFragment.getId() == 0L)) {
			logger.error("碎片编码为空");
			super.setErrorMessage("碎片编码不能为空");
			return "failure";
		}
		staticFragment = staticFragmentService.get(staticFragment.getId());			//根据静态碎片编码查询静态碎片
		
		staticFragmentModuleItemList = staticFragmentModuleService.findItemList(staticFragment.getModuleId());	//根据碎片模板编码查询碎片模板属性列表
		if (staticFragmentModuleItemList == null || staticFragmentModuleItemList.size() == 0) {
			logger.error("{}模板属性为空", staticFragment.getModuleId());
			super.setErrorMessage("请先配置" + staticFragment.getModuleId() + "模板的属性");
			return "failure";
		}
		
		String json = null;
		try {
			logger.info(rootDir + fragmentModulePath + staticFragment.getTargetUrl());
			json = CoreFileUtils.readFile(rootDir + fragmentModulePath + staticFragment.getTargetUrl(), CharsetConstant.CHARSET_UTF8);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (json == null) {
			return "view_page";
		}
		JSONObject jsonObject = JSONObject.fromObject(json);
		if (jsonObject == null || jsonObject.isNullObject()) {
			return "view_page";
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = jsonObject.getJSONArray(KEY_DATA);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (jsonArray == null || jsonArray.size() == 0) {
			return "view_page";
		}
		items = new ArrayList<String[]>();
		for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
			JSONObject object = (JSONObject) iterator.next();
			String[] strObj = new String[staticFragmentModuleItemList.size()];
			for (int i = 0;i < staticFragmentModuleItemList.size(); i ++) {
				String key = staticFragmentModuleItemList.get(i).getItemKey();
				if(object.containsKey(key)){
					strObj[i] = object.getString(key);
				}
			}
			items.add(strObj);
		}
		return "view_page";
	}
	
	/**
	 * 输出json文件
	 * @return
	 */
	public String outputJson () {
		if (items == null || items.size() == 0) {
			logger.error("生成页面数据为空");
			super.setErrorMessage("生成页面数据不能为空");
			return "failure";
		}
		if (staticFragment == null || staticFragment.getId() == null || staticFragment.getId() == 0L) {
			logger.error("碎片编码为空");
			super.setErrorMessage("碎片编码不能为空");
			return "failure";
		}
		staticFragment = staticFragmentService.get(staticFragment.getId());		//根据静态碎片编码查询静态碎片
		
		staticFragmentModuleItemList = staticFragmentModuleService.findItemList(staticFragment.getModuleId());	//根据静态碎片模板编码查询静态碎片模板自定义属性列表
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		
		for (int i = 0; i < items.size(); i++) {
			String[] valList = items.get(i);
			
			for (int j = 0; j < valList.length; j++) {
				if (jsonArray.size() == j) {
					jsonArray.add(new JSONObject());
				}
				JSONObject tempJsonObject = jsonArray.getJSONObject(j);
				if (staticFragmentModuleItemList.get(i).getItemType().getValue() == ItemType.YESNOTYPE.getValue()) {
					tempJsonObject.put(staticFragmentModuleItemList.get(i).getItemKey(), Integer.valueOf(valList[j]));
				} else {
					tempJsonObject.put(staticFragmentModuleItemList.get(i).getItemKey(), valList[j]);
				}
			}
		}
		jsonObject.put(KEY_DATA, jsonArray);
		
		logger.info("rootDir：{}", rootDir);
		String filePath = rootDir + fragmentModulePath + staticFragment.getTargetUrl();
		logger.info("filePath：{}", filePath);
		
		CoreFileUtils.createFile(filePath, jsonObject.toString(), CharsetConstant.CHARSET_UTF8);
		
		super.setForwardUrl("/statics/staticFragment.do?action=viewPage&staticFragment.Id=" + staticFragment.getId());
		return "success";
	}

	public List<ItemType> getItemTypes () {
		return ItemType.list;
	}
	
	public List<YesNoStatus> getYesNoStatus () {
		return YesNoStatus.getItems();
	}
	
	public ItemType getTextItemType () {
		return ItemType.TEXTTYPE;
	}
	
	public ItemType getLinkItemType () {
		return ItemType.LINKTYPE;
	}
	
	public ItemType getYesNoType() {
		return ItemType.YESNOTYPE;
	}
	
	public YesNoStatus getYesStatus() {
		return YesNoStatus.YES;
	}
	
	public YesNoStatus getNoStatus() {
		return YesNoStatus.NO;
	}
	
	public StaticFragmentService getStaticFragmentService() {
		return staticFragmentService;
	}

	public void setStaticFragmentService(StaticFragmentService staticFragmentService) {
		this.staticFragmentService = staticFragmentService;
	}

	public StaticFragmentModuleService getStaticFragmentModuleService() {
		return staticFragmentModuleService;
	}

	public void setStaticFragmentModuleService(
			StaticFragmentModuleService staticFragmentModuleService) {
		this.staticFragmentModuleService = staticFragmentModuleService;
	}

	public List<StaticFragment> getStaticFragmentList() {
		return staticFragmentList;
	}

	public void setStaticFragmentList(List<StaticFragment> staticFragmentList) {
		this.staticFragmentList = staticFragmentList;
	}

	public List<StaticFragmentModule> getStaticFragmentModuleList() {
		return staticFragmentModuleList;
	}

	public void setStaticFragmentModuleList(
			List<StaticFragmentModule> staticFragmentModuleList) {
		this.staticFragmentModuleList = staticFragmentModuleList;
	}

	public List<StaticFragmentModuleItem> getStaticFragmentModuleItemList() {
		return staticFragmentModuleItemList;
	}

	public void setStaticFragmentModuleItemList(
			List<StaticFragmentModuleItem> staticFragmentModuleItemList) {
		this.staticFragmentModuleItemList = staticFragmentModuleItemList;
	}

	public StaticFragment getStaticFragment() {
		return staticFragment;
	}

	public void setStaticFragment(StaticFragment staticFragment) {
		this.staticFragment = staticFragment;
	}

	public List<String[]> getItems() {
		return items;
	}

	public void setItems(List<String[]> items) {
		this.items = items;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public String getFragmentModulePath() {
		return fragmentModulePath;
	}

	public void setFragmentModulePath(String fragmentModulePath) {
		this.fragmentModulePath = fragmentModulePath;
	}

}
