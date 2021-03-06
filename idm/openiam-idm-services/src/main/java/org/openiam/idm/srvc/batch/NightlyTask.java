/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.batch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.EngineException;
import org.openiam.base.id.UUIDGen;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.batch.birt.ReportGenerator;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.service.BatchDataService;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.report.domain.ReportInfoEntity;
import org.openiam.idm.srvc.report.domain.ReportSubCriteriaParamEntity;
import org.openiam.idm.srvc.report.domain.ReportSubscriptionEntity;
import org.openiam.idm.srvc.report.service.ReportDataService;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.role.ws.UserRoleListResponse;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserSearch;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.idm.srvc.user.ws.UserListResponse;
import org.openiam.idm.srvc.user.ws.UserResponse;
import org.openiam.script.BindingModelImpl;
import org.openiam.script.ScriptFactory;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Job bean that is called by quartz to kick off the nightly tasks.
 * 
 * @author suneet
 * 
 */
public class NightlyTask implements ApplicationContextAware {

	private static final Log log = LogFactory.getLog(NightlyTask.class);

	/*
	 * The flags for the running tasks are handled by this Thread-Safe Set. It
	 * stores the taskIds of the currently executing tasks. This is faster and
	 * as reliable as storing the flags in the database, if the tasks are only
	 * launched from ONE host in a clustered environment. It is unique for each
	 * class-loader, which means unique per war-deployment.
	 */
	private static Set<String> runningTask = Collections
			.newSetFromMap(new ConcurrentHashMap());
	protected LoginDataWebService loginManager;
	protected PolicyDataService policyDataService;
	@Autowired
	protected ReportDataService reportDataService;
	protected UserDataWebService userManager;
	protected GroupDataService groupManager;
	protected RoleDataWebService roleDataService;
	protected MailService mailService;
	protected BatchDataService batchService;
	protected String scriptEngine;
	protected AuditHelper auditHelper;
	
	static protected ResourceBundle res = ResourceBundle
			.getBundle("datasource");
	boolean isPrimary = Boolean.parseBoolean(res.getString("IS_PRIMARY"));

	// used to inject the application context into the groovy scripts
	public static ApplicationContext ac;

	public void execute() {
		log.debug("NightlyBatchJob called.");

		ScriptIntegration se = null;
		Map<String, Object> bindingMap = new HashMap<String, Object>();
		bindingMap.put("context", ac);

		if (!isPrimary) {
			log.debug("Scheduler: Not primary instance");
			return;
		}

		try {
			se = ScriptFactory.createModule(this.scriptEngine);
		} catch (Exception e) {
			log.error(e);
			return;

		}

		// get the list of domains
		List<BatchTask> taskList = batchService
				.getAllTasksByFrequency("NIGHTLY");
		log.debug("-Tasklist=" + taskList);

		if (taskList != null) {
			for (BatchTask task : taskList) {
				log.debug("Executing task:" + task.getTaskName());

				String requestId = UUIDGen.getUUID();

				try {
					if (task.getEnabled() != 0) {
						// This needs to be synchronized, because the check for
						// the taskId and the insertion need to
						// happen atomically. It is possible for two threads,
						// started by Quartz, to reach this point at
						// the same time for the same task.
						synchronized (runningTask) {
							if (runningTask.contains(task.getTaskId())) {
								log.debug("Task " + task.getTaskName()
										+ " already running");
								continue;
							}
							runningTask.add(task.getTaskId());
						}

						log.debug("Executing task:" + task.getTaskName());
						if (task.getLastExecTime() == null) {
							task.setLastExecTime(new Date(System
									.currentTimeMillis()));
						}

						bindingMap.put("taskObj", task);
						bindingMap.put("lastExecTime", task.getLastExecTime());
						bindingMap.put("parentRequestId", requestId);

						Integer output = (Integer) se.execute(new BindingModelImpl(bindingMap, ac),
								task.getTaskUrl());
						if (output.intValue() == 0) {
							auditHelper.addLog(task.getTaskName(), null, null,
									"IDM BATCH TASK", null, "0", "DAILY BATCH",
									task.getTaskId(), null, "FAIL", null, null,
									null, null, null, null, null);
						} else {
							auditHelper.addLog(task.getTaskName(), null, null,
									"IDM BATCH TASK", null, "0", "DAILY BATCH",
									task.getTaskId(), null, "SUCCESS", null,
									null, null, null, null, null, null);
						}
					}
				} catch (Exception e) {
					log.error(e);
				} finally {
					if (task.getEnabled() != 0) {
						// this point can only be reached by the thread, which
						// put the taskId into the map
						runningTask.remove(task.getTaskId());
						// Get the updated status of the task
						task = batchService.getBatchTask(task.getTaskId());
						task.setLastExecTime(new Date(System
								.currentTimeMillis()));
						batchService.updateTask(task);
					}
				}

			}
		}
		executeBIRTTasks();

	}

	/**
	 * This method takes care of the following 1. Deletes existing generated
	 * report directories 2. Generates reports for all active subscriptions 3.
	 * Emails reports as applicable - this task can be moved to groovy if
	 * needed.
	 */
	private void executeBIRTTasks() {
		ReportGenerator.deleteGeneratedDirs();
		// task to generate BIRT reports
		List<ReportSubscriptionEntity> subcribedReportList = reportDataService
				.getAllActiveSubscribedReports();
		for (ReportSubscriptionEntity report : subcribedReportList) {
			ReportInfoEntity reportInfo = reportDataService
					.getReportByName(report.getReportName());
			List<ReportSubCriteriaParamEntity> reportParameters = reportDataService
					.getSubReportParametersByReportName(report.getReportName());
			Map<String, String> params = new LinkedHashMap<String, String>();
			for (ReportSubCriteriaParamEntity parameter : reportParameters) {
				params.put(parameter.getName(), parameter.getValue());
			}
			try {
				UserResponse userResponse = userManager.getUserWithDependent(report.getUserId(), false);
				User user = userResponse.getUser();
					UserSearch search = new UserSearch();
					List<String> emailAddresses = new ArrayList<String>();
					List<String> userIds = new ArrayList<String>();
					if ("SELF".equalsIgnoreCase(report.getDeliveryAudience())) {
						emailAddresses.add(user.getEmail());
						userIds.add(user.getUserId());
					} else{ 
						if ("ROLE".equalsIgnoreCase(report
								.getDeliveryAudience())) {
							UserRoleListResponse userRolesResponse = roleDataService.getUserRolesForUser(report.getUserId());
							List<UserRole> userRoles = userRolesResponse.getUserRoleList();
							List<String> roleList = new ArrayList<String>();
							String domainId="";
							//Assuming that domain of all users is same, or it will pick last one
							//TODO --clarify the above assumption
							for (UserRole role: userRoles){
								roleList.add(role.getRoleId());
								domainId = role.getServiceId();
							}
							search.setRoleIdList(roleList);
							search.setDomainId(domainId);
						} else if ("DEPT".equalsIgnoreCase(report
								.getDeliveryAudience())) {
							search.setDeptCd(user.getDeptCd());
						} else if ("ORGANIZATION".equalsIgnoreCase(report
								.getDeliveryAudience())) {
							search.setOrgId(user.getCompanyId());
						} else if ("DIVISION".equalsIgnoreCase(report
								.getDeliveryAudience())) {
							search.setDivision(user.getDivision());
						} else if ("GROUP".equalsIgnoreCase(report
								.getDeliveryAudience())) {
							List<Group> groups = groupManager.getUserInGroups(report.getUserId());
							List<String> groupList = new ArrayList<String>();
							for (Group group: groups){
								groupList.add(group.getGrpId());
							}
							search.setGroupIdList(groupList);
						}
						UserListResponse userListResponse = userManager.search(search);
						List<User> userList = userListResponse.getUserList();
						for (User user1: userList){
							emailAddresses.add(user1.getEmail());
							userIds.add(user.getUserId());
						}
					}
					//emailAddresses.toArray();
					//userIds.toArray();
					//send email
				
				ReportGenerator.generateReport(report.getReportName(),
						reportInfo.getReportFilePath(),
						report.getDeliveryFormat(), report.getUserId(), params, report.getDeliveryMethod(), emailAddresses, userIds, mailService);

			} catch (EngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BirtException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ac = applicationContext;
	}

	public LoginDataWebService getLoginManager() {
		return loginManager;
	}

	public void setLoginManager(LoginDataWebService loginManager) {
		this.loginManager = loginManager;
	}

	public PolicyDataService getPolicyDataService() {
		return policyDataService;
	}

	public void setPolicyDataService(PolicyDataService policyDataService) {
		this.policyDataService = policyDataService;
	}

	public BatchDataService getBatchService() {
		return batchService;
	}

	public void setBatchService(BatchDataService batchService) {
		this.batchService = batchService;
	}

	public String getScriptEngine() {
		return scriptEngine;
	}

	public void setScriptEngine(String scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public AuditHelper getAuditHelper() {
		return auditHelper;
	}

	public void setAuditHelper(AuditHelper auditHelper) {
		this.auditHelper = auditHelper;
	}

	public ReportDataService getReportDataService() {
		return reportDataService;
	}

	public void setReportDataService(ReportDataService reportDataService) {
		this.reportDataService = reportDataService;
	}

	public UserDataWebService getUserManager() {
		return userManager;
	}

	public void setUserManager(UserDataWebService userManager) {
		this.userManager = userManager;
	}


	public RoleDataWebService getRoleDataService() {
		return roleDataService;
	}

	public void setRoleDataService(RoleDataWebService roleDataService) {
		this.roleDataService = roleDataService;
	}

	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public GroupDataService getGroupManager() {
		return groupManager;
	}

	public void setGroupManager(GroupDataService groupManager) {
		this.groupManager = groupManager;
	}

}
