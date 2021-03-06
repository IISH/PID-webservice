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

package org.socialhistoryservices.pid.ws;

import org.springframework.ws.transport.http.WsdlDefinitionHandlerAdapter;

import javax.servlet.http.HttpServletRequest;

public class WsdlAdapter extends WsdlDefinitionHandlerAdapter {

    /**
     * We override this method because we need to be able to return absolute URLs.
     *
     * @param location Location of the wsdl
     * @param request Http request
     * @return The absolute url
     */
    @Override
    protected String transformLocation(String location, HttpServletRequest request) {

        if (!location.startsWith("/"))
            return location;
        StringBuilder url = new StringBuilder(request.getScheme());
        url.append("://").append(request.getServerName()).append(':').append(request.getServerPort());
        // a relative path, prepend the context path
        url.append(request.getContextPath()).append(location);
        return url.toString();
    }
}
