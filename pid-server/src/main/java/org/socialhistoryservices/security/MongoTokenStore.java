/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2012, International Institute of Social History
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
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth2 provider of tokens. Made for MongoDB
 */
final public class MongoTokenStore implements TokenStore {

    private static final String DATABASE = "iaa";
    private static final String OAUTH_ACCESS_TOKEN = "oauth_access_token";
    private static final String OAUTH_REFRESH_TOKEN = "oauth_refresh_token";
    private final ConcurrentHashMap<String, OAuth2AccessToken> accessTokenStore = new ConcurrentHashMap<String, OAuth2AccessToken>();
    private final ConcurrentHashMap<String, OAuth2Authentication> authenticationTokenStore = new ConcurrentHashMap<String, OAuth2Authentication>();
    private final ConcurrentHashMap<String, Long> expirationTokenStore = new ConcurrentHashMap<String, Long>();
    private long sliderExpiration = 30000; // Thirty seconds
    private String database;
    private Mongo mongo;

    /**
     * selectKeys
     * <p/>
     * returns all keys that belong to a principal
     *
     * @param username The identifier of the principal
     * @return The OAuth2AccessToken associated with this principal
     */
    public OAuth2AccessToken selectKeys(String username) {
        final BasicDBObject query = new BasicDBObject("name", username);
        final DBCollection collection = getCollection(OAUTH_ACCESS_TOKEN);
        DBObject document = collection.findOne(query);
        return (document == null)
                ? null
                : (OAuth2AccessToken) SerializationUtils.deserialize((byte[]) document.get("token"));
    }

    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        // insert into oauth_access_token (token_id, token, authentication_id, authentication, refresh_token) values (?, ?, ?, ?, ?)
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }
        final String name = ( authentication.getUserAuthentication() == null ) ? null : authentication.getUserAuthentication().getName();
        final BasicDBObject document = new BasicDBObject();
        document.put("token_id", token.getValue());
        document.put("token", SerializationUtils.serialize(token));
        document.put("authentication_id", null);
        document.put("authentication", SerializationUtils.serialize(authentication));
        document.put("refresh_token", refreshToken);
        document.put("name", name);
        final DBCollection collection = getCollection(OAUTH_ACCESS_TOKEN);
        collection.insert(document);


    }

    public OAuth2AccessToken readAccessToken(String tokenValue) {

        OAuth2AccessToken accessToken = (isFresh(tokenValue))
                ? this.accessTokenStore.get(tokenValue)
                : null;
        if (accessToken == null) {
            // select token_id, token from oauth_access_token where token_id = ?
            final BasicDBObject query = new BasicDBObject("token_id", tokenValue);
            final DBCollection collection = getCollection(OAUTH_ACCESS_TOKEN);
            DBObject document = collection.findOne(query);
            if (document == null) {
            } else {
                accessToken = SerializationUtils.deserialize((byte[]) document.get("token"));
                this.accessTokenStore.put(tokenValue, accessToken);
                expiration(tokenValue);
            }
        }
        return accessToken;
    }

    public void removeAccessToken(String tokenValue) {

        final BasicDBObject query = new BasicDBObject("token_id", tokenValue);
        final DBCollection collection = getCollection(OAUTH_ACCESS_TOKEN);
        collection.remove(query);
        this.accessTokenStore.remove(tokenValue);
    }

    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {

        final String tokenValue = token.getValue();
        OAuth2Authentication authentication = (isFresh(tokenValue))
                ? this.authenticationTokenStore.get(tokenValue)
                : null;
        if (authentication == null) {
            // select token_id, authentication from oauth_access_token where token_id = ?
            final BasicDBObject query = new BasicDBObject();
            query.put("token_id", token.getValue());
            final DBCollection collection = getCollection(OAUTH_ACCESS_TOKEN);
            final DBObject document = collection.findOne(query);
            if (document == null) {
            } else {
                authentication = SerializationUtils.deserialize((byte[]) document.get("authentication"));
                this.authenticationTokenStore.put(tokenValue, authentication);
                expiration(tokenValue);
            }
        }
        return authentication;
    }

    public void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

        // insert into oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)
        final BasicDBObject document = new BasicDBObject();
        document.put("token_id", refreshToken.getValue());
        document.put("token", SerializationUtils.serialize(refreshToken));
        document.put("authentication", SerializationUtils.serialize(authentication));
        final DBCollection collection = getCollection(OAUTH_REFRESH_TOKEN);
        collection.insert(document);
    }

    public ExpiringOAuth2RefreshToken readRefreshToken(String token) {

        // select token_id, token from oauth_refresh_token where token_id = ?
        ExpiringOAuth2RefreshToken refreshToken = null;
        final BasicDBObject query = new BasicDBObject("token_id", token);
        final DBCollection collection = getCollection(OAUTH_REFRESH_TOKEN);
        final DBObject document = collection.findOne(query);
        if (document == null) {
        } else {
            refreshToken = SerializationUtils.deserialize((byte[]) document.get("token"));
        }
        return refreshToken;
    }

    public void removeRefreshToken(String token) {

        // remove from oauth_refresh_token where token_id = ?
        final BasicDBObject query = new BasicDBObject("token_id", token);
        final DBCollection collection = getCollection(OAUTH_REFRESH_TOKEN);
        collection.remove(query);
    }

    public OAuth2Authentication readAuthentication(ExpiringOAuth2RefreshToken token) {

        // select token_id, authentication from oauth_refresh_token where token_id = ?
        OAuth2Authentication authentication = null;
        final BasicDBObject query = new BasicDBObject("token_id", token.getValue());
        final DBCollection collection = getCollection(OAUTH_REFRESH_TOKEN);
        final DBObject document = collection.findOne(query);
        if (document == null) {
        } else {
            authentication = SerializationUtils.deserialize((byte[]) document.get("authentication"));
        }
        return authentication;
    }

    public void removeAccessTokenUsingRefreshToken(String refreshToken) {

        // remove from oauth_access_token where refresh_token = ?
        final BasicDBObject query = new BasicDBObject("refresh_token", refreshToken);
        final DBCollection collection = getCollection(OAUTH_ACCESS_TOKEN);
        collection.remove(query);
    }

    private void expiration(String tokenValue) {
        this.expirationTokenStore.put(tokenValue, new Date().getTime() + sliderExpiration);
    }

    /**
     * isFresh
     * 
     * Determines if we can use the cache...
     *
     * @param tokenValue the token to look for
     * @return Fresh when the token is requested within the sliding expiration span
     */
    private boolean isFresh(String tokenValue) {

        long expiration = (expirationTokenStore.containsKey(tokenValue))
                ? expirationTokenStore.get(tokenValue)
                : 0;
        long time = new Date().getTime();
        if (expiration > time) {
            this.expirationTokenStore.put(tokenValue, time + sliderExpiration);
            return true;
        }
        expirationTokenStore.remove(tokenValue);
        accessTokenStore.remove(tokenValue);
        authenticationTokenStore.remove(tokenValue);
        return false;
    }

    public void setSliderExpiration(long sliderExpiration) {
        this.sliderExpiration = sliderExpiration;
    }

    private DBCollection getCollection(String collection) {

        final DB db = mongo.getDB(getDatabase());
        return db.getCollection(collection);
    }

    public String getDatabase() {
        if (database == null)
            database = DATABASE;
        return database;
    }

    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    public void setDatabase(String database) {
        this.database = database;
        final DBCollection c = getCollection(OAUTH_ACCESS_TOKEN);
        c.ensureIndex("token_id");
        c.ensureIndex("token_id");
    }
}