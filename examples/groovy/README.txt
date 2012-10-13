EAD2pids

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