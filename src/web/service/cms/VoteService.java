package web.service.cms;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.domain.cms.Vote;

public interface VoteService {
	void manage(Vote vote);
	List<Vote> list(String title, Integer voteTypeId, String valid, Date fromCreateDate, Date toCreateDate, Date fromBeginDate, Date toBeginDate, Date fromEndDate, Date toEndDate, PageBean pageBean);
	PageBean getPageBean(String title, Integer voteTypeId, String valid, Date fromCreateDate, Date toCreateDate, Date fromBeginDate, Date toBeginDate, Date fromEndDate, Date toEndDate, PageBean pageBean);
	Vote get(Long ID);
	void del(Vote vote);
}