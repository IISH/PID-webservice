<%--
~ The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
~
~ Copyright (C) 2010-2012, International Institute of Social History
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
<jsp:useBean id="pid" scope="request" type="java.lang.String"/>
<jsp:useBean id="handles" scope="request" type="java.util.List"/>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>PID not found</title>
    <style type="text/css">
        h2 {
            font-size: small;
        }

        .cursor {
            cursor: pointer;
            width: 100px
        }
    </style>
    <script type="text/javascript">var toggle=['100px','400px'];var i=1</script>
</head>
<body>
<h1>${pid}</h1>

<table>
    <%--type | handle | resolveUrl | qr image--%>
    <c:forEach var="handle" items="${handles}">
        <c:if test="${fn:containsIgnoreCase(handle[0],'URL') || fn:containsIgnoreCase(handle[0],'LOC')}">
            <tr>
                <td><img class="cursor" alt="quick response matrix" title=""
                         src="/qr/${handle[3]}" onclick="this.style.width=toggle[i++ % 2]"/></td>
                <td>handle: <a href="${handle[1]}" target="_blank">${handle[1]}</a>
                    <br/>
                    Resolve url: <a href="${handle[2]}" target="_blank">${handle[2]}</a></td>
            </tr>
        </c:if>
    </c:forEach>
</table>

</body>

</html>
