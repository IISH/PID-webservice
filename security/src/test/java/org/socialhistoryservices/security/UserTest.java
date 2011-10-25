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

package org.socialhistoryservices.security;

import com.mongodb.Mongo;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.net.UnknownHostException;

public class UserTest {

    private static String host = "localhost";
    private static String database = "test_iaa";
    private static MongoUserDetailService service;

    @BeforeClass
    public static void setup() throws UnknownHostException {

        final Mongo mongo = new Mongo(host);
        mongo.dropDatabase(database);
        service = new MongoUserDetailService();
        service.setMongo(mongo);
        service.setDatabase(database);
    }

    @Test
    public void userDetailService() throws UnknownHostException {

        createUser();

        // Proof the  addition
        final UserDetails userDetails = service.loadUserByUsername("test1");
        Assert.assertNotNull(userDetails);

        // Remove the user
        final boolean remove = service.remove("test1");
        Assert.assertTrue(remove);

        // confirm that the removal throws an exception
        UserDetails u = null;
        try {
            u = service.loadUserByUsername("test1");
        } catch (UsernameNotFoundException e) {
        }
        Assert.assertNull(u);
    }

    @Test
    public void update() throws UnknownHostException {

        createUser();

        // Now update... basically all should remain the same
        String[] args = new String[]{
                "-action", "upsert",
                "-h", host,
                "-d", database,
                "-u", "test1",
        };

        User.main(args);

        // Proof the update
        final UserDetails userDetails = service.loadUserByUsername("test1");
        Assert.assertNotNull(userDetails.getPassword());
        Assert.assertEquals(2, userDetails.getAuthorities().size());
    }

    private void createUser() throws UnknownHostException {
        String[] args = new String[]{
                "-action", "upsert",
                "-h", host,
                "-d", database,
                "-u", "test1",
                "-p", "test2",
                "-a", "USER,NA_10622"
        };

        User.main(args);
         // Proof the  addition
        final UserDetails userDetails = service.loadUserByUsername("test1");
        Assert.assertNotNull(userDetails);
        Assert.assertEquals(2, userDetails.getAuthorities().size());
    }

    @AfterClass
    public static void tearDown() throws UnknownHostException {

        final Mongo mongo = new Mongo(host);
        mongo.dropDatabase(database);
    }
}
