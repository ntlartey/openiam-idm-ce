
/*
 * 
 */

package org.openiam.idm.srvc.org.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import java.util.List;
import java.util.Map;

import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.org.dto.OrganizationAttributeMapAdapter;

/**
 * This class was generated by Apache CXF 2.1.1
 * Tue Jul 01 12:04:18 EDT 2008
 * Generated source version: 2.1.1
 * 
 */
 
 /*
  * 
  */


@WebService(targetNamespace = "urn:idm.openiam.org/srvc/org/service", name = "OrganizationDataService")
//@XmlSeeAlso({org.openiam.idm.srvc.user.dto.ObjectFactory.class,org.openiam.idm.srvc.continfo.dto.ObjectFactory.class,org.openiam.idm.srvc.org.dto.ObjectFactory.class,org.openiam.idm.srvc.org.types.ObjectFactory.class,org.openiam.idm.srvc.meta.dto.ObjectFactory.class})

public interface OrganizationDataService {

/*
 * 
 */

    @RequestWrapper(localName = "updateOrganization", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.UpdateOrganization")
    @ResponseWrapper(localName = "updateOrganizationResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.UpdateOrganizationResponse")
    @WebMethod
    public void updateOrganization(
        @WebParam(name = "organization", targetNamespace = "")
        org.openiam.idm.srvc.org.dto.Organization organization
    );

/*
 * 
 */

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getTopLevelOrganizations", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.GetTopLevelOrganizations")
    @ResponseWrapper(localName = "getTopLevelOrganizationsResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.GetTopLevelOrganizationsResponse")
    @WebMethod
    public java.util.List<org.openiam.idm.srvc.org.dto.Organization> getTopLevelOrganizations();

/*
 * 
 */

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "subOrganizations", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.SubOrganizations")
    @ResponseWrapper(localName = "subOrganizationsResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.SubOrganizationsResponse")
    @WebMethod
    public java.util.List<org.openiam.idm.srvc.org.dto.Organization> subOrganizations(
        @WebParam(name = "orgId", targetNamespace = "")
        java.lang.String orgId
    );

/*
 * 
 */

    @RequestWrapper(localName = "removeAttribute", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.RemoveAttribute")
    @ResponseWrapper(localName = "removeAttributeResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.RemoveAttributeResponse")
    @WebMethod
    public void removeAttribute(
        @WebParam(name = "organizationAttribute", targetNamespace = "")
        org.openiam.idm.srvc.org.dto.OrganizationAttribute organizationAttribute
    );

/*
 * 
 */

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getOrganization", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.GetOrganization")
    @ResponseWrapper(localName = "getOrganizationResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.GetOrganizationResponse")
    @WebMethod
    public org.openiam.idm.srvc.org.dto.Organization getOrganization(
        @WebParam(name = "orgId", targetNamespace = "")
        java.lang.String orgId
    );

/*
 * 
 */

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "search", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.Search")
    @ResponseWrapper(localName = "searchResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.SearchResponse")
    @WebMethod
    public java.util.List<org.openiam.idm.srvc.org.dto.Organization> search(
        @WebParam(name = "name", targetNamespace = "")
        java.lang.String name,
        @WebParam(name = "type", targetNamespace = "")
        java.lang.String type
    );

/*
 * 
 */

    @RequestWrapper(localName = "addOrganization", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.AddOrganization")
    @ResponseWrapper(localName = "addOrganizationResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.AddOrganizationResponse")
    @WebMethod
    public void addOrganization(
        @WebParam(name = "organization", targetNamespace = "")
        org.openiam.idm.srvc.org.dto.Organization organization
    );

/*
 * 
 */

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "getAttribute", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.GetAttribute")
    @ResponseWrapper(localName = "getAttributeResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.GetAttributeResponse")
    @WebMethod
    public org.openiam.idm.srvc.org.dto.OrganizationAttribute getAttribute(
        @WebParam(name = "attributeId", targetNamespace = "")
        java.lang.String attributeId
    );

/*
 * 
 */

    @RequestWrapper(localName = "addAttribute", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.AddAttribute")
    @ResponseWrapper(localName = "addAttributeResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.AddAttributeResponse")
    @WebMethod
    public void addAttribute(
        @WebParam(name = "organizationAttribute", targetNamespace = "")
        org.openiam.idm.srvc.org.dto.OrganizationAttribute organizationAttribute
    );

/*
 * 
 */

    @RequestWrapper(localName = "updateAttribute", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.UpdateAttribute")
    @ResponseWrapper(localName = "updateAttributeResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.UpdateAttributeResponse")
    @WebMethod
    public void updateAttribute(
        @WebParam(name = "organizationAttribute", targetNamespace = "")
        org.openiam.idm.srvc.org.dto.OrganizationAttribute organizationAttribute
    );

/*
 * 
 */

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "containsChildren", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.ContainsChildren")
    @ResponseWrapper(localName = "containsChildrenResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.ContainsChildrenResponse")
    @WebMethod
    public boolean containsChildren(
        @WebParam(name = "orgId", targetNamespace = "")
        java.lang.String orgId
    );

/*
 * 
 */

    @WebResult(name = "return", targetNamespace = "")
    @RequestWrapper(localName = "isRootOrganization", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.IsRootOrganization")
    @ResponseWrapper(localName = "isRootOrganizationResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.IsRootOrganizationResponse")
    @WebMethod
    public boolean isRootOrganization(
        @WebParam(name = "orgId", targetNamespace = "")
        java.lang.String orgId
    );

/*
 * 
 */

    @RequestWrapper(localName = "removeOrganization", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.RemoveOrganization")
    @ResponseWrapper(localName = "removeOrganizationResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.RemoveOrganizationResponse")
    @WebMethod
    public void removeOrganization(
        @WebParam(name = "orgId", targetNamespace = "")
        java.lang.String orgId
    );

/*
 * 
 */

    @RequestWrapper(localName = "removeAllAttributes", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.RemoveAllAttributes")
    @ResponseWrapper(localName = "removeAllAttributesResponse", targetNamespace = "urn:idm.openiam.org/srvc/org/types", className = "org.openiam.idm.srvc.org.types.RemoveAllAttributesResponse")
    @WebMethod
    public void removeAllAttributes(
        @WebParam(name = "orgId", targetNamespace = "")
        java.lang.String orgId
    );

/*
 * 
 */
    // Note: do not include response wrapper if we plan to use Map and Adapter class
    @WebMethod
    @XmlJavaTypeAdapter(OrganizationAttributeMapAdapter.class) 
    public Map<String,org.openiam.idm.srvc.org.dto.OrganizationAttribute> getAllAttributes(
        @WebParam(name = "orgId", targetNamespace = "")
        java.lang.String orgId
    );
    
    List<Organization> getOrganizationByType(String typeId);
    
    List<Organization> getAllOrganizations();
}
