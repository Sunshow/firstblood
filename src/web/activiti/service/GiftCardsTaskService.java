/**
 * 
 */
package web.activiti.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.lehecai.admin.web.activiti.entity.GiftCardsTask;

/**
 * @author chirowong
 *
 */
public class GiftCardsTaskService {
	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public GiftCardsTask create(GiftCardsTask giftCardsTask) {
		entityManager.persist(giftCardsTask);
		entityManager.flush();
		return giftCardsTask;
	}
	@Transactional
	public void merge(GiftCardsTask giftCardsTask) {
		entityManager.merge(giftCardsTask);
	}
	
	public GiftCardsTask get(Long id) {
		return entityManager.find(GiftCardsTask.class, id);
	}
	
	public GiftCardsTask getByProcessId(String processId) {
		String sql = "select t from GiftCardsTask t where t.processId=:processId";
		return (GiftCardsTask)entityManager.createQuery(sql).setParameter("processId", processId).getSingleResult();
	}
}
