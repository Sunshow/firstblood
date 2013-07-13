package web.service.impl.cms;

import com.lehecai.admin.web.domain.cms.TodayRecommendedItem;
import com.lehecai.admin.web.service.cms.TodayRecommendedService;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 今日推荐业务逻辑层实现类
 * @author yanweijie
 *
 */
public class TodayRecommendedServiceImpl implements TodayRecommendedService {

	protected transient final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	/**
	 * 查询所有的今日推荐
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodayRecommendedItem> itemList(String filename) {
        List<TodayRecommendedItem> list = new ArrayList<TodayRecommendedItem>();

        if (!CoreFileUtils.isExist(filename)) {
            CoreFileUtils.createFile(filename, "", CharsetConstant.CHARSET_UTF8);
        }

		File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			String jsonString = "";
			while((tempString = reader.readLine()) != null){
				jsonString += tempString;
			}
			reader.close();
			if (StringUtils.isNotEmpty(jsonString)) {
				JSONObject object = JSONObject.fromObject(jsonString);
				if (object.has("data")) {
					JSONArray array = object.getJSONArray("data");
					list = JsonUtil.getList4Json(array.toString(), TodayRecommendedItem.class);
					list = orderList(list);
				}
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
		return list;
	}

	/**
	 * 排序
	 * @param list
	 * @return
	 */
	private List<TodayRecommendedItem> orderList(List<TodayRecommendedItem> list) {
		if (list == null || list.isEmpty() || list.size() == 1) {
			return list;
		}
		Collections.sort(list, new Comparator<TodayRecommendedItem>() {
            @Override
            public int compare(TodayRecommendedItem item1, TodayRecommendedItem item2) {
                return new Integer(item2.getOrder()).compareTo(new Integer(item1.getOrder()));
            }
        });
		return list;
	}

	@Override
	public void itemManage(List<TodayRecommendedItem> itemList, String filename) {
		try {
			this.orderList(itemList);

			JSONArray json = JSONArray.fromObject(itemList);
			JSONObject object = new JSONObject();
			object.put("data", json);

            CoreFileUtils.createFile(filename, object.toString(), CharsetConstant.CHARSET_UTF8);
		} catch(Exception e) {
            logger.error("写入文件出错", e);
		}
		
	}

}
