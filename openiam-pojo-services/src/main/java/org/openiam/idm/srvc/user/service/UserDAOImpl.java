package org.openiam.idm.srvc.user.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.ReconcileUserEntity;
import org.openiam.idm.srvc.user.dto.DateSearchAttribute;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.SearchAttribute;
import org.openiam.idm.srvc.user.dto.UserSearch;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

/**
 * Data access implementation for domain model class User and UserWS. UserWS is
 * similar to User, however, the interface has been simplified to support usage
 * in a web service.
 * 
 * @author Suneet Shah
 * @see org.openiam.idm.srvc.user
 */
public class UserDAOImpl implements UserDAO {

	private static final Log log = LogFactory.getLog(UserDAOImpl.class);

	private SessionFactory sessionFactory;
	// private SequenceGenDAO seqGenDao;
	private String emailSearchVal;
	private String phoneSearchVal;
	private Integer maxResultSetSize;
	private String dbType;

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

	public void add(UserEntity usr) {
		log.debug("persisting User instance");
		try {
			// If the object has not been assigned an id, we should
			// automatically assign one
			// based on the predefined id generator. Hibernate ID generator does
			// not give us
			// the flexibility to let the user assign the id.
			/*
			 * if (usr.getUserId() == null || usr.getUserId().length() == 0) {
			 * usr.setUserId( this.seqGenDao.getNextId("USER_ID")); }
			 */

			sessionFactory.getCurrentSession().persist(usr);
			log.debug("persist successful");
		} catch (HibernateException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void remove(UserEntity persistentInstance) {
		log.debug("deleting User instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (HibernateException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UserEntity update(UserEntity detachedInstance) {
		log.debug("merging User instance");
		try {
			UserEntity result = (UserEntity) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (HibernateException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UserEntity findById(java.lang.String id) {
		log.debug("getting User instance with id: " + id);
		try {
			Session session = sessionFactory.getCurrentSession();

			// Since contact info objects can be shared between users and other
			// entities, the
			// filters will help ensure that we only see the User related
			// object.
			Filter filter = session.enableFilter("parentTypeFilter");
			filter.setParameter("parentFilter", "USER");

			UserEntity instance = (UserEntity) session
					.get(UserEntity.class, id);
			session.disableFilter("parentTypeFilter");
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (HibernateException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public UserEntity findByName(String firstName, String lastName) {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(UserEntity.class)
					.add(Restrictions.eq("lastName", lastName))
					.add(Restrictions.eq("firstName", firstName));

			List<UserEntity> results = (List<UserEntity>) criteria.list();

			if (results != null && !results.isEmpty()) {
				return results.get(0);
			} else {
				return null;
			}
		} catch (HibernateException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<ReconcileUserEntity> findAllUsers() {
		try {
			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session
					.createCriteria(ReconcileUserEntity.class)
					.createAlias("logins", "l")
					.add(Restrictions.eq("l.managedSysId", "0"))
					.setFetchMode("logins", FetchMode.JOIN);

			return (List<ReconcileUserEntity>) criteria.list();
		} catch (HibernateException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<UserEntity> findByStatus(UserStatusEnum status) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(UserEntity.class)
				.add(Restrictions.eq("status", status))
				.addOrder(Order.asc("lastName"));

		List<UserEntity> results = (List<UserEntity>) criteria.list();
		return results;
	}

	public List<UserEntity> findByLastUpdateRange(Date startDate, Date endDate) {
		log.debug("finding User based on the lastUpdate date range that is provided");
		try {

			Session session = sessionFactory.getCurrentSession();
			Criteria criteria = session.createCriteria(UserEntity.class);
			criteria.add(Restrictions.between("lastUpdate", startDate, endDate));
			return criteria.list();

		} catch (HibernateException re) {
			re.printStackTrace();
			log.error("findByLastUpdateRange failed.", re);
			throw re;
		}
	}

	public List<UserEntity> search(UserSearch search) {
		if (dbType != null && dbType.equalsIgnoreCase("ORACLE_INSENSITIVE")) {
			return searchOracleInsensitive(search);
		}
		return defaultSearch(search);
	}

	@Override
	public Integer searchCount(UserSearch search) {
		if (dbType != null && dbType.equalsIgnoreCase("ORACLE_INSENSITIVE")) {
			return searchOracleInsensitiveCount(search);
		}
		return defaultSearchCount(search);
	}

	private Integer searchOracleInsensitiveCount(UserSearch search) {
		String select = " SELECT COUNT(DISTINCT u.USER_ID) from USERS u "
				+ "  		LEFT JOIN LOGIN lg ON ( lg.USER_ID = u.USER_ID) "
				+ "  		LEFT JOIN EMAIL_ADDRESS em ON ( em.PARENT_ID = u.USER_ID) "
				+ "  		LEFT JOIN PHONE p ON ( p.PARENT_ID = u.USER_ID) "
				+ "  		LEFT JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID) "
				+ "  		LEFT JOIN USER_GRP g ON ( g.USER_ID = u.USER_ID) "
				+ "  		LEFT JOIN COMPANY c ON ( c.COMPANY_ID = u.COMPANY_ID) "
				+ "	 	LEFT JOIN USER_ROLE_VW urv on (u.USER_ID = urv.USER_ID) ";

		SQLQuery qry = searchOracleInsensitiveQueryPrepare(search, select);
		try {
			BigDecimal result = (BigDecimal) qry.uniqueResult();
			if (result == null) {
				log.debug("search result is null");
				return null;
			}
			log.debug("search resultset size=" + result);
			return result.intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private Integer defaultSearchCount(UserSearch search) {

		String select = " select COUNT(DISTINCT u.USER_ID) from USERS u ";

		// MySQL's optimizer has a hard time with the large number of outer
		// joins
		// changing outer joins to inner-joins has a big impact on performance

		SQLQuery qry = defaultSearchQueryPrepare(search, select);
		try {
            BigDecimal result = (BigDecimal) qry.uniqueResult();
			if (result == null) {
				log.debug("search result is null");
				return null;
			}
			log.debug("search resultset size=" + result);
			return result.intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<UserEntity> searchOracleInsensitive(UserSearch search) {

		String select = " select /*+ INDEX(IDX_USER_FIRSTNAME_UPPER) INDEX(IDX_USER_LASTNAME_UPPER) INDEX(IDX_LOGIN_PRINCIPAL_UPPER) INDEX(IDX_UA_NAME_UPPER)  */ "
				+ " DISTINCT u.USER_ID, u.TYPE_ID, "
				+ " u.TITLE, u.MIDDLE_INIT, u.LAST_NAME, u.FIRST_NAME,"
				+ " u.BIRTHDATE, u.STATUS, u.SECONDARY_STATUS, u.DEPT_NAME, u.DEPT_CD, "
				+ " u.LAST_UPDATE, u.CREATED_BY, u.CREATE_DATE, u.SEX, "
				+ " u.USER_TYPE_IND, u.SUFFIX, u.PREFIX, u.LAST_UPDATED_BY,"
				+ " u.LOCATION_NAME, u.LOCATION_CD, u.EMPLOYEE_TYPE, u.EMPLOYEE_ID, "
				+ " u.JOB_CODE, u.MANAGER_ID, u.COMPANY_OWNER_ID, u.COMPANY_ID, "
				+ " u.LAST_DATE, u.START_DATE, u.COST_CENTER, u.DIVISION,"
				+ " u.PASSWORD_THEME, u.NICKNAME, u.MAIDEN_NAME, u.MAIL_CODE, "
				+ " u.COUNTRY, u.BLDG_NUM, u.STREET_DIRECTION, u.SUITE,  "
				+ " u.ADDRESS1, u.ADDRESS2, u.ADDRESS3, u.ADDRESS4, u.ADDRESS5, u.ADDRESS6, u.ADDRESS7,"
				+ " u.CITY, u.STATE, u.POSTAL_CD, u.EMAIL_ADDRESS, u.ALTERNATE_ID, u.USER_OWNER_ID, u.DATE_PASSWORD_CHANGED, u.DATE_CHALLENGE_RESP_CHANGED, "
				+ " u.PHONE_NBR, u.PHONE_EXT, u.AREA_CD, u.COUNTRY_CD, u.CLASSIFICATION, u.SHOW_IN_SEARCH, u.DEL_ADMIN "
				+ " from 	USERS u "
				+ "  		LEFT JOIN LOGIN lg ON ( lg.USER_ID = u.USER_ID) "
				+ "  		LEFT JOIN EMAIL_ADDRESS em ON ( em.PARENT_ID = u.USER_ID) "
				+ "  		LEFT JOIN PHONE p ON ( p.PARENT_ID = u.USER_ID) "
				+ "  		LEFT JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID) "
				+ "  		LEFT JOIN USER_GRP g ON ( g.USER_ID = u.USER_ID) "
				+ "  		LEFT JOIN COMPANY c ON ( c.COMPANY_ID = u.COMPANY_ID) "
				+ "	 	LEFT JOIN USER_ROLE_VW urv on (u.USER_ID = urv.USER_ID) ";

		SQLQuery qry = searchOracleInsensitiveQueryPrepare(search, select);
		qry.addEntity(UserEntity.class);
		try {
			List<UserEntity> result = (List<UserEntity>) qry.list();
			if (result == null || result.size() == 0) {
				log.debug("search result is null");
				return null;
			}
			log.debug("search resultset size=" + result.size());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private SQLQuery searchOracleInsensitiveQueryPrepare(UserSearch search,
			String select) {
		StringBuffer where = new StringBuffer();
		boolean firstName = false;
		boolean lastName = false;
		boolean nickName = false;
		boolean status = false;
		boolean secondaryStatus = false;
		boolean deptCd = false;
		boolean division = false;
		boolean phoneAreaCd = false;
		boolean phoneNbr = false;
		boolean employeeId = false;
		boolean groupId = false;
		boolean roleId = false;
		boolean emailAddress = false;
		boolean orgId = false;
		boolean userId = false;
		boolean principal = false;
		boolean domainId = false;
		boolean attributeName = false;
		boolean attributeValue = false;
		boolean metadataElementId = false;
		boolean showInSearch = false;
		boolean locationId = false;

		boolean userTypeInd = false;
		boolean classification = false;
		boolean orgName = false;
		boolean zipCode = false;
		boolean bOrgIdList = false;
		boolean bDeptIdList = false;
		boolean bDivIdList = false;
		boolean bAttrIdList = false;

        boolean dateOfBirth = false;

		List<String> nameList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();
		if (search.getShowInSearch() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.SHOW_IN_SEARCH = :showInSearch ");
			showInSearch = true;
		}

		if (search.getUserId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.USER_ID = :userId ");
			userId = true;
		}

		if (search.getUserTypeInd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.USER_TYPE_IND = :userTypeInd ");
			userTypeInd = true;
		}

		if (search.getLocationCd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.LOCATION_CD = :locationCd ");
			locationId = true;
		}

		if (search.getClassification() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.CLASSIFICATION = :classification ");
			classification = true;
		}

        if (search.getDateOfBirth() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.BIRTHDATE = :dob ");
            dateOfBirth = true;
        }

		if (search.getFirstName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" UPPER(u.FIRST_NAME) like :firstName ");
			firstName = true;
		}
		if (search.getLastName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" UPPER( u.LAST_NAME) like :lastName ");
			lastName = true;
		}
		if (search.getNickName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.NICKNAME like :nickName ");
			nickName = true;
		}

		if (search.getStatus() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.STATUS = :status ");
			status = true;
		}

		if (search.getSecondaryStatus() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.SECONDARY_STATUS = :secondaryStatus ");
			secondaryStatus = true;
		}

		if (search.getZipCode() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.POSTAL_CD = :zipCode ");
			zipCode = true;
		}

		if (search.getDeptCd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DEPT_CD = :deptCd ");
			deptCd = true;
		}
		if (search.getDivision() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DIVISION = :division ");
			division = true;
		}

		if (search.getEmployeeId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.EMPLOYEE_ID = :employeeId ");
			employeeId = true;
		}
		if (search.getOrgId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.COMPANY_ID = :orgId ");
			orgId = true;
		}
		if (search.getOrgName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" UPPER(c.COMPANY_NAME) like :orgName ");
			orgName = true;
		}
		if (search.getPhoneAreaCd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" p.AREA_CD = :phoneAreaCd ");
			phoneAreaCd = true;
		}
		if (search.getPhoneNbr() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" p.PHONE_NBR = :phoneNbr ");
			phoneNbr = true;
		}

		if (search.getEmailAddress() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			// where.append(" u.EMAIL_ADDRESS = :emailAddress ");
			where.append(" ( UPPER(em.EMAIL_ADDRESS) LIKE :emailAddress  OR UPPER(u.EMAIL_ADDRESS) LIKE :emailAddress) ");

			// where.append(" em.EMAIL_ADDRESS LIKE :emailAddress ");
			emailAddress = true;
		}
		if (!search.getGroupIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" g.GRP_ID in (:groupList) ");
			groupId = true;
		}
		if (!search.getRoleIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" urv.ROLE_ID in (:roleList) ");
			where.append(" and urv.SERVICE_ID = :domainId ");
			roleId = true;
		}

		/* org list */
		if (!search.getOrgIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.COMPANY_ID in (:orgList)  ");
			bOrgIdList = true;
		}

		if (!search.getDeptIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DEPT_CD in (:deptList)  ");
			bDeptIdList = true;
		}

		/* division list */

		if (!search.getDivisionIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DIVISION in (:divisionList)  ");
			bDivIdList = true;
		}

		/* Login */
		if (search.getPrincipal() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" UPPER(lg.LOGIN) like :principal ");
			principal = true;
		}

		if (search.getDomainId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" lg.SERVICE_ID = :domainId ");
			domainId = true;
		}

		if (search.getLoggedIn() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			if (search.getLoggedIn().equalsIgnoreCase("Yes")) {
				where.append(" lg.LAST_LOGIN IS NOT NULL ");
			} else {
				where.append(" lg.LAST_LOGIN IS NULL ");
			}

		}

		/* User Attributes fields */
		if (search.getAttributeName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" UPPER(ua.NAME) = :attributeName ");
			attributeName = true;
		}
		if (search.getAttributeValue() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" UPPER(ua.VALUE) like :attributeValue ");
			attributeValue = true;
		}
		if (search.getAttributeElementId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" ua.METADATA_ID = :elementId ");
			metadataElementId = true;
		}
		/* Date Search Attribute */
		if (!search.getDateAttributeList().isEmpty()) {
			// create a list for each set of values

			log.debug("Building query parameters for date search attributes");

			for (DateSearchAttribute atr : search.getDateAttributeList()) {
				if (where.length() > 0) {
					where.append(" and ");
				}
				// attribute name is of the form tableName.columnName
				// assumption is that table is already part of the join
				if (atr.getAttributeName() != null
						&& atr.getOperation() != null
						&& (!atr.getOperation().equals("IS NULL"))
						&& atr.getAttributeValue() != null) {
					where.append(" " + atr.getAttributeName() + " "
							+ atr.getOperation() + " :"
							+ atr.getAttributeName());
				} else if (atr.getAttributeName() != null
						&& atr.getOperation() != null
						&& atr.getOperation().equals("IS NULL")) {
					where.append(" " + atr.getAttributeName() + " "
							+ atr.getOperation());
				}

			}
		}
		if (where.length() > 0) {
			select = select + " WHERE " + where.toString();
		}

		select = select + "  ORDER BY u.LAST_NAME, u.FIRST_NAME";
		log.debug("search select: " + select);

		Session session = sessionFactory.getCurrentSession();

		SQLQuery qry = session.createSQLQuery(select);

		if (userId) {
			qry.setString("userId", search.getUserId());
		}
		if (firstName) {
			qry.setString("firstName", search.getFirstName().toUpperCase());
		}
		if (lastName) {
			qry.setString("lastName", search.getLastName().toUpperCase());
		}
		if (nickName) {
			qry.setString("nickName", search.getNickName());
		}
		if (status) {
			qry.setString("status", search.getStatus());
		}
		if (secondaryStatus) {
			qry.setString("secondaryStatus", search.getSecondaryStatus());
		}

		if (zipCode) {
			qry.setString("zipCode", search.getZipCode());
		}

		if (deptCd) {
			qry.setString("deptCd", search.getDeptCd());
		}
		if (division) {
			qry.setString("division", search.getDivision());
		}
		if (locationId) {
			qry.setString("locationCd", search.getLocationCd());
		}

		if (employeeId) {
			qry.setString("employeeId", search.getEmployeeId());
		}
		if (orgId) {
			qry.setString("orgId", search.getOrgId());
		}
		if (orgName) {
			qry.setString("orgName", search.getOrgName().toUpperCase());
		}
		if (phoneAreaCd) {
			qry.setString("phoneAreaCd", search.getPhoneAreaCd());
		}
		if (phoneNbr) {
			qry.setString("phoneNbr", search.getPhoneNbr());
		}
		if (emailAddress) {
			qry.setString("emailAddress", search.getEmailAddress()
					.toUpperCase());
		}
		if (principal) {
			qry.setString("principal", search.getPrincipal().toUpperCase());
		}
		if (domainId) {
			qry.setString("domainId", search.getDomainId());
		}
		if (attributeName) {
			qry.setString("attributeName", search.getAttributeName()
					.toUpperCase());
		}
		if (attributeValue) {
			qry.setString("attributeValue", search.getAttributeValue()
					.toUpperCase());
		}
		if (metadataElementId) {
			qry.setString("elementId", search.getAttributeElementId());
		}
		if (showInSearch) {
			qry.setInteger("showInSearch", search.getShowInSearch());
		}
		if (groupId) {
			qry.setParameterList("groupList", search.getGroupIdList());
			// qry.setString("groupId", search.getGroupId());
		}
		if (roleId) {
			qry.setParameterList("roleList", search.getRoleIdList());
			// qry.setString("role", search.getRoleId());
		}
		if (classification) {
			qry.setString("classification", search.getClassification());
		}

        if (dateOfBirth) {
            qry.setDate("dob", search.getDateOfBirth());
        }

		if (userTypeInd) {
			qry.setString("userTypeInd", search.getUserTypeInd());
		}

		if (bOrgIdList) {
			qry.setParameterList("orgList", search.getOrgIdList());

		}
		if (bDeptIdList) {
			qry.setParameterList("deptList", search.getDeptIdList());

		}

		if (bDivIdList) {
			qry.setParameterList("divisionList", search.getDivisionIdList());

		}

		if (bAttrIdList) {
			qry.setParameterList("nameList", nameList);
			qry.setParameterList("valueList", valueList);

		}
		/* Date Search Attribute */
		if (!search.getDateAttributeList().isEmpty()) {
			for (DateSearchAttribute atr : search.getDateAttributeList()) {
				if (atr.getAttributeName() != null
						&& atr.getOperation() != null
						&& atr.getAttributeValue() != null) {
					qry.setDate(atr.getAttributeName(), atr.getAttributeValue());
				}

			}
		}

		if (search.getMaxResultSize() != null
				&& search.getMaxResultSize().intValue() > 0) {
			qry.setFetchSize(search.getMaxResultSize().intValue());
			qry.setMaxResults(search.getMaxResultSize().intValue());
		} else {
			qry.setFetchSize(this.maxResultSetSize);
			qry.setMaxResults(this.maxResultSetSize);
		}
		return qry;
	}

	private List<UserEntity> defaultSearch(UserSearch search) {

		String select = " select DISTINCT u.USER_ID, u.TYPE_ID, "
				+ " u.TITLE, u.MIDDLE_INIT, u.LAST_NAME, u.FIRST_NAME,"
				+ " u.BIRTHDATE, u.STATUS, u.SECONDARY_STATUS, u.DEPT_NAME, u.DEPT_CD, "
				+ " u.LAST_UPDATE, u.CREATED_BY, u.CREATE_DATE, u.SEX, "
				+ " u.USER_TYPE_IND, u.SUFFIX, u.PREFIX, u.LAST_UPDATED_BY,"
				+ " u.LOCATION_NAME, u.LOCATION_CD, u.EMPLOYEE_TYPE, u.EMPLOYEE_ID, "
				+ " u.JOB_CODE, u.MANAGER_ID, u.COMPANY_OWNER_ID, u.COMPANY_ID, "
				+ " u.LAST_DATE, u.START_DATE, u.COST_CENTER, u.DIVISION,"
				+ " u.PASSWORD_THEME, u.NICKNAME, u.MAIDEN_NAME, u.MAIL_CODE, "
				+ " u.COUNTRY, u.BLDG_NUM, u.STREET_DIRECTION, u.SUITE,  "
				+ " u.ADDRESS1, u.ADDRESS2, u.ADDRESS3, u.ADDRESS4, u.ADDRESS5, u.ADDRESS6, u.ADDRESS7,"
				+ " u.CITY, u.STATE, u.POSTAL_CD, u.EMAIL_ADDRESS, u.ALTERNATE_ID, u.USER_OWNER_ID, u.DATE_PASSWORD_CHANGED, u.DATE_CHALLENGE_RESP_CHANGED,"
				+ " u.PHONE_NBR, u.PHONE_EXT, u.AREA_CD, u.COUNTRY_CD, u.CLASSIFICATION, u.SHOW_IN_SEARCH, u.DEL_ADMIN "
				+ " from 	USERS u ";

		// MySQL's optimizer has a hard time with the large number of outer
		// joins
		// changing outer joins to inner-joins has a big impact on performance

		SQLQuery qry = defaultSearchQueryPrepare(search, select);
		qry.addEntity(UserEntity.class);
		try {
			List<UserEntity> result = (List<UserEntity>) qry.list();
			if (result == null || result.size() == 0) {
				log.debug("search result is null");
				return null;
			}
			log.debug("search resultset size=" + result.size());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private SQLQuery defaultSearchQueryPrepare(UserSearch search, String select) {
		StringBuilder join = new StringBuilder();
		join.append("   JOIN LOGIN lg ON ( lg.USER_ID = u.USER_ID) ");
		join.append("   LEFT JOIN USER_ROLE_VW urv on (u.USER_ID = urv.USER_ID)");

		StringBuilder where = new StringBuilder();
		boolean firstName = false;
		boolean lastName = false;
		boolean nickName = false;
		boolean status = false;
		boolean secondaryStatus = false;
		boolean deptCd = false;
		boolean division = false;
		boolean phoneAreaCd = false;
		boolean phoneNbr = false;
		boolean employeeId = false;
		boolean groupId = false;
		boolean roleId = false;
		boolean emailAddress = false;
		boolean orgId = false;
		boolean userId = false;
		boolean principal = false;
		boolean domainId = false;
		boolean attributeName = false;
		boolean attributeValue = false;
		boolean metadataElementId = false;
		boolean showInSearch = false;
		boolean locationCd = false;

		boolean userTypeInd = false;
		boolean classification = false;
		boolean orgName = false;
		boolean zipCode = false;

		boolean startDate = false;
		boolean lastDate = false;
		boolean dateOfBirth = false;

		boolean bOrgIdList = false;
		boolean bDeptIdList = false;
		boolean bDivIdList = false;
		boolean bAttrIdList = false;
		if (search.getShowInSearch() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.SHOW_IN_SEARCH = :showInSearch ");
			showInSearch = true;
		}

		if (search.getUserId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.USER_ID = :userId ");
			userId = true;
		}

		if (search.getUserTypeInd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.USER_TYPE_IND = :userTypeInd ");
			userTypeInd = true;
		}

		if (search.getClassification() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.CLASSIFICATION = :classification ");
			classification = true;
		}

        if (search.getDateOfBirth() != null) {
            if (where.length() > 0) {
                where.append(" and ");
            }
            where.append(" u.BIRTHDATE = :dob ");
            dateOfBirth = true;
        }

		if (search.getLocationCd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.LOCATION_CD = :locationCd ");
			locationCd = true;
		}

		if (search.getFirstName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.FIRST_NAME like :firstName ");
			firstName = true;
		}
		if (search.getLastName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.LAST_NAME like :lastName ");
			lastName = true;
		}
		if (search.getNickName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.NICKNAME like :nickName ");
			nickName = true;
		}

		if (search.getStatus() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.STATUS = :status ");
			status = true;
		}

		if (search.getSecondaryStatus() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.SECONDARY_STATUS = :secondaryStatus ");
			secondaryStatus = true;
		}

		if (search.getZipCode() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.POSTAL_CD = :zipCode ");
			zipCode = true;
		}

		if (search.getDeptCd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DEPT_CD = :deptCd ");
			deptCd = true;
		}
		if (search.getDivision() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DIVISION = :division ");
			division = true;
		}

		if (search.getEmployeeId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.EMPLOYEE_ID = :employeeId ");
			employeeId = true;
		}
		if (search.getOrgId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.COMPANY_ID = :orgId ");
			orgId = true;
		}

		if (search.getOrgName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" c.COMPANY_NAME like :orgName ");
			join.append("  JOIN COMPANY c ON ( c.COMPANY_ID = u.COMPANY_ID) ");
			orgName = true;
		} /*
		 * else {
		 * 
		 * join.append("   LEFT JOIN COMPANY c ON ( c.COMPANY_ID = u.COMPANY_ID) "
		 * );
		 * 
		 * }
		 */

		if (search.getPhoneAreaCd() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" p.AREA_CD = :phoneAreaCd ");
			phoneAreaCd = true;
		}
		if (search.getPhoneNbr() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" p.PHONE_NBR = :phoneNbr ");
			phoneNbr = true;
		}

		if (phoneNbr || phoneAreaCd) {
			join.append("   JOIN PHONE p ON ( p.PARENT_ID = u.USER_ID) ");
		}/*
		 * else {
		 * 
		 * join.append("   LEFT JOIN PHONE p ON ( p.PARENT_ID = u.USER_ID) ");
		 * 
		 * 
		 * }
		 */

		if (search.getEmailAddress() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			// where.append(" u.EMAIL_ADDRESS = :emailAddress ");
			where.append(" (em.EMAIL_ADDRESS LIKE :emailAddress  OR u.EMAIL_ADDRESS LIKE :emailAddress) ");
			join.append("   JOIN EMAIL_ADDRESS em ON ( em.PARENT_ID = u.USER_ID)");
			emailAddress = true;
		} /*
		 * else {
		 * 
		 * join.append(
		 * "   LEFT JOIN EMAIL_ADDRESS em ON ( em.PARENT_ID = u.USER_ID)");
		 * 
		 * }
		 */
		if (!search.getGroupIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" g.GRP_ID in (:groupList) ");
			join.append("   JOIN USER_GRP g ON ( g.USER_ID = u.USER_ID) ");
			groupId = true;
		}/*
		 * else {
		 * join.append("   LEFT JOIN USER_GRP g ON ( g.USER_ID = u.USER_ID) ");
		 * }
		 */

		if (!search.getRoleIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" urv.ROLE_ID in (:roleList) ");
			where.append(" and urv.SERVICE_ID = :domainId ");
			roleId = true;
		}

		/* org list */
		if (!search.getOrgIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.COMPANY_ID in (:orgList)  ");
			bOrgIdList = true;
		}

		/* dept list */

		if (!search.getDeptIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DEPT_CD in (:deptList)  ");
			bDeptIdList = true;
		}

		/* division list */

		if (!search.getDivisionIdList().isEmpty()) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" u.DIVISION in (:divisionList)  ");
			bDivIdList = true;
		}

		/* single attribute */
		if (search.getAttributeName() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" ua.NAME = :attributeName ");
			attributeName = true;
		}
		if (search.getAttributeValue() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" ua.VALUE like :attributeValue ");
			attributeValue = true;
		}
		if (search.getAttributeElementId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" ua.METADATA_ID = :elementId ");
			metadataElementId = true;
		}

		if (attributeName || attributeValue || metadataElementId) {
			join.append(" JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID)");
		}

		/* attribute list */

		List<String> nameList = new ArrayList<String>();
		List<String> valueList = new ArrayList<String>();

		if (!search.getAttributeList().isEmpty()) {
			// create a list for each set of values

			log.debug("Building query parameters for attributes");

			for (SearchAttribute atr : search.getAttributeList()) {
				if (atr.getAttributeName() != null) {
					nameList.add(atr.getAttributeName());
				}
				if (atr.getAttributeValue() != null) {
					valueList.add(atr.getAttributeValue());
				}
			}

			if (where.length() > 0) {
				where.append(" and ");
			}
			if (nameList.size() > 0) {
				where.append(" ua.NAME in (:nameList)  ");
			}

			if (where.length() > 0) {
				where.append(" and ");
			}
			if (nameList.size() > 0) {
				where.append(" ua.VALUE in (:valueList)  ");
			}
			bAttrIdList = true;

		}

		if (bAttrIdList && !attributeName) {

			join.append(" JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID)");

		}/*
		 * else {
		 * 
		 * join.append(" LEFT JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID)"
		 * ) ;
		 * 
		 * }
		 */

		/* Date Search Attribute */
		if (!search.getDateAttributeList().isEmpty()) {
			// create a list for each set of values

			log.debug("Building query parameters for date search attributes");

			for (DateSearchAttribute atr : search.getDateAttributeList()) {
				if (where.length() > 0) {
					where.append(" and ");
				}
				// attribute name is of the form tableName.columnName
				// assumption is that table is already part of the join
				if (atr.getAttributeName() != null
						&& atr.getOperation() != null
						&& (!atr.getOperation().equals("IS NULL"))
						&& atr.getAttributeValue() != null) {
					where.append(" " + atr.getAttributeName() + " "
							+ atr.getOperation() + " :"
							+ atr.getAttributeName());
				} else if (atr.getAttributeName() != null
						&& atr.getOperation() != null
						&& atr.getOperation().equals("IS NULL")) {
					where.append(" " + atr.getAttributeName() + " "
							+ atr.getOperation());
				}

			}
		}

		/* Login */
		if (search.getPrincipal() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" lg.LOGIN like :principal ");
			principal = true;
		}
		if (search.getDomainId() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			where.append(" lg.SERVICE_ID = :domainId ");
			domainId = true;
		}

		if (search.getLoggedIn() != null) {
			if (where.length() > 0) {
				where.append(" and ");
			}
			if (search.getLoggedIn().equalsIgnoreCase("Yes")) {
				where.append(" lg.LAST_LOGIN IS NOT NULL");
			} else {
				where.append(" lg.LAST_LOGIN IS NULL");
			}

		}

		if (where.length() > 0) {
			select = select + join.toString() + " WHERE " + where.toString();
		}

		select = select + "  ORDER BY u.LAST_NAME, u.FIRST_NAME";

		log.debug("search select: " + select);

		Session session = sessionFactory.getCurrentSession();

		SQLQuery qry = session.createSQLQuery(select);

		if (userId) {
			qry.setString("userId", search.getUserId());
		}
		if (firstName) {
			qry.setString("firstName", search.getFirstName());
		}
		if (lastName) {
			qry.setString("lastName", search.getLastName());
		}
		if (nickName) {
			qry.setString("nickName", search.getNickName());
		}
		if (status) {
			qry.setString("status", search.getStatus());
		}
		if (secondaryStatus) {
			qry.setString("secondaryStatus", search.getSecondaryStatus());
		}

		if (zipCode) {
			qry.setString("zipCode", search.getZipCode());
		}

		if (deptCd) {
			qry.setString("deptCd", search.getDeptCd());
		}

		if (locationCd) {
			qry.setString("locationCd", search.getLocationCd());
		}

		if (division) {
			qry.setString("division", search.getDivision());
		}

		if (employeeId) {
			qry.setString("employeeId", search.getEmployeeId());
		}
		if (orgId) {
			qry.setString("orgId", search.getOrgId());
		}
		if (orgName) {
			qry.setString("orgName", search.getOrgName());
		}
		if (phoneAreaCd) {
			qry.setString("phoneAreaCd", search.getPhoneAreaCd());
		}
		if (phoneNbr) {
			qry.setString("phoneNbr", search.getPhoneNbr());
		}
		if (emailAddress) {
			qry.setString("emailAddress", search.getEmailAddress());
		}
		if (principal) {
			qry.setString("principal", search.getPrincipal());
		}
		if (domainId) {
			qry.setString("domainId", search.getDomainId());
		}
		if (attributeName) {
			qry.setString("attributeName", search.getAttributeName());
		}
		if (attributeValue) {
			qry.setString("attributeValue", search.getAttributeValue());
		}
		if (metadataElementId) {
			qry.setString("elementId", search.getAttributeElementId());
		}
		if (showInSearch) {
			qry.setInteger("showInSearch", search.getShowInSearch());
		}
		if (groupId) {
			qry.setParameterList("groupList", search.getGroupIdList());
			// qry.setString("groupId", search.getGroupId());
		}
		if (roleId) {
			qry.setParameterList("roleList", search.getRoleIdList());
			// qry.setString("role", search.getRoleId());
		}
		if (classification) {
			qry.setString("classification", search.getClassification());
		}

        if (dateOfBirth) {
            qry.setDate("dob", search.getDateOfBirth());
        }

		if (userTypeInd) {
			qry.setString("userTypeInd", search.getUserTypeInd());
		}

		if (bOrgIdList) {
			qry.setParameterList("orgList", search.getOrgIdList());

		}
		if (bDeptIdList) {
			qry.setParameterList("deptList", search.getDeptIdList());

		}

		if (bDivIdList) {
			qry.setParameterList("divisionList", search.getDivisionIdList());

		}

		if (bAttrIdList) {
			qry.setParameterList("nameList", nameList);
			qry.setParameterList("valueList", valueList);

		}
		/* Date Search Attribute */
		if (!search.getDateAttributeList().isEmpty()) {
			for (DateSearchAttribute atr : search.getDateAttributeList()) {
				if (atr.getAttributeName() != null
						&& atr.getOperation() != null
						&& atr.getAttributeValue() != null) {
					qry.setDate(atr.getAttributeName(), atr.getAttributeValue());
				}

			}
		}

		if (search.getMaxResultSize() != null
				&& search.getMaxResultSize().intValue() > 0) {
			qry.setFetchSize(search.getMaxResultSize().intValue());
			qry.setMaxResults(search.getMaxResultSize().intValue());
		} else {
			qry.setFetchSize(this.maxResultSetSize);
			qry.setMaxResults(this.maxResultSetSize);
		}
		return qry;
	}

	public List<UserEntity> findByOrganization(String orgId) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(UserEntity.class)
				.add(Restrictions.eq("companyId", orgId))
				.addOrder(Order.asc("lastName"));
		List<UserEntity> results = (List<UserEntity>) criteria.list();
		return results;

	}

	public List<UserEntity> findStaff(String supervisorId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session
				.createQuery("from SupervisorEntity as s inner join s.employee as staff left outer join s.supervisor as supervisor where supervisor.userId = :supervisorId order by supervisor.userId asc");
		qry.setString("supervisorId", supervisorId);
		List<UserEntity> results = (List<UserEntity>) qry.list();
		return results;
	}

	public List<UserEntity> findSupervisors(String staffId) {
		Session session = sessionFactory.getCurrentSession();
		Query qry = session
				.createQuery("from SupervisorEntity as s inner join s.supervisor as supervisor left join s.employee as staff where staff.userId = :staffId order by staff.userId asc");
		qry.setString("staffId", staffId);
		List<UserEntity> results = (List<UserEntity>) qry.list();
		return results;
	}

	public List<UserEntity> findByDelegationProperties(
			DelegationFilterSearch search) {
		boolean rl = false;
		boolean orgFilter = false;

		StringBuffer bufSql = new StringBuffer();
		bufSql.append(" select DISTINCT u.USER_ID, u.TYPE_ID, "
				+ " u.TITLE, u.MIDDLE_INIT, u.LAST_NAME, u.FIRST_NAME,"
				+ " u.BIRTHDATE, u.STATUS, u.SECONDARY_STATUS, u.DEPT_NAME, u.DEPT_CD, "
				+ " u.LAST_UPDATE, u.CREATED_BY, u.CREATE_DATE, u.SEX, "
				+ " u.USER_TYPE_IND, u.SUFFIX, u.PREFIX, u.LAST_UPDATED_BY,"
				+ " u.LOCATION_NAME, u.LOCATION_CD, u.EMPLOYEE_TYPE, u.EMPLOYEE_ID, "
				+ " u.JOB_CODE, u.MANAGER_ID, u.COMPANY_OWNER_ID, u.COMPANY_ID, "
				+ " u.LAST_DATE, u.START_DATE, u.COST_CENTER, u.DIVISION,"
				+ " u.PASSWORD_THEME, u.NICKNAME, u.MAIDEN_NAME, u.MAIL_CODE, "
				+ " u.COUNTRY, u.BLDG_NUM, u.STREET_DIRECTION, u.SUITE,  "
				+ " u.ADDRESS1, u.ADDRESS2, u.ADDRESS3, u.ADDRESS4, u.ADDRESS5, u.ADDRESS6, u.ADDRESS7,"
				+ " u.CITY, u.STATE, u.POSTAL_CD, u.EMAIL_ADDRESS, u.ALTERNATE_ID, u.USER_OWNER_ID, u.DATE_PASSWORD_CHANGED, u.DATE_CHALLENGE_RESP_CHANGED,"
				+ " u.PHONE_NBR, u.PHONE_EXT, u.AREA_CD, u.COUNTRY_CD, u.CLASSIFICATION, u.SHOW_IN_SEARCH, u.DEL_ADMIN "
				+ " from 	USERS u "
				+ "  		LEFT JOIN USER_ATTRIBUTES ua ON ( ua.USER_ID = u.USER_ID) "
				+ "	 	LEFT JOIN USER_ROLE ur on (u.USER_ID = ur.USER_ID) ");

		StringBuffer whereBuf = new StringBuffer();
		if (search.getRole() != null) {
			whereBuf.append(" ur.ROLE_ID = :role ");
			rl = true;
		}

		if (search.getDelAdmin() == 1) {
			if (whereBuf.length() > 0) {
				whereBuf.append(" and ");
			}
			whereBuf.append(" u.DEL_ADMIN = 1");

			if (search.getOrgFilter() != null) {
				if (whereBuf.length() > 0) {
					whereBuf.append(" and ");
				}
				whereBuf.append(" ua.NAME = 'DLG_FLT_ORG' and ua.VALUE LIKE :orgFilter ");
				orgFilter = true;

			}

		}

		if (whereBuf.length() > 0) {
			bufSql.append(" WHERE ");
			bufSql.append(whereBuf);
		}
		bufSql.append("order by u.LAST_NAME asc");

		String sql = bufSql.toString();

		System.out.println("sql to get approvers:" + sql);

		Session session = sessionFactory.getCurrentSession();

		SQLQuery qry = session.createSQLQuery(sql);
		qry.addEntity(UserEntity.class);

		if (rl) {
			qry.setString("role", search.getRole());
		}
		if (orgFilter) {
			qry.setString("orgFilter", search.getOrgFilter());
		}

		List<UserEntity> results = (List<UserEntity>) qry.list();
		return results;

	}

	public String getEmailSearchVal() {
		return emailSearchVal;
	}

	public void setEmailSearchVal(String emailSearchVal) {
		this.emailSearchVal = emailSearchVal;
	}

	public String getPhoneSearchVal() {
		return phoneSearchVal;
	}

	public void setPhoneSearchVal(String phoneSearchVal) {
		this.phoneSearchVal = phoneSearchVal;
	}

	public Integer getMaxResultSetSize() {
		return maxResultSetSize;
	}

	public void setMaxResultSetSize(Integer maxResultSetSize) {
		this.maxResultSetSize = maxResultSetSize;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

}
