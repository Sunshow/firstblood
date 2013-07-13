package web.action.cms;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.TodayRecommendedItem;
import com.lehecai.admin.web.service.cms.TodayRecommendedService;
import com.lehecai.admin.web.utils.DateUtil;
import com.lehecai.core.type.cooperator.Cooperator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class TodayRecommendedAction extends BaseAction{
	private static final long serialVersionUID = 2436161530465382824L;

	private TodayRecommendedService todayRecommendedService;
	private TodayRecommendedItem todayRecommendedItem;
	
	private List<TodayRecommendedItem> todayRecommendedItemList;

    protected static String JSON_PATH = "todayrecommended/";
    protected static String JSON_FILE = "ItemList";
    protected static String JSON_FILE_SUFFIX = ".json";
	private String staticDir;

    private Cooperator cooperator;

    protected String getTodayRecommendedFilename() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String path = request.getSession().getServletContext().getRealPath("/")  + staticDir + JSON_PATH;
        String filename = JSON_FILE;
        if (cooperator != null) {
            filename += "_" + StringUtils.uncapitalize(cooperator.getName());
        }
        filename += JSON_FILE_SUFFIX;

        return path + filename;
    }

    protected String getTodayRecommendedForwardUrl() {
        return this.getContextURI();
    }

	public String handle() {
		logger.info("进入查询推荐项");
		todayRecommendedItemList = todayRecommendedService.itemList(this.getTodayRecommendedFilename());
		return "list";
	}
	
	/**
	 * 添加修改推荐项
	 * @return
	 */
	public String itemManage() {
		logger.info("进入添加修改推荐项信息");
		if (todayRecommendedItem != null) {
			todayRecommendedItemList = todayRecommendedService.itemList(this.getTodayRecommendedFilename());
			if (todayRecommendedItemList == null) {
				todayRecommendedItemList = new ArrayList<TodayRecommendedItem>();
			}
			//修改
			if (todayRecommendedItem.getId() != null && !todayRecommendedItem.getId().isEmpty()) {
				logger.info("修改推荐项信息");
				for(int i = 0; i< todayRecommendedItemList.size(); i++) {
					TodayRecommendedItem item = todayRecommendedItemList.get(i);
					if (todayRecommendedItem.getId().equals(item.getId())) {
						item = todayRecommendedItem;
						
						todayRecommendedItemList.remove(i);
						todayRecommendedItemList.add(i,item);
						
						todayRecommendedService.itemManage(todayRecommendedItemList, this.getTodayRecommendedFilename());
						
						super.setForwardUrl(this.getTodayRecommendedForwardUrl());
						return "success";
					}
				}
			//添加
			} else {
				logger.info("添加推荐项信息");
				todayRecommendedItem.setId(String.valueOf(DateUtil.getTimtstamp()));
				todayRecommendedItemList.add(todayRecommendedItem);
				todayRecommendedService.itemManage(todayRecommendedItemList, this.getTodayRecommendedFilename());
			}
		}
		super.setForwardUrl(this.getTodayRecommendedForwardUrl());
		logger.info("添加修改推荐项信息结束");
		return "success";
	}
	
	
	/**
	 * 查看推荐项
	 * @return
	 */
	public String itemView() {
		logger.info("进入查看推荐项");
		if (todayRecommendedItem != null) {
			todayRecommendedItemList = todayRecommendedService.itemList(this.getTodayRecommendedFilename());
			if (todayRecommendedItemList == null) {
				todayRecommendedItemList = new ArrayList<TodayRecommendedItem>();
			}
			if (todayRecommendedItem.getId() != null && !todayRecommendedItem.getId().isEmpty()) {
                for (TodayRecommendedItem item : todayRecommendedItemList) {
					if (todayRecommendedItem.getId().equals(item.getId())) {
						todayRecommendedItem = item;
                        break;
					}
				}
			}
		}
		logger.info("查看推荐项结束");
		return "view";
	}
	
	/**
	 * 删除推荐项
	 * @return
	 */
	public String itemDel() {
		logger.info("进入删除推荐项");
		if (todayRecommendedItem != null) {
			if (todayRecommendedItem.getId() != null) {
				todayRecommendedItemList = todayRecommendedService.itemList(this.getTodayRecommendedFilename());
				for(int i = 0;i<todayRecommendedItemList.size();i++) {
					TodayRecommendedItem item = (TodayRecommendedItem)todayRecommendedItemList.get(i);
					if (todayRecommendedItem.getId().equals(item.getId())) {
						todayRecommendedItemList.remove(i);
					}
				}
				todayRecommendedService.itemManage(todayRecommendedItemList, this.getTodayRecommendedFilename());
			}
		}
		logger.info("删除推荐项结束");
		super.setForwardUrl(this.getTodayRecommendedForwardUrl());
		return "success";
	}
	
	/**
	 * 转向添加/修改推荐项
	 * @return
	 */
	public String itemInput() {
		logger.info("进入输入推荐项信息");
		if (todayRecommendedItem != null) {
			if (todayRecommendedItem.getId() != null) {
				todayRecommendedItemList = todayRecommendedService.itemList(this.getTodayRecommendedFilename());
                for (TodayRecommendedItem item : todayRecommendedItemList) {
					if (todayRecommendedItem.getId().equals(item.getId())) {
						todayRecommendedItem = item;
                        break;
					}
				}
			}
		} else {
            todayRecommendedItem = new TodayRecommendedItem();
        }
		return "itemInput";
	}

	public TodayRecommendedService getTodayRecommendedService() {
		return todayRecommendedService;
	}

	public void setTodayRecommendedService(
			TodayRecommendedService todayRecommendedService) {
		this.todayRecommendedService = todayRecommendedService;
	}

	public TodayRecommendedItem getTodayRecommendedItem() {
		return todayRecommendedItem;
	}

	public void setTodayRecommendedItem(TodayRecommendedItem todayRecommendedItem) {
		this.todayRecommendedItem = todayRecommendedItem;
	}

	public List<TodayRecommendedItem> getTodayRecommendedItemList() {
		return todayRecommendedItemList;
	}

	public void setTodayRecommendedItemList(
			List<TodayRecommendedItem> todayRecommendedItemList) {
		this.todayRecommendedItemList = todayRecommendedItemList;
	}

	public String getStaticDir() {
		return staticDir;
	}

	public void setStaticDir(String staticDir) {
		this.staticDir = staticDir;
	}

    public void setCooperatorId(Integer cooperatorId) {
        if (cooperatorId != null) {
            this.cooperator = Cooperator.getItem(cooperatorId);
        }
    }
}
