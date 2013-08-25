/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2013, International Institute of Social History
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

import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * ServletTest
 * <p/>
 * Use for integration testing
 */
public class ServletTest {

    final static String na = "00000.0";

    public static void main(String[] args) throws URISyntaxException {

        try {
            insertHandles();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }

        final URL url = MongoDBHandleStorageTest.class.getResource("/config.dct");
        final File file = new File(url.toURI());

        // Usage: hdl-server <config-directory>
        net.handle.server.Main.main(new String[]{file.getParent()});
    }

    /**
     * insertHandles
     * <p/>
     * Create two handles: URL and LOC
     *
     * @throws Exception
     */
    private static void insertHandles() throws Exception {
        final MongoDBHandleStorageTest mongoDBHandleStorageTest = new MongoDBHandleStorageTest();
        mongoDBHandleStorageTest.setHaveNA(Util.encodeString(String.valueOf(na)), true);

        final HandleValue handle1 = new HandleValue(1, Util.encodeString("URL"), Util.encodeString("http://localhost:8000?handle=URL"));
        final HandleValue adminValue = mongoDBHandleStorageTest.createAdminValue("0.NA/" + na, 200, 100);
        mongoDBHandleStorageTest.createHandle(Util.encodeString(na + "/URL"), new HandleValue[]{
                handle1, adminValue
        });

        final HandleValue handle2 = new HandleValue(1000, Util.encodeString("10320/loc"), Util.encodeString(
                "<locations>\n" +
                        "<location id=\"0\" href=\"http://localhost:8000?handle=10320/loc&amp;id=0&amp;country=gb&amp;weight=0/\" country=\"gb\" weight=\"0\" />\n" +
                        "<location id=\"1\" href=\"http://localhost:8000?handle=10320/loc&amp;id=1&amp;weight=1/\" weight=\"1\" />\n" +
                        "<location id=\"2\" href=\"http://localhost:8000?handle=10320/loc&amp;id=2&amp;weight=1/\" weight=\"1\" />\n" +
                        "</locations>"));
        mongoDBHandleStorageTest.createHandle(Util.encodeString(na + "/LOC"), new HandleValue[]{
                handle2, adminValue
        });
    }

}
