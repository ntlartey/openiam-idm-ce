package org.openiam.webadmin.res;
/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the Lesser GNU General Public License 
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


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validation class for the Resources.
 *
 * @author suneet
 */
public class ResourceDetailValidator implements Validator {


    public boolean supports(Class cls) {
        return ResourceDetailCommand.class.equals(cls);
    }

    public void validate(Object cmd, Errors err) {
        // TODO Auto-generated method stub


        ResourceDetailCommand connectionCommand = (ResourceDetailCommand) cmd;

/*		if (connectionCommand.getStartDt() != null && connectionCommand.getEndDt() != null ) {
            if (connectionCommand.getStartDt().after( connectionCommand.getEndDt())) {
                err.rejectValue("startDate","invalidRange");
            }
        }
        // name
        if (connectionCommand.getName() == null || connectionCommand.getName().length() == 0 ) {
            err.rejectValue("name","required");
        }
        // connectorId
        if (connectionCommand.getConnectorId().equals("-")) {
            err.rejectValue("connectorId","required");
        }
*/
    }


}
