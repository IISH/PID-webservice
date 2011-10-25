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

package net.handle.server;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import net.cnri.util.StreamTable;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ScanCallback;
import net.handle.hdllib.Util;
import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

public class MongoDBHandleStorageTest extends MongoDBHandleStorage {

    final static String na = "00000.0";
    final static String funnyChar = String.valueOf('\u00C2'); //  LATIN CAPITAL LETTER A WITH CIRCUMFLEX

    /**
     * This test class is instantiated each time we call a test method.
     *
     * @throws Exception
     */
    public MongoDBHandleStorageTest() throws Exception {
        super();

        final StreamTable config = new StreamTable();
        final URL url = MongoDBHandleStorageTest.class.getResource("/config.dct");
        final File file = new File(url.toURI());
        config.readFromFile(file);
        final StreamTable serverInfo = (StreamTable) config.get("server_config");
        init((StreamTable) serverInfo.get("storage_config"));
        clean();
    }

    @Test
    public void haveNATest() throws HandleException {

        final byte[] authHandle = Util.encodeString(na);
        setHaveNA(authHandle, false);
        Assert.assertFalse(haveNA(authHandle));

        setHaveNA(authHandle, true);
        Assert.assertTrue(haveNA(authHandle));

        setHaveNA(authHandle, false);
        Assert.assertFalse(haveNA(authHandle));
    }

    @Test
    public void createHandleTest() throws HandleException {

        // handle, index, type, data, ttl_type, ttl, timestamp, references,
        // admin_read, admin_write, pub_read, pub_write
        final String data = na + "/a_h" + funnyChar + "ndle_comes_&here";
        byte[] handle = Util.encodeString(data);
        deleteHandle(handle);
        HandleValue[] values = setHandleValues();
        createHandle(handle, values);
        boolean result = deleteHandle(handle);
        Assert.assertTrue(result);
    }

    private HandleValue[] setHandleValues() {

        HandleValue handle1 = new HandleValue(1, Util.encodeString("URL"), Util.encodeString("http://www..."));
        byte b = 0;
        HandleValue handle2 = new HandleValue(100, Util.encodeString("CUSTOM"), Util.encodeString("???"), b, 123, 123, null, true, true, true, true);
        return new HandleValue[]{
                handle1, handle2
        };
    }

    @Test
    public void getRawHandleValuesTest() throws HandleException {

        final String data = na + "/another_h" + funnyChar + "ndle_comes_&here";
        byte[] handle = Util.encodeString(data);
        deleteHandle(handle);
        HandleValue[] values = setHandleValues();
        createHandle(handle, values);
        final byte[][] rawHandleValues = getRawHandleValues(handle, null, null);
        HandleValue check0 = new HandleValue();
        HandleValue check1 = new HandleValue();
        net.handle.hdllib.Encoder.decodeHandleValue(rawHandleValues[0], 0, check0);
        net.handle.hdllib.Encoder.decodeHandleValue(rawHandleValues[1], 0, check1);
        Assert.assertEquals(check0.getDataAsString(), values[0].getDataAsString());
        deleteHandle(handle);
        final byte[][] noHandles = getRawHandleValues(handle, null, null);
        Assert.assertNull("Handle " + data + " should be null.", noHandles);
    }

    @Test
    public void getHandleValuesTest() throws HandleException {

        final String data = na + "/another_h" + funnyChar + "ndle_comes_&here";
        byte[] handle = Util.encodeString(data);
        deleteHandle(handle);
        HandleValue[] values = setHandleValues();
        createHandle(handle, values);
        final List<HandleValue> handleValues = getHandleValues(data);
        Assert.assertEquals(2L, handleValues.size());
    }

    @Test
    public void updateValueTest() throws HandleException {

        final String data = na + "/another_h" + funnyChar + "ndle_comes_&here";
        byte[] handle = Util.encodeString(data);
        deleteHandle(handle);
        HandleValue[] values = setHandleValues();
        createHandle(handle, values);

        final int index = values[0].getIndex();
        values[0].setIndex(index + 1);
        updateValue(handle, values);
        HandleValue check0 = new HandleValue();
        final byte[][] rawHandleValues = getRawHandleValues(handle, null, null);
        net.handle.hdllib.Encoder.decodeHandleValue(rawHandleValues[0], 0, check0);
        Assert.assertNotSame(check0.getIndex(), index);
    }

    static long count;

    @Test
    public void scanHandlesTest() throws HandleException {

        count = 0;
        long expect = 50;
        for (int i = 0; i < expect; i++) {
            final String data = na + "/another_h" + funnyChar + "ndle_comes_&here." + i;
            byte[] handle = Util.encodeString(data);
            deleteHandle(handle);
            HandleValue[] values = setHandleValues();
            createHandle(handle, values);
        }

        ScanCallback callback = new ScanCallback() {
            @Override
            public void scanHandle(byte[] handle) throws HandleException {
                count++;
                deleteHandle(handle);
            }
        };
        scanHandles(callback);
        Assert.assertEquals(expect, count);
    }

    @Test
    public void scanNasTest() throws HandleException {

        count = 0;
        int expect = 50;
        for (int i = 0; i < expect; i++) {
            final int na = 1000 + i;
            setHaveNA(Util.encodeString(String.valueOf(na)), true);
        }

        ScanCallback callback = new ScanCallback() {
            @Override
            public void scanHandle(byte[] authHandle) throws HandleException {
                count++;
                setHaveNA(authHandle, false);
            }
        };
        scanNAs(callback);
        Assert.assertEquals(expect, count);
    }

    @Test
    public void getHandlesForNATest() throws HandleException {

        final byte[] authHandle = Util.encodeString(String.valueOf(na));
        setHaveNA(authHandle, false);
        setHaveNA(authHandle, true);

        count = 0;
        long expect = 50;
        for (int i = 0; i < expect; i++) {
            final String data = na + "/another_h" + funnyChar + "ndle_comes_&here." + i;
            byte[] handle = Util.encodeString(data);
            deleteHandle(handle);
            HandleValue[] values = setHandleValues();
            createHandle(handle, values);
        }

        final Enumeration handlesForNA = getHandlesForNA(authHandle);
        while (handlesForNA.hasMoreElements()) {
            byte[] handle = (byte[]) handlesForNA.nextElement();
            deleteHandle(handle);
            count++;
        }

        Assert.assertEquals(expect, count);
        setHaveNA(authHandle, false);
    }

    @Test
    public void checkpointDatabaseTest() throws HandleException {

        // Works.... but
        checkpointDatabase();
    }

    @Test
    /**
     * Not a test method... just used to remove the test database.
     */
    public void tearDown() {

        clean();
        shutdown();
    }

    private void clean() {

        final DBCollection collection = getCollection(Util.encodeString(na));
        final DB db = collection.getDB();
        final String name = db.getName();
        final Mongo mongo = db.getMongo();
        final List<String> databaseNames = mongo.getDatabaseNames();
        for (String dbName : databaseNames) {
            if (dbName.startsWith(name)) {
                mongo.getDB(dbName).dropDatabase();
            }
        }
    }
}
