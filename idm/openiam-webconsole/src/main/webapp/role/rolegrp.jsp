<%@ page language="java" contentType="text/html; charset=utf-8"     pageEncoding="utf-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



<table  width="600pt" >
    <tr>
        <td>
            <table width="100%">
                <tr>
                    <td class="pageTitle" width="70%">
                        <h2 class="contentheading">Associate Groups to Role: ${roleid}</h2>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <% if (request.getAttribute("msg") != null) { %>
    <tr>
        <td colspan="2" align="center" class="msg" >
            <%=request.getAttribute("msg") %>
        </td>
    </tr>
    <% } %>

    <tr>
        <td>

            <form:form commandName="roleGroupCmd">
                <form:hidden path="roleId" />
                <form:hidden path="domainId" />

                <table width="600pt">
                    <tr>
                        <td align="center" height="100%">
                            <fieldset class="userform" >
                                <legend>SELECT BASE GROUP</legend>

                                <table class="fieldsetTable"  width="600pt" >

                                    <tr>
                                        <td class="plaintext">Select Group:</td>
                                        <td>

                                            <form:select path="groupId" multiple="false">
                                                <form:option value="" label="-Please Select-"/>
                                                <form:options items="${rootGroupList}" itemValue="grpId" itemLabel="grpName"/>
                                            </form:select>

                                        </td>
                                    </tr>
                                    <tr>
                                        <td></td>
                                        <td class="error"><form:errors path="groupId" /></td>
                                    </tr>
                                </table>
                            </fieldset>
                    </tr>
                    <tr class="buttonRow">
                        <td align="right"> <input type="submit" name="formBtn" value="Search"> <input type="submit" name="_cancel" value="Cancel" />  </td>
                    </tr>
                </table>
            </form:form>


            <table width="600pt">
                <tr>
                    <td align="center" height="100%">
                        <fieldset class="userform" >
                            <legend>ASSOCIATE GROUPS TO A ROLE</legend>


                            <table class="resourceTable" cellspacing="2" cellpadding="2" width="600pt">
                                <tr class="header">
                                    <th colspan="2">GROUP Name</th>
                                    <th></th>

                                </tr>
                                <c:if test="${groupList != null}" >
                                    <c:forEach items="${groupList}" var="grp">

                                        <tr class="plaintext">
                                            <td class="tableEntry">
                                                <c:choose>
                                                    <c:when test="${grp.selected}">
                                                        <img src="images/checkbox.png">
                                                    </c:when>
                                                    <c:otherwise>

                                                    </c:otherwise>
                                                </c:choose>



                                            </td>
                                            <td class="tableEntry"> ${grp.grpName}
                                            </td>
                                            <td class="tableEntry">
                                                <c:choose>
                                                    <c:when test="${!grp.selected}">
                                                        <a href="updateRoleMembership.cnt?objtype=GROUP&action=ADD&role=${roleid}&domain=${domainid}&objid=${grp.grpId}&parentGrp=${parentGrp}">ADD to Role</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a href="updateRoleMembership.cnt?objtype=GROUP&action=DEL&role=${roleid}&domain=${domainid}&objid=${grp.grpId}&parentGrp=${parentGrp}">REMOVE from Role</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>

                                    </c:forEach>
                                </c:if>

                            </table>
                        </fieldset>
                    </td>
                </tr>
            </table>

        </td>
    </tr>
</table>
</div>
