/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2011, International Institute of Social History
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**********************************************************************\
 MongoDB driver for Handle System
 \**********************************************************************/

package net.handle.server;

import com.mongodb.*;
import net.cnri.util.StreamTable;
import net.cnri.util.StreamVector;
import net.cnri.util.StringUtils;
import net.handle.hdllib.*;
import org.socialhistoryservices.dao.MongoDBSingleton;

import java.util.*;

/**
 * **********************************************************
 * Class that provides a datasource mechanism for handle records
 * using an Mongo document database
 * <p/>
 * Note on mirrors and mirroring.
 * Typically this is managed by a MongoDB replicaset setup.
 * There is no distinction any more from the part of the handle system resolver of what
 * is the mirror or master.
 * ***********************************************************
 */
public class MongoDBHandleStorage
        implements HandleStorage {


    private static final String URL = "urls";
    private static final String LOGIN = "login";
    private static final String PASSWD = "passwd";
    private static final String DRIVER_CLASS = "custom";
    private static final String READ_ONLY = "read_only";
    private static final String DATABASE_NAME = "database_name";
    private static final String COLLECTION_NAS = "collection_nas";
    private static final String COLLECTION_HANDLE_PREFIX = "handles_";
    private static final String COLLECTION_INDICES = "indices";
    private static final String CASE_SENSITIVE = "case_sensitive";
    private static final String WRITECONCERN = "write_concern";
    private static final String CONNECTIONS_PER_HOST = "connections_per_host";

    private Mongo mongo;

    private String database;

    private StreamVector databaseURL;
    private List<String> indices;
    private String username;
    private String passwd;
    private boolean readOnly = false;
    private Boolean case_sensitive = false; // unsupported
    private String collection_nas;

    public MongoDBHandleStorage() throws Exception {
    }

    public MongoDBHandleStorage(Mongo mongo) throws Exception {
        this.mongo = mongo;
    }

    /**
     * Initialize the MongoDB storage object with the given settings.
     */
    public void init(StreamTable config)
            throws Exception {

        // load the MongoDB driver, if configured. Otherwise this will throw a class not found exception.
        if (config.containsKey(DRIVER_CLASS)) {
            Class.forName(String.valueOf(config.get(DRIVER_CLASS)));
        }

        // get the database URL and other connection parameters
        this.username = (String) config.get(LOGIN);
        this.passwd = (String) config.get(PASSWD);
        setDatabase((String) config.get(DATABASE_NAME));
        this.databaseURL = (StreamVector) config.get(URL);
        StreamVector indices = (StreamVector) config.get(COLLECTION_INDICES);
        if (indices == null) {
            indices = new StreamVector();
            indices.add("handle");
        }
        this.indices = indices.subList(0, indices.size());
        this.collection_nas = (String) config.get(COLLECTION_NAS);
        final String c_s = ((String) config.get(CASE_SENSITIVE, "no")).toLowerCase();
        this.case_sensitive = (c_s.equalsIgnoreCase("yes"));
        this.readOnly = config.getBoolean(READ_ONLY, false);
        if (mongo == null) {
            final MongoClientOptions.Builder builder = new MongoClientOptions.Builder()
                    .description("Handle System driver " + getClass())
                    .writeConcern(new WriteConcern(config.getInt(WRITECONCERN, 1)))
                    .connectionsPerHost(config.getInt(CONNECTIONS_PER_HOST, 11))
                    .readPreference(ReadPreference.nearest()) ;

            this.mongo = new MongoDBSingleton((String[]) databaseURL.toArray(new String[databaseURL.size()]),
                    builder.build()).getInstance();
            if (username != null && passwd != null) {
                final boolean authenticate = authenticate(database, username, passwd.toCharArray());
                if (!authenticate) {
                    throw new HandleException(HandleException.UNABLE_TO_AUTHENTICATE, "Access denied.");
                }
            }
        }
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * ******************************************************************
     * Returns true if this server is responsible for the given naming
     * authority.
     * *******************************************************************
     */
    public boolean haveNA(byte authHandle[])
            throws HandleException {

        if (Util.startsWithCI(authHandle, Common.NA_HANDLE_PREFIX))
            authHandle = Util.getSuffixPart(authHandle);
        authHandle = Util.upperCase(authHandle);

        final DBCollection collection = getCollection(database, collection_nas);

        BasicDBObject query = new BasicDBObject();
        query.put("na", Util.decodeString(authHandle));
        DBObject na = collection.findOne(query);
        if (na != null)
            return true;
        if (Util.hasSlash(authHandle))
            return false;

        authHandle = Util.getZeroNAHandle(authHandle);
        query.clear();
        query.put("na", Util.decodeString(authHandle));
        na = collection.findOne(query);
        return (na != null);
    }

    /**
     * ******************************************************************
     * Sets a flag indicating whether or not this server is responsible
     * for the given naming authority.
     * *******************************************************************
     */

    public void setHaveNA(byte authHandle[], boolean flag)
            throws HandleException {
        if (readOnly) throw new HandleException(HandleException.STORAGE_RDONLY,
                "Server is read-only");

        boolean currentlyHaveIt = haveNA(authHandle);
        if (currentlyHaveIt == flag)
            return;

        final DBCollection collection = getCollection(database, collection_nas);
        final BasicDBObject na = new BasicDBObject();
        na.put("na", Util.decodeString(authHandle));
        authHandle = Util.upperCase(authHandle);
        if (currentlyHaveIt) { // we already have it but need to remove it
            {
                collection.remove(na);
            }
        } else { // we need to add the NA to the database
            {
                collection.insert(na);

                // Add indices to the handle collection.
                final DBCollection handle_na = getCollection(authHandle);
                for (String index : indices) {
                    handle_na.ensureIndex(index);
                }
            }
        }
    }

    protected boolean handleExists(byte handle[])
            throws HandleException {

        final DBCollection collection = getCollection(handle);
        final BasicDBObject query = new BasicDBObject();
        query.put("handle", Util.decodeString(handle));
        final DBObject result = collection.findOne(query);
        return (result != null);
    }

    public static final String encodeString(String str) {
        int len = str.length();
        StringBuffer sb = new StringBuffer(len + 4);
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch >= 0x7f || ch < 0x20 || ch == '%') {
                sb.append('%');
                sb.append(HEX_VALUES[(ch >> 12) & 0xf]);
                sb.append(HEX_VALUES[(ch >> 8) & 0xf]);
                sb.append(HEX_VALUES[(ch >> 4) & 0xf]);
                sb.append(HEX_VALUES[ch & 0xf]);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static final char HEX_VALUES[] = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * ******************************************************************
     * Creates the specified handle in the "database" with the specified
     * initial values
     * <p/>
     * The document looks like this:
     * <p/>
     * handle:handle here: na\identifier
     * handles:[
     * {
     * index:
     * type:
     * data:
     * ttl_type:
     * ttl:
     * timestamp:
     * refs:
     * admin_read:
     * admin_write:
     * pub_read:
     * pub_write:
     * }
     * ,
     * {
     * index:
     * type:
     * data:
     * ttl_type:
     * ttl:
     * timestamp:
     * refs:
     * admin_read:
     * admin_write:
     * pub_read:
     * pub_write:
     * }
     * ]
     * *******************************************************************
     */
    public void createHandle(byte handle[], HandleValue values[])
            throws HandleException {

        if (readOnly) throw new HandleException(HandleException.STORAGE_RDONLY,
                "Server is read-only");
        final String handleStr = Util.decodeString(handle);

        // if the handle already exists, throw an exception
        if (handleExists(handle))
            throw new HandleException(HandleException.HANDLE_ALREADY_EXISTS,
                    handleStr);

        if (values == null)
            throw new HandleException(HandleException.INVALID_VALUE);

        BasicDBList handles = new BasicDBList();
        for (int i = 0; i < values.length; i++) {
            // not the handle,
            // but index, type, data, ttl_type, ttl, timestamp, refs,
            // admin_read, admin_write, pub_read, pub_write

            BasicDBObject hv = setHandleValue(values[i]);
            handles.add(hv);
        }

        BasicDBObject h = new BasicDBObject("handle", handleStr);
        h.put("handles", handles);
        final DBCollection collection = getCollection(handle);
        collection.insert(h);
    }

    /**
     * ******************************************************************
     * Delete the specified handle in the database.
     * *******************************************************************
     */
    public boolean deleteHandle(byte handle[])
            throws HandleException {
        if (readOnly) throw new HandleException(HandleException.STORAGE_RDONLY,
                "Server is read-only");

        final String handleStr = Util.decodeString(handle);
        final BasicDBObject query = new BasicDBObject("handle", handleStr);
        final DBCollection collection = getCollection(handle);
        final WriteResult result = collection.remove(query);
        return (result.getN() != 0);
    }

    /**
     * ******************************************************************
     * Return the pre-packaged values of the given handle that are either
     * in the indexList or the typeList.  This method should return any
     * values of type ALIAS or REDIRECT, even if they were not requested.
     * *******************************************************************
     */
    public byte[][] getRawHandleValues(byte handle[], int indexList[],
                                       byte typeList[][])
            throws HandleException {

        final String handleStr = Util.decodeString(handle);
        final BasicDBObject query = new BasicDBObject("handle", handleStr);
        final DBCollection collection = getCollection(handle);
        final DBObject _handles = collection.findOne(query);
        if (_handles == null)
            return null;
        final Object handles = _handles.get("handles");
        final BasicDBList results = (BasicDBList) handles;

        boolean allValues = ((typeList == null || typeList.length == 0) &&
                (indexList == null || indexList.length == 0));

        Vector values = new Vector();

        final Iterator<Object> iterator = results.iterator();
        while (iterator.hasNext()) {

            HandleValue value = getHandleValue((BasicDBObject) iterator.next());
            if (allValues) {
            } else if (!Util.isParentTypeInArray(typeList, value.getType()) &&
                    !Util.isInArray(indexList, value.getIndex())) // ignore non-requested types
                continue;
            values.addElement(value);
        }

        byte rawValues[][] = new byte[values.size()][];
        for (int i = 0; i < rawValues.length; i++) {
            HandleValue value = (HandleValue) values.elementAt(i);
            rawValues[i] = new byte[Encoder.calcStorageSize(value)];
            Encoder.encodeHandleValue(rawValues[i], 0, value);
        }

        return rawValues;
    }

    public BasicDBObject setHandleValue(HandleValue val) {

        BasicDBObject h = new BasicDBObject();
        h.put("index", val.getIndex());
        h.put("type", Util.decodeString(val.getType()));
        if (Util.looksLikeBinary(val.getData()))
            h.put("data", val.getData());
        else
            h.put("data", Util.decodeString(val.getData()));
        h.put("ttl_type", val.getTTLType());
        h.put("ttl", val.getTTL());
        h.put("timestamp", val.getTimestamp());
        StringBuffer sb = new StringBuffer();
        ValueReference refs[] = val.getReferences();
        for (int rv = 0; refs != null && rv < refs.length; rv++) {
            if (rv != 0) {
                sb.append('\t');
            }
            sb.append(refs[rv].index);
            sb.append(':');
            sb.append(StringUtils.encode(Util.decodeString(refs[rv].handle)));
        }
        h.put("refs", encodeString(sb.toString()));
        h.put("admin_read", val.getAdminCanRead());
        h.put("admin_write", val.getAdminCanWrite());
        h.put("pub_read", val.getAnyoneCanRead());
        h.put("pub_write", val.getAnyoneCanWrite());
        return h;
    }

    public HandleValue getHandleValue(BasicDBObject o) {

        HandleValue value = new HandleValue();
        value.setIndex((Integer) o.get("index"));
        value.setType(Util.encodeString((String) o.get("type")));
        final Object data = o.get("data");
        if (data instanceof String) {
            value.setData(Util.encodeString((String) data));
        } else {
            value.setData((byte[]) data);
        }
        value.setTTLType(Byte.parseByte(String.valueOf(o.get("ttl_type"))));
        value.setTTL((Integer) o.get("ttl"));
        value.setTimestamp((Integer) o.get("timestamp"));
        String referencesStr = (String) o.get("refs");

        // parse references...
        String references[] = StringUtils.split(referencesStr, '\t');
        if (references != null && referencesStr.length() > 0 && references.length > 0) {
            ValueReference valReferences[] = new ValueReference[references.length];
            for (int i = 0; i < references.length; i++) {
                valReferences[i] = new ValueReference();
                int colIdx = references[i].indexOf(':');
                try {
                    valReferences[i].index = Integer.parseInt(references[i].substring(0, colIdx));
                } catch (Exception t) {
                }
                valReferences[i].handle =
                        Util.encodeString(StringUtils.decode(references[i].substring(colIdx + 1)));
            }
            value.setReferences(valReferences);
        }

        value.setAdminCanRead((Boolean) o.get("admin_read"));
        value.setAdminCanWrite((Boolean) o.get("admin_write"));
        value.setAnyoneCanRead((Boolean) o.get("pub_read"));
        value.setAnyoneCanWrite((Boolean) o.get("pub_write"));
        return value;
    }

    /**
     * Equivalent to getRawHandleValues. Added for the beneficial of non Handle System clients.
     *
     * @param handle
     * @return
     * @throws HandleException
     */
    public List<HandleValue> getHandleValues(String handle) {

        final BasicDBObject query = new BasicDBObject("handle", handle);
        final DBCollection collection = getCollection(Util.encodeString(handle));
        final DBObject h = collection.findOne(query);
        final List<HandleValue> handles = new ArrayList<HandleValue>();
        if (h == null) {
            return handles;
        }

        final BasicDBList results = (BasicDBList) h.get("handles");
        final Iterator<Object> iterator = results.iterator();
        while (iterator.hasNext()) {

            HandleValue value = getHandleValue((BasicDBObject) iterator.next());
            handles.add(value);
        }
        return handles;
    }

    /**
     * ******************************************************************
     * Replace the current values for the given handle with new values.
     * *******************************************************************
     */
    public void updateValue(byte handle[], HandleValue values[])
            throws HandleException {
        if (readOnly) throw new HandleException(HandleException.STORAGE_RDONLY,
                "Server is read-only");

        if (!handleExists(handle))
            throw new HandleException(HandleException.HANDLE_DOES_NOT_EXIST);

        Throwable e = null;
        try {
            deleteHandle(handle);
            createHandle(handle, values);
        } catch (Exception sqlExc) {
            e = sqlExc;
        }
        if (e != null) {
            throw new HandleException(HandleException.INTERNAL_ERROR,
                    "Error updating values: " + e);
        }
    }

    /**
     * ******************************************************************
     * Scan the database, calling a method in the specified callback for
     * every handle in the database.
     * <p/>
     * Method selects all distinct handle from handles
     * *******************************************************************
     */
    public void scanHandles(ScanCallback callback)
            throws HandleException {

        final DB db = mongo.getDB(database);
        final Set<String> collectionNames = db.getCollectionNames();
        final Iterator<String> iterator = collectionNames.iterator();
        while (iterator.hasNext()) {
            String collectionName = iterator.next();
            if (collectionName.startsWith(COLLECTION_HANDLE_PREFIX)) {
                final DBCollection collection = getCollection(database, collectionName);
                final BasicDBObject query = new BasicDBObject(); // Find all records
                final BasicDBObject filter = new BasicDBObject("handle", 1); // Only want handles
                final DBCursor dbCursor = collection.find(query, filter);
                while (dbCursor.hasNext()) {
                    final DBObject o = dbCursor.next();
                    final String handle = (String) o.get("handle");
                    callback.scanHandle(Util.encodeString(handle));
                }
            }
        }
    }

    /**
     * ******************************************************************
     * Scan the NA database, calling a method in the specified callback for
     * every naming authority handle in the database.
     * *******************************************************************
     */
    public void scanNAs(ScanCallback callback)
            throws HandleException {

        final DBCollection collection = getCollection(database, collection_nas);
        final DBCursor dbCursor = collection.find();
        while (dbCursor.hasNext()) {
            final DBObject o = dbCursor.next();
            final String authHandle = (String) o.get("na");
            callback.scanHandle(Util.encodeString(authHandle));
        }
    }


    /**
     * ******************************************************************
     * Scan the database for handles with the given naming authority
     * and return an Enumeration of byte arrays with each byte array
     * being a handle.  <i>naHdl</i> is the naming authority handle
     * for the naming authority that you want to list the handles for.
     * *******************************************************************
     */
    public final Enumeration getHandlesForNA(byte naHdl[])
            throws HandleException {
        if (!haveNA(naHdl)) {
            throw new HandleException(HandleException.INVALID_VALUE,
                    "The requested naming authority doesn't live here");
        }

        boolean isZeroNA = Util.startsWithCI(naHdl, Common.NA_HANDLE_PREFIX);
        if (isZeroNA) naHdl = Util.getSuffixPart(naHdl);
        return new ListHdlsEnum(naHdl);
    }


    /**
     * ******************************************************************
     * Remove all of the records from the database.
     * ******************************************************************
     */
    public void deleteAllRecords()
            throws HandleException {
        if (readOnly) throw new HandleException(HandleException.STORAGE_RDONLY,
                "Server is read-only");

        throw new HandleException(HandleException.SERVER_ERROR,
                "Deletion of all handles is not supported");
    }

    /**
     * Removes a collection
     *
     * @param na
     */
    public long deleteAllRecords(String na) {

        final DBCollection collection = getCollection(Util.encodeString(na));
        long count = collection.count();
        collection.drop();
        return count;
    }

    /**
     * Copies the database to another:
     * checkpoint_[database name]
     *
     * @throws HandleException
     */
    public void checkpointDatabase()
            throws HandleException {

        final String checkpoint = database + "_checkpoint_" + new Date().getTime();
        final BasicDBObject command = new BasicDBObject();
        command.put("copydb", 1);
        command.put("fromdb", database);
        command.put("todb", checkpoint);

        final DB db = mongo.getDB("admin");
        final CommandResult result = db.command(command);
        if (!result.ok()) {
            throw new HandleException(HandleException.SERVER_ERROR, "The checkpoint action failed:\n" + result.getErrorMessage());
        }
    }

    /**
     * ******************************************************************
     * Close the database and clean up
     * *******************************************************************
     */
    public void shutdown() {

        mongo.close();
    }

    private class ListHdlsEnum
            implements Enumeration {

        private byte nextVal[] = null;
        private byte[] prefix;
        DBCursor dbCursor = null;

        ListHdlsEnum(byte prefix[])
                throws HandleException {
            this.prefix = prefix;
            final DBCollection collection = getCollection(prefix);
            final BasicDBObject query = new BasicDBObject(); // Find all records
            final BasicDBObject filter = new BasicDBObject("handle", 1); // Only want handles
            dbCursor = collection.find(query, filter);
            getNextValue();
        }

        public boolean hasMoreElements() {
            return nextVal != null;
        }

        public Object nextElement() {
            byte returnVal[] = nextVal;
            if (returnVal != null) getNextValue();
            return returnVal;
        }

        private void getNextValue() {
            nextVal = null;
            if (dbCursor.hasNext()) {
                final DBObject o = dbCursor.next();
                final String handle = (String) o.get("handle");
                byte[] candNextVal = Util.encodeString(handle);
                if (candNextVal[prefix.length] == (byte) '/' ||
                        (candNextVal[prefix.length] == (byte) '.' && Util.indexOf(candNextVal, (byte) '/') == -1)) {
                    nextVal = candNextVal;
                } else {
                    getNextValue();
                }
            }
        }
    }

    public HandleValue createAdminValue(final String adminHandle,
                                        final int keyIndex, int index)
            throws HandleException {
        AdminRecord adminRecord =
                new AdminRecord(Util.encodeString(adminHandle), keyIndex, true, true, true,
                        true, true, true, true, true, true, true, true, true);
        return
                new HandleValue(index, Common.ADMIN_TYPE,
                        Encoder.encodeAdminRecord(adminRecord),
                        HandleValue.TTL_TYPE_RELATIVE,
                        86400, 0, null, true, true, true, false);
    }

    /**
     * The collection name is derived from the index:
     * handles_prefix + prefix
     * Any dot (.) will be normalized as an underscore to prevent namespace issues with the MongoDB.
     *
     * @param handle
     * @return
     */
    public DBCollection getCollection(byte[] handle) {

        final byte[] prefixPart = Util.getPrefixPart(handle);
        return getCollection(database, COLLECTION_HANDLE_PREFIX + Util.decodeString(prefixPart).replace(".", "_"));
    }

    protected DBCollection getCollection(String database, String collection) {

        final DB db = mongo.getDB(database);
        return db.getCollection(collection);
    }

    protected boolean authenticate(String database, String username, char[] chars) {

        final DB db = mongo.getDB(database);
        return db.authenticate(username, chars);
    }
}