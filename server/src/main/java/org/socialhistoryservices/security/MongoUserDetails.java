/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2018, International Institute of Social History
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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple user details
 */
public class MongoUserDetails implements UserDetails {

    protected static boolean ACCOUNT_NON_EXPIRED = true;
    protected static boolean ACCOUNT_NON_LOCKED = true;
    protected static boolean CREDENTIALS_NON_EXPIRED = true;
    protected static boolean ENABLED = true;

    private String username;
    private String password;
    private Boolean accountNonExpired = ACCOUNT_NON_EXPIRED;
    private Boolean accountNonLocked = ACCOUNT_NON_LOCKED;
    private Boolean credentialsNonExpired = CREDENTIALS_NON_EXPIRED;
    private Boolean enabled = ENABLED;
    private Collection<GrantedAuthority> grantedAuthorities;

    public void setAuthorities(Collection<GrantedAuthority> grantedAuthorities){
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        if ( grantedAuthorities == null )
            grantedAuthorities = new ArrayList<GrantedAuthority>();
        return grantedAuthorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
