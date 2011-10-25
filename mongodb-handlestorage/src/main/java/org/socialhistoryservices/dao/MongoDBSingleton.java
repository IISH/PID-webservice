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

package org.socialhistoryservices.dao;

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Instantiates a single Mongo instance.
 */
public class MongoDBSingleton {

    private Mongo mongo = null;

    public MongoDBSingleton() {
    }

    public MongoDBSingleton(String[] hosts, int connectionsPerHost, int writeConcern) {
        MongoOptions options = new MongoOptions();
        options.w = writeConcern;
        options.connectionsPerHost = connectionsPerHost;
        setMongo(hosts, options);
    }

    public MongoDBSingleton(String[] hosts, MongoOptions options) {
        setMongo(hosts, options);
    }

    private synchronized Mongo setMongo(String[] serverAddresses, MongoOptions options) {

        if (mongo == null) {

            List<ServerAddress> replSet = new ArrayList<ServerAddress>(serverAddresses.length);
            for (String url : serverAddresses) {
                String[] split = url.split(":", 2);
                try {
                    ServerAddress host = (split.length == 2)
                            ? new ServerAddress(split[0], Integer.parseInt(split[1]))
                            : new ServerAddress(url);
                    replSet.add(host);
                } catch (UnknownHostException e) {
                    System.err.println(e);
                    e.printStackTrace(System.err);
                }
            }
            mongo = new Mongo(replSet, options);
        }
        return mongo;
    }

    public Mongo getInstance() {
        return mongo;
    }
}