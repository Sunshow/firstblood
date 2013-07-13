package web.dao.impl.business;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.dao.business.SmsMailModelDao;
import com.lehecai.admin.web.domain.business.SmsMailModel;
import com.lehecai.admin.web.enums.ModelType;
import com.lehecai.admin.web.enums.TextType;

public class SmsMailModelDaoImpl extends HibernateDaoSupport implements SmsMailModelDao {

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String title, final String content, final Integer modelType, final Integer textTypeId,
			final Date createTimeFrom, final Date createTimeTo, final Date updateTimeFrom, final Date updateTimeTo, final PageBean pageBean) {
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from SmsMailModel u where 1 = 1");
						if (content != null && !"".equals(content)) {
							hql.append(" and u.content = :content");
						}
						if (modelType != null && modelType != 0 && modelType != -1) {
							hql.append(" and u.type = :modelType");
						}
						if (textTypeId != null && textTypeId != 0 && textTypeId != -1) {
							hql.append(" and u.textType = :textType");
						}
						if (title != null && !"".equals(title)) {
							hql.append(" and u.title like :title");
						}
						if (createTimeFrom != null) {
							hql.append(" and u.createTime >= :createTimeForm");
						}
						if (createTimeTo != null) {
							hql.append(" and u.createTime < :createTimeTo");
						}
						if (updateTimeFrom != null) {
							hql.append(" and u.updateTime >= :updateTimeForm");
						}
						if (updateTimeTo != null) {
							hql.append(" and u.updateTime < :updateTimeTo");
						}
						
						Query query = session.createQuery(hql.toString());
								
						if (content != null && !"".equals(content)) {
							query.setParameter("content", content);
						}
						if (modelType != null && modelType != 0 && modelType != -1) {
							query.setParameter("modelType", ModelType.getItem(modelType));
						}
						if (textTypeId != null && textTypeId != 0 && textTypeId != -1) {
							query.setParameter("textType", TextType.getItem(textTypeId));
						}
						if (title != null && !"".equals(title)) {
							query.setParameter("title", "%" + title + "%");
						}
						if (createTimeFrom != null) {
							query.setParameter("createTimeForm", createTimeFrom);
						}
						if (createTimeTo != null) {
							query.setParameter("createTimeTo", createTimeTo);
						}
						if (updateTimeFrom != null) {
							query.setParameter("updateTimeForm", updateTimeFrom);
						}
						if (updateTimeTo != null) {
							query.setParameter("updateTimeTo", updateTimeTo);
						}
						if(pageBean.isPageFlag()){
							int totalCount = ((Long)query.iterate().next()).intValue();
							pageBean.setCount(totalCount);
							int pageCount = 0;//页数
							if(pageBean.getPageSize() != 0) {
					            pageCount = totalCount / pageBean.getPageSize();
					            if(totalCount % pageBean.getPageSize() != 0) {
					                pageCount ++;
					            }
					        }
							pageBean.setPageCount(pageCount);
						}
						return pageBean;
					}
				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SmsMailModel> list(final String title, final String content, final Integer modelType, final Integer textTypeId,
			final Date createTimeFrom, final Date createTimeTo, final Date updateTimeFrom, final Date updateTimeTo, final PageBean pageBean) {
		return (List<SmsMailModel>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
						StringBuffer hql = new StringBuffer("from SmsMailModel u where 1 = 1");
						if (content != null && !"".equals(content)) {
							hql.append(" and u.content = :content");
						}
						if (modelType != null && modelType != 0 && modelType != -1) {
							hql.append(" and u.type = :modelType");
						}
						if (textTypeId != null && textTypeId != 0 && textTypeId != -1) {
							hql.append(" and u.textType = :textType");
						}
						if (title != null && !"".equals(title)) {
							hql.append(" and u.title like :title");
						}
						if (createTimeFrom != null) {
							hql.append(" and u.createTime >= :createTimeForm");
						}
						if (createTimeTo != null) {
							hql.append(" and u.createTime < :createTimeTo");
						}
						if (updateTimeFrom != null) {
							hql.append(" and u.updateTime >= :updateTimeForm");
						}
						if (updateTimeTo != null) {
							hql.append(" and u.updateTime < :updateTimeTo");
						}
						
						Query query = session.createQuery(hql.toString());
								
						if (content != null && !"".equals(content)) {
							query.setParameter("content", content);
						}
						if (modelType != null && modelType != 0 && modelType != -1) {
							query.setParameter("modelType", ModelType.getItem(modelType));
						}
						if (textTypeId != null && textTypeId != 0 && textTypeId != -1) {
							query.setParameter("textType", TextType.getItem(textTypeId));
						}
						if (title != null && !"".equals(title)) {
							query.setParameter("title", "%" + title + "%");
						}
						if (createTimeFrom != null) {
							query.setParameter("createTimeForm", createTimeFrom);
						}
						if (createTimeTo != null) {
							query.setParameter("createTimeTo", createTimeTo);
						}
						if (updateTimeFrom != null) {
							query.setParameter("updateTimeForm", updateTimeFrom);
						}
						if (updateTimeTo != null) {
							query.setParameter("updateTimeTo", updateTimeTo);
						}
						if(pageBean != null && pageBean.isPageFlag()){
							if(pageBean.getPageSize() != 0){
								query.setFirstResult((pageBean.getPage() - 1) * pageBean.getPageSize());
								query.setMaxResults(pageBean.getPageSize());
							}
						}
						return query.list();
					}
				});
	}

	@Override
	public SmsMailModel merge(SmsMailModel smsMailModel) {
		return (SmsMailModel)getHibernateTemplate().merge(smsMailModel);
	}

	@Override
	public SmsMailModel get(Long id) {
		return (SmsMailModel) getHibernateTemplate().get(SmsMailModel.class, id);
	}

	@Override
	public void del(SmsMailModel smsMailModel) {
		getHibernateTemplate().delete(smsMailModel);		
	}

}
