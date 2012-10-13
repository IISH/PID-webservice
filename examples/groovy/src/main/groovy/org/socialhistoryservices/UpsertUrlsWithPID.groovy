package org.socialhistoryservices

/**
 * UpsertUrlsWithPID
 *
 * Suppose we have a simple text file. That file contains resolve URLs
 * and persistent identifiers, like:
 * http://mywebsite.com/12345        1000/abcdefg
 *
 * In such situations it is very easy binding the PID to the URL as follows.
 * Start the script thus:
 * groovy upsertURLsWithPIDs.groovy -file [filename] -na [naming authority] -endpoint [webservice endpoint] -wskey [webservice key]
 *
 * @author: Lucien van wouw <lwo@iisg.nl>
 */

//@GrabResolver(name = 'codehaus', root = 'http://snapshots.repository.codehaus.org')
//@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.2')
//@GrabExclude('net.sf.json-lib:json-lib:2.3')
import groovy.xml.MarkupBuilder
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.entity.StringEntity

class UpsertUrlsWithPID {

    Map<String, String> map = [:]

    public UpsertUrlsWithPID(def args) {
        for (int i = 0; i < args.size(); i++) {
            if (args[i][0] == '-') map[args[i]] = args[i + 1]
        }
        ['-wskey', '-na', '-endpoint', '-file'].each {
            if (!map.get(it)) throw new Exception("Required argument " + it + " is missing")
        }
    }

    public void run() {
        new File(map['-file']).eachLine {   String line ->

            String[] split = line.split("\\s|\\t|,|;")
            def resolveUrl = split[0]
            def pid = split[1]

            StringWriter writer = new StringWriter()
            def xml = new MarkupBuilder(writer)
            writer.write("<?xml version='1.0' encoding='UTF-8'?>")

            xml.'soapenv:Envelope'(
                    'xmlns:soapenv': "http://schemas.xmlsoap.org/soap/envelope/",
                    'xmlns:pid': "http://pid.socialhistoryservices.org/") {
                'soapenv:Body' {
                    'pid:UpsertPidRequest' {
                        'pid:na'(map['-na'])
                        'pid:handle' {
                            'pid:pid'(pid)
                            'pid:resolveUrl'(resolveUrl)
                        }
                    }
                }
            }

            def enc = "text/xml; charset=utf-8"
            def http = new HTTPBuilder(endpoint)

            http.encoder.'text/xml' = {  body -> // The encoder must not return application/xml but text/xml
                def se = new StringEntity(body, "utf-8")
                se.setContentType(enc)
                se
            }

            http.request(Method.POST) {
                headers = [Authorization: wskey, 'User-Agent': 'Groovy client', 'Accept': '*/*']
                send enc, writer.toString()

                response.success = { resp ->
                    if (resp.statusLine.statusCode == 200) {
                        println("Bound " + resolveUrl + " to " + pid)
                    } else {
                        println("Error for " + pid)
                        println(resp.data?.xml)
                    }
                }
                response.failure = { resp ->
                    println("Error for " + pid)
                    println(resp.data?.xml)
                }
            }
        }
    }

    public static void main(String[] args) {
        new UpsertUrlsWithPID(args).run()
    }

}