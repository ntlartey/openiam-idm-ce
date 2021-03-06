package org.openiam.idm.srvc.recon.service;

import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;

import java.util.List;

public interface ReconciliationCommand {
    //    IDM_DELETED__SYS_EXISTS   (Record in resource but mark as Deleted in IDM)
    final String IDM_DELETED__SYS_EXISTS = "IDM[deleted] and Resource[exists]";

    //    IDM_EXISTS__SYS_EXISTS   (Record exists in resource and exists in IDM)
    final String IDM_EXISTS__SYS_EXISTS = "IDM[exists] and Resource[exists]";

    //    IDM_EXISTS__SYS_NOT_EXISTS   (Record not exists in resource and exists in IDM)
    final String IDM_EXISTS__SYS_NOT_EXISTS = "IDM[exists] and Resource[not exists]";

    //    SYS_EXISTS__IDM_NOTEXISTS   (Record not exists in IDM and exists in Resource)
    final String SYS_EXISTS__IDM_NOT_EXISTS = "IDM[not exists] and Resource[exists]";

    boolean execute(Login login, ProvisionUser user, List<ExtensibleAttribute> attributes);
}
