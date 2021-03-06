package org.openiam.selfsrvc.pswd;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.beans.propertyeditors.CustomDateEditor;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.dto.PasswordSync;
import org.openiam.provision.service.ProvisionService;

/**
 * Controller for the new hire form.
 * @author suneet
 *
 */
public class SingleSysPasswordChangeController extends SimpleFormController {


	protected UserDataWebService userMgr;
	protected LoginDataWebService loginManager;
	protected PasswordConfiguration configuration;
	protected ProvisionService provisionService;
		
	String defaultDomainId;
	String menuGroup;
	
	private static final Log log = LogFactory.getLog(SingleSysPasswordChangeController.class);

	
	public SingleSysPasswordChangeController() {
		super();
	}
	

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"),true) );
	}
	

	protected Object formBackingObject(HttpServletRequest request)		throws Exception {
		
		String rMenu = request.getParameter("hideRMenu");
		String header = request.getParameter("hideHeader");
		String cd = request.getParameter("cd"); 
		String sysId = request.getParameter("sysid");
		
		HttpSession session =  request.getSession();
		String userId = (String)session.getAttribute("userId");
		
		List<Login> principalList = loginManager.getLoginByUser(userId).getPrincipalList();
		Login lg = null;
		for ( Login l: principalList) {
			if (l.getId().getManagedSysId().equalsIgnoreCase(sysId)) {
				lg = l;
			}
		}
		// if the identity was not found for this managed system, then throw an exception - We can not change their identity

		
		if (cd != null) {
			if (cd.equalsIgnoreCase("pswdreset")) {
				request.setAttribute("msg", "Your account has been reset. Please change your password.");
			}
			if (cd.equalsIgnoreCase("pswdexp")) {
				request.setAttribute("msg", "Your password has expired. Please change your password.");
			}
		}
		
		if (rMenu != null && rMenu.length() > 0) {		
			request.setAttribute("hideRMenu","1");
		}
		if (header != null && header.length() > 0) {
			request.setAttribute("hideHeader","1");
		}
		
		SingleSysPasswordChangeCommand pswdChangeCmd = new SingleSysPasswordChangeCommand();	
				
		if (lg != null) {
			pswdChangeCmd.setPrincipal(lg.getId().getLogin());
			pswdChangeCmd.setUserId(lg.getUserId());
			pswdChangeCmd.setSysId(sysId);
			pswdChangeCmd.setDomainId(configuration.getDefaultSecurityDomain());
		}
		return pswdChangeCmd;
	}

	

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors) throws Exception {
		
		log.info("onSubmit called.");
		
		String userId = (String)request.getSession().getAttribute("userId");
		
		SingleSysPasswordChangeCommand pswdChangeCmd =(SingleSysPasswordChangeCommand)command;
		
		// get objects from the command object
		String principal = pswdChangeCmd.getPrincipal();
		String password = pswdChangeCmd.getPassword();
		
		
		
		PasswordSync passwordSync = new PasswordSync("CHANGE PASSWORD", pswdChangeCmd.getSysId(), password, 
					principal, userId, pswdChangeCmd.getDomainId(), "SELFSERVICE", false );
			
		provisionService.setPassword(passwordSync);
			
		log.info("-Sync password complete");
				
		ModelAndView mav = new ModelAndView(getSuccessView());
		mav.addObject("pswdChangeCmd",pswdChangeCmd);
		
		
		return mav;
	}






	public PasswordConfiguration getConfiguration() {
		return configuration;
	}


	public void setConfiguration(PasswordConfiguration configuration) {
		this.configuration = configuration;
	}


	public ProvisionService getProvisionService() {
		return provisionService;
	}


	public void setProvisionService(ProvisionService provisionService) {
		this.provisionService = provisionService;
	}


	public UserDataWebService getUserMgr() {
		return userMgr;
	}


	public void setUserMgr(UserDataWebService userMgr) {
		this.userMgr = userMgr;
	}


	public LoginDataWebService getLoginManager() {
		return loginManager;
	}


	public void setLoginManager(LoginDataWebService loginManager) {
		this.loginManager = loginManager;
	}


}
