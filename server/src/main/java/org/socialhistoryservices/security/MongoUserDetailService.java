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

import com.mongodb.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provider with MongoDB storage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <h4>The Users collection</h4>
 * <p/>
 * This collection contains the login name, password and enabled status of the user.
 * Because we aim at the Spring Security container the authorities are prefixed with "ROLE_"
 * <p/>
 * collection users
 * document:
 * {
 * username: String
 * password: String
 * enabled: Boolean
 * authorities:[String]
 * }
 *
 * @author Lucien van Wouw <lwo@iisg.nl>
 */

public class MongoUserDetailService implements UserDetailsService {

    private final Log log = LogFactory.getLog(this.getClass());
    private Mongo mongo;
    private String database;
    private String collection;
    private static final String DATABASE = "iaa";
    private static final String COLLECTION = "users";
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String HASH = "SHA-256";

    public MongoUserDetailService() {
    }

    public MongoUserDetailService(Mongo mongo) {
        this.mongo = mongo;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails userDetails = getUser(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Query returned no results for user '" + username + "'");
        }
        return userDetails;
    }

    public void createUser(MongoUserDetails user) {

        if ( user.getPassword()!=null)
            user.setPassword(HashPassword.encrypt(HASH, user.getPassword()));

        final DBCollection coll = coll();
        BasicDBObject query = new BasicDBObject("username", user.getUsername());
        DBObject tmp = coll.findOne(query);
        if (tmp != null) {
            if (user.getPassword() == null) {
                user.setPassword((String) tmp.get("password"));
            }
            if (user.getAuthorities().size() == 0) {
                BasicDBList authorities = (BasicDBList) tmp.get("authorities");
                for (Object authority : authorities) {
                    user.getAuthorities().add(new org.socialhistoryservices.security.MongoAuthority((String) authority));
                }
            }
        }

        BasicDBObject document = new BasicDBObject();
        document.put("username", user.getUsername());
        document.put("password", user.getPassword());
        document.put("enabled", user.isEnabled());
        document.put("accountNonExpired", user.isAccountNonExpired());
        document.put("accountNonLocked", user.isAccountNonLocked());
        document.put("credentialsNonExpired", user.isCredentialsNonExpired());
        BasicDBList authorities = new BasicDBList();
        for (GrantedAuthority authority : user.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        document.put("authorities", authorities);
        final WriteResult result = coll.update(query, document, true, false, WriteConcern.SAFE);
        if (result.getN() == 0)
            log.error(new Exception("Adding the user failed: " + result.getError()));
        log.info("Persisted:\n" + document.toString());
    }

    /**
     * There should normally only be one matching user.
     * Authorities are mapped to Spring roles
     */
    private UserDetails getUser(String username) {

        final DBCollection coll = coll();
        final BasicDBObject query = new BasicDBObject("username", username);
        final DBObject document = coll.findOne(query);
        if (document == null)
            return null;
        final MongoUserDetails userDetails = new MongoUserDetails();

        userDetails.setUsername((String) document.get("username"));
        userDetails.setPassword((String) document.get("password"));
        userDetails.setEnabled(getBoolean(document.get("enabled"), MongoUserDetails.ENABLED));
        userDetails.setAccountNonExpired(getBoolean(document.get("accountNonExpired"), MongoUserDetails.ACCOUNT_NON_EXPIRED));
        userDetails.setAccountNonLocked(getBoolean(document.get("accountNonLocked"), MongoUserDetails.ACCOUNT_NON_LOCKED));
        userDetails.setCredentialsNonExpired(getBoolean(document.get("credentialsNonExpired"), MongoUserDetails.CREDENTIALS_NON_EXPIRED));

        Object o = document.get("authorities");
        if (o != null) {
            BasicDBList authorities = (BasicDBList) o;
            List<GrantedAuthority> grants = new ArrayList<GrantedAuthority>(authorities.size());
            for (Object authority : authorities) {
                MongoAuthority grant = new MongoAuthority(ROLE_PREFIX + authority);
                grants.add(grant);
            }
            userDetails.setAuthorities(grants);
        }
        return userDetails;
    }

    private Boolean getBoolean(Object value, boolean d) {
        if (value == null)
            return d;
        return (Boolean) value;
    }

    public boolean remove(String username) {

        final DBCollection coll = coll();
        final BasicDBObject query = new BasicDBObject("username", username);
        WriteResult result = coll.remove(query, WriteConcern.SAFE);
        return (result.getN() != 0);
    }

    private DBCollection coll() {

        final DB db = mongo.getDB(getDatabase());
        return db.getCollection(getCollection());
    }

    public final void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    public void setDatabase(String database) {
        this.database = database;
        final DBCollection c = mongo.getDB(database).getCollection(getCollection());
        c.ensureIndex("username");
    }

    private String getDatabase() {
        if (database == null)
            database = DATABASE;
        return database;
    }

    public void setCollection(String collection) {
        if (this.collection == null)
            this.collection = COLLECTION;
        this.collection = collection;
    }

    private String getCollection() {
        if (collection == null)
            collection = COLLECTION;
        return collection;
    }
}