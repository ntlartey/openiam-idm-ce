package org.openiam.idm.srvc.auth.login;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import java.util.Date;
import java.util.List;

/**
 * Data access interface for domain model class Login.
 *
 * @author Suneet Shah
 */
public interface LoginDAO {

    Login add(Login transientInstance);

    void remove(Login persistentInstance);

    Login update(Login detachedInstance);

    Login findById(org.openiam.idm.srvc.auth.dto.LoginId id);

    List<Login> findUser(String userId);

    List<Login> findLoginByDomain(String domain);

    Login findLoginByManagedSys(String domain, String managedSys, String userId);
    //void updateIdentity(Login lg);

    int changeIdentity(String principal, String pswd, String userId, String managedSysId);

    int bulkUnlock(String domainId, UserStatusEnum status, int autoUnlockTime);

    int bulkResetPasswordChangeCount();

    public List findLoginByDept(String managedSysId, String department, String div);

    public List<Login> findLockedUsers(Date startTime);

    List<Login> findInactiveUsers(int startDays, int endDays, String managedSysId);

    List<Login> findUserNearPswdExp(int daysToExpiration);


    List<Login> findUserPswdExpYesterday();

    /**
     * Returns a list of Login objects for the managed system specified by the sysId
     *
     * @param managedSysId
     * @return
     */
    List<Login> findAllLoginByManagedSys(String managedSysId);

    List<Login> findLoginByManagedSys(String principalName, String managedSysId);

    Login findByPasswordResetToken(String token);

}

