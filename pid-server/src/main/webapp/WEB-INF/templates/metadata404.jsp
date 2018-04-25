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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>PID not found</title>
    <style type="text/css">
        textarea {
            width: 100%;
            height: 400px;
        }
    </style>
</head>
<body>
<h1>${pid}</h1>

<p>Pid not found</p>
</body>

</html>
