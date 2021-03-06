package org.openiam.idm.srvc.synch.srcadapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.module.client.MuleClient;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.role.ws.RoleDataWebService;
import org.openiam.idm.srvc.synch.dto.SyncResponse;
import org.openiam.idm.srvc.synch.dto.SynchConfig;
import org.openiam.idm.srvc.synch.service.SourceAdapter;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.ProvisionUser;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

/**
 * Abstract class which all Source System adapters must extend
 * User: suneetshah
 * Date: 3/10/11
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSrcAdapter implements SourceAdapter {

    // Time for threads to be closed when Interrupted. Used in multithreading
    protected final long SHUTDOWN_TIME = 5000;

    public static ApplicationContext ac;
    private static final Log log = LogFactory.getLog(AbstractSrcAdapter.class);

    protected AuditHelper auditHelper;
    protected MuleContext muleContext;
    protected UserDataWebService userMgr;

    protected LoginDataWebService loginManager;
    protected RoleDataWebService roleDataService;

    static protected ResourceBundle res = ResourceBundle.getBundle("datasource");

    static String serviceHost = res.getString("openiam.service_base");
    static String serviceContext = res.getString("openiam.idm.ws.path");



    public abstract SyncResponse startSynch(SynchConfig config) ;



    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

    public void addUser(ProvisionUser pUser) {
        long startTime = System.currentTimeMillis();

        Map<String,String> msgPropMap =  new HashMap<String,String>();
        msgPropMap.put("SERVICE_HOST", serviceHost);
        msgPropMap.put("SERVICE_CONTEXT", serviceContext);


        try {
            //Create the client with the context
            MuleClient client = new MuleClient(muleContext);
            client.sendAsync("vm://provisionServiceAddMessage", (ProvisionUser)pUser, msgPropMap);

        }catch(MuleException me) {

            log.error(me.getMessage());
        }
        long endTime = System.currentTimeMillis();
        log.debug("--AddUser:SynchAdapter execution time=" + (endTime-startTime));
    }

    public void modifyUser(ProvisionUser pUser) {

        long startTime = System.currentTimeMillis();

        Map<String,String> msgPropMap =  new HashMap<String,String>();
        msgPropMap.put("SERVICE_HOST", serviceHost);
        msgPropMap.put("SERVICE_CONTEXT", serviceContext);

        log.debug("ModifyUser() called asynchronously....");

        try {
            //Create the client with the context
            MuleClient client = new MuleClient(muleContext);
            client.sendAsync("vm://provisionServiceModifyMessage", (ProvisionUser)pUser, msgPropMap);

        }catch(MuleException me) {

            log.error(me.getMessage());
        }catch (Exception e) {
            log.error("Error occurred during modify user. " + e.getMessage());
            log.error(e);
        }
        long endTime = System.currentTimeMillis();
        log.debug("-- ModifyUser:SynchAdapter execution time=" + (endTime-startTime));
    }

    /**
     * This method used for awaiting while all threads will be finished.
     * Used in multithreading
     *
     * @param results
     * @throws InterruptedException
     */
    protected void waitUntilWorkDone(List<Future> results) throws InterruptedException {
        int successCounter = 0;
        while(successCounter != results.size()) {
            successCounter = 0;
            for(Future future : results) {
                if(future.isDone()) {
                    successCounter ++;
                }
            }
            Thread.sleep(500);
        }
    }

    public void setMuleContext(MuleContext ctx) {
        muleContext = ctx;
    }

    public UserDataWebService getUserMgr() {
        return userMgr;
    }

    public void setUserMgr(UserDataWebService userMgr) {
        this.userMgr = userMgr;
    }

    public AuditHelper getAuditHelper() {
        return auditHelper;
    }


    public void setAuditHelper(AuditHelper auditHelper) {
        this.auditHelper = auditHelper;
    }

    public LoginDataWebService getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginDataWebService loginManager) {
        this.loginManager = loginManager;
    }

    public RoleDataWebService getRoleDataService() {
        return roleDataService;
    }

    public void setRoleDataService(RoleDataWebService roleDataService) {
        this.roleDataService = roleDataService;
    }
}
