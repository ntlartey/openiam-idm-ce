/*
 * Copyright 2013, OpenIAM LLC
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
package org.openiam.idm.srvc.pswd.rule;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.PasswordHistoryDozerConverter;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.dto.Password;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;
import org.openiam.idm.srvc.pswd.dto.PasswordValidationCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Validates a password to ensure that is conforms to the history rules.
 *
 * @author suneet
 */
public class PasswordHistoryRule extends AbstractPasswordRule {

    private static final Log log = LogFactory.getLog(PasswordHistoryRule.class);
    //@Autowired
    //private PasswordHistoryDozerConverter passwordHistoryDozerConverter;

    public PasswordValidationCode isValid() {

        log.info("PasswordHistoryRule called.");

        PasswordValidationCode retval = PasswordValidationCode.SUCCESS;
        boolean enabled = false;

        PolicyAttribute attribute = policy.getAttribute("PWD_HIST_VER");
        if (attribute.getValue1() != null && attribute.getValue1().length() > 0) {
            enabled = true;

        }

        if (enabled) {
            log.info("password history rule is enabled.");
            Password pswd = new Password();
            pswd.setDomainId(lg.getId().getDomainId());
            pswd.setManagedSysId(lg.getId().getManagedSysId());
            pswd.setPrincipal(lg.getId().getLogin());
            pswd.setPassword(password);

            int version = Integer.parseInt(attribute.getValue1());

            List<PasswordHistoryEntity> historyEntityList = passwordHistoryDao.findPasswordHistoryByPrincipal(
                    pswd.getDomainId(), pswd.getPrincipal(), pswd.getManagedSysId(), version);
            if (historyEntityList == null || historyEntityList.isEmpty()) {
                // no history
                return retval;
            }
            List<PasswordHistory> historyList = passwordHistoryDozerConverter.convertToDTOList(historyEntityList, true);

            // check the list.
            log.info("Found " + historyList.size() + " passwords in the history");
            for (PasswordHistory hist : historyList) {
                String pwd = hist.getPassword();
                String decrypt = null;
                try {
                    decrypt = cryptor.decrypt(pwd);
                } catch (EncryptionException e) {
                    log.error("PasswordHistoryRule failed due to decrption error. ");
                    return PasswordValidationCode.FAIL_HISTORY_RULE;
                }
                if (pswd.getPassword().equals(decrypt)) {
                    log.info("matching password found.");
                    return PasswordValidationCode.FAIL_HISTORY_RULE;
                }
            }


        }

        return retval;

    }


}
