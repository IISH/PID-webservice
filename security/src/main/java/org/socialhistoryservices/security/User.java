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

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Console based application to create, update and delete users.
 * <p/>
 * Usage:
 * -action=upsert ( create or update user) | remove (delete user)
 * -u username
 * -p password
 * -h host
 * -d database
 * -a authorities ( seperated with comma's, like CAN_READ,CAN_WRITE,NA_12345, etc )
 */
public class User {

    public static void main(String[] argv) throws UnknownHostException {

        final Expect[] values = Expect.values();
        Map<String, String> map = new HashMap(values.length);
        for (int i = 0; i < argv.length; i += 2) {
            char c = argv[i].charAt(0);
            String key = (c == '-' || c == '/' || c == '\\')
                    ? argv[i].substring(1)
                    : argv[i];
            map.put(key, argv[i + 1]);
        }

        for (Expect expect : values) {
            if (!map.containsKey(expect.name())) {
                System.out.println("Expected case sensitive parameter: -" + expect.name());
                System.out.println("Usage:\n" +
                        "-action upsert ( create or update user) | remove (delete user)\n" +
                        "-u username\n" +
                        "-p password ( must be encoded with the same hash algoritm as the application )\n" +
                        "-h host (Mongo address of master)\n" +
                        "-d database\n" +
                        "-a authorities ( grants or roles seperated with comma's, like CAN_READ,CAN_WRITE,NA_12345, etc )");
                System.exit(-1);
            }
        }

        final MongoUserDetailService service = new MongoUserDetailService();
        service.setMongo(new Mongo(map.get(Expect.h.name())));
        service.setDatabase(map.get(Expect.d.name()));

        String action = map.get(Expect.action.name());
        if (action.equals("delete") || action.equals("remove")) {
            service.remove(map.get(Expect.u.name()));
        } else if (action.equals("upsert")) {
            MongoUserDetails user = new MongoUserDetails();
            user.setUsername(map.get(Expect.u.name()));
            user.setPassword(map.get(Optional.p.name()));
            final String grantKey = Optional.a.name();
            String[] split = (map.get(grantKey) == null)
                    ? new String[]{}
                    : map.get(grantKey).split(",|;|\\s");
            for (String role : split) {
                MongoAuthority authority = new MongoAuthority(role);
                user.getAuthorities().add(authority);
            }
            service.createUser(user);
        }
    }

    public enum Expect {
        action,
        h,
        d,
        u
    }

    public enum Optional {
        a,
        p
    }
}
