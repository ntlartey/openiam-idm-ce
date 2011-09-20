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
package org.openiam.idm.srvc.auth.ws;

import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.Subject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Response object for a web service operation that returns a role.
 * @author suneet
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthzResponse", propOrder = {
    "authorized",
    "authErrorCode",
    "authErrorMessage",
    "status"
})
public class AuthzResponse {

	@XmlAttribute(required = true)
    protected ResponseStatus status;
    protected boolean authorized;

		protected int authErrorCode;
	protected String authErrorMessage;

	public AuthzResponse() {
		super();
	}


    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }


    public int getAuthErrorCode() {
        return authErrorCode;
    }

    public void setAuthErrorCode(int authErrorCode) {
        this.authErrorCode = authErrorCode;
    }

    public String getAuthErrorMessage() {
        return authErrorMessage;
    }

    public void setAuthErrorMessage(String authErrorMessage) {
        this.authErrorMessage = authErrorMessage;
    }
}
