package org.openiam.idm.srvc.auth.service;

/**
 * Lists a set of constants that are used by the authentication service.
 *
 * @author Suneet Shah
 */
public class AuthenticationConstants {

    /**
     * Constants indicating which type of authentication to use.
     */
    public static final String AUTHN_TYPE_PASSWORD = "PASSWORD";
    public static final String AUTHN_TYPE_TOKEN = "TOKEN";
    public static final String AUTHN_TYPE_SAML2 = "SAML2";
    public static final String AUTHN_TYPE_CERT = "CERT";

    /* TOKEN TYPES */
    public static final String OPENIAM_TOKEN = "OPENIAM_TOKEN";
    public static final String SAML1_TOKEN = "SAML1_TOKEN";
    public static final String SAML2_TOKEN = "SAML2_TOKEN";


    public static final int INTERNAL_ERROR = -1;
    /**
     * SUCCESS - successful login.
     */
    public static final int RESULT_SUCCESS = 1;
    /**
     * SUCCESS_PASSWORD_EXP - successful login, but the password is expiring soon.
     */
    public static final int RESULT_SUCCESS_PASSWORD_EXP = 2;
    /**
     * SUCCESS_FIRST_TIME - successful login, but its a first time login..
     */
    public static final int RESULT_SUCCESS_FIRST_TIME = 3;
    /**
     * INVALID_LOGIN - Invalid login id
     */
    public static final int RESULT_INVALID_LOGIN = 100;
    /**
     * INVALID_PASSWORD - Invalid password
     */
    public static final int RESULT_INVALID_PASSWORD = 101;

    public static final int RESULT_INVALID_DOMAIN = 109;
    /**
     * PASSWORD_EXPIRED - Password has expired
     */
    public static final int RESULT_PASSWORD_EXPIRED = 102;
    /**
     * LOGIN_LOCKED - Login is locked
     */
    public static final int RESULT_LOGIN_LOCKED = 103;
    /**
     * LOGIN_DISABLED
     */
    public static final int RESULT_LOGIN_DISABLED = 110;
    /**
     * INVALID_USER_STATUS - User is not in a valid status
     */
    public static final int RESULT_INVALID_USER_STATUS = 104;
    /**
     * SERVICE_UNAVAILABLE - Service is unavailable.
     */
    public static final int RESULT_SERVICE_UNAVAILABLE = 105;
    /**
     * SERVICE_NOT_FOUND - Service Id does not exist in the system
     */
    public static final int RESULT_SERVICE_NOT_FOUND = 107;

    /* INVALID_TOKEN - Token used for authentication was not valid */
    public static final int RESULT_INVALID_TOKEN = 108;
    public static final int RESULT_INVALID_CONFIGURATION = 120;

    /**
     * SENSITIVE_APP - Sensitive application which require re-authentication. Typically thrown
     * when attempting to use Single Sign On.
     */
    public static final int RESULT_SENSITIVE_APP = 106;
}
