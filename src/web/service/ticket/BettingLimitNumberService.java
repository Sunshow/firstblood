package web.service.ticket;

import java.util.Map;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.core.exception.ApiRemoteCallFailedException;
import com.lehecai.core.lottery.ticket.BettingLimitNumber;

public interface BettingLimitNumberService {
	
	/**
	 * 查询
	 * @param bettingLimitNumber
	 * @param pageBean
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	Map<String, Object> getResult(BettingLimitNumber bettingLimitNumber, PageBean pageBean) throws ApiRemoteCallFailedException ;
	
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	BettingLimitNumber get(Integer id) throws ApiRemoteCallFailedException ;
	
	/**
	 * 新增
	 * @param bettingLimitNumber
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean add(BettingLimitNumber bettingLimitNumber) throws ApiRemoteCallFailedException ;
	
	/**
	 * 更新
	 * @param bettingLimitNumber
	 * @return
	 * @throws ApiRemoteCallFailedException
	 */
	boolean update(BettingLimitNumber bettingLimitNumber) throws ApiRemoteCallFailedException ;
	
	
}