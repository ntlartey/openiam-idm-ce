package org.openiam.webconsole.web.validator;

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

import org.openiam.webconsole.web.model.GroupDetailCommand;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: Alexander Duckardt<br/>
 * Date: 09/06/12
 */
@Component
public class GroupDetailValidator implements Validator {

    public boolean supports(Class cls) {
        return GroupDetailCommand.class.equals(cls);
    }

    public void validate(Object cmd, Errors err) {
        GroupDetailCommand command = (GroupDetailCommand) cmd;

        if (command.getGroup().getGrpName() == null
                || command.getGroup().getGrpName().length() == 0) {
            err.rejectValue("group.grpName", "required");
        }
    }
}
