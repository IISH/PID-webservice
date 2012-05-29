<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<form id='loginForm' name='loginForm' action='<c:url value="login.do"/>' method='POST'>
    <p>Login with</p>
    <p><label>Username: <input type='text' name='j_username' value=''></label></p>

    <p><label>Password: <input type='password' name='j_password' value=''></label></p>

    <p><input name='login' value='Login' type='submit'></p>
</form>
<hr/>
<form id="loginLink" name="loginLink" action="oauth/keys" method="post">
    <label>Or use a webservice key: <input type="password" id="access_token" name="access_token" value="" size="50"></label>

    <p><input name='login' value='Login' type='submit'></p>
</form>