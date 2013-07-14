package com.bjgl.web.dao.impl.log;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.bjgl.web.bean.PageBean;
import com.bjgl.web.dao.log.LogDao;
import com.bjgl.web.entity.log.Log;
import com.bjgl.web.enums.LogType;

public class LogDaoImpl extends HibernateDaoSupport implements LogDao {

	@Override
	public Log get(Long ID) {
		// TODO Auto-generated method stub
		return getHibernateTemplate().get(Log.class, ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Log> list(final String userName, final String name, final Date beginDate,
			final Date endDate, final Long logType, final String url, final String actionName, final String params, final String ip, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (List<Log>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("from Log u where 1 = 1");

						if(userName != null && !"".equals(userName)){
							hql.append(" and u.userName like :userName");
						}
						if(name != null && !"".equals(name)){
							hql.append(" and u.name like :name");
						}
						if(beginDate != null){
							hql.append(" and u.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and u.createTime < :endDate");
						}
						if(logType != null && logType != 0){
							hql.append(" and u.logType=:logType");
						}
						if(url != null && !"".equals(url)){
							hql.append(" and u.url like :url");
						}
						if(actionName != null && !"".equals(actionName)){
							hql.append(" and u.actionName like :actionName");
						}
						if(params != null && !"".equals(params)){
							hql.append(" and u.params like :params");
						}
						if(ip != null && !"".equals(ip)){
							hql.append(" and u.remoteIP like :ip");
						}
						hql.append(" order by u.createTime desc");
						Query query = session.createQuery(hql.toString());
								
						if(userName != null && !"".equals(userName)){
							query.setParameter("userName", "%" + userName + "%");
						}
						if(name != null && !"".equals(name)){
							query.setParameter("name", "%" + name + "%");
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
						}
						if(logType != null && logType != 0){
							query.setParameter("logType", LogType.getItem(logType.intValue()));
						}
						if(url != null && !"".equals(url)){
							query.setParameter("url", "%" + url + "%");
						}
						if(actionName != null && !"".equals(actionName)){
							query.setParameter("actionName", "%" + actionName + "%");
						}
						if(params != null && !"".equals(params)){
							query.setParameter("params", "%" + params + "%");
						}
						if(ip != null && !"".equals(ip)){
							query.setParameter("ip", "%" + ip + "%");
						}
			
						if(pageBean.isPagging()){
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
	public void save(Log log) {
		// TODO Auto-generated method stub
		getHibernateTemplate().save(log);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageBean getPageBean(final String userName, final String name, final Date beginDate,
			final Date endDate, final Long logType, final String url, final String actionName, final String params, final String ip, final PageBean pageBean) {
		// TODO Auto-generated method stub
		return (PageBean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						StringBuffer hql = new StringBuffer("select count(u) from Log u where 1 = 1");

						if(userName != null && !"".equals(userName)){
							hql.append(" and u.userName like :userName");
						}
						if(name != null && !"".equals(name)){
							hql.append(" and u.name like :name");
						}
						if(beginDate != null){
							hql.append(" and u.createTime >= :beginDate");
						}
						if(endDate != null){
							hql.append(" and u.createTime < :endDate");
						}
						if(logType != null && logType != 0){
							hql.append(" and u.logType=:logType");
						}
						if(url != null && !"".equals(url)){
							hql.append(" and u.url like :url");
						}
						if(actionName != null && !"".equals(actionName)){
							hql.append(" and u.actionName like :actionName");
						}
						if(params != null && !"".equals(params)){
							hql.append(" and u.params like :params");
						}
						if(ip != null && !"".equals(ip)){
							hql.append(" and u.remoteIP like :ip");
						}
						Query query = session.createQuery(hql.toString());
								
						if(userName != null && !"".equals(userName)){
							query.setParameter("userName", "%" + userName + "%");
						}
						if(name != null && !"".equals(name)){
							query.setParameter("name", "%" + name + "%");
						}
						if(beginDate != null){
							query.setParameter("beginDate", beginDate);
						}
						if(endDate != null){
							query.setParameter("endDate", endDate);
						}
						if(logType != null && logType != 0){
							query.setParameter("logType", LogType.getItem(logType.intValue()));
						}
						if(url != null && !"".equals(url)){
							query.setParameter("url", "%" + url + "%");
						}
						if(actionName != null && !"".equals(actionName)){
							query.setParameter("actionName", "%" + actionName + "%");
						}
						if(params != null && !"".equals(params)){
							query.setParameter("params", "%" + params + "%");
						}
						if(ip != null && !"".equals(ip)){
							query.setParameter("ip", "%" + ip + "%");
						}
						if(pageBean.isPagging()){
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
}
