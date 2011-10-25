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

package org.socialhistoryservices.pid.security;

import org.socialhistoryservices.pid.util.NamingAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.List;

/**
 * Authorization by authority.
 *
 * @author Lucien van Wouw <lwo@iisg.nl>
 */
public class NAAuthentication {
    /**
     * Retrieves the user.
     *
     * @return
     */
    public List<String> authenticate() {

        final SecurityContext context = SecurityContextHolder.getContext();
        final OAuth2Authentication authentication = (OAuth2Authentication) context.getAuthentication();
        return NamingAuthority.getNaRole(authentication.getUserAuthentication());
    }

    /**
     * Checks if the user is allowed to manage the resource.
     *
     * @param pid
     * @return
     */
    public String authorize(String pid) {

        List<String> nas = authenticate();
        String authority = NamingAuthority.getNaRole(pid);
        if (nas.contains(authority))
            return authority;
        throw new SecurityException("The user has not got the authority to use the resource \"" + authority + "\"");
    }
}