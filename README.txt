Pid webservice

1. Installation
First of all, setup your environment. We recommend:
- one Apache webservice with SSL to act as proxy and load balance method
- a minimum of two service nodes. Each ought to have a tomcat6\7 instance.
- the webservice uses MongoDB to store the handles. You need to install on the same service nodes a replica set.
See http://www.mongodb.org/ to set it up.
- Install your Handle System resolver(s). See http://hdl.handle.net/ for complete documentation.

2. Master and mirrors
Note that replication is now left to MongoDB's replica set. Hence the synchronization by the Handle System
resolver from master to mirror(s) is no longer required.

3. Drivers
You need to install the mongodb-handlestorage custom database driver and register it in the config.cfg file. For example
your installation of Handle System's resolver /lib folder ought to contain:
mongodb-handlestorage-7.0.jar
mongo-java-driver-2.6.5.jar
... the other handle dependencies

4. Config.cfg
Register the MongoDB replicaset in the 'urls' key and register the custom drivers. Example:

  "server_config" = {

	"case_sensitive" = "no"
	"storage_class" = "net.handle.server.MongoDBHandleStorage"
	"storage_type" = "custom"
	"storage_config" = {
	"database_name" = handlesystem
	"collection_nas" = nas
	"collection_handles" = handles
	"connections_per_host" = 20
	"write_concern" = 1
	"indices" = (
		handle
		handles.type
		_lookup
	)
	urls = (
		repplicaset1.host
		repplicaset1.host
		repplicaset1.host

	)

... other settings
}

==============================================================================

Usage

1. What is the Pid webservice
The Pid webservice is a driver for Handle System's resolver technology. It eases the creation of persistent identifiers
and their bindings to attributes such as web locations with the use of a Soap protocol. This means it is system
agnostic: an external application offering web methods that can be integrated into your catalog and archival system
solutions. All with the purpose to offer a consistent delivery via pids of your web resources to yourself, your users
and external discovery services alike.

2. Webservice keys
Access to the webservice is HTTP over SSL. A private webservice key is needed to be able to create and update your pids.
This single key can be changed at any time you want, should it be compromised. You can manage pids from different naming
authorities ( for example a naming authority for production pids, and another for testing). There are two ways to obtain
webservice keys:

 - The pid webservice offers an Oauth 2 protocol with which to distribute and refresh access tokens. The Oauth entry
 point is at http://localhost/oauth/. The Oauth consumer client id must be set in the properties file
 - An elegant administration page at http://localhost/oauth/keys to produce keys . 

Place the key in a HTTP header request as expressed in this pseude code:
HTTP-header(headerName, headerValue) = { "Authorization", "oauth [key]" } 
Whereby [key] is the webservice key. For example, if the key is 12345, the header is: 
HTTP-header("Authorization", "oauth 12345") 

3. Discover the webservice
The Pid webservice offers seven operations:

1.Creation and update of pids using an Upsert method
2.Creation of pids (automatic and custom) and its bind properties: single resolve url, set of resolvable urls, or local
identifier.
3.Update of pids and its bindings
4.Lookup of bindings via a pid
5.Reverse lookup of a pid via resolve url or an attribute (local identifier)
6.Quick binding
7.Deletion of pids

Use Soap to operate the webservice API:

- WSDL document is at http://localhost/pid.wsdl 
- and the webservice endpoint is located at http://localhost/secure/ 
 
4. Examples
4.1. Create a Pid with a resolve Url
The following example will create a pid 10622.1/32dc9242-a978-43b0-befd-831fa02af673 Once created, the url handle:
http://hdl.handle.net/10622.1/32dc9242-a978-43b0-befd-831fa02af673 would resolve to the url http://some.domain.org/

Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://localhost/">
        <soapenv:Body>
            <pid:CreatePidRequest>
                <pid:na>10622.1</pid:na>
                <pid:handle>
                    <pid:resolveUrl>http://some.domain.org/</pid:resolveUrl>
                </pid:handle>
            </pid:CreatePidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:CreatePidResponse xmlns:ns2="http://localhost/">
                <ns2:handle>
                    <ns2:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:CreatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
You can also create the PIDs yourself as demonstrated in this movie:

4.1.2 Create a custom pid with multiple urls
This example demonstrates a custom pid that is bound to three resolve urls. This will make possible three ways of
resolving with one pid:
1.http://hdl.handle.net/10622.1/EU:ARCHIVE83:ITEM23:FILE3
2.http://hdl.handle.net/10622.1/EU:ARCHIVE83:ITEM23:FILE3?view=master
3.http://hdl.handle.net/10622.1/EU:ARCHIVE83:ITEM23:FILE3?view=thumbnail
Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://localhost/">
        <soapenv:Body>
            <pid:CreatePidRequest>
                <pid:na>10622.1</pid:na>
                <pid:handle>
                    <pid:pid>10622.1/EU:ARCHIVE83:ITEM23:FILE3</pid:pid>
                    <pid:resolveUrl>http://some.domain.org/</pid:resolveUrl>
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
            <ns2:CreatePidResponse xmlns:ns2="http://localhost/">
                <ns2:handle>
                    <ns2:pid>10622.1/EU:ARCHIVE83:ITEM23:FILE3</ns2:pid>
                    <ns2:locAtt>
                        <ns2:location href="http://www.archivalius.org?id=original83.23.3" view="master"/>
                        <ns2:location href="http://www.archivalius.org?id=image83.23.3.jpg" view="thumbnail"/>
                        <ns2:location href="http://some.domain.org/" weight="100"/>
                    </ns2:locAtt>
                    <ns2:localIdentifier>?</ns2:localIdentifier>
                </ns2:handle>
            </ns2:CreatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

You do not need the locatt expicitly mentioned in the handle to redirect endusers to the desired locations. The country
attribute can be used to direct users using GeoIP. We demonstrate this here:

4.2 Update a Pid with a new resolve Url
To change a resolve url, use the update method

Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://localhost/">
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
            <ns2:UpdatePidResponse xmlns:ns2="http://localhost/">
                <ns2:handle>
                    <ns2:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</ns2:pid>
                    <ns2:resolveUrl>http://new-domain/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:UpdatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

4.3.1 Lookup bound attributes of a known pid
To know that bindings exist for a given pid, use the getPid method. In this example we find out that the resolve url is
http://new-domain/

Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://localhost/">
        <soapenv:Body>
            <pid:GetPidRequest>
                <pid:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</pid:pid>
            </pid:GetPidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:GetPidResponse xmlns:ns2="http://localhost/">
                <ns2:handle>
                    <ns2:pid>10622.1/32dc9242-a978-43b0-befd-831fa02af673</ns2:pid>
                    <ns2:resolveUrl>http://new-domain/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:GetPidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

4.3.2 Lookup a pid from its know resolve urls
It is possible to find a pid through it's bound attributes such as resolve urls. In this example we look for pids that
are bound to the resolve url http://some.domain.org/. Note that the reverse lookup is case sensitive and the result set
is limited to 10 records.

Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://localhost/">
        <soapenv:Body>
            <pid:GetPidByAttributeRequest>
                <pid:na>10622.1</pid:na>
                <pid:attribute>http://some.domain.org/</pid:attribute>
            </pid:GetPidByAttributeRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:GetPidByAttributeResponse xmlns:ns2="http://localhost/">
                <ns2:handle>
                    <ns2:pid>10622.1/7f0f3216-ee06-4cd8-8b15-f5790e40a62e</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/1fac4977-9f52-422e-b84c-7d55db71574b</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/f056a39a-398c-4d38-9933-a78f0691354b</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/f4511e6c-7c94-41ab-b59a-ce708f26f26c</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/aaa3481b-442e-416e-bb87-91dcfcd5a51c</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/19ef01a2-8c0a-4932-b39e-b62c8362fa30</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/fcfd6272-3084-4308-bcea-ae47698d497b</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
                <ns2:handle>
                    <ns2:pid>10622.1/2fd87b05-fbda-4945-b696-9843c3a7fdb3</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                </ns2:handle>
            </ns2:GetPidByAttributeResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

4.3.3 Lookup a pid from its know local identifiers
It is possible to bind other attributes to a pid, such as a local identifier or any other tag for that matter. In this
case, the local identifier needed to be set when creating or updating the pid.

For example, if the local identifier was 12345 and bound like this:

Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://localhost/">
        <soapenv:Body>
            <pid:CreatePidRequest>
                <pid:na>10622.1</pid:na>
                <pid:handle>
                    <pid:resolveUrl>http://some.domain.org/</pid:resolveUrl>
                    <pid:localIdentifier>12345</pid:localIdentifier>
                </pid:handle>
            </pid:CreatePidRequest>
        </soapenv:Body>
    </soapenv:Envelope>

    Response:
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <ns2:CreatePidResponse xmlns:ns2="http://localhost/">
                <ns2:handle>
                    <ns2:pid>10622.1/f855fcd2-c503-4dd5-a8f6-d87845b75fb0</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                    <ns2:localIdentifier>12345</ns2:localIdentifier>
                </ns2:handle>
            </ns2:CreatePidResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

Then the reverse lookup would be:

Request:
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:pid="http://localhost/">
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
            <ns2:GetPidByAttributeResponse xmlns:ns2="http://localhost/">
                <ns2:handle>
                    <ns2:pid>10622.1/f855fcd2-c503-4dd5-a8f6-d87845b75fb0</ns2:pid>
                    <ns2:resolveUrl>http://some.domain.org/</ns2:resolveUrl>
                    <ns2:localIdentifier>12345</ns2:localIdentifier>
                </ns2:handle>
            </ns2:GetPidByAttributeResponse>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

4.4 Quick pid method
Unless you are an service provider whose clients cannot supply anything else but local identifiers, this method would
not be useful to an organization delivering pids. The GetQuickPid method utilizes the earlier mentioned methods:

1.Pid creation: when the localIdentifier is not bound to a known pid, the webservice creates a pid and then binds it to
the resolveUrl and localIdentifier.
2.Pid lookup: when the localIdentifier is bound to an existing Pid, the method will echo back all data bound to the pid. 
3.Pid update: when the localIdentifier is bound to an existing Pid and the supplied resolveUrl is different to the bound
resolveUrl, a rebind will be made.
5.5 UpsertPid method
The upsert method does exactly the same as the createPid and updatePid combined; and is more efficient. It will create
new pids; and update a pid if it already exists. Use this method if you do not need to check explicitly for PIDs that
do not exist whilst updating... or already exist while creating them.

4.6 DeletePid method
This method will delete a pid and all it's bound attributes.