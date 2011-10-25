<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.oauth2.provider.verification.BasicUserApprovalFilter" %>
<%@ page import="org.springframework.security.oauth2.provider.verification.VerificationCodeFilter" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%--
  ~ The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
  ~
  ~ Copyright (C) 2010-2011, International Institute of Social History
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>Access confirmation</title>
</head>

<body>

  <h1>Access confirmation</h1>

    <% if (session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null && !(session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) instanceof UnapprovedClientAuthenticationException)) { %>
        <h2>Woops!</h2>

        <p>Access could not be granted. (<%= ((AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>)</p>
    <% } %>
    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>

    <authz:authorize ifAllGranted="ROLE_USER">
      <h2>Please Confirm</h2>

      <p>You hereby authorize "${client.clientId}" to access your protected resources.</p>

      <form id="confirmationForm" name="confirmationForm" action="<%=request.getContextPath() + VerificationCodeFilter.DEFAULT_PROCESSING_URL%>" method="POST">
        <input name="<%=BasicUserApprovalFilter.DEFAULT_APPROVAL_REQUEST_PARAMETER%>" value="<%=BasicUserApprovalFilter.DEFAULT_APPROVAL_PARAMETER_VALUE%>" type="hidden"/>
        <label><input name="authorize" value="Authorize" type="submit"></label>
      </form>
      <form id="denialForm" name="denialForm" action="<%=request.getContextPath() + VerificationCodeFilter.DEFAULT_PROCESSING_URL%>" method="POST">
        <input name="<%=BasicUserApprovalFilter.DEFAULT_APPROVAL_REQUEST_PARAMETER%>" value="not_<%=BasicUserApprovalFilter.DEFAULT_APPROVAL_PARAMETER_VALUE%>" type="hidden"/>
        <label><input name="deny" value="Deny" type="submit"></label>
      </form>
    </authz:authorize>

</body>
</html>
