<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="c2" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    final String bearer = org.springframework.security.oauth2.common.OAuth2AccessToken.BEARER_TYPE;
%>

<%--
  ~ The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
  ~
  ~ Copyright (C) 2010-2018, International Institute of Social History
  ~                                        Pid webservice
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
    <title>Webservice keys</title>
    <style type="text/css">
        th, td {
            text-align: left;
            vertical-align: top;
        }
    </style>
</head>

<body><h1>Webservice keys</h1>

<p style="text-align:right"><a href="/">home</a>|
    <a href="admin/logout.do">logout</a></p>
<table>
    <tr>
        <th style="width:100px;">your naming authorities</th>
        <th>key</th>
        <th>valid until</th>
        <th>action</th>
    </tr>
    <tr>
        <td>
            <c2:forEach items="${nas}" var="na">
                ${na}<br/>
            </c2:forEach>
        </td>
        <td><input type="text" size="50" value="${token.value}" onfocus="this.select();"/></td>
        <td><fmt:formatDate value="${token.expiration}" pattern="yyyy-MM-dd"/></td>
        <td><a href="?token=${token.refreshToken}">change this key</a></td>
    </tr>
</table>
<p>If you know or feel your key is compromised in some way, just use the refresh option. This will generate a new key for
    you.</p>
<hr/>
<p>Place the key in a HTTP header request as expressed in this pseude code:<br/>
    HTTP-header("Authorization", "<%=bearer%> ${token.value}")</p>

<form action="<c:url value="admin/logout.do"/>"><input type="submit" value="Logout"></form>
</body>

</html>