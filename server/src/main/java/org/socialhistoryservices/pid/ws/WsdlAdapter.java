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

import org.springframework.util.StringUtils;
import org.springframework.ws.transport.http.WsdlDefinitionHandlerAdapter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lwo
 * Date: 23-4-11
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
public class WsdlAdapter extends WsdlDefinitionHandlerAdapter {

    /**
     * We override this method because we need to be able to return absolute URLs.
     *
     * @param location
     * @param request
     * @return
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
