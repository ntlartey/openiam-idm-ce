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
package org.openiam.idm.srvc.pswd.service;

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.dozer.converter.IdentityQuestionDozerConverter;
import org.openiam.dozer.converter.UserIdentityAnswerDozerConverter;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.dto.ChallengeResponseUser;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suneet
 * 
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.pswd.service.ChallengeResponseService", targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", portName = "ChallengeResponseWebServicePort", serviceName = "ChallengeResponseWebService")
public class ChallengeResponseServiceImpl implements ChallengeResponseService {

	protected Cryptor cryptor;
	protected PolicyDataService policyDataService;
	protected IdentityQuestionDAO identityQuestDao;
	protected UserIdentityAnswerDAO identityAnswerDao;
	protected IdentityQuestGroupDAO identityQuestGroupDao;
	protected LoginDataService loginManager;
	// protected ChallengeResponseValidator responseValidator;

	protected String respValidatorObjName;
	protected String respValidatorObjType;;
	protected ValidatorFactory respValidatorFactory;

	protected UserDataService userMgr;
	protected org.openiam.idm.srvc.audit.service.AuditHelper auditHelper;
	protected PasswordService passwordMgr;

	@Autowired
	private IdentityQuestionDozerConverter identityQuestionDozerConverter;

	@Autowired
	private UserIdentityAnswerDozerConverter userIdentityAnswerDozerConverter;

	private static final Log log = LogFactory
			.getLog(ChallengeResponseServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.pswd.service.ChallengeResponseService#getAllQuestions
	 * ()
	 */
	public List<IdentityQuestion> allQuestions() {
		ChallengeResponseValidator responseValidator = respValidatorFactory
				.createValidator(respValidatorObjName, respValidatorObjType);
		return responseValidator.getQuestions(null);
	}

    @Transactional(readOnly = true)
	public List<IdentityQuestion> questionsByUser(String userId) {
		if (userId == null) {
			throw new NullPointerException("UserId is null");
		}
		List<IdentityQuestionEntity> entityList = identityQuestDao.findAllQuestionsByUser(userId);
		if (entityList != null) {
			return identityQuestionDozerConverter.convertToDTOList(entityList
				, false);
		}else{
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openiam.idm.srvc.pswd.service.ChallengeResponseService#
	 * getQuestionsByGroup(java.lang.String)
	 */
	public List<IdentityQuestion> questionsByGroup(String group) {
		if (group == null) {
			throw new NullPointerException("group is null");
		}
		ChallengeResponseUser respUser = new ChallengeResponseUser();
		respUser.setQuestionGroup(group);

		ChallengeResponseValidator responseValidator = respValidatorFactory
				.createValidator(respValidatorObjName, respValidatorObjType);
		return responseValidator.getQuestions(respUser);

	}

	public IdentityQuestion getQuestion(String questionId) {
		if (questionId == null) {
			throw new NullPointerException("questionId is null");
		}

		ChallengeResponseValidator responseValidator = respValidatorFactory
				.createValidator(respValidatorObjName, respValidatorObjType);
		return responseValidator.getQuestion(questionId);

	}

    @Transactional
	public IdentityQuestion addQuestion(IdentityQuestion question) {
		if (question == null) {
			throw new NullPointerException("question is null");
		}
		return identityQuestionDozerConverter.convertToDTO(identityQuestDao
				.add(identityQuestionDozerConverter.convertToEntity(question,
						true)), true);
	}

    @Transactional
	public IdentityQuestion updateQuestion(IdentityQuestion question) {
		if (question == null) {
			throw new NullPointerException("question is null");
		}
		return identityQuestionDozerConverter.convertToDTO(identityQuestDao
				.update(identityQuestionDozerConverter.convertToEntity(
						question, true)), true);
	}

    @Transactional
	public void removeQuestion(String questionId) {
		if (questionId == null) {
			throw new NullPointerException("question is null");
		}
		IdentityQuestion question = new IdentityQuestion();
		question.setIdentityQuestionId(questionId);
		identityQuestDao.remove(identityQuestionDozerConverter.convertToEntity(
				question, true));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.pswd.service.ChallengeResponseService#AnswersByUser
	 * (java.lang.String)
	 */
	public List<UserIdentityAnswer> answersByUser(String userId) {
		if (userId == null) {
			throw new NullPointerException("UserId is null");
		}

		ChallengeResponseValidator responseValidator = respValidatorFactory
				.createValidator(respValidatorObjName, respValidatorObjType);
		return responseValidator.answersByUser(userId);
	}

	public boolean userAnserExists(String userId) {
		List<UserIdentityAnswer> answerList = answersByUser(userId);
		if (answerList == null || answerList.isEmpty()) {
			return false;
		}
		return true;
	}

    @Transactional
	public UserIdentityAnswer addAnswer(UserIdentityAnswer answer) {
		if (answer == null) {
			throw new NullPointerException("Answer object is null");
		}
		return userIdentityAnswerDozerConverter.convertToDTO(identityAnswerDao
				.add(userIdentityAnswerDozerConverter.convertToEntity(answer,
						true)), true);
	}

    @Transactional
	public UserIdentityAnswer updateAnswer(UserIdentityAnswer answer) {
		if (answer == null) {
			throw new NullPointerException("Answer object is null");
		}
		return userIdentityAnswerDozerConverter.convertToDTO(identityAnswerDao
				.update(userIdentityAnswerDozerConverter.convertToEntity(
						answer, true)), true);
	}

    @Transactional (readOnly = true)
	public UserIdentityAnswer getAnswer(String answerId) {
		if (answerId == null) {
			throw new NullPointerException("answerId object is null");
		}
		return userIdentityAnswerDozerConverter.convertToDTO(
				identityAnswerDao.findById(answerId), true);
	}

    @Transactional
	public void removeAnswer(String answerId) {
		if (answerId == null) {
			throw new NullPointerException("answerId object is null");
		}
		UserIdentityAnswer answer = new UserIdentityAnswer();
		answer.setIdentityAnsId(answerId);
		identityAnswerDao.delete(userIdentityAnswerDozerConverter
				.convertToEntity(answer, true));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.pswd.service.ChallengeResponseService#addAnswers
	 * (java.util.List)
	 */
	public void addAnswers(List<UserIdentityAnswer> ansList) {
		if (ansList == null) {
			throw new NullPointerException("anslist is null");
		}
		for (UserIdentityAnswer ans : ansList) {
			addAnswer(ans);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.pswd.service.ChallengeResponseService#isResponseValid
	 * (java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public boolean isResponseValid(String domainId, String login,
			String managedSysId, String questGrpId,
			List<UserIdentityAnswer> newAnswerList) {

		int requiredCorrect = newAnswerList.size();

		// get the password policy to determine how many answers are required.
		Policy polcy = passwordMgr.getPasswordPolicy(domainId, login,
				managedSysId);
		PolicyAttribute attr = polcy.getAttribute("QUEST_ANSWER_CORRECT");

		if (attr != null) {
			if (attr.getValue1() != null && attr.getValue1().length() > 0) {
				requiredCorrect = Integer.parseInt(attr.getValue1());
			}
		}

		/*
		 * Validate that there are no null responses
		 */
		ChallengeResponseUser req = new ChallengeResponseUser();
		if (domainId != null) {
			req.setDomain(domainId);
		}
		if (managedSysId != null) {
			req.setManagedSysId(managedSysId);
		}
		if (login != null) {
			req.setPrincipal(login);
		}
		if (questGrpId != null) {
			req.setQuestionGroup(questGrpId);
		}

		ChallengeResponseValidator responseValidator = respValidatorFactory
				.createValidator(respValidatorObjName, respValidatorObjType);
		return responseValidator.isResponseValid(req, newAnswerList,
				requiredCorrect);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openiam.idm.srvc.pswd.service.ChallengeResponseService#saveAnswers
	 * (java.util.List)
	 */
    @Transactional (propagation = Propagation.REQUIRED)
	public void saveAnswers(List<UserIdentityAnswer> ansList) {
		if (ansList == null) {
			throw new NullPointerException("anslist is null");
		}
		String requestId = "R" + UUIDGen.getUUID();

		ChallengeResponseValidator responseValidator = respValidatorFactory
				.createValidator(respValidatorObjName, respValidatorObjType);
		responseValidator.saveAnswers(ansList);

		// add to audit log and update the user record that challenge response
		// answers have been updated
		// get the user Id
		UserIdentityAnswer ans = ansList.get(0);
		String userId = ans.getUserId();
		User usr = userMgr.getUserWithDependent(userId, false);
		usr.setDateChallengeRespChanged(new Date(System.currentTimeMillis()));
		userMgr.updateUserWithDependent(usr, false);

		auditHelper.addLog("SET CHALLENGE QUESTIONS", null, null,
				"IDM SERVICE", userId, "PASSWORD", "CHALLENGE QUESTION", null,
				null, "SUCCESS", null, null, null, requestId, null, null, null);

	}

	public Cryptor getCryptor() {
		return cryptor;
	}

	public void setCryptor(Cryptor cryptor) {
		this.cryptor = cryptor;
	}

	public PolicyDataService getPolicyDataService() {
		return policyDataService;
	}

	public void setPolicyDataService(PolicyDataService policyDataService) {
		this.policyDataService = policyDataService;
	}

	public IdentityQuestionDAO getIdentityQuestDao() {
		return identityQuestDao;
	}

	public void setIdentityQuestDao(IdentityQuestionDAO identityQuestDao) {
		this.identityQuestDao = identityQuestDao;
	}

	public UserIdentityAnswerDAO getIdentityAnswerDao() {
		return identityAnswerDao;
	}

	public void setIdentityAnswerDao(UserIdentityAnswerDAO identityAnswerDao) {
		this.identityAnswerDao = identityAnswerDao;
	}

	public IdentityQuestGroupDAO getIdentityQuestGroupDao() {
		return identityQuestGroupDao;
	}

	public void setIdentityQuestGroupDao(
			IdentityQuestGroupDAO identityQuestGroupDao) {
		this.identityQuestGroupDao = identityQuestGroupDao;
	}

	public LoginDataService getLoginManager() {
		return loginManager;
	}

	public void setLoginManager(LoginDataService loginManager) {
		this.loginManager = loginManager;
	}

	public ValidatorFactory getRespValidatorFactory() {
		return respValidatorFactory;
	}

	public void setRespValidatorFactory(ValidatorFactory respValidatorFactory) {
		this.respValidatorFactory = respValidatorFactory;
	}

	public String getRespValidatorObjName() {
		return respValidatorObjName;
	}

	public void setRespValidatorObjName(String respValidatorObjName) {
		this.respValidatorObjName = respValidatorObjName;
	}

	public String getRespValidatorObjType() {
		return respValidatorObjType;
	}

	public void setRespValidatorObjType(String respValidatorObjType) {
		this.respValidatorObjType = respValidatorObjType;
	}

	public UserDataService getUserMgr() {
		return userMgr;
	}

	public void setUserMgr(UserDataService userMgr) {
		this.userMgr = userMgr;
	}

	public PasswordService getPasswordMgr() {
		return passwordMgr;
	}

	public void setPasswordMgr(PasswordService passwordMgr) {
		this.passwordMgr = passwordMgr;
	}

	public AuditHelper getAuditHelper() {
		return auditHelper;
	}

	public void setAuditHelper(AuditHelper auditHelper) {
		this.auditHelper = auditHelper;
	}
}
