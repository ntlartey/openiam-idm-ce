package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Pascal
 * Date: 27.04.12
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public interface ReconciliationCommand {
    boolean execute(Login login, ProvisionUser user, List<ExtensibleAttribute> attributes);
}
