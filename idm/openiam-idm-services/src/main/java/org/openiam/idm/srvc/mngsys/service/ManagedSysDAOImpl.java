package org.openiam.idm.srvc.mngsys.service;

// Generated Nov 3, 2008 12:14:44 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.user.dto.UserAttribute;

/**
 * Home object for domain model class ManagedSys.
 * @see org.openiam.idm.srvc.ManagedSys
 * @author Hibernate Tools
 */
public class ManagedSysDAOImpl implements ManagedSysDAO {

	private static final Log log = LogFactory.getLog(ManagedSysDAOImpl.class);

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory session) {
		   this.sessionFactory = session;
	}

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}


	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#add(org.openiam.idm.srvc.mngsys.dto.ManagedSys)
	 */
	public ManagedSys add(ManagedSys transientInstance) {
		log.debug("persisting ManagedSys instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			return transientInstance;
			
		} catch (HibernateException re) {
			log.error("persist failed", re);
			throw re;
		}
	}


	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#remove(org.openiam.idm.srvc.mngsys.dto.ManagedSys)
	 */
	public void remove(ManagedSys persistentInstance) {
		log.debug("deleting ManagedSys instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (HibernateException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#update(org.openiam.idm.srvc.mngsys.dto.ManagedSys)
	 */
	public ManagedSys update(ManagedSys detachedInstance) {
		log.debug("merging ManagedSys instance");
		try {
			ManagedSys result = (ManagedSys) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (HibernateException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#findById(java.lang.String)
	 */
	public ManagedSys findById(java.lang.String id) {
		log.debug("getting ManagedSys instance with id: " + id);
		try {
			ManagedSys instance = (ManagedSys) sessionFactory
					.getCurrentSession().get(ManagedSys.class,
							id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}
	
	public List<ManagedSys> findbyConnectorId(String connectorId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.mngsys.dto.ManagedSys ms " +
				" where ms.connectorId = :conId order by ms.managedSysId asc");
		qry.setString("conId", connectorId);
		List<ManagedSys> results = (List<ManagedSys>)qry.list();
		return results;	
	}
	
	public List<ManagedSys> findbyDomain(String domainId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.mngsys.dto.ManagedSys ms " +
				" where ms.domainId = :domainId order by ms.managedSysId asc");
		qry.setString("domainId", domainId);
		List<ManagedSys> results = (List<ManagedSys>)qry.list();
		return results;			
	}
	
	 public List<ManagedSys> findAllManagedSys() {
			Session session = sessionFactory.getCurrentSession();
			Query qry = session.createQuery("from org.openiam.idm.srvc.mngsys.dto.ManagedSys ms " +
					" order by ms.name asc");
			List<ManagedSys> results = (List<ManagedSys>)qry.list();
			return results;				 
	 }

	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#findByName(java.lang.String)
	 */
	public ManagedSys findByName(String name) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.mngsys.dto.ManagedSys ms " +
				" where ms.name = :name order by ms.name, ms.managedSysId ");
		qry.setString("name", name);
		List<ManagedSys> results = (List<ManagedSys>)qry.list();

		if (results != null) {
			// avoids an exception in the event that there is more than 1 row with the same name
			log.info("ManagedSys resultSet = " + results.size());	
			return results.get(0);
		}
		log.info("No managedSys objects fround.");
		return null;
	
	}

	public ManagedSys findByResource(String resourceId, String status) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session.createQuery("from org.openiam.idm.srvc.mngsys.dto.ManagedSys ms " +
				" where ms.resourceId = :resourceId and  " +
				"		ms.status = :status " +
				" order by ms.name ");
		qry.setString("resourceId", resourceId);
		qry.setString("status",status);
		List<ManagedSys> results = (List<ManagedSys>)qry.list();

		if (CollectionUtils.isNotEmpty(results)) {
			// avoids an exception in the event that there is more than 1 row with the same name
			log.info("ManagedSys resultSet = " + results.size());	
			return results.get(0);
		}
		log.info("No managedSys objects found.");
		return null;
	
	}
	
	/* (non-Javadoc)
	 * @see org.openiam.idm.srvc.mngsys.service.ManagedSysDAO#findByExample(org.openiam.idm.srvc.mngsys.dto.ManagedSys)
	 */
	public List<ManagedSys> findByExample(ManagedSys instance) {
		log.debug("finding ManagedSys instance by example");
		try {
			List<ManagedSys> results = (List<ManagedSys>) sessionFactory
					.getCurrentSession().createCriteria(ManagedSys.class).add(
							create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}


}
