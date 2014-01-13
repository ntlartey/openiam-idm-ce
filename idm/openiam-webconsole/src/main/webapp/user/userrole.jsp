<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<table width="900pt">
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td class="pageTitle" width="70%">
                        <h2 class="contentheading">User Management</h2>
                    </td>
                </tr>
            </table>
        </td>
    <tr>
        <td>
            <form:form commandName="userRoleCmd">

                <form:hidden path="perId"/>

                <table width="900pt">
                    <tr>
                        <td align="center" height="100%">
                            <fieldset class="userform">
                                <legend>SELECT ROLES FOR USER</legend>

                                <table class="resourceTable" cellspacing="2" cellpadding="2" width="100%">

                                    <table width="100%">
                                        <tr class="header">
                                            <th>Role ID</th>
                                            <th>Role Name</th>
                                            <th>Type</th>
                                            <th>Parent Role</th>
                                            <th>Status</th>
                                        </tr>
                                        <c:forEach items="${userRoleCmd.roleList}" var="roleList" varStatus="role">

                                            <tr>
                                                <td class="tableEntry">
                                                    <form:checkbox path="roleList[${role.index}].selected"/>
                                                        ${roleList.id.serviceId} - ${roleList.id.roleId}
                                                    <form:hidden path="roleList[${role.index}].id.serviceId"/>
                                                    <form:hidden path="roleList[${role.index}].id.roleId"/>
                                                </td>
                                                <td class="tableEntry">${roleList.roleName}</td>
                                                <td class="tableEntry">${roleList.metadataTypeId}</td>
                                                <td class="tableEntry">${roleList.parentRoleId}</td>
                                                <td class="tableEntry">${roleList.status}</td>
                                            </tr>

                                        </c:forEach>
                                    </table>
                                    </td>
                                    </tr>


                                    <tr>
                                        <td colspan="4" align="right">
                                            <input type="submit" value="Save"/> <input type="submit" name="_cancel"
                                                                                       value="Cancel"/>
                                        </td>
                                    </tr>

                                </table>
                                </fieldset>
                        </TD>
                    </TR>
                </TABLE>

            </form:form>

        </td>
    </tr>
</table>
