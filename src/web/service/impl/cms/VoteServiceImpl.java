package web.service.impl.cms;

import java.util.Date;
import java.util.List;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.cms.VoteDao;
import com.lehecai.admin.web.domain.cms.Vote;
import com.lehecai.admin.web.service.cms.VoteService;

public class VoteServiceImpl implements VoteService {
	/* (non-Javadoc)
	 * @see com.lehecai.admin.web.service.impl.VoteService#add()
	 */
	private VoteDao voteDao;
	
	public void manage(Vote vote){
		voteDao.merge(vote);
	}

	public VoteDao getVoteDao() {
		return voteDao;
	}

	public void setVoteDao(VoteDao voteDao) {
		this.voteDao = voteDao;
	}

	@Override
	public List<Vote> list(String title,Integer voteTypeId,String valid,Date fromCreateDate,Date toCreateDate,Date fromBeginDate,Date toBeginDate,Date fromEndDate,Date toEndDate, PageBean pageBean) {
		// TODO Auto-generated method stub
		return voteDao.list(title, voteTypeId, valid, fromCreateDate, toCreateDate, fromBeginDate, toBeginDate, fromEndDate, toEndDate, pageBean);
	}

	@Override
	public Vote get(Long ID) {
		// TODO Auto-generated method stub
		return voteDao.get(ID);
	}

	@Override
	public void del(Vote vote) {
		// TODO Auto-generated method stub
		voteDao.del(vote);
	}

	@Override
	public PageBean getPageBean(String title,Integer voteTypeId,String valid,Date fromCreateDate,Date toCreateDate,Date fromBeginDate,Date toBeginDate,Date fromEndDate,Date toEndDate, PageBean pageBean) {
		// TODO Auto-generated method stub
		return voteDao.getPageBean(title, voteTypeId, valid, fromCreateDate, toCreateDate, fromBeginDate, toBeginDate, fromEndDate, toEndDate, pageBean);
	}
}
