package org.openiam.idm.srvc.grp.dto;

// Generated Jul 18, 2009 8:49:09 AM by Hibernate Tools 3.2.2.GA

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import org.openiam.dozer.DozerDTOCorrespondence;
import org.openiam.idm.srvc.grp.domain.UserGroupEntity;

/**
 * Object used for representing the Group-User relationship
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "usergroup", propOrder = {
        "userGrpId",
        "grpId",
        "userId",
        "createDate",
        "status",
        "createdBy"
})
@DozerDTOCorrespondence(UserGroupEntity.class)
public class UserGroup implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5686110876382504665L;
    protected String userGrpId;
    protected String grpId;
    protected String userId;
    protected String status;
    @XmlSchemaType(name = "dateTime")
    protected Date createDate;
    protected String createdBy;

    public UserGroup() {
    }

    public UserGroup(String grpId, String userId) {
        this.grpId = grpId;
        this.userId = userId;
        status = "ACTIVE";
        createDate = new Date(System.currentTimeMillis());
    }

    public UserGroup(String userGrpId, String grpId, String userId,
                     String status, Date createDate, String createdBy) {
        this.userGrpId = userGrpId;
        this.grpId = grpId;
        this.userId = userId;
        this.status = status;
        this.createDate = createDate;
        this.createdBy = createdBy;
    }

    public UserGroup(UserGroupEntity groupEntity) {
        this.userGrpId = groupEntity.getUserGrpId();
        this.grpId = groupEntity.getGroup() != null ? groupEntity.getGroup().getGrpId() : "";
        this.status = groupEntity.getStatus();
        this.createDate = groupEntity.getCreateDate();
        this.createdBy = groupEntity.getCreatedBy();
        this.userId = groupEntity.getUser() != null ? groupEntity.getUser().getUserId() : "";
    }

    public String getUserGrpId() {
        return this.userGrpId;
    }

    public void setUserGrpId(String userGrpId) {
        this.userGrpId = userGrpId;
    }

    public String getGrpId() {
        return this.grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
