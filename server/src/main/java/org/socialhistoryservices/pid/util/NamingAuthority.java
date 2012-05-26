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

package org.socialhistoryservices.pid.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The providers authorities are converted into spring roles.
 *
 * @author Lucien van Wouw <lwo@iisg.nl>
 */
public class NamingAuthority {

    private static final String role_prefix = "ROLE_NA_";
    private static final String role_anonymous = "IS_AUTHENTICATED_ANONYMOUSLY";

    public static List<String> getNaRole(Authentication userAuthentication) {

        final Collection<? extends GrantedAuthority> authorities = userAuthentication.getAuthorities();
        final List<String> nas = new ArrayList(authorities.size());
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority().replace("\n",""); // ToDo: find out why there sometimes is a \n in the role ?
            if (role.startsWith(role_prefix)) {
                nas.add(role.substring(role_prefix.length()));
            }
        }
        if (nas.size() == 0)
            throw new SecurityException("User " + userAuthentication.getName() + " has not got the required roles to use this service.");
        return nas;
    }

    /**
     * Get the Nameing authority. It is either an prefix in itself or part of a pid.
     *
     * @param na naming authority or prefix of the pid, separated by a forward slash.
     * @return The naming authority
     */
    public static String getNaRole(String na) {
        if ( na == null )
            return role_anonymous;
        String[] split = na.split("/", 2);
        return split[0];
    }
}
