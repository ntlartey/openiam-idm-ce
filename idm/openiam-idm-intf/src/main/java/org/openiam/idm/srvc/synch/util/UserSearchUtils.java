package org.openiam.idm.srvc.synch.util;

import org.openiam.idm.srvc.synch.dto.BulkMigrationConfig;
import org.openiam.idm.srvc.user.dto.DateSearchAttribute;
import org.openiam.idm.srvc.user.dto.UserSearch;
import org.openiam.idm.srvc.user.service.UserDataService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserSearchUtils {

    public static UserSearch buildSearch(BulkMigrationConfig config){
        UserSearch search = new UserSearch();
        if (config.getOrganizationId() != null && !config.getOrganizationId().isEmpty()) {
            search.setOrgId(config.getOrganizationId());
        }

        if (config.getLastName() != null && !config.getLastName().isEmpty()) {
            search.setLastName(config.getLastName() + "%");
        }

        if (config.getDeptId() != null && !config.getDeptId().isEmpty()) {
            search.setDeptCd(config.getDeptId());
        }

        if (config.getDivision() != null && !config.getDivision().isEmpty()) {
            search.setDivision(config.getDivision());
        }

        if (config.getAttributeName() != null && !config.getAttributeName().isEmpty()) {
            search.setAttributeName(config.getAttributeName());
            search.setAttributeValue(config.getAttributeValue());
        }

        if (config.getUserStatus() != null ) {
            search.setStatus(config.getUserStatus().toString());
        }
        if (config.getLastLoginDate() != null ) {
        	DateSearchAttribute dateSearchAttribute = new DateSearchAttribute();
			dateSearchAttribute.setAttributeName(UserDataService.LAST_LOGIN);
			dateSearchAttribute.setOperation(config.getDateOperation());
			dateSearchAttribute.setAttributeValue(config.getLastLoginDate());
			search.addDateSearchAttribute(dateSearchAttribute);
        }
        if (config.getDateOperation() != null && config.getDateOperation().equals("IS NULL") ) {
        	DateSearchAttribute dateSearchAttribute = new DateSearchAttribute();
			dateSearchAttribute.setAttributeName(UserDataService.LAST_LOGIN);
			dateSearchAttribute.setOperation(config.getDateOperation());
			search.addDateSearchAttribute(dateSearchAttribute);
        }
        // allow selection by a role
        if (config.getRole() != null && !config.getRole().isEmpty())     {
            String r = config.getRole();
            int indx = r.indexOf("*");
            String roleId = r.substring(indx+1, r.length()) ;
            String domainId = r.substring(0, indx);

            List<String> roleList = new ArrayList<String>();
            roleList.add(roleId );
            search.setRoleIdList(roleList);
            search.setDomainId(domainId);
        }
        if(config.getMaxResultSize() != null) {
           search.setMaxResultSize(config.getMaxResultSize());
        }
        return search;

    }
}
