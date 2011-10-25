<%@ page import="org.aspectj.weaver.patterns.PerFromSuper" %>
<%@ page import="com.sun.corba.se.spi.orbutil.fsm.FSM" %>
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

<%
    final StringBuffer requestURL = request.getRequestURL();
    final String requestURI = request.getRequestURI();
    final String baseUrl = requestURL.substring(0, requestURL.length() - requestURI.length()) + "/";
    final String service = "[Service name here]";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><%=service%></title>
    <style type="text/css">
        textarea {
            width: 100%;
            height: 400px;
        }
    </style>
</head>
<body>

<h1><%=service%>: <%=baseUrl%>
</h1>

<p style="text-align:right">
    <a href="oauth/keys">Manage your keys
    </a>
    |
    <a href="logout.do">logout</a>
</p>

<h2>1. What is the <%=service%></h2>

<p>The Pid webservice is a driver for
    <a href="http://www.handle.net/">Handle System's</a>
    resolver technology. It eases
    the creation of persistent identifiers and their bindings to attributes such as web locations with the use of a Soap
    protocol.
    This means
    it is system agnostic: an external application offering web methods that can be integrated into your catalog and
    archival system
    solutions. All with the purpose to
    offer a consistent delivery via pids of your web resources to yourself, your users and external discovery
    services
    alike.</p>

<p><a href="http://www.handle.net/"><img src="images/hs_logo_bundle2.gif" alt="icon" title=""
                                         width="64px" height="21px" style="border: 1px solid #000000"/></a></p>

<h2>2. Who operates the <%=service%></h2>

<p>It is a shared service run by the [your detalis here]. Hence as a
    member you
    you can use the <%=service%>.
</p>

<p>If you
    already obtained a naming authority from Handle System naming authority, then don't forget to add that
    prefix that
    came with the submission form. If not,
    our friendly <%=service%> will emancipate you with a naming authority.
</p>

<h2>3. Webservice keys</h2>

<p>Access to the webservice is HTTP over SSL. A <a href="oauth/keys">private webservice key</a> is needed to be able to
    create and update
    your
    pids. This
    single key can be changed at any time you want, should it be compromised. You can manage pids from different
    naming authorities ( for example a naming authority for production pids, and another for testing).
    There
    are two ways to obtain webservice keys:
</p>
<ul>
    <li>The pid webservice offers an Oauth 2 protocol with which to distribute and refresh access tokens. The Oauth
        entry
        point is at
        <a
                href="/oauth/"><%=baseUrl%>oauth/</a>. The Oauth consumer client id must be
        "socialhistoryservices.org".
    </li>
    <li>An elegant administration page at
        <a href="/oauth/keys"><%=baseUrl%>oauth/keys</a>
        to produce keys .
    </li>
</ul>
<p>Place the key in a HTTP header request as expressed in this pseude code:</p>

<p>
    HTTP-header(headerName, headerValue) = { "Authorization", "oauth [key]" }
    <br/>
    Whereby [key] is the webservice key. For example, if the key is 12345, the header is:
    <br/>
    HTTP-header("Authorization", "oauth 12345")
</p>

<iframe width="425" height="349" src="http://www.youtube.com/embed/BC1noSxy59c" frameborder="0"
        allowfullscreen></iframe>

<h2>4. Discover the webservice</h2>

<p>The Pid webservice offers seven operations:</p>
<ol>
    <li>Creation and update of pids using an Upsert method</li>
    <li>Creation of pids (automatic and custom) and its bind properties: single resolve url, set of resolvable
        urls, or
        local identifier.
    </li>
    <li>Update of pids and its bindings</li>
    <li>Lookup of bindings via a pid</li>
    <li>Reverse lookup of a pid via resolve url or an attribute (local identifier)</li>
    <li>Quick binding</li>
    <li>Deletion of pids</li>
</ol>


<p>Use Soap to operate the webservice API:</p>
<ul>
    <li>WSDL document is at
        <a href="pid.wsdl"><%=baseUrl%>pid.wsdl</a>
    </li>
    <li>and the webservice endpoint is located at
        <a href="secure/"><%=baseUrl%>secure/</a>
    </li>
    <li>Also see the
        <a href="javadoc/">Java docs of the API methods</a>
    </li>
</ul>

<h2>5. Examples</h2>

<h3>5.1. Create a Pid with a resolve Url</h3>
<iframe width="425" height="349" src="http://www.youtube.com/embed/ETj3GbBCCwY" frameborder="0"
        allowfullscreen></iframe>

<p>The following example will create a pid 10622.1/32dc9242-a978-43b0-befd-831fa02af673 Once created, the url handle:
    http://hdl.handle.net/10622.1/32dc9242-a978-43b0-befd-831fa02af673 would resolve to the url
    http://socialhistoryservices.org/</p>

<form><textarea>Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://pid.socialhistoryservices.org/">
        <soapenv:Body>
            <pid:CreatePidRequest>
                <pid:na>10622.1</pid:na>
                <pid:handle>
                    <pid:resolveUrl>http://socialhistoryservices.org/</pid:resolveUrl>
                </pid:handle>
            </pid:CreatePidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:CreatePidResponse xmlns:ns2="http://pid.socialhistoryservices.org/">
                <ns2:handle>
                    <ns2:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:CreatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope></textarea></form>
<p>You can also create the PIDs yourself as demonstrated in this movie:</p>
<iframe width="425" height="349" src="http://www.youtube.com/embed/8kOtNau-VdM" frameborder="0"
        allowfullscreen></iframe>
<h3>5.1.2 Create a custom pid with multiple urls</h3>
This example demonstrates a custom pid that is bound to three resolve urls. This will make possible three ways of
resolving with one pid:
<ol>
    <li>http://hdl.handle.net/10622.1/EU:ARCHIVE83:ITEM23:FILE3</li>
    <li>http://hdl.handle.net/10622.1/EU:ARCHIVE83:ITEM23:FILE3?view=master</li>
    <li>http://hdl.handle.net/10622.1/EU:ARCHIVE83:ITEM23:FILE3?view=thumbnail</li>
</ol>

<form><textarea>Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://pid.socialhistoryservices.org/">
        <soapenv:Body>
            <pid:CreatePidRequest>
                <pid:na>10622.1</pid:na>
                <pid:handle>
                    <pid:pid>10622.1/EU:ARCHIVE83:ITEM23:FILE3</pid:pid>
                    <pid:resolveUrl>http://socialhistoryservices.org/</pid:resolveUrl>
                    <pid:locAtt>
                        <pid:location href="http://www.archivalius.org?id=original83.23.3" view="master"/>
                        <pid:location href="http://www.archivalius.org?id=image83.23.3.jpg" view="thumbnail"/>
                    </pid:locAtt>
                    <pid:localIdentifier>?</pid:localIdentifier>
                </pid:handle>
            </pid:CreatePidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Header/>
        <SOAP-ENV:Body>
            <ns2:CreatePidResponse xmlns:ns2="http://pid.socialhistoryservices.org/">
                <ns2:handle>
                    <ns2:pid>10622.1/EU:ARCHIVE83:ITEM23:FILE3</ns2:pid>
                    <ns2:locAtt>
                        <ns2:location href="http://www.archivalius.org?id=original83.23.3" view="master"/>
                        <ns2:location href="http://www.archivalius.org?id=image83.23.3.jpg" view="thumbnail"/>
                        <ns2:location href="http://socialhistoryservices.org/" weight="100"/>
                    </ns2:locAtt>
                    <ns2:localIdentifier>?</ns2:localIdentifier>
                </ns2:handle>
            </ns2:CreatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
</textarea></form>

<p>You do not need the locatt expicitly mentioned in the handle to redirect endusers to the desired locations. The
    country attribute can be used to direct
    users using GeoIP. We demonstrate this here:</p>
<iframe width="425" height="349" src="http://www.youtube.com/embed/_lTPPlbwQ00" frameborder="0"
        allowfullscreen></iframe>

<h3>5.2 Update a Pid with a new resolve Url</h3>

<p>To change a resolve url, use the update method</p>

<form><textarea>Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://pid.socialhistoryservices.org/">
        <soapenv:Body>
            <pid:UpdatePidRequest>
                <pid:handle>
                    <pid:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</pid:pid>
                    <pid:resolveUrl>http://new-domain/</pid:resolveUrl>

                </pid:handle>
            </pid:UpdatePidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:UpdatePidResponse xmlns:ns2="http://pid.socialhistoryservices.org/">
                <ns2:handle>
                    <ns2:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</ns2:pid>
                    <ns2:resolveUrl>http://new-domain/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:UpdatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
</textarea></form>

<h3>5.3.1 Lookup bound attributes of a known pid</h3>
<iframe width="425" height="349" src="http://www.youtube.com/embed/ACHhmHoMFMk" frameborder="0"
        allowfullscreen></iframe>

<p>To know that bindings exist for a given pid, use the getPid method. In this example we find out that the resolve url
    is
    http://new-domain/</p>

<form><textarea>Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://pid.socialhistoryservices.org/">
        <soapenv:Body>
            <pid:GetPidRequest>
                <pid:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</pid:pid>
            </pid:GetPidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:GetPidResponse xmlns:ns2="http://pid.socialhistoryservices.org/">
                <ns2:handle>
                    <ns2:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</ns2:pid>
                    <ns2:resolveUrl>http://new-domain/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:GetPidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope></textarea></form>


<h3>5.3.2 Lookup a pid from its know resolve urls</h3>
<iframe width="425" height="349" src="http://www.youtube.com/embed/mXRnp94TnVw" frameborder="0"
        allowfullscreen></iframe>
<p>It is possible to find a pid through it's bound attributes such as resolve urls. In this example we look for pids
    that are bound to the resolve
    url http://socialhistoryservices.org/. Note that the reverse lookup is case sensitive and the result set is limited
    to
    10 records.</p>

<form><textarea>Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://pid.socialhistoryservices.org/">
        <soapenv:Body>
            <pid:GetPidByAttributeRequest>
                <pid:na>10622.1</pid:na>
                <pid:attribute>http://socialhistoryservices.org/</pid:attribute>
            </pid:GetPidByAttributeRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:GetPidByAttributeResponse xmlns:ns2="http://pid.socialhistoryservices.org/">
                <ns2:handle>
                    <ns2:pid>10622.1/7f0f3216-ee06-4cd8-8b15-f5790e40a62e</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/1fac4977-9f52-422e-b84c-7d55db71574b</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/f056a39a-398c-4d38-9933-a78f0691354b</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/f4511e6c-7c94-41ab-b59a-ce708f26f26c</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/aaa3481b-442e-416e-bb87-91dcfcd5a51c</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/19ef01a2-8c0a-4932-b39e-b62c8362fa30</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/fcfd6272-3084-4308-bcea-ae47698d497b</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/2fd87b05-fbda-4945-b696-9843c3a7fdb3</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:GetPidByAttributeResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope></textarea></form>

<h3>5.3.3 Lookup a pid from its know local identifiers</h3>

<p>It is possible to bind other attributes to a pid, such as a local identifier or any other tag for that matter. In
    this
    case, the local identifier needed to be set when creating or updating the pid.</p>

<p>For example, if the local identifier was 12345 and bound like this:</p>

<form><textarea>Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://pid.socialhistoryservices.org/">
        <soapenv:Body>
            <pid:CreatePidRequest>
                <pid:na>10622.1</pid:na>
                <pid:handle>
                    <pid:resolveUrl>http://socialhistoryservices.org/</pid:resolveUrl>
                    <pid:localIdentifier>12345</pid:localIdentifier>
                </pid:handle>
            </pid:CreatePidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:CreatePidResponse xmlns:ns2="http://pid.socialhistoryservices.org/">
                <ns2:handle>
                    <ns2:pid>10622.1/f855fcd2-c503-4dd5-a8f6-d87845b75fb0</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                    <ns2:localIdentifier>12345</ns2:localIdentifier>
                </ns2:handle>
            </ns2:CreatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
</textarea></form>
<p>Then the reverse lookup would be:</p>

<form><textarea>Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://pid.socialhistoryservices.org/">
        <soapenv:Body>
            <pid:GetPidByAttributeRequest>
                <pid:na>10622.1</pid:na>
                <pid:attribute>12345</pid:attribute>
            </pid:GetPidByAttributeRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:GetPidByAttributeResponse xmlns:ns2="http://pid.socialhistoryservices.org/">
                <ns2:handle>
                    <ns2:pid>10622.1/f855fcd2-c503-4dd5-a8f6-d87845b75fb0</ns2:pid>
                    <ns2:resolveUrl>http://socialhistoryservices.org/</ns2:resolveUrl>
                    <ns2:localIdentifier>12345</ns2:localIdentifier>
                </ns2:handle>
            </ns2:GetPidByAttributeResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
</textarea></form>

<h2>5.4 Quick pid method</h2>

<p>Unless you are an service provider whose clients cannot supply anything else but local identifiers, this method would
    not be useful to an organization delivering pids. The GetQuickPid method utilizes the earlier mentioned methods.
    Specifically:</p>
<ol>
    <li>Pid creation: when the localIdentifier is not bound to a known pid, the webservice creates a pid and
        then binds it to the resolveUrl and localIdentifier.
    </li>
    <li>Pid lookup: when the localIdentifier is bound to an existing Pid, the method will echo back all data
        bound to the pid.
    </li>
    <li>Pid update: when the localIdentifier is bound to an existing Pid and the supplied resolveUrl is
        different to the bound resolveUrl, a rebind will be made.
    </li>
</ol>

<h2>5.5 UpsertPid method</h2>

<p>The upsert method does exactly the same as the createPid and updatePid combined; and is more efficient. It will
    create new pids; and
    update a pid if it already exists. Use this method if you do not need to check explicitly for PIDs that do not
    exist whilst updating... or already exist while creating them.</p>

<h2>5.6 DeletePid method</h2>

<p>This method will delete a pid and all it's bound attributes.</p>

</body>
</html>
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        