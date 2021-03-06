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
package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.synch.dto.Attribute;
import org.openiam.idm.srvc.synch.dto.LineObject;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.MatchObjectRule;
import org.openiam.idm.srvc.synch.service.TransformScript;
import org.openiam.idm.srvc.synch.service.ValidationScript;
import org.openiam.idm.srvc.synch.util.DatabaseUtil;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.service.ProvisionService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Reads a CSV file for use during the synchronization process
 *
 * @author suneet
 */
public class RDBMSAdapter extends AbstractSrcAdapter {

    private LineObject rowHeader = new LineObject();
    private ApplicationContext ac;

    private String systemAccount;

    private MatchRuleFactory matchRuleFactory;

    private static final Log log = LogFactory.getLog(RDBMSAdapter.class);
    private Connection con = null;
    private int THREAD_DELAY_BEFORE_SECOUNR_RECORD_EXECUTION = 20000;

    // synchronization monitor
    private final Object mutex = new Object();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }


    public SyncResponse startSynch(final SynchConfig config) {
        int THREAD_COUNT = Integer.parseInt(res.getString("rdbmsvadapter.thread.count"));
        int THREAD_DELAY_BEFORE_START = Integer.parseInt(res.getString("rdbmsvadapter.thread.delay.beforestart"));


        THREAD_DELAY_BEFORE_SECOUNR_RECORD_EXECUTION = Integer.parseInt(res.getString("rdbmsvadapter.thread.delay.workaround_for_ad"));

        final ProvisionService provService = (ProvisionService) ac.getBean("defaultProvision");

        log.debug("^^^^^^^^ RDBMS SYNCH STARTED ^^^^^^^^");

        String requestId = UUIDGen.getUUID();

        IdmAuditLog synchStartLog_ = new IdmAuditLog();
        synchStartLog_.setSynchAttributes("SYNCH_USER", config.getSynchConfigId(), "START", "SYSTEM", requestId);
        final IdmAuditLog synchStartLog = auditHelper.logEvent(synchStartLog_);

        if (!connect(config)) {

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FAIL_SQL_ERROR);
            return resp;
        }

        try {

            java.util.Date lastExec = null;

            if (config.getLastExecTime() != null) {
                lastExec = config.getLastExecTime();
            }
            final String changeLog = config.getQueryTimeField();
            StringBuilder sql = new StringBuilder(config.getQuery());
            // if its incremental synch, then add the change log parameter
            if (config.getSynchType().equalsIgnoreCase("INCREMENTAL")) {
                // execute the query
                if (StringUtils.isNotEmpty(sql.toString()) && (lastExec != null)) {

                    String temp = sql.toString().toUpperCase();
                    // strip off any trailing semi-colons. Not needed for jbdc
                    if (temp.endsWith(";")) {
                        temp = removeLastChar(temp);
                    }

                    if (temp.contains("WHERE")) {
                        sql.append(" AND ");
                    } else {
                        sql.append(" WHERE ");
                    }
                    sql.append(changeLog).append(" >= ?");
                }
            }


            log.debug(" - SYNCH SQL= " + sql.toString());
            log.debug(" - Last processed record =" + lastExec);


            PreparedStatement ps = con.prepareStatement(sql.toString());
            if (config.getSynchType().equalsIgnoreCase("INCREMENTAL") && (lastExec != null)) {
                ps.setTimestamp(1, new Timestamp(lastExec.getTime()));
            }
            ResultSet rs = ps.executeQuery();

            // get the list of columns
            ResultSetMetaData rsMetadata = rs.getMetaData();
            DatabaseUtil.populateTemplate(rsMetadata, rowHeader);

            //Read Resultset to List
            List<LineObject> results = new LinkedList<LineObject>();
            while (rs.next()) {
                LineObject rowObj = rowHeader.copy();
                DatabaseUtil.populateRowObject(rowObj, rs, changeLog);
                results.add(rowObj);
            }

            // test
            log.debug(" - Number of records being processed for the synch:" + results.size());
            log.debug(" - Result set contains following number of columns : " + rowHeader.getColumnMap().size());


            final ValidationScript validationScript = StringUtils.isNotEmpty(config.getValidationRule()) ? SynchScriptFactory.createValidationScript(config.getValidationRule()) : null;
            final TransformScript transformScript = StringUtils.isNotEmpty(config.getTransformationRule()) ? SynchScriptFactory.createTransformationScript(config.getTransformationRule()) : null;

            // Multithreading
            int allRowsCount = results.size();
            if (allRowsCount > 0) {
                int threadCoount = THREAD_COUNT;
                int rowsInOneExecutors = allRowsCount / threadCoount;
                int remains = rowsInOneExecutors > 0 ? allRowsCount % (rowsInOneExecutors * threadCoount) : 0;
                if (remains != 0) {
                    threadCoount++;
                }
                log.debug("Thread count = " + threadCoount + "; Rows in one thread = " + rowsInOneExecutors + "; Remains rows = " + remains);

                List<Future> threadResults = new LinkedList<Future>();
                // store the latest processed record by thread indx
                final Map<String, Timestamp> recentRecordByThreadInx = new HashMap<String, Timestamp>();
                final ExecutorService service = Executors.newCachedThreadPool();
                for (int i = 0; i < threadCoount; i++) {
                    final int threadIndx = i;
                    final int startIndex = i * rowsInOneExecutors;
                    // Start index for current thread
                    int shiftIndex = threadCoount > THREAD_COUNT && i == threadCoount - 1 ? remains : rowsInOneExecutors;
                    // Part of the rowas that should be processing with this thread
                    final List<LineObject> part = results.subList(startIndex, startIndex + shiftIndex);
                    threadResults.add(service.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Timestamp mostRecentRecord = proccess(config, provService, synchStartLog, part, validationScript, transformScript, startIndex);
                                recentRecordByThreadInx.put("Thread_" + threadIndx, mostRecentRecord);
                            } catch (ClassNotFoundException e) {
                                log.error(e);

                                synchStartLog.updateSynchAttributes("FAIL", ResponseCode.CLASS_NOT_FOUND.toString(), e.toString());
                                auditHelper.logEvent(synchStartLog);
                            }
                        }
                    }));
                    //Give THREAD_DELAY_BEFORE_START seconds time for thread to be UP (load all cache and begin the work)
                    Thread.sleep(THREAD_DELAY_BEFORE_START);
                }
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        service.shutdown();
                        try {
                            if (!service.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS)) { //optional *
                                log.warn("Executor did not terminate in the specified time."); //optional *
                                List<Runnable> droppedTasks = service.shutdownNow(); //optional **
                                log.warn("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed."); //optional **
                            }
                        } catch (InterruptedException e) {
                            log.error(e);

                            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.INTERRUPTED_EXCEPTION.toString(), e.toString());
                            auditHelper.logEvent(synchStartLog);

                            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
                            resp.setErrorCode(ResponseCode.INTERRUPTED_EXCEPTION);
                        }
                    }
                });
                waitUntilWorkDone(threadResults);

            }
        } catch (ClassNotFoundException cnfe) {

            log.error(cnfe);

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.CLASS_NOT_FOUND.toString(), cnfe.toString());
            auditHelper.logEvent(synchStartLog);


            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            resp.setErrorText(cnfe.toString());

            return resp;
        } catch (IOException fe) {

            log.error(fe);

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.FILE_EXCEPTION.toString(), fe.toString());
            auditHelper.logEvent(synchStartLog);


            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.FILE_EXCEPTION);
            resp.setErrorText(fe.toString());
            return resp;

        } catch (SQLException se) {

            log.error(se);
            closeConnection();

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.SQL_EXCEPTION.toString(), se.toString());
            auditHelper.logEvent(synchStartLog);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.SQL_EXCEPTION);
            resp.setErrorText(se.toString());
            return resp;
        } catch (InterruptedException e) {
            log.error(e);

            synchStartLog.updateSynchAttributes("FAIL", ResponseCode.INTERRUPTED_EXCEPTION.toString(), e.toString());
            auditHelper.logEvent(synchStartLog);

            SyncResponse resp = new SyncResponse(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.INTERRUPTED_EXCEPTION);
        } finally {
            // mark the end of the synch
            IdmAuditLog synchEndLog = new IdmAuditLog();
            synchEndLog.setSynchAttributes("SYNCH_USER", config.getSynchConfigId(), "END", "SYSTEM", synchStartLog.getSessionId());
            auditHelper.logEvent(synchEndLog);
        }

        log.debug("^^^^^^^^ RDBMS SYNCH COMPLETE.^^^^^^^^");


        closeConnection();

        return new SyncResponse(ResponseStatus.SUCCESS);
    }

    private Timestamp proccess(SynchConfig config, ProvisionService provService, IdmAuditLog synchStartLog, List<LineObject> part, final ValidationScript validationScript, final TransformScript transformScript, int ctr) throws ClassNotFoundException {
        Timestamp mostRecentRecord = null;
        int rowCounter = 0;

        if ( part != null ) {
            log.debug(" - In Process(). Number of lineObjects in part = " + part.size());
        }

        for (LineObject rowObj : part) {
            rowCounter ++;
            if(rowCounter == 2) {
                try {
                    Thread.sleep(THREAD_DELAY_BEFORE_SECOUNR_RECORD_EXECUTION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.debug("-RDBMS ADAPTER: SYNCHRONIZING  RECORD # ---" + ctr++);
            // make sure we have a new object for each row
            ProvisionUser pUser = new ProvisionUser();

            log.debug(" - Record update time=" + rowObj.getLastUpdate());

            if (mostRecentRecord == null) {
                mostRecentRecord = rowObj.getLastUpdate();
            } else {
                // if current record is newer than what we saved, then update the most recent record value

                if (mostRecentRecord.before(rowObj.getLastUpdate())) {
                    log.debug("- MostRecentRecord value updated to=" + rowObj.getLastUpdate());
                    mostRecentRecord.setTime(rowObj.getLastUpdate().getTime());
                }
            }

            // start the synch process
            // 1) Validate the data
            // 2) Transform it
            // 3) if not delete - then match the object and determine if its a new object or its an udpate
            // validate
            if (validationScript != null) {
                synchronized (mutex) {
                    int retval = validationScript.isValid(rowObj);
                    if (retval == ValidationScript.NOT_VALID) {
                        log.debug(" - Validation failed...transformation will not be called.");

                        continue;
                    }
                    if (retval == ValidationScript.SKIP) {
                        continue;
                    }
                }
            }

            // check if the user exists or not
            Map<String, Attribute> rowAttr = rowObj.getColumnMap();
            //
            // rule used to match object from source system to data in IDM
            MatchObjectRule matchRule = matchRuleFactory.create(config);
            User usr = matchRule.lookup(config, rowAttr);


            // transform
            int retval = -1;
            if (transformScript != null) {
                synchronized (mutex) {
                    // initialize the transform script
                    transformScript.init();

                    if (usr != null) {
                        transformScript.setNewUser(false);
                        transformScript.setUser(userMgr.getUserWithDependent(usr.getUserId(), true).getUser());
                        transformScript.setPrincipalList(loginManager.getLoginByUser(usr.getUserId()).getPrincipalList());
                        transformScript.setUserRoleList(roleDataService.getUserRolesAsFlatList(usr.getUserId()).getRoleList());

                    } else {
                        transformScript.setNewUser(true);
                        transformScript.setUser(null);
                        transformScript.setPrincipalList(null);
                        transformScript.setUserRoleList(null);
                    }

                    retval = transformScript.execute(rowObj, pUser);

                    log.debug("- Transform result=" + retval);

                    // show the user object
                    //log.debug("- User After Transformation =" + pUser);
                    log.debug("- User = " + pUser.getUserId() + "-" + pUser.getFirstName() + " " + pUser.getLastName());
                    log.debug("- User Attributes = " + pUser.getUserAttributes());
                }
                pUser.setSessionId(synchStartLog.getSessionId());

                if (retval != -1) {
                    if (retval == TransformScript.DELETE && usr != null) {
                        log.debug("deleting record - " + usr.getUserId());
                        provService.deleteByUserId(new ProvisionUser(usr), UserStatusEnum.DELETED, systemAccount);

                    } else {
                        // call synch
                        if (retval != TransformScript.DELETE) {


                            if (usr != null) {
                                log.debug("- Calling ModifyUser for existing user...UserID=" + pUser.getUserId());
                                pUser.setUserId(usr.getUserId());

                                modifyUser(pUser);

                            } else {
                                log.debug("- adding new user...");

                                pUser.setUserId(null);
                                addUser(pUser);


                            }
                        }
                    }
                }
            }
            ctr++;
            //ADD the sleep pause to give other threads possibility to be alive
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.error("The thread was interrupted when sleep paused after row [" + ctr + "] execution.", e);
            }

        }
        return mostRecentRecord;
    }

    public Response testConnection(SynchConfig config) {
        try {
            Class.forName(config.getDriver());

            con = DriverManager.getConnection(
                    config.getConnectionUrl(),
                    config.getSrcLoginId(),
                    config.getSrcPassword());
            closeConnection();
            Response resp = new Response(ResponseStatus.SUCCESS);
            return resp;
        } catch (SQLException e) {
            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.SQL_EXCEPTION);
            resp.setErrorText(e.getMessage());
            return resp;
        } catch (ClassNotFoundException e) {
            Response resp = new Response(ResponseStatus.FAILURE);
            resp.setErrorCode(ResponseCode.CLASS_NOT_FOUND);
            resp.setErrorText(e.getMessage());
            return resp;
        }
    }


    private boolean connect(SynchConfig config) {

        try {
            Class.forName(config.getDriver());

            con = DriverManager.getConnection(
                    config.getConnectionUrl(),
                    config.getSrcLoginId(),
                    config.getSrcPassword());
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (ClassNotFoundException cf) {
            cf.printStackTrace();
        }
        return false;

    }

    private void closeConnection() {
        try {
            con.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length() - 1);
    }


    public MatchRuleFactory getMatchRuleFactory() {
        return matchRuleFactory;
    }


    public void setMatchRuleFactory(MatchRuleFactory matchRuleFactory) {
        this.matchRuleFactory = matchRuleFactory;
    }


    public String getSystemAccount() {
        return systemAccount;
    }


    public void setSystemAccount(String systemAccount) {
        this.systemAccount = systemAccount;
    }


}
