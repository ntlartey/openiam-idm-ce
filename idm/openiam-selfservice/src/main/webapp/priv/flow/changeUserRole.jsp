<%@ page language="java" contentType="text/html; charset=utf-8"     pageEncoding="utf-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>



<form:form commandName="changeUserRole" cssClass="profile">
    <fieldset>
        <div class="block">
            <div class="wrap alt">
                <p>Request Role Membership</p>
                <!-- column 1  -->
                <div class="col">
                    <div class="row">
                        <label for="t-1">Operation:<span>*</span></label>
                        <form:select path="operation" multiple="false">
                            <form:option value="ADD" label="ADD"/>
                            <form:option value="REMOVE" label="REMOVE"/>
                        </form:select>
                    </div>
                    <div class="row">
                        <label for="t-2">Role Membership:</label>
                        <form:select path="roleId" multiple="false">
                            <form:option value="" label="-Please Select-"/>
                            <c:forEach items="${roleList}" var="role">
                                <form:option value="${role.id.serviceId}*${role.id.roleId}" label="${role.id.serviceId}-${role.roleName}" />
                            </c:forEach>
                        </form:select>
                        <p><form:errors path="roleId"/></p>
                    </div>
                </div>
                <div class="col">
                    <!-- show the users current group membership -->
                    <p> ${changeUserRole.selectedUser.firstName} ${changeUserRole.selectedUser.lastName} is currently a member of the following Roles:</p>
                    <c:forEach items="${changeUserRole.currentRoleMemberships}" var="role" varStatus="roleStatus">
                        <div class="row">
                        ${role.id.serviceId}-${role.roleName}
                        </div>

                    </c:forEach>
                </div>
            </div>
        </div>

        <div class="button">
            <input type="submit" value="Save" name="btnSave">
        </div>

        <div class="button">
            <input type="submit" name="_cancel" value="Cancel" />
        </div>

    </fieldset>
</form:form>
