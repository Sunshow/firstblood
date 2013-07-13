package web.dao.multiconfirm;

import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.multiconfirm.MulticonfirmRecord;
import com.lehecai.admin.web.multiconfirm.MulticonfirmTask;

public interface MulticonfirmRecordDao {

	MulticonfirmRecord get(MulticonfirmTask task, Long userId);

	MulticonfirmRecord manage(MulticonfirmRecord record);

	List<MulticonfirmRecord> getRecordList(MulticonfirmTask task, PageBean pageBean);

	PageBean getRecordPageBean(MulticonfirmTask task, PageBean pageBean);

}